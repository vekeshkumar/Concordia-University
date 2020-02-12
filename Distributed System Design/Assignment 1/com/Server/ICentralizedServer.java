package com.Server;

import java.rmi.Remote;
import java.rmi.RemoteException;

import com.Beans.Event;

public interface ICentralizedServer extends Remote {
	//Operations performed by Event Managers
	public String addEvent(String eventID, String eventType, int bookingCapactiy) throws RemoteException;	
	public String removeEvent(String eventID, String eventType) throws RemoteException;
	public String listEventAvailability(String eventType) throws RemoteException;
	
	//Operations performed by Customers
	public String bookEvent(String customerID,String eventID, String eventType) throws RemoteException;
	public String getBookingSchedule(String customerID) throws RemoteException;
	public String cancelEvent(String customerID, String eventID,String eventType) throws RemoteException;
	
	public String checkEventExists(String eventID, String eventType) throws RemoteException;
	
	
}
