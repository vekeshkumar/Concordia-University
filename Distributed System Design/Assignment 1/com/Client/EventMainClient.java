package com.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandles.Lookup;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.Config.ServerCenterLocation;
import com.Server.ICentralizedServer;
import com.Server.ServerImplementation;

public class EventMainClient {
	
	public static void main(String[] args) throws IOException, NotBoundException {
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
							System.out.println(client.toString());
							break;
						case "QUE":
							System.out.println("Accessing the QUE server location");
							client  = new ClientImplementation(ServerCenterLocation.QUE,clientId);
							break;
						case "SHE":
							System.out.println("Accessing the SHE server location");
							client  = new ClientImplementation(ServerCenterLocation.SHE,clientId);
							break;
						default:
							System.out.println("Please enter the correct ClientId!");
							break;
						
					}

					//Two switch case for Manager and Customer
					if(clientType.equalsIgnoreCase("c") || clientType.equalsIgnoreCase("m")) {
						if(clientType.equalsIgnoreCase("c")) {
							int t=1;
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
										break;
									case 2:
										//ClientId is enough Display the List
										break;
									case 3:
										System.out.println("Enter event Id and event type");
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
							while(t!=0) {
								System.out.println("1.Add Event");
								System.out.println("2.Remove Event");
								System.out.println("3.List Event Availability");
								System.out.println("4.Logout");
								Integer option = Integer.parseInt(br.readLine());
								switch (option) {
									case 1:
										System.out.println("Please enter EventID- MTLE1002220, Booking Capacity eg. 10");										
										String eventId = br.readLine();
										String eventType = br.readLine();
										int bookingCapacity = Integer.parseInt(br.readLine());
										System.out.println(eventId+" "+eventType+" "+bookingCapacity);
										client.addEvent(eventId, eventType, bookingCapacity);
										
										break;
									case 2:
										break;
									case 3:
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
					}
											
				}else  
					System.out.println("Please enter a valid clientId");
					
				}
			
		}

	}	
	
	
	
}
