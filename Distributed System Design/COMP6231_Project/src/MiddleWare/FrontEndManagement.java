package MiddleWare;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.omg.CORBA.ORB;

import Dependencies.FrontEndData;
import Dependencies.Request;
import Dependencies.Result;
import FrontEndApp.FrontEndServerPOA;

public class FrontEndManagement extends FrontEndServerPOA{
	private ORB orb;
	
	/*
	 * 1. Receives each request.
	 * 2. Formats in - ID,methodName,eventType,eventID,oldEventType,oldEventID,bookingCapacity,CID
	 * 3. UDPUniCastSend runs on 8001.
	 * 4. UDPUniCastReceive 
	 */
	DatagramSocket aSocket = null;
	InetAddress aHost;
	int serverPort;
	long startTime, duration, dynamicTime=5250;
	boolean isFirstReq = true;
	
	//Maintains all Request-Response data..
	Map<Integer, FrontEndData> FrontEndDB = new TreeMap<Integer, FrontEndData>();
		
	public FrontEndManagement() {
		try {
			this.aSocket = new DatagramSocket(8000);
			this.aHost = InetAddress.getByName("127.0.0.1");
			this.serverPort = 8001;
		} catch (SocketException e) {
			System.out.println("SocketExeption in FrontEndManagement.. " + e.getMessage());
		} catch (UnknownHostException e) {
			System.out.println("UnknownHostException in FrontEndManagement.. " + e.getMessage());
		}
	}
	
	public void setORB(ORB orb_val) {
		// TODO Auto-generated method stub
		orb = orb_val;
	}

	@Override
	public String sendtoFE(String message) {
		// TODO Auto-generated method stub
		int seqNo;
		
		Request requestObj = new Request();
		requestObj.setRequestType("CLIENT_REQUEST");
		requestObj.setRequestMsg(message);
		
		//Send the request..
		UDPUniCastSend(requestObj);
		
		//Receive the result..
		seqNo = UDPUniCastReceive();
		
		switch (FrontEndDB.get(seqNo).getRequestStatus()) {
		case "SOFTWARE_FAILURE":
			requestObj = new Request();
			requestObj.setRequestType(FrontEndDB.get(seqNo).getRequestStatus());
			requestObj.setRequestMsg(FrontEndDB.get(seqNo).getSwFailedRMID());
			
			if (isConsecutiveFailure(FrontEndDB.get(seqNo).getSwFailedRMID(), seqNo)) {
				requestObj.setConsecutiveswFailure(true);
			}
			
			//Send the request..
			UDPUniCastSend(requestObj);
			return FrontEndDB.get(seqNo).getCorrectResponse();
		
		case "PROCESS_CRASH":
			requestObj = new Request();
			requestObj.setRequestType("PROCESS_CRASH");
			requestObj.setRequestMsg(FrontEndDB.get(seqNo).getCrashedRMIDs().toString());
			
			//Send the request..
			UDPUniCastSend(requestObj);
			return FrontEndDB.get(seqNo).getCorrectResponse();
		
		default: 
			return FrontEndDB.get(seqNo).getCorrectResponse();
		}
	}

	//UDP UniCastSend
	public void UDPUniCastSend(Request requestObj) {
		ByteArrayOutputStream bStream = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oo = new ObjectOutputStream(bStream);
			oo.writeObject(requestObj);
			oo.close();
			
			byte[] serializedMessage = new byte[10000];
			serializedMessage = bStream.toByteArray();
			
			DatagramPacket packet = new DatagramPacket(serializedMessage, serializedMessage.length, aHost, serverPort);
			aSocket.send(packet);
			System.out.println("Request = " +requestObj +" - sent to the Sequencer.");
			
		}catch (SocketException e) {System.out.println("Socket: " + e.getMessage());}
		catch (IOException e) {System.out.println("IO: " + e.getMessage());}
		finally {
			//if (aSocket != null) aSocket.close();
		}
	}
	
	//UDP UniCastReceive
	public int UDPUniCastReceive() {
		FrontEndData frontendDataObj = new FrontEndData();
		DatagramSocket aSocket = null;
		List<Result> replyList = new LinkedList<Result>();
		List<Long> timeList = new LinkedList<Long>();
		Result resultObj = null;
		
		//Main Logic
		try {
			DatagramPacket request;
			aSocket = new DatagramSocket(8006);
			byte[] buffer = new byte[10000];
			
			System.out.println("FontEnd UDPUniCastReceive Started at Port-  "+8006);
			long tempdynamicTime = dynamicTime;
			System.out.println("Dynamic Time= " +dynamicTime);
			startTime = System.currentTimeMillis();
			
			while(true) {
				System.out.println("New tempdynamicTime= " +tempdynamicTime);
				aSocket.setSoTimeout((int) tempdynamicTime);
				
				request = new DatagramPacket(buffer, buffer.length);	
				aSocket.receive(request);
				
				//Add received time of all requests...
				duration = System.currentTimeMillis()- startTime;
				timeList.add(duration);
				tempdynamicTime = dynamicTime - duration;
				System.out.println("Timings of RMs.. " +timeList);
				
				ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(buffer));
				resultObj = (Result) iStream.readObject();
				iStream.close();
				
				System.out.println("Reply Received at - " +new java.util.Date().toString() +" is = " +resultObj +" - From Port no." +request.getPort());
				replyList.add(resultObj);
				
				
				//Check whether it's a first request..
				if (isFirstReq == true) {  //No Process Crash..
					if (replyList.size() == 3) {
						//All 3 RMs sent their responses.. Check for Software Failure..
						dynamicTime =  2* timeList.get(timeList.size()-1);
						String failedRMID = checkSoftwareFailure(replyList);
						
						if (failedRMID == null) {  //No Software Failure..
							frontendDataObj = new FrontEndData(replyList.get(0).getSeqNo(), replyList.get(0).getRequestMsg(), "Success", replyList.get(0).getResponse(), timeList, null, null);
							FrontEndDB.put(resultObj.getSeqNo(), frontendDataObj);
						}
						else {  //Software Failure occurred..							
							frontendDataObj = new FrontEndData(replyList.get(0).getSeqNo(), replyList.get(0).getRequestMsg(),"SOFTWARE_FAILURE", null, timeList, failedRMID, null);
							
							//Update correct result..
							for(Result result: replyList) {
								if (!result.getRMID().equals(failedRMID)) frontendDataObj.setCorrectResponse(result.getResponse());
							}
							
							FrontEndDB.put(replyList.get(0).getSeqNo(), frontendDataObj);			
						}
						isFirstReq = false;
						return replyList.get(0).getSeqNo();
					}
				}
				else {
					
				}
			}
		}
		catch(SocketTimeoutException e) {
			System.out.println("Socket Timeout Exception in FrontEndManagement UDPUniCastReceive: " + e.getMessage());
			
//			//Time Limit Exceeded.. Check which RM got crashed..
//			Map<String, Character> RMStatus = checkRMStatus(replyList);
//			System.out.println("RM Status = "+RMStatus);
//			
//			StringBuilder sb = new StringBuilder();
//			sb.append("PROCESS_CRASH");
//			char crashedRM = 'N';
//			
//			for(String RMID: RMStatus.keySet()) {
//				if (RMStatus.get(RMID) != 'Y') {
//					sb.append("|").append(RMID);
//					crashedRM = 'Y';
//				}
//			}
//			
//			if (crashedRM == 'Y') return sb.toString();
//			else return null;
		}
		catch (SocketException e){System.out.println("Socket error in FrontEndManagement UDPUniCastReceive: " + e.getMessage());}
		catch (IOException e) {System.out.println("IO error in FrontEndManagement UDPUniCastReceive: " + e.getMessage());}
		catch (ClassNotFoundException e) {System.out.println("ClassNotFoundException exceception in UDPUniCastReceive.." +e.getMessage());}
		finally {
			if (aSocket !=null) aSocket.close();
		}
		return 0;
	}

	//Check for Process crash..
	public Map<String, Character> checkRMStatus(List<Result> replyList) {
		Map<String, Character> RMStatus = new HashMap<String, Character>();
		
		//Initialize..
		RMStatus.put("RM01", 'N');
		RMStatus.put("RM02", 'N');
		RMStatus.put("RM03", 'N');
		
		for(Result result: replyList) {
			switch(result.getRMID()) {
			case "RM01":
				RMStatus.put("RM01", 'Y');
				break;
			case "RM02": 
				RMStatus.put("RM02", 'Y');
				break;
			case "RM03":
				RMStatus.put("RM03", 'Y');
				break;
			}
		}
		
		//Find which all RMs crashed..
		for(String RMID: RMStatus.keySet()) {
			if (RMStatus.get(RMID) != 'Y') System.out.println(RMID +" is crashed..");
		}
		
		return RMStatus;
	}
	
	//Check for Software Failure..
	public String checkSoftwareFailure(List<Result> replyList) {
		if (replyList.get(0).getResponse().equalsIgnoreCase(replyList.get(1).getResponse()) && replyList.get(0).getResponse().equalsIgnoreCase(replyList.get(2).getResponse())) {
			return null;   //"No Software Failure";
		}
		else if(replyList.get(0).getResponse().equalsIgnoreCase(replyList.get(1).getResponse())) return replyList.get(2).getRMID();
		else if(replyList.get(0).getResponse().equalsIgnoreCase(replyList.get(2).getResponse())) return replyList.get(1).getRMID();
		else if(replyList.get(1).getResponse().equalsIgnoreCase(replyList.get(2).getResponse())) return replyList.get(0).getRMID();
		else return null; //"No two RMs sent the same response..";
	}
	
	//Checks whether it is a Consecutive Failure..
	private boolean isConsecutiveFailure(String failedRMID, int seqNo) {
		// TODO Auto-generated method stub
		
		if (FrontEndDB.get(seqNo-1) != null && FrontEndDB.get(seqNo-2) != null) {
			if (FrontEndDB.get(seqNo-1).getSwFailedRMID().equals(failedRMID) && FrontEndDB.get(seqNo-2).getSwFailedRMID().equals(failedRMID)) return true;
			else return false;
		}
		else return false;
	}
}