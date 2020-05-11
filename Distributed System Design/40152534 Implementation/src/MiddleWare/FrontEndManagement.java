package MiddleWare;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import org.omg.CORBA.ORB;

import Dependencies.FrontEndData;
import Dependencies.Network;
import Dependencies.Request;
import Dependencies.Result;
import FrontEndApp.FrontEndServerPOA;

public class FrontEndManagement extends FrontEndServerPOA{
	private ORB orb;
	
	/*
	 * 1. Receives each request
	 * 2. UDPUniCastSend
	 * 3. UDPUniCastReceive
	 * 4. Calculate Dynamic Time
	 * 5. Check Process Crash
	 * 6. Check Software Failure 
	 */
	DatagramSocket sSocket = null, rSocket = null;
	InetAddress sHost, rHost;
	int sServerPort, rServerPort;
	long dynamicTime = 2000;
	
	//Maintains all Request-Response data..
	Map<Integer, FrontEndData> FrontEndDBDEL = new TreeMap<Integer, FrontEndData>();
	volatile ConcurrentHashMap<String, FrontEndData> FrontEndDB = new ConcurrentHashMap<String, FrontEndData>();
	Map<String, List<Network>> NetworkMap =  new HashMap<String, List<Network>>();
	
	public FrontEndManagement() {
		String textline;
		String text[];
		List<Network> list = new ArrayList<Network>();
		
		//Read the Network file.
		File file = new File("Network.txt");
		try(BufferedReader br = new BufferedReader(new FileReader(file))){
			while((textline = br.readLine()) != null){ //assign and check
				if (textline.charAt(0) == '*') continue;
				text = textline.split("\\,");
				
				list = NetworkMap.get(text[0]);
				if (list == null) {
					list= new ArrayList<Network>();
					list.add(new Network(text[1],text[2],Integer.parseInt(text[3])));
				}
				else list.add(new Network(text[1],text[2],Integer.parseInt(text[3])));
				
				NetworkMap.put(text[0], list);
			}
		} catch (FileNotFoundException e1) {System.out.println("Can't open the file" +file.toString());}
		catch (IOException e1) {System.out.println("unable to read the file" +file.toString());}
		
		//Setup 
		try {
			this.sSocket = new DatagramSocket(NetworkMap.get("FRONTEND").get(0).getRunningPort());
			this.sHost = InetAddress.getByName(NetworkMap.get("SEQUENCER").get(0).getRunningIP());
			this.sServerPort = NetworkMap.get("SEQUENCER").get(0).getRunningPort();
			this.rSocket = new DatagramSocket(NetworkMap.get("FRONTEND").get(1).getRunningPort());
			
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
	public void shutdown() {
		// TODO Auto-generated method stub
		orb.shutdown(false);
	}

	@Override
	public String sendtoFE(String message) {
		// TODO Auto-generated method stub
		
		Request requestObj = new Request();
		requestObj.setRequestType("CLIENT_REQUEST");
		requestObj.setRequestMsg(message);
		
//		//Simulate Process Crash..
//		if (message.substring(9, 18).equals("bookEvent") && message.substring(19, 26).equals("Seminar")) {
//			requestObj.setImplProcessCrash(true);
//		}
		
		//Send the request..
		FrontEndDB.put(message, new FrontEndData());
		UDPUniCastSend(requestObj);
		
		//Initiate UniCast Receive...
		synchronized(this) {
			UDPUniCastReceive(message);
		}
		
		//Check whether you got the result.. If yes, come out..
		while(true) {
			if (FrontEndDB.get(message).getRequestStatus() != null) break;
		}
		
		//Got the result
		switch (FrontEndDB.get(message).getRequestStatus()) {
		case "SOFTWARE_FAILURE":
			System.out.println("SOFTWARE_FAILURE for the request: "+message +" - " +FrontEndDB.get(message));
			requestObj = new Request();
			requestObj.setRequestType("SOFTWARE_FAILURE");
			requestObj.setRequestMsg(FrontEndDB.get(message).getSwFailedRMID());
			
			if (isConsecutiveFailure(FrontEndDB.get(message).getSwFailedRMID(), message)) {
				requestObj.setConsecutiveswFailure(true);
			}
			
			//Send the request..
			UDPUniCastSend(requestObj);
			return FrontEndDB.get(message).getCorrectResponse();
		
		case "PROCESS_CRASH":
			System.out.println("PROCESS_CRASH for the request: "+message +" - " +FrontEndDB.get(message));
			if (FrontEndDB.get(message).getCrashedRMIDs().size() == 3) return "Time Out from all 3 RMs.."; 
			
			requestObj = new Request();
			requestObj.setRequestType("PROCESS_CRASH");
			
			StringBuilder requestMsg = new StringBuilder();
			for(String crashedRM : FrontEndDB.get(message).getCrashedRMIDs()) {
				requestMsg.append(crashedRM).append(",");
			}
			
			String reqMsg = requestMsg.toString();
			requestObj.setRequestMsg(reqMsg.substring(0, reqMsg.length()-1));
			
			//Send the request..
			UDPUniCastSend(requestObj);
			return FrontEndDB.get(message).getCorrectResponse();
		
		default:
			System.out.println("SUCCESS for the request: "+message +" - " +FrontEndDB.get(message));
			return FrontEndDB.get(message).getCorrectResponse();
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
			
			DatagramPacket packet = new DatagramPacket(serializedMessage, serializedMessage.length, sHost, sServerPort);
			sSocket.send(packet);
			System.out.println("Request = " +requestObj +" - sent to the Sequencer.");
			
		}catch (SocketException e) {System.out.println("Socket: " + e.getMessage());}
		catch (IOException e) {System.out.println("IO: " + e.getMessage());}
		finally {
			//if (aSocket != null) aSocket.close();
		}
	}
	
	// UDP UniCastReceive..
	public void UDPUniCastReceive(String message) {
		if (FrontEndDB.get(message).getRequestStatus() != null) return;
		DatagramPacket request;
		byte[] buffer = new byte[10000];
		Result resultObj;
		long tempDynamicTime = dynamicTime, duration, startTime;
		String failedRMID;
		List<String> crashedRMs;
		System.out.println(">>> FrontEnd UDPUniCastReceive Started at Port-  " + NetworkMap.get("FRONTEND").get(1).getRunningPort() +". With Dynamic Time: "+dynamicTime);
		
		// Main Logic
		startTime = System.currentTimeMillis();
		try {
			while (true) {
				request = new DatagramPacket(buffer, buffer.length);
				rSocket.setSoTimeout((int) tempDynamicTime);
				rSocket.receive(request);
				duration = System.currentTimeMillis() - startTime; // Calculate duration time and update Temp Dynamic time..

				ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(buffer)); // Get the Result Object..
				resultObj = (Result) iStream.readObject();
				iStream.close();
				System.out.println("Reply Received at - " + duration + " is = " + resultObj + " - From Port no."+ request.getPort());
				
				//If old responses received, ignore them..
				if (duration == 0) continue;

				// Store in FrontEndDB
				if (FrontEndDB.get(resultObj.getRequestMsg()).getRequestStatus() == null) {
					tempDynamicTime = dynamicTime - duration;
					if (tempDynamicTime <= 0) tempDynamicTime = dynamicTime;
					System.out.println("Temp Dynamic Time = " + tempDynamicTime);
					
					FrontEndDB.get(resultObj.getRequestMsg()).setSeqNo(resultObj.getSeqNo());
					FrontEndDB.get(resultObj.getRequestMsg()).setTimeListValue(duration);
					FrontEndDB.get(resultObj.getRequestMsg()).setReplyListValue(resultObj);
				}
				
				// Calculate the Dynamic Time if all 3 responses are received for a request..
				if (FrontEndDB.get(resultObj.getRequestMsg()).getCountOfReplies() == 3) {
					if (FrontEndDB.get(resultObj.getRequestMsg()).getSlowestTime() != 0) {
						dynamicTime = 2 * FrontEndDB.get(resultObj.getRequestMsg()).getSlowestTime();
					}
					
					/*//Set Min and Max Limit..
					if (dynamicTime < 300) dynamicTime = 300;
					if (dynamicTime > 5000) dynamicTime = 5000;*/
					
					tempDynamicTime = dynamicTime;
					System.out.println("Dynamic Time Updated = " + dynamicTime);
				}
			}
		} catch (SocketTimeoutException e) { // Time out now...Will not receive any more responses.. Set the status for so far received responses..
			for (String reqMessage : FrontEndDB.keySet()) {
				if (FrontEndDB.get(reqMessage).getCountOfReplies() == 3) {
					failedRMID = checkSoftwareFailure(FrontEndDB.get(reqMessage).getReplyList());
					if (failedRMID == null) {
						FrontEndDB.get(reqMessage).setCorrectResponse(FrontEndDB.get(reqMessage).getReplyList().get(0).getResponse());
						FrontEndDB.get(reqMessage).setRequestStatus("SUCCESS");
					} else {
						FrontEndDB.get(reqMessage).setSwFailedRMID(failedRMID);
						// Give the Correct result..
						for (Result result : FrontEndDB.get(reqMessage).getReplyList()) {
							if (!result.getRMID().equals(failedRMID))
								FrontEndDB.get(reqMessage).setCorrectResponse(result.getResponse());
						}
						FrontEndDB.get(reqMessage).setRequestStatus("SOFTWARE_FAILURE");
					}
				}
				else {
					crashedRMs = checkRMStatus(FrontEndDB.get(reqMessage).getReplyList());
					FrontEndDB.get(reqMessage).setCrashedRMIDs(crashedRMs);
					//Give Some result back..
					if (crashedRMs.size() != 3) FrontEndDB.get(reqMessage).setCorrectResponse(FrontEndDB.get(reqMessage).getReplyList().get(0).getResponse());
					FrontEndDB.get(reqMessage).setRequestStatus("PROCESS_CRASH");
				}
			}
		} catch (SocketException e) {System.out.println("Socket error in UDPUniCastReceive: " + e.getMessage());
		} catch (IOException e) {System.out.println("IO error in UDPUniCastReceive: " + e.getMessage());
		} catch (ClassNotFoundException e) {System.out.println("Class Not Found Exception in UDPUniCastReceive: " + e.getMessage());
		}
	}	
			
	//Check for Process crash..
	public List<String> checkRMStatus(List<Result> replyList) {
		List<String> crashedRMs = new ArrayList<String>();
		crashedRMs.add("RM01");
		crashedRMs.add("RM02");
		crashedRMs.add("RM03");
		
		for(Result result: replyList) {
			crashedRMs.remove(result.getRMID());
		}
		return crashedRMs;
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
	public synchronized boolean isConsecutiveFailure(String failedRMID, String message) {
		// TODO Auto-generated method stub
		int currentSeqNo = FrontEndDB.get(message).getSeqNo();
		boolean firstPrev = false, secondPrev = false;
		
		for (String str: FrontEndDB.keySet()) {
			if (FrontEndDB.get(str).getSeqNo() ==  (currentSeqNo-1)) {
				if (FrontEndDB.get(str).getSwFailedRMID() != null && FrontEndDB.get(str).getSwFailedRMID().equals(failedRMID)) firstPrev = true;
			}
			
			if (FrontEndDB.get(str).getSeqNo() ==  (currentSeqNo-2)) {
				if (FrontEndDB.get(str).getSwFailedRMID() != null && FrontEndDB.get(str).getSwFailedRMID().equals(failedRMID)) secondPrev = true;
			}
		}
		
		if (firstPrev && secondPrev) return true;
		else return false;
	}
}