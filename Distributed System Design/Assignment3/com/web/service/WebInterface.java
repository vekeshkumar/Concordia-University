package com.web.service;

import java.text.ParseException;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)

public interface WebInterface {
	@WebMethod
	public String addEvent(String eventID, String eventType, int bookingCapactiy);	
	@WebMethod
	public String removeEvent(String eventID, String eventType) throws  ParseException;
	@WebMethod
	public String listEventAvailability(String eventType) ;
	
	//Operations performed by Customers
	@WebMethod
	public String bookEvent(String customerID,String eventID, String eventType) throws ParseException;
	@WebMethod
	public String getBookingSchedule(String customerID) ;
	@WebMethod
	public String cancelEvent(String customerID, String eventID,String eventType)  ;
	@WebMethod
	public String swapEvent(String customerID,String newEventID, String newEventType,String oldEventID,String oldEventType);
}
