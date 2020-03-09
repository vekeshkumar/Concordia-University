package com.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandles.Lookup;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.Config.LogManager;
import com.Config.ServerCenterLocation;
import com.Server.ICentralizedServer;
import com.Server.ServerImplementation;

public class EventMainClient {
	static com.Config.LogManager logManager;
	public static void main(String[] args) throws IOException, NotBoundException, ParseException {
		while(true) {
			System.out.println("Distributed Event Management System");
			//Initializing the client implementation object for method invoke
			ClientImplementation client = null;
			
			Pattern validateNumber = Pattern.compile("([0-9]*)");
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Please enter your ID");
			String clientId= br.readLine();
			//Check for last 4 digits as number
			String customerNum =clientId.substring(4, 6);			
			Matcher matchID = validateNumber.matcher(customerNum);
			
			//Check for eight digits		
			if(clientId.length()!=8) {
				logManager.logger.log(Level.INFO,"Not a valid Customer Id (CustomerId:" + clientId + ")");
				System.out.println("Please enter a valid customerID");
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
							logManager = new LogManager("MTL");
							break;
						case "QUE":
							System.out.println("Accessing the QUE server location");
							client  = new ClientImplementation(ServerCenterLocation.QUE,clientId);
							logManager = new LogManager("QUE");
							break;
						case "SHE":
							System.out.println("Accessing the SHE server location");
							client  = new ClientImplementation(ServerCenterLocation.SHE,clientId);
							logManager = new LogManager("SHE");
							break;
						default:
							System.out.println("Please enter the correct ClientId!");
							break;
					}

					//Two switch case for Manager and Customer
					if(clientType.equalsIgnoreCase("c") || clientType.equalsIgnoreCase("m")) {
						if(clientType.equalsIgnoreCase("c")) {
							int t=1;
							String eventId = null;
							String eventType = null;
							String msg = null;
							while(t!=0) {
								System.out.println("1.Book Event");
								System.out.println("2.Get Event Schedule");
								System.out.println("3.Cancel Event");
								System.out.println("4.Logout");
								System.out.println("--------------------");
								Integer option = Integer.parseInt(br.readLine());								
								switch (option) {
									case 1:
										//Get all the input Required.
										System.out.println("Enter event Id and event type");
										 eventId = br.readLine();
										 eventType= br.readLine();
										msg = client.bookEvent(clientId, eventId, eventType);
										System.out.println(msg);
										break;
									case 2:
										//ClientId is enough Display the List
										msg = client.getBookingSchedule(clientId);
										System.out.println(msg);
										break;
									case 3:
										System.out.println("Enter event Id and event type");
										 eventId = br.readLine();
										 eventType= br.readLine();
										msg = client.cancelEvent(clientId, eventId, eventType);
										System.out.println(msg);
										break;
									case 4:
										 t =0;
										 System.out.println("Logged out Successfully");
										 break;
		
									default:
										break;
								}
		
							}
					}
						else {
							int t=1;
							String eventId = null;
							String eventType = null;
							while(t!=0) {
								System.out.println("1.Add Event");
								System.out.println("2.Remove Event");
								System.out.println("3.List Event Availability");
								System.out.println("4.Cancel event for customer");
								System.out.println("5.Logout");
								Integer option = Integer.parseInt(br.readLine());
								switch (option) {
									case 1:
										System.out.println("Please enter the following:");										
										System.out.println("1.Event Id: MTL/SHE/QUE -Location,M/A/E-Timing, 102220 -Date");
										 eventId = br.readLine();
										System.out.println("2.Event Type: Conference/Seminar/Trade Show");
										 eventType = br.readLine();
										System.out.println("3.BookingCapacity :");
										int bookingCapacity = Integer.parseInt(br.readLine());	
										
										if(client!=null) {
											if(eventId.length()==10) 
												{
													if(checkEventIdValidation(eventId,clientId)) {
														if(locName.equalsIgnoreCase(eventId.substring(0, 3))) {
															String msg = client.addEvent(eventId, eventType, bookingCapacity);
															System.out.println("Message from the server:-"+msg);
														}else {
															System.out.println("Event can be added only to your server");
														}
													}
												
												}
											else
												System.out.println("Please check your Event id format and re-enter it again");
										}
										break;
									case 2:
										System.out.println("Enter the event id and type to remove");
										eventId = br.readLine();
										eventType= br.readLine();
										String msgResult = client.removeEvent(eventId,eventType);
										System.out.println("Message from the server:-"+msgResult);
										break;
									case 3:
										System.out.println("Enter the event type to know the availability");
										eventType = br.readLine();
										String eventListString = client.listEventAvailability(eventType);
										System.out.println(eventListString);
										break;
									case 4:
										System.out.println("Enter event Id and event type and customer ID");
										 eventId = br.readLine();
										 eventType= br.readLine();
										 String cusId = br.readLine();
										 System.out.println(eventId+eventType+cusId);
										 String msg =  client.cancelEvent(cusId, eventId, eventType);
										
										 System.out.println(msg);
										break;
									case 5:
										 t=0;
										 System.out.println("Logged out Successfully");
									 break;
			
									default:
										break;
								}
							} 
						
						}
					}
											
				}else  
					System.out.println("Please enter a valid clientId");
				}
		}
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
