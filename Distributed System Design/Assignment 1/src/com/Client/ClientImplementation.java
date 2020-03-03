package com.Client;

import java.lang.invoke.MethodHandles.Lookup;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.ParseException;

import com.Beans.Event;
import com.Config.ServerCenterLocation;
import com.Server.ICentralizedServer;

// Implementation of Client Class
public class ClientImplementation {
	
	static Registry registryMTL;
	static Registry registryQUE;
	static Registry registrySHE;
	
	ICentralizedServer iCenterServer = null;
	//Get the port registered from the registry
		static {
			try {
				registryMTL = LocateRegistry.getRegistry("localhost", com.Config.Constants.RMI_PORT_NO_MTL);
				registryQUE = LocateRegistry.getRegistry("localhost", com.Config.Constants.RMI_PORT_NO_QUE);
				registrySHE = LocateRegistry.getRegistry("localhost", com.Config.Constants.RMI_PORT_NO_SHE);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	
	//Parameterized constructor
	//Mapping the client instance to the associated server based on the location
	ClientImplementation(ServerCenterLocation loc, String clientId) throws AccessException, RemoteException, NotBoundException{
		if(loc.equals(ServerCenterLocation.MTL)) {
			 iCenterServer = (ICentralizedServer)registryMTL.lookup(loc.toString());
			
		}else if(loc.equals(ServerCenterLocation.QUE)) {
			 iCenterServer = (ICentralizedServer)registryQUE.lookup(loc.toString());
		}
		else if(loc.equals(ServerCenterLocation.SHE)) {
			 iCenterServer = (ICentralizedServer)registrySHE.lookup(loc.toString());
		}
	}
	
	//Sending the request to the server after converting to event POJO Instance
	public String addEvent(String eventId, String eventType, int bookingCapacity) {
		String msgResult = null;
		Event eventDetails = new  Event();
		eventDetails.setId(eventId);
		eventDetails.setEventType(eventType);
		eventDetails.setBookingCapacity(bookingCapacity);		
		try {		
			if(iCenterServer!=null) {				
				msgResult = iCenterServer.addEvent(eventId, eventType, bookingCapacity);
			}			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(eventId==null)
			msgResult = "There is an error in event creation details";
		return msgResult;
		
	}
	
	public String removeEvent(String eventId, String eventType) throws ParseException {
		String msgResult = "Error";
		try {
			if(iCenterServer!=null) {	
				msgResult = iCenterServer.removeEvent(eventId, eventType);
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return msgResult;
	}
	public String listEventAvailability(String eventType) {
		String msgResult = "Not Available";
		try {
			msgResult = iCenterServer.listEventAvailability(eventType);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return msgResult;
	}
	
	public String bookEvent(String customerId,String eventId, String eventType) throws ParseException {
		String msgResult = null;
		try {
			msgResult = iCenterServer.bookEvent(customerId,eventId,eventType);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return msgResult;
	}

	public String getBookingSchedule(String customerId) {
		String msgResult = null;
		try {
			msgResult = iCenterServer.getBookingSchedule(customerId);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return msgResult;
	}
	public String cancelEvent(String customerId,String eventId, String eventType) {
		String msgResult = null;
		try {
			msgResult = iCenterServer.cancelEvent(customerId,eventId,eventType);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return msgResult;
	}
	
	


}
