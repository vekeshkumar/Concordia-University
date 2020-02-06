package com.Server;

import java.beans.EventSetDescriptor;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.Beans.Event;
import com.Beans.EventClient;
import com.Config.Constants;
import com.Config.ServerCenterLocation;

@SuppressWarnings("serial")
public class ServerImplementation implements ICentralizedServer {
	UDPServer udpServer;
	String IPAddress;
	int bookingCount;
	//Stores event details
	HashMap<String,HashMap<String, List<Event>>> eventRecordMTL;
	HashMap<String,HashMap<String, List<Event>>> eventRecordQUE;
	HashMap<String,HashMap<String, List<Event>>> eventRecordSHE;
	//Store client details
	HashMap<String, List<EventClient>> clientRecordMTL;
	HashMap<String, List<EventClient>> clientRecordQUE;
	HashMap<String, List<EventClient>> clientRecordSHE;
	
	String location;

	protected ServerImplementation(ServerCenterLocation scloc)
			throws RemoteException {
		eventRecordMTL = new HashMap<>();
		eventRecordQUE = new HashMap<>();
		eventRecordSHE = new HashMap<>();
		//Start udp server with location.	
		udpServer = new UDPServer(scloc, this);
		udpServer.start();
		location = scloc.toString();
		setIPAddress(scloc);
	}

	@Override
	public String addEvent(Event event) throws RemoteException {
		event.setBookingCapacity(event.getBookingCapacity());			
		return event.getId();
	}
	@SuppressWarnings("null")
	public String addEventRecHashMap(String eventTypeKey, Event eventDetails) {
		String msg = "Error";
		//Storing the event details with the Key
		String eventServLocation = eventTypeKey.substring(0,2);
		String eventID = eventDetails.getId();
		System.out.println(eventServLocation);
		
		switch (eventServLocation) {
		case "MTL":			
			if(eventTypeKey!=null && eventDetails!=null) { 
				List<Event> eveRecList =  (List<Event>) eventRecordMTL.get(eventTypeKey);
				 HashMap<String, List<Event>> innerRec = null;
				 if(eveRecList!=null) {
					 eveRecList.add(eventDetails);
					 innerRec.put(eventID, eveRecList);
				 }else {
					 List<Event> records = new ArrayList<Event>();
					 records.add(eventDetails);
					 innerRec.put(eventID, records);
				 }
				 eventRecordMTL.put(eventTypeKey, innerRec);	
				 msg= "Record added!! Success";
			}
				
			break;
		case "QUE":
			if(eventTypeKey!=null && eventDetails!=null) { 
				List<Event> eveRecList =  (List<Event>) eventRecordQUE.get(eventTypeKey);
				 HashMap<String, List<Event>> innerRec = null;
				 if(eveRecList!=null) {
					 eveRecList.add(eventDetails);
					 innerRec.put(eventID, eveRecList);
				 }else {
					 List<Event> records = new ArrayList<Event>();
					 records.add(eventDetails);
					 innerRec.put(eventID, records);
				 }
				 eventRecordQUE.put(eventTypeKey, innerRec);	
				 msg= "Record added!! Success";
			}

			break;
		case "SHE":
			if(eventTypeKey!=null && eventDetails!=null) { 
				List<Event> eveRecList =  (List<Event>) eventRecordSHE.get(eventTypeKey);
				 HashMap<String, List<Event>> innerRec = null;
				 if(eveRecList!=null) {
					 eveRecList.add(eventDetails);
					 innerRec.put(eventID, eveRecList);
				 }else {
					 List<Event> records = new ArrayList<Event>();
					 records.add(eventDetails);
					 innerRec.put(eventID, records);
				 }
				 eventRecordSHE.put(eventTypeKey, innerRec);	
				 msg= "Record added!! Success";
			}
			
			break;
		default:
			break;
		}
			
		
		
		return msg;
		
	}

	@Override
	public String removeEvent(String eventID, String eventType) {
		return null;
		// TODO Auto-generated method stub
		
	}

	@Override
	public String listEventAvailability(String eventType) {
		return null;
		// TODO Auto-generated method stub
		
	}

	@Override
	public String bookEvent(String customerID, String eventID, String eventType) {
		return null;
		// TODO Auto-generated method stub
		
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
	
	public void setIPAddress(ServerCenterLocation scloc) {
		switch (scloc) {
		case MTL:
			IPAddress = Constants.RMI_SERV_ADDR_MTL;
			break;
		case QUE:
			IPAddress = Constants.RMI_SERV_ADDR_QUE;
			break;
		case SHE:
			IPAddress = Constants.RMI_SERV_ADDR_SHE;
			break;
		default:
			break;
		}
		
	}

	@Override
	public String addEvent(String eventID, String eventType, int bookingCapactiy) throws RemoteException {
		// TODO Auto-generated method stub
		Event eventDetails = new Event();
		eventDetails.setEventType(eventType);
		eventDetails.setId(eventID);
		eventDetails.setBookingCapacity(bookingCapactiy);
		addEventRecHashMap(eventType, eventDetails);
		
		return null;
	}




}
