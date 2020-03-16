package FrontEnd;

import java.awt.List;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import org.omg.CORBA.ORB;

import DEMS_FrontEnd.DEMSInterfacePOA;

public class FrontEndImpl extends DEMSInterfacePOA {
	/*
	 * 1. Set default wait time for the first request.
	 * 2. Take all the times.
	 * 3. Calculate Dynamic wait time = 2* slowest RM time
	 * 4. For the 2nd request, check if all the RM responses received within Dynamic wait time.
	 * 5. If a response is not received, we say the process crashed , re-start RMs...
	 */
	
	// TODO Auto-generated method stub
	DatagramSocket aSocket = null;
	InetAddress ahost;
	int serverPort = 8500;
	//Time calculation
	long startTime;
	long endTime;
	long dynamicTime=0;
	
	FrontEndImpl() {
		try {	
			aSocket = new DatagramSocket(8888);
			ahost = InetAddress.getByName("192.168.43.71");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private ORB orb;

	public void setORB(ORB orb_val) {
		orb = orb_val;
	}

	// Hashmap for storing message :
	HashMap<String, String> storeMessage = new HashMap<>();
	// request id: 0001
	// request type : book/cancel
	// user id : MTLM1234
	// Method Name :
	// Event type:
	// EventId :
	// Old Event Type:
	// Old Event Id :
	// Booking Capacity:
	// Customer ID

	@Override
	public String addEvent(String Id,String eventID, String eventType, int bookingCapacity) {
		String request = null;
		// TODO Auto-generated method stub
		//ID,methodName,eventType,eventID,oldEventType,oldEventID,bookingCapacity,CID
		 request = Id+ "," +"addEvent" +"," +eventType +"," +eventID +",,,"+bookingCapacity;
		System.out.println("Inside Add event");			
		System.out.println(request);
		
		sendValuePrimaryServer(request);
		receiveUnicastUDPMessages();

		
		return request;
	}

	@Override
	public String removeEvent(String Id,String eventID, String eventType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String listEventAvailability(String Id,String eventType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String bookEvent(String customerID, String eventID, String eventType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getBookingSchedule(String customerID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String cancelEvent(String customerID, String eventID, String eventType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String swapEvent(String CustomerID, String newEventID, String newEventType, String oldEventID,
			String oldEventType) {
		// TODO Auto-generated method stub
		return null;
	}

	public void sendValuePrimaryServer(String message) {
		try {
			byte[] m = message.getBytes();

			DatagramPacket request = new DatagramPacket(m, message.length(), ahost, serverPort);
			
			startTime = System.currentTimeMillis();
			aSocket.send(request);

		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		} /*
			 * finally { if (aSocket != null) aSocket.close(); }
			 */
	}

	/*
	 * public void receiveMulticastRMMesaages(String ip, int port) throws
	 * IOException { byte[] buffer = new byte[1024]; MulticastSocket socket = new
	 * MulticastSocket(8000); InetAddress group =
	 * InetAddress.getByName("230.1.0.0"); socket.joinGroup(group);
	 * 
	 * while (true) { System.out.println("Waiting for multicast message...");
	 * DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
	 * socket.receive(packet); String msg = new String(packet.getData(),
	 * packet.getOffset(), packet.getLength());
	 * System.out.println("[Multicast UDP message received]>> " + msg);
	 * 
	 * if ("OK".equals(msg)) { System.out.println("No more message. Exiting : " +
	 * msg); break; } } socket.leaveGroup(group); socket.close(); }
	 */
	public String receiveUnicastUDPMessages() {
		String finalizedResult =null;
		LinkedList<String> replyList = new LinkedList<String>();
		LinkedList<Long> timeList = new LinkedList<Long>();
		
		DatagramSocket aSocket = null;
		try{
			aSocket = new DatagramSocket(7421);
			byte[] buffer = new byte[1000];
			while(true){
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				
				aSocket.setSoTimeout((int) dynamicTime);
				aSocket.receive(request);							
				
				System.out.println(new String(buffer) + " " + request.getPort());	
				
				timeList.add(System.currentTimeMillis()- startTime);
				
				String tempString = new String(buffer).trim();
				replyList.add(tempString);
				
				if (replyList.size() == 3) {
					dynamicTime =  2* timeList.get(timeList.size()-1);
					System.out.println(dynamicTime);
					
					if(replyList.get(0).equalsIgnoreCase(replyList.get(1))) {
						//1st nd 2nd same
						return replyList.get(0);
					}
					else if(replyList.get(0).equalsIgnoreCase(replyList.get(2))) 
					{
						replyList.get(0);
					}else if(replyList.get(1).equalsIgnoreCase(replyList.get(2))) {
						return replyList.get(1);
					}else {
						return "Process didn't send the correct reply";
					}
				}else {
					//re-send the request and also re-start the RMS
					
					return "Process is crashed";
				}
							 
			  }
		  }
		catch (SocketTimeoutException e){
			System.out.println("Socket: " + e.getMessage());
			System.out.println("Time wait exceeded and process crashed");
		}
		catch (SocketException e){
			System.out.println("Socket: " + e.getMessage());
		}
		catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		}
		finally {
		if(aSocket != null) aSocket.close();
		}
		return finalizedResult;
	}

}
