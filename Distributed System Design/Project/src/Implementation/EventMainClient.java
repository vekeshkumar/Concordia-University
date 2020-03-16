package Implementation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandles.Lookup;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class EventMainClient {
	public static String  mainClient(String request) throws IOException, NotBoundException, ParseException {
		ClientImplementation client = null;


			//ID,methodName,eventType,eventID,oldEventType,oldEventID,bookingCapacity,CID
			String[] requestData = request.split("\\,");
			System.out.println(requestData);
			System.out.println("Distributed Event Management System");			
			Pattern validateNumber = Pattern.compile("([0-9]*)");
			String clientId= requestData[0];
			String result = null;
			//Check for last 4 digits as number
			String customerNum =clientId.substring(4,6);			
			Matcher matchID = validateNumber.matcher(customerNum);
			
			//Check for eight digits		
			if(clientId.length()!=8) {
				System.out.println("Not an enter a valid customerID");
			}
			else { 
				//Check for the role of the Location and role of the customer(User or Manager)				
				String locName = clientId.substring(0, 3);
				String  clientType =clientId.substring(3, 4);
				if(!locName.isEmpty()) {
					switch(locName) {
						case "MTL":
							//Access the MTL server
							System.out.println("Accessing the MTL server location");							
							client  = new ClientImplementation(ServerCenterLocation.MTL,clientId); 
							//logManager = new LogManager("MTL");
							break;
						case "QUE":
							System.out.println("Accessing the QUE server location");
							client  = new ClientImplementation(ServerCenterLocation.QUE,clientId);
							//logManager = new LogManager("QUE");
							break;
						case "SHE":
							System.out.println("Accessing the SHE server location");
							client  = new ClientImplementation(ServerCenterLocation.SHE,clientId);
							//logManager = new LogManager("SHE");
							break;
						default:
							System.out.println("Please enter the correct ClientId!");
							break;
					}
					//Two switch case for Manager and Customer
					if(clientType.equalsIgnoreCase("c") || clientType.equalsIgnoreCase("m")) {
						if(clientType.equalsIgnoreCase("c")) {
							String eventId = null;
							String eventType = null;
							String msg = null;						
							/*
							 * System.out.println("1.Book Event");
							 * System.out.println("2.Get Event Schedule");
							 * System.out.println("3.Cancel Event"); System.out.println("4.Swap Event");
							 * System.out.println("5.Logout"); System.out.println("--------------------");
							 */
								String option =requestData[1];								
								switch (option) {
									case "bookEvent":
										//Get all the input Required.
										System.out.println("Enter event Id and event type");
										 eventId =  requestData[3];
										 eventType= requestData[2];
										msg = client.bookEvent(clientId, eventId, eventType);
										System.out.println(msg);
										result = msg;
										break;
									case "getBookingSchedule":
										//ClientId is enough Display the List
										msg = client.getBookingSchedule(clientId);
										System.out.println(msg);
										result = msg;
										break;
									case "cancelEvent":
										System.out.println("Enter event Type, oldEventID, oldEventType");
										eventId = requestData[3];
										eventType= requestData[2];
										msg = client.cancelEvent(clientId, eventId, eventType);
										result = msg;
										System.out.println(msg);
										break;
									case "swapEvent":
										System.out.println("Enter newEventID, newEventType, oldEventID, oldEventType");
										 eventId =requestData[3];
										 eventType=requestData[2];
										 String oldEventId = requestData[5];
										 String  oldEventType = requestData[4];
										msg = client.swapEvent(clientId, eventId, eventType, oldEventId, oldEventType);
										result = msg;
										System.out.println(msg);
										break;									
									default:
										break;
								}
							
					}
						else {
							String eventId = null;
							String eventType = null;
							/*while(t!=0) {
								System.out.println("1.Add Event");
								System.out.println("2.Remove Event");
								System.out.println("3.List Event Availability");
								System.out.println("4.Cancel event for customer");
								System.out.println("5.Logout");*/
								String option = requestData[1];
								switch (option) {
									case "addEvent":
										//System.out.println("Please enter the following:");										
										//System.out.println("1.Event Id: MTL/SHE/QUE -Location,M/A/E-Timing, 102220 -Date");
										 eventId = requestData[3];
										System.out.println("2.Event Type: Conference/Seminar/Trade Show");
										 eventType =  requestData[2];
										System.out.println("3.BookingCapacity :");
										int bookingCapacity = Integer.parseInt(requestData[6]);	
										
										if(client!=null) {
											if(eventId.length()==10) 
												{
													if(checkEventIdValidation(eventId,clientId)) {
														if(locName.equalsIgnoreCase(eventId.substring(0, 3))) {
															String msg = client.addEvent(eventId, eventType, bookingCapacity);															
															System.out.println("Message from the server:-"+msg);
															result = msg;
														}else {
															System.out.println("Event can be added only to your server");
														}
													}
												
												}
											else
												System.out.println("Please check your Event id format and re-enter it again");
										}
										
										break;
									case "removeEvent":
										System.out.println("Enter the event id and type to remove");
										eventId = requestData[3];
										eventType= requestData[2];
										String msgResult = client.removeEvent(eventId,eventType);
										System.out.println("Message from the server:-"+msgResult);
										result = msgResult;
										break;
									case "listEventAvailability":
										System.out.println("Enter the event type to know the availability");
										eventType =requestData[2];
										String eventListString = client.listEventAvailability(eventType);
										result = eventListString;
										System.out.println(eventListString);
										break;
									/*case 4:
										System.out.println("Enter event Id and event type and customer ID");
										 eventId = br.readLine();
										 eventType= br.readLine();
										 String cusId = br.readLine();
										 System.out.println(eventId+eventType+cusId);
										 String msg =  client.cancelEvent(cusId, eventId, eventType);
										
										 System.out.println(msg);
										break;*/
	
									default:
										break;
								}
							} 
					}
											
				}else  
					System.out.println("Please enter a valid clientId");
				}
			return result;
		
	}	
	public static boolean checkEventIdValidation(String eventId, String clientId) {
		boolean isValidEveId =false;
		Pattern eventPattern = Pattern.compile("([A-Z]{1,3})([M,A,E]{1})([0-9]{5,6})");
		if(eventId.substring(0, 2).equalsIgnoreCase(clientId.substring(0,2))) {
			Matcher eventIdPattern = eventPattern.matcher(eventId);
			if(eventIdPattern.matches()) {
				isValidEveId = true;
			}else {
				System.out.println("Please check your Event id format and re-enter it again");
			}
		}
		return isValidEveId;
	}
	
	
	
}
