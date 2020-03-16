package Implementation;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;




public class UDPIntReqServer extends Thread {

	DatagramSocket serverSocket;
	ServerCenterLocation location;
	private DatagramPacket receivePacket;
	private ServerImplementation server;
	
	public UDPIntReqServer(DatagramPacket dgPacket, ServerImplementation serverImpl) {
		// TODO Auto-generated constructor stub
		receivePacket = dgPacket;
		server = serverImpl;
		try {
			serverSocket = new DatagramSocket();
		} catch (SocketException se) {
			// TODO: handle exception
			se.printStackTrace();
		}
	}

	@Override
	public void run() {
		byte[] responseData;
		try {
			String inputPacket = new String(receivePacket.getData()).trim();
			String eventArr[] = inputPacket.split(",");
			if(eventArr[eventArr.length-1].equalsIgnoreCase("listing")) {
				responseData = getListEventAvailability(eventArr[0],eventArr[1]).getBytes();				
				
			}			
			else if(eventArr[eventArr.length-1].equalsIgnoreCase("book")) {					
				String result =  bookEventForRemote(eventArr[0],eventArr[1],eventArr[2],eventArr[3]);
				responseData = result.getBytes();
			}
			else if(eventArr[eventArr.length-1].equalsIgnoreCase("cancel")) {
				String result = cancelEventForRemote(eventArr[0],eventArr[1],eventArr[2],eventArr[3]);
				responseData = result.getBytes();
			}
//			}else if(eventArr[eventArr.length-1].equalsIgnoreCase("customerBookedEvents")) {
//				String result = server.checkCustomerEvents(eventArr[0],eventArr[1]);
//				responseData = result.getBytes();
//			}
			else {
				responseData = "No event available with the given type".getBytes();
				
			}
			serverSocket.send(new DatagramPacket(responseData, responseData.length, receivePacket.getAddress(),
					receivePacket.getPort()));
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private String getListEventAvailability(String eventType, String loc) {
		// get events not empty from all servers of same eventType
		String value= "No Event Available";
		//String loc = server.location;	
		if(loc.equalsIgnoreCase("mtl")) {			
			//loop through to get ids
			value = ServerImplementation.getEventIds(server.eventRecordMTL, eventType);
		}else if(loc.equalsIgnoreCase("que")) {
			value = ServerImplementation.getEventIds(server.eventRecordQUE, eventType);			
		}else {
			value = ServerImplementation.getEventIds(server.eventRecordSHE, eventType);
		}
		return value;
	}
	private String bookEventForRemote(String eventType, String customerId, String eventId, String loc) throws ParseException {
		// get events not empty from all servers of same eventType		
		String value= null;
		
		if(loc.equalsIgnoreCase("mtl")) {
			value = server.bookCurrEvent(server.eventRecordMTL, customerId, eventId,eventType,loc);
		}else if(loc.equalsIgnoreCase("que")) {
			value = server.bookCurrEvent(server.eventRecordQUE, customerId, eventId,eventType,loc);			
		}else {
			value = server.bookCurrEvent(server.eventRecordSHE, customerId, eventId,eventType,loc);
		}
		/*
		 * if(server.checkThreeEventsOrMore(customerId, eventId,loc)==true) { value =
		 * "You can't book more than 3 events from other servers";
		 * System.out.println("You can't book more than 3 events from other servers");
		 * }else {
		 * 
		 * }
		 */
		
		return value;
	}
	
	
	private String cancelEventForRemote(String eventType, String customerId, String eventId, String loc) {
		// get events not empty from all servers of same eventType		
		String value= null;
		if(loc.equalsIgnoreCase("mtl")) {			
			//loop through to get ids
			value = server.cancelCurrEvent(server.eventRecordMTL, customerId, eventId,eventType,loc);
		}else if(loc.equalsIgnoreCase("que")) {
			value = server.cancelCurrEvent(server.eventRecordQUE, customerId, eventId,eventType,loc);			
		}else {
			value = server.cancelCurrEvent(server.eventRecordSHE, customerId, eventId,eventType,loc);
		}
		return value;
	}
}