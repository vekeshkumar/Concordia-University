package MiddleWare;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
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

import org.omg.CORBA.ORB;

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
	
	public FrontEndManagement() {
		try {
			this.aSocket = new DatagramSocket(8000);
			this.aHost = InetAddress.getByName("127.0.0.1");
			this.serverPort = 8001;
		} catch (SocketException e) {System.out.println("SocketExeption in FrontEndManagement.. " +e.getMessage());}
		catch (UnknownHostException e) {System.out.println("UnknownHostException in FrontEndManagement.. " +e.getMessage());}
	}
	
	public void setORB(ORB orb_val) {
		// TODO Auto-generated method stub
		orb = orb_val;
	}

	@Override
	public String sendtoFE(String message) {
		// TODO Auto-generated method stub
		String response;
		UDPUniCastSend("CLIENT_REQUEST|"+ message);
		
		response = UDPUniCastReceive();
		if (response.indexOf('|') != -1) {
			UDPUniCastSend(response);
			return UDPUniCastReceive();
		}
		else return response;
	}

		
	//UDP UniCastSend
	public void UDPUniCastSend(String message) {
		try {
			byte[] m = new byte[1024];
			m = message.getBytes();
			DatagramPacket request = new DatagramPacket(m, message.length(), aHost, serverPort);
			aSocket.send(request);
			System.out.println("Request = " +message +" - sent to the Sequencer.");
		}catch (SocketException e) {System.out.println("Socket: " + e.getMessage());}
		catch (IOException e) {System.out.println("IO: " + e.getMessage());}
		finally {
			//if (aSocket != null) aSocket.close();
		}
	}
	
	//UDP UniCastReceive
	public String UDPUniCastReceive() {
		DatagramSocket aSocket = null;
		List<Result> replyList = new LinkedList<Result>();
		List<Long> timeList = new LinkedList<Long>();
		Result ResultObj = null;
		
		//Main Logic
		try {
			DatagramPacket request;
			aSocket = new DatagramSocket(8006);
			byte[] buffer = new byte[1024];
			
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
				System.out.println("Timings of RMs.. " +timeList);
				
				tempdynamicTime = dynamicTime - duration;
				if (tempdynamicTime <=0) {
					System.out.println("Raising SocketTimeoutException..");
					throw new SocketTimeoutException("Socket Timeout");
				}
				
				//Take the object..
				ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(buffer));
				ResultObj = (Result) iStream.readObject();
				iStream.close();
				
				System.out.println("Reply Received at - " +new java.util.Date().toString() +" is = " +ResultObj +" - From Port no." +request.getPort());
				replyList.add(ResultObj);
				
				//Validate the result..
				if (replyList.size() == 3) {
					//All 3 RMs sent their responses.. Check for Software Failure..
					dynamicTime =  2* timeList.get(timeList.size()-1);
					String failedRMID = checkSoftwareFailure(replyList);
					
					//Return correct result..
					for(Result result: replyList) {
						if (!result.getRMID().equals(failedRMID)) return result.getResponse();
					}
				}
				else {
					//<3 RMs sent their response.. 
					if (System.currentTimeMillis() - startTime > dynamicTime) {
						System.out.println("Raising SocketTimeoutException..");
						throw new SocketTimeoutException("Socket Timeout");
					}
				}
			}
		}
		catch(SocketTimeoutException e) {
			System.out.println("Socket Timeout Exception in FrontEndManagement UDPUniCastReceive: " + e.getMessage());
			
			//Time Limit Exceeded.. Check which RM got crashed..
			Map<String, Character> RMStatus = checkRMStatus(replyList);
			System.out.println("RM Status = "+RMStatus);
			
			StringBuilder sb = new StringBuilder();
			sb.append("PROCESS_CRASH");
			char crashedRM = 'N';
			
			for(String RMID: RMStatus.keySet()) {
				if (RMStatus.get(RMID) != 'Y') {
					sb.append("|").append(RMID);
					crashedRM = 'Y';
				}
			}
			
			if (crashedRM == 'Y') return sb.toString();
			else return null;
		}
		catch (SocketException e){System.out.println("Socket error in FrontEndManagement UDPUniCastReceive: " + e.getMessage());}
		catch (IOException e) {System.out.println("IO error in FrontEndManagement UDPUniCastReceive: " + e.getMessage());}
		catch (ClassNotFoundException e) {System.out.println("ClassNotFoundException exceception in UDPUniCastReceive.." +e.getMessage());}
		finally {
			if (aSocket !=null) aSocket.close();
		}
		return null;
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
			return "No Software Failure";
		}
		else if(replyList.get(0).getResponse().equalsIgnoreCase(replyList.get(1).getResponse())) return replyList.get(2).getRMID();
		else if(replyList.get(0).getResponse().equalsIgnoreCase(replyList.get(2).getResponse())) return replyList.get(1).getRMID();
		else if(replyList.get(1).getResponse().equalsIgnoreCase(replyList.get(2).getResponse())) return replyList.get(0).getRMID();
		else return "No two RMs sent the same response..";
	}
}