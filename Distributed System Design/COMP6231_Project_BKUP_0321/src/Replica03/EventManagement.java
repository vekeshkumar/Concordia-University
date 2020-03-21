package Replica03;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.SimpleLayout;
import org.omg.CORBA.ORB;

import EventApp03.EventServer03POA;

public class EventManagement extends EventServer03POA{
	private ORB orb;
	public String serverName;
	Logger logger;
	SimpleLayout layout = new SimpleLayout();
	RollingFileAppender appender =null;
	String filename;
	
	//Create Main database and Customer database -- Key is CUST ID
	public volatile ConcurrentHashMap<String, ConcurrentHashMap<String, EventDetails>> eventDB = new ConcurrentHashMap<String, ConcurrentHashMap<String, EventDetails>>();
	public volatile ConcurrentHashMap<String, ConcurrentHashMap<String, ConcurrentHashMap<String, CustDetails>>> custDB =  new ConcurrentHashMap<String, ConcurrentHashMap<String, ConcurrentHashMap<String, CustDetails>>>();
		
	public void setORB(ORB orb_val) {
		// TODO Auto-generated method stub
		orb = orb_val;
	}
	
	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		orb.shutdown(false);
	}
	
	public EventManagement(String serverName){
		this.serverName = serverName;
		//System.out.println("Inside constructor of " +this.serverName +" Server");
		
		//For logging
		filename  = "C:/Java/EclipseWorkspace/COMP6231_Project/src/Replica03/" +this.serverName +"_Server.txt";
		logger = Logger.getLogger(Logger.class.getName());
		try {appender = new RollingFileAppender(layout,filename,true);
		} catch (IOException e) { System.out.println("Exception in Logging in Server");} 
	}
	
	//***********************************************************************************************************//
	//**********************************************Manager methods**********************************************//
	//***********************************************************************************************************//
	//Add Event. Invoked by Manager.
	public String addEvent(String eventID, String eventType, int bookingCapacity) {
		System.out.println("\nInside Add Event of " +this.serverName + " Server - " +eventType +" - " +eventID + " - "+bookingCapacity + " - Event DB before add:" +eventDB);
		String addResult;
		
		//Populate event DB
		if (!(eventID.substring(0,3).equals(this.serverName))) {
			addResult = "Cannot add other citi's event";
			logging("Add event - " + eventType +" - "+ eventID +" - " +bookingCapacity +" - " +addResult);
			return addResult;
		}
		synchronized(this) {
			if (eventDB.get(eventType) == null) { //eventType doesn't exist.
				eventDB.put(eventType, new ConcurrentHashMap<String, EventDetails>());
				eventDB.get(eventType).put(eventID, new EventDetails(bookingCapacity,0));
			}
			else {  
				if (eventDB.get(eventType).get(eventID) == null) {  //eventType exists but not eventID
					eventDB.get(eventType).put(eventID, new EventDetails(bookingCapacity,0));
				}
				else {  //Both eventType and eventID exist. Just update booking Capacity
					if (bookingCapacity >= eventDB.get(eventType).get(eventID).currentlyBooked) {
						eventDB.get(eventType).get(eventID).bookingCapacity = bookingCapacity;
					}
				}
			}
		}
		System.out.println("Event DB after add: " +eventDB);
		logging("Add event - " + eventType +" - "+ eventID +" - " +bookingCapacity +" - " +"Success");
		return "Success";
	}

	//Remove Event. Invoked by Manager.
	public String removeEvent(String eventID, String eventType) {
		System.out.println("\nInside removeEvent of " +this.serverName + " Server -" +eventType +" - " +eventID + " - Event DB before remove:" +eventDB + " - Cust DB before remove:" +custDB);
		String bookedeventID, bookResult, removeResult;
		
		//Do validations
		if (eventDB.get(eventType) == null) {
			removeResult = "Event Type " +eventType +" doesn't exist to remove the event";
			logging("Remove event - " + eventType +" - "+ eventID +" - " +removeResult);
			return removeResult;
		}
		if (eventDB.get(eventType).get(eventID) == null) {
			removeResult = "Event ID " +eventID +" doesn't exist to remove the event of Type "+eventType;
			logging("Remove event - " + eventType +" - "+ eventID +" - " +removeResult);
			return removeResult;
		}
		if (!(eventID.substring(0,3).equals(this.serverName))) {
			removeResult = "Cannot remove other citi's event";
			logging("Remove event - " + eventType +" - "+ eventID +" - " +removeResult);
			return removeResult;
		}
		
		//Passed- remove it- Get all the customers who booked this event and book the next available event.
		List<String> customerIDList = new LinkedList<String>();
		synchronized(this) {
			customerIDList = eventDB.get(eventType).get(eventID).customersBooked;
		}
		System.out.println("Customers booked this event: " +customerIDList);
		
		//Sort the eventIDs
		Map<String, EventDetails> NestedMap = new TreeMap<String, EventDetails>(new MyComparator()); 
		synchronized(this) {
			for(String keyN: eventDB.get(eventType).keySet()) { //Gives all event IDs as keys
				NestedMap.put(keyN, eventDB.get(eventType).get(keyN));
			}
		}
		System.out.println("Nested Sorted DB: " +NestedMap);
		
		//For every customer- try booking the next available event ID.
		for (String customerID: customerIDList) {
			bookResult = null;
			bookedeventID = null;
			
			//Book event
			char foundcurrent ='N';
			for(String keyN: NestedMap.keySet()) {      //Check the next available events.
				if (keyN.equals(eventID)) {
					foundcurrent = 'Y';
					continue;             //Skip till the exact matched one.
				}
				if (foundcurrent == 'Y') {
					if (NestedMap.get(keyN).currentlyBooked == NestedMap.get(keyN).bookingCapacity) continue; //No seats
					if (eventDB.get(eventType).get(keyN).customersBooked.contains(customerID)) continue; //already booked it. Try next one.
					
					System.out.println("Booking " +keyN +" for Customer: " +customerID);
					bookResult= bookEvent(customerID, keyN, eventType);
					if (bookResult.trim().equals("Success")) {
						bookedeventID = keyN;
						break; 
					}
				}
			}
			//updateRemote for other city events.
			if (!(customerID.substring(0,3).equals(this.serverName)) && bookResult != null && bookResult.trim().equals("Success")) {
				String[] sendargs = new String[7];
				String updateRemote = null;
				
				sendargs[0] = customerID.substring(0,3);
				sendargs[2] = "updateCustDB";
				sendargs[3] = customerID;
				sendargs[4] = eventType;
				sendargs[5] = bookedeventID;
				sendargs[6] = "B";
				updateRemote = UDPClient(sendargs); 
				//Remove the old eventID.
				if (updateRemote.trim().equals("Success")) {
					sendargs[5] = eventID;
					sendargs[6] = "R";
					updateRemote = UDPClient(sendargs);
				}
			}
		}
		
		//Done with all the customers- Remove event from eventDB and local custDB.
		synchronized(this) {
			eventDB.get(eventType).remove(eventID);
			if (eventDB.get(eventType).isEmpty()) eventDB.remove(eventType);
			
			for(String keyM: custDB.keySet()) { //keyM stores customers.
				updateCustDB(keyM, eventID, eventType, 'R');
			}
		}
		
		//Remove for other city customers as well - irrespective of their booking status.
		for (String customerID: customerIDList) {
			if (!(customerID.substring(0,3).equals(this.serverName))) {
				String[] sendargs = new String[7];
				sendargs[0] = customerID.substring(0,3);
				sendargs[2] = "updateCustDB";
				sendargs[3] = customerID;
				sendargs[4] = eventType;
				sendargs[5] = eventID;
				sendargs[6] = "R";
				UDPClient(sendargs); 
			}
		}
		System.out.println("Event DB after remove: " +eventDB + " - Cust DB after remove: " +custDB);
		
		//Log the result
		logging("Remove event - " + eventType +" - "+ eventID +" - " +"Success");
		return "Success";
	}
		
	//List Event Availability. Invoked by Manager.	
	public String listEventAvailability(String eventType){
		System.out.println("\nInside listEventAvailability of " +this.serverName + " Server- "+eventType+ " - Event DB:" +eventDB + " - Cust DB:" +custDB);
		StringBuilder eventList = new StringBuilder();
		int seatsAvailable; 
		
		//Get from current server first
		if (eventDB.get(eventType) == null || eventDB.get(eventType).isEmpty()) {
			eventList.append("No events from " +this.serverName + " for the eventType- "+eventType);
		}
		else {
			for (String keyN : eventDB.get(eventType).keySet()) {
				eventList.append("[").append(keyN).append(" - ");
				seatsAvailable = eventDB.get(eventType).get(keyN).bookingCapacity - eventDB.get(eventType).get(keyN).currentlyBooked;
				eventList.append(seatsAvailable).append("]");
			}
		}
		eventList.append("\n");
		
		//Get from Other two servers.
		String[] sendargs = new String[7];
		String eventListRemote = null;
		
		switch(this.serverName) {
		case "QUE":
			sendargs[0] = "MTL";
			sendargs[1] = "SHE";
			break;
		case "MTL":
			sendargs[0] = "QUE";
			sendargs[1] = "SHE";
			break;
		case "SHE":
			sendargs[0] = "QUE";
			sendargs[1] = "MTL";
			break;
		}
		
		sendargs[2] = "listRemoteEventAvailability";
		sendargs[3] = null; //Customer ID
		sendargs[4] = eventType;
		sendargs[5] = null; //eventID
		sendargs[6] = null; //bookRemove Flag
		eventListRemote = UDPClient(sendargs); 
		
		//Append all servers data
		eventList.append(eventListRemote);
		logging("List event Availability- " + eventType +" - "+ "Success");
		
		return eventList.toString();
	}
	
	//***********************************************************************************************************//
	//**********************************************Customer methods**********************************************//
	//***********************************************************************************************************//
	//Book Event. Invoked by Customer.
	public synchronized String bookEvent(String customerID, String eventID, String eventType) {
		System.out.println("\nInside Book Event of " +this.serverName +" Server - " +customerID +" - " +eventType +" - " +eventID + " - Event DB before book:" +eventDB + " - Cust DB before book:" +custDB);
		String bookResult = null;
		
		//Do Validations
		if ((custDB.get(customerID) != null) && (custDB.get(customerID).get(eventType) != null) && (custDB.get(customerID).get(eventType).get(eventID) != null)) {
				bookResult = "Customer " +customerID +" has already booked the event "+eventType +" - "+eventID;
				logging("Book Event- " + customerID +" - " +eventType +" - " +eventID +" - " +bookResult);
				return bookResult;
		}
		
		//Passed - book it
		if (eventID.substring(0, 3).equals(this.serverName)) {
			
			//Do Validations
			if (eventDB.get(eventType) == null) {
				bookResult = "The eventType you are trying to book doesn't exist";
				logging("Book Event- " + customerID +" - " +eventType +" - " +eventID +" - " +bookResult);
				return bookResult;
			}
			if (eventDB.get(eventType).get(eventID) == null) {
				bookResult = "The eventID you are trying to book doesn't exist";
				logging("Book Event- " + customerID +" - " +eventType +" - " +eventID +" - " +bookResult);
				return bookResult;
			}
			if (eventDB.get(eventType).get(eventID).currentlyBooked == eventDB.get(eventType).get(eventID).bookingCapacity) {
				bookResult = "The event you are trying to book is Full";
				logging("Book Event- " + customerID +" - " +eventType +" - " +eventID +" - " +bookResult);
				return bookResult;
			}
			
			//Update eventDB
			eventDB.get(eventType).get(eventID).currentlyBooked += 1;
			eventDB.get(eventType).get(eventID).customersBooked.add(customerID);
			bookResult = "Success";
			
			//update custDB
			if (customerID.substring(0,3).equals(this.serverName)) {
				bookResult = updateCustDB(customerID, eventID, eventType, 'B');
			}
		}
		else {
			System.out.println("Customer "+ customerID +" is booking " +eventID.substring(0,3) +" city event.");
			
			//Do validations - A customer can book as many events in his/her own city, but only at most 3 events from other cities overall in a week.
			String thiseventdate = eventID.substring(4);
			String startendofWeek[] =  getStartEndofWeek(thiseventdate);
			System.out.println("Event Date- " +thiseventdate +" Week start Date- " +startendofWeek[0] +" Week end Date- " +startendofWeek[1]);
			
			//Check if the customer exists scan his DB.
			if (custDB.get(customerID) != null && !custDB.get(customerID).isEmpty()) {
				int noofremoteBookings =0;
				//Scan the custDB and see the no.of other citi's 
				for(String keyM: custDB.get(customerID).keySet()) { //Gives all eventTypes in key set
					for(String keyN: custDB.get(customerID).get(keyM).keySet()) { //Gives all eventIDs in key set
						if (!(keyN.substring(0,3).equals(this.serverName))) {
							if(isinbetween(keyN.substring(4), startendofWeek[0], startendofWeek[1])) noofremoteBookings += 1;
						}}}
				
				if (noofremoteBookings >= 3) {
					bookResult = "You cannot book more than 3 events from other cities overall in a week";
					logging("Book Event- " + customerID +" - " +eventType +" - " +eventID +" - " +bookResult);
					return bookResult;
				}
			}
			
			//Passed- book it
			String[] sendargs = new String[7];
			sendargs[0] = eventID.substring(0,3);
			sendargs[1] = null;
			sendargs[2] = "bookRemoteEvent";
			sendargs[3] = customerID;
			sendargs[4] = eventType;
			sendargs[5] = eventID;
			sendargs[6] = null;
			
			bookResult = UDPClient(sendargs); 
			
			//Update local custDB.
			if(bookResult.trim().equals("Success")) {
				bookResult = updateCustDB(customerID, eventID, eventType, 'B');
			}
		}
		System.out.println("Event DB after booking: " +eventDB + " - Cust DB after booking: " +custDB);
		
		logging("Book Event- " + customerID +" - " +eventType +" - " +eventID +" - " +bookResult);
		return bookResult;
	}

	//Get Booking Schedule. Invoked by Customer.
	public String getBookingSchedule(String customerID){
		System.out.println("\nInside getBookingSchedule of " +this.serverName +" Server- " +customerID + " - Event DB:" +eventDB + " - Cust DB:" +custDB);
		StringBuilder tempstr = new StringBuilder();
		StringBuilder bookingSchedule = new StringBuilder();
		List<String> bookingScheduleList = new ArrayList<String>();
		
		//Do Validations
		if ((custDB.get(customerID) == null) || custDB.get(customerID).isEmpty()) {
			logging("Get Booking Schedule- " + customerID +" - "+ "You haven't booked any events yet..");
			return "[EMPTY] You have NO bookings at this moment..";
		}
		
		//Passed- Get the schedule
		for(String keyM: custDB.get(customerID).keySet()) {
			
			if (!(custDB.get(customerID).get(keyM).isEmpty())) {
				tempstr.append(keyM).append(": ").append("\n");   //Appending eventType
				
				for(String keyN: custDB.get(customerID).get(keyM).keySet()) {
					tempstr.append(keyN).append(" - ")       			          		//Appending event ID
								   .append(custDB.get(customerID).get(keyM).get(keyN))  //Appending CustDetails  
								   .append("\n");
				}}}
				
		//Append the result
		bookingScheduleList.add(tempstr.toString());
		bookingSchedule.append(bookingScheduleList);

		logging("Get Booking Schedule- " + customerID +" - "+ "Success");				
		return bookingSchedule.toString();
	}
	
	//Cancel Event. Invoked by Customer.
	public String cancelEvent(String customerID, String eventID, String eventType) {
		System.out.println("\nInside Cancel Event of " +this.serverName +" Server- " +customerID +" - " +eventType + " - " +eventID + " - Event DB before cancel:" +eventDB + " - Cust DB before cancel:" +custDB);
		String cancelResult = null;
		
		//Do validations
		if ((custDB.get(customerID) == null) || (custDB.get(customerID).get(eventType) == null) || (custDB.get(customerID).get(eventType).get(eventID) == null)){
			cancelResult = "Hello " +customerID +" You haven't booked this event to cancel...";
			logging("Cancel Event- " + customerID +" - " +eventType +" - " +eventID +" - " +cancelResult);
			return cancelResult;
		}
		
		//Passed validations
		if (eventID.substring(0,3).equals(this.serverName)) {
			//update eventDB
			synchronized(this) {
				eventDB.get(eventType).get(eventID).customersBooked.remove(customerID);
				eventDB.get(eventType).get(eventID).currentlyBooked -= 1;
				
				//update custDB
				cancelResult = updateCustDB(customerID, eventID, eventType, 'R');
			}
		}
		else {
			System.out.println("Customer " +customerID + " is cancelling "+eventID.substring(0,3) +" city event.");
			String[] sendargs = new String[7];
			
			sendargs[0] = eventID.substring(0,3);
			sendargs[2] = "cancelRemoteEvent";
			sendargs[3] = customerID;
			sendargs[4] = eventType;
			sendargs[5] = eventID;
			sendargs[6] = null;
			
			cancelResult = UDPClient(sendargs); 
			
			//update custDB
			if ("Success".equals(cancelResult.trim())) cancelResult =  updateCustDB(customerID, eventID, eventType, 'R');
		}
		
		System.out.println("Event DB after cancel: " +eventDB + " - Cust DB after cancel: " +custDB);
		logging("Cancel Event- " + customerID +" - " +eventType +" - " +eventID +" - " +cancelResult);
		return cancelResult;
	}
	
	//Swap Event. Invoked by Customer.
	public synchronized String swapEvent(String customerID, String newEventID, String newEventType, String oldEventID, String oldEventType) {	
		System.out.println("\nInside swapEvent of " +this.serverName +" Server- " +customerID +" - " +newEventType + " - " +newEventID + " - " +oldEventType + " - " +oldEventID + " - Event DB before Swap:" +eventDB + " - Cust DB before Swap:" +custDB);
		String bookResult, swapResult;
		
		//Check whether customer has really booked the old event.
		if (custDB.get(customerID) == null || custDB.get(customerID).get(oldEventType) == null || custDB.get(customerID).get(oldEventType).get(oldEventID) == null) {
			swapResult = "Customer "+customerID + " has not booked the event - " +oldEventType +" - " +oldEventID;
			logging("Swap Event- " + customerID +" - " +oldEventType +" - " +oldEventID +" - " +newEventType +" - " +newEventID +" - " +swapResult);
			return swapResult;
		}
		
		//Customer has booked the event(check1). Check whether he can book the new event (check2), and if yes, book the new Event.
		
		if (custDB.get(customerID) != null && custDB.get(customerID).get(newEventType) != null && custDB.get(customerID).get(newEventType).get(newEventID) != null) {
			bookResult = "Already booked the new Event";
		}	
		else if (newEventID.substring(0,3).equals(this.serverName)) { //booking current city event
			bookResult = bookEvent(customerID, newEventID, newEventType);
		}
		else { //booking Remote event
			//Do validations - A customer can book as many events in his/her own city, but only at most 3 events from other cities overall in a week.
			List<String> remoteEventList = new ArrayList<String>();
			String thiseventdate = newEventID.substring(4);
			String startendofWeek[] =  getStartEndofWeek(thiseventdate);
			System.out.println("Event Date- " +thiseventdate +" Week start Date- " +startendofWeek[0] +" Week end Date- " +startendofWeek[1]);
			
			//Check if the customer exists scan his DB.
			int noofremoteBookings =0;
			
			//Scan the custDB and see the no.of other citi's
			for(String keyM: custDB.get(customerID).keySet()) { //Gives all eventTypes in key set
				for(String keyN: custDB.get(customerID).get(keyM).keySet()) { //Gives all eventIDs in key set
					if (!(keyN.substring(0,3).equals(this.serverName)) && isinbetween(keyN.substring(4), startendofWeek[0], startendofWeek[1])) {
						remoteEventList.add(keyN);
						noofremoteBookings += 1;
					}}}
			
			if (noofremoteBookings >= 3 && (!remoteEventList.contains(oldEventID))) bookResult = "You cannot book more than 3 events from other cities overall in a week";
			else {
				String[] sendargs = new String[7];
				sendargs[0] = newEventID.substring(0,3);
				sendargs[1] = null;
				sendargs[2] = "bookRemoteEvent";
				sendargs[3] = customerID;
				sendargs[4] = newEventType;
				sendargs[5] = newEventID;
				sendargs[6] = null;
				
				bookResult = UDPClient(sendargs); 
				
				//Update local custDB.
				if(bookResult.trim().equals("Success")) bookResult = updateCustDB(customerID, newEventID, newEventType, 'B');
			}	
		}
		
		//Check whether booking is successful. If yes, cancel the old event.
		if (bookResult.trim().equals("Success")) { //Cancel will be successful with the first check done above.
			swapResult = cancelEvent(customerID, oldEventID, oldEventType);
		}
		else { //Do not cancel it.
			swapResult = bookResult + ". Swap is unsuccessful.";
		}
		
		logging("Swap Event- " + customerID +" - " +oldEventType +" - " +oldEventID +" - " +newEventType +" - " +newEventID +" - " +swapResult);
		return swapResult;
	}
	
	//UDP Client of 'this' server
	public synchronized String UDPClient(String[] args) {
		/*
		 * [0] - Server to call
		 * [1] - Server to call
		 * [2] - Method name
		 * [3] - Customer ID
		 * [4] - eventType
		 * [5] - eventID
		 * [6] - bookRemove flag
		 */
		DatagramSocket aSocket = null;
		byte [] m;
		int serverPort = 0;
		String[] requestdata = new String[6];
		StringBuilder requeststr = new StringBuilder();
		StringBuilder sb = new StringBuilder();
		DatagramPacket request = null;
		int i;
		List<Thread> ClientThreads = new LinkedList<Thread>();
		List<ClientResponder> ClientResponders = new LinkedList<ClientResponder>();
		
		//Pass the required data
		requestdata[1] = args[2]; //Method name
		requestdata[2] = args[3]; //Customer ID
		requestdata[3] = args[4]; //eventType
		requestdata[4] = args[5]; //eventID
		requestdata[5] = args[6]; //bookRemove flag
		
		try {
			aSocket = new DatagramSocket();
			InetAddress aHost = InetAddress.getByName("127.0.0.1");  //Server's IP address
			ClientResponder CR = null;
			
			//Create the threads as needed 
			for(i=0;i<2; i++) {
				if (args[i] == null) continue;
				
				//Find the UDP Port based on the server.
				if (args[i].equals("QUE") ) {serverPort = 8070; requestdata[0] = args[i];}
				else if (args[i].equals("MTL")) {serverPort = 8080; requestdata[0] = args[i];}
				else if (args[i].equals("SHE")) {serverPort = 8090; requestdata[0] = args[i];}
				
				//Format Request Data as a string
				for(String str: requestdata) {requeststr.append(str).append("|");}
				
				m = requeststr.toString().getBytes();
				request = new DatagramPacket(m, requeststr.length(), aHost, serverPort);
				
				//Clear the request string builder.
				requeststr.setLength(0);
				
				CR = new ClientResponder(aSocket, request);
				ClientResponders.add(CR);
				
				Thread t = new Thread(CR);
				ClientThreads.add(t);
				t.start();
			}
			
			//Check whether threads are alive- If yes, keep waiting.
			for(int k=0; k < ClientThreads.size() ; k++) {
				while (ClientThreads.get(k).isAlive()) {
					if (ClientResponders.get(k).result != null) {ClientThreads.get(k).interrupt();}
				}
			}
			
			//Combine the result from all the threads.			
			System.out.println("Appending all the servers data- in UDPClientStart");
			for(ClientResponder obj: ClientResponders) {
				sb.append(obj.result).append("\n");
			}
			
		}catch (SocketException e){ System.out.println("Socket Exception in UPDClientStart of: " +this.serverName +" Server" + e.getMessage()); }
		catch (IOException e){ System.out.println("IO Exceptin in UPDClientStart of: " +this.serverName +" Server"  + e.getMessage());}
		finally {if(aSocket != null) aSocket.close();}
		return sb.toString();
	}
	
	////////////////////////////////Local methods /////////////////////////////////

	//Update CustomerDB
	public synchronized String updateCustDB(String customerID, String eventID, String eventType, char bookorRemove) {
		System.out.println("\nInside updateCustDB of " +this.serverName +" Server. " +customerID +" - " +eventType +" - " +eventID + " - " +bookorRemove + " - Event DB before update:" +eventDB + " - Cust DB before update:" +custDB);
		
		if (bookorRemove == 'B'){//book
			if(custDB.get(customerID) == null || custDB.get(customerID).isEmpty()) {
				custDB.put(customerID, new ConcurrentHashMap<String, ConcurrentHashMap<String, CustDetails>>());
				custDB.get(customerID).put(eventType, new ConcurrentHashMap<String, CustDetails>());
				custDB.get(customerID).get(eventType).put(eventID, new CustDetails(java.time.LocalDate.now().toString(), "Success"));
			}
			else if (custDB.get(customerID).get(eventType) == null) {
				custDB.get(customerID).put(eventType, new ConcurrentHashMap<String, CustDetails>());
				custDB.get(customerID).get(eventType).put(eventID, new CustDetails(java.time.LocalDate.now().toString(), "Success"));
			}
			else if (custDB.get(customerID).get(eventType).get(eventID) == null) {
				custDB.get(customerID).get(eventType).put(eventID, new CustDetails(java.time.LocalDate.now().toString(), "Success"));
			}
		}
		else { //remove eventID
			//Do validations
			if(custDB.get(customerID) == null) return "Customer "+customerID +" not found";
			
			if((custDB.get(customerID).get(eventType) == null) || (custDB.get(customerID).get(eventType).isEmpty()) || 
					(custDB.get(customerID).get(eventType).get(eventID) == null))
				return "Customer "+customerID + " has not booked the event - "+eventType +" - " +eventID;
			
			//Passed. remove the eventID for this customer.
			custDB.get(customerID).get(eventType).remove(eventID);
			
			//Remove event Type If empty.
			if (custDB.get(customerID).get(eventType).isEmpty()) custDB.get(customerID).remove(eventType);
			
			//Check whether whole DB can be made empty
			char isEmpty = 'Y'; 
			for (String keyM: custDB.get(customerID).keySet()) { //keyM contains event Types
				if (!(custDB.get(customerID).get(keyM).isEmpty())){
					isEmpty = 'N';
					break;
				}
			}
			if (isEmpty == 'Y') custDB.get(customerID).clear(); //Made empty for this customer only. :)
		}
		System.out.println("Event DB after update: " +eventDB + " - Cust DB after update: " +custDB);
		return "Success";
	}
	
	//Get the start and end week days of a date.
	public String[] getStartEndofWeek(String date) {
		int day = Integer.parseInt(date.substring(0,2));
		int month = Integer.parseInt(date.substring(2,4));
		
		//Get year
		String year = null;
		SimpleDateFormat format1 = new SimpleDateFormat("ddMMyy");
		SimpleDateFormat format2 = new SimpleDateFormat("ddMMyyyy");
		
		Date ddate;
		try {
			ddate = format1.parse(date);	
			year = format2.format(ddate).substring(4);
		} catch (ParseException e) {e.printStackTrace();}
		
		LocalDate datePassed = java.time.LocalDate.of(Integer.parseInt(year), month, day);
		String weekofdayPassed = datePassed.getDayOfWeek() .toString();
		
		int daystoSubtract =0;
		switch (weekofdayPassed) {
		case "TUESDAY":
			daystoSubtract = 1;
			break;
		case "WEDNESDAY":
			daystoSubtract = 2;
			break;
		case "THURSDAY":
			daystoSubtract = 3;
			break;
		case "FRIDAY":
			daystoSubtract = 4;
			break;
		case "SATURDAY":
			daystoSubtract = 5;
			break;
		case "SUNDAY":
			daystoSubtract = 6;
			break;
		}
		
		String[] startendofWeek = new String[2];
		
		LocalDate startdate = datePassed.minusDays(daystoSubtract);
		LocalDate enddate = startdate.plusDays(6);
		
		String start = startdate.toString();
		String end = enddate.toString();
				
		start = start.substring(8) + start.substring(5,7) + start.substring(2,4);
		end = end.substring(8) + end.substring(5,7) + end.substring(2,4);
		
		startendofWeek[0] = start;
		startendofWeek[1] = end;
		return startendofWeek;
	}
	
	//Check whether a date is in between two dates.
	public boolean isinbetween(String check, String start, String end) {
		int day, month;
		String year = null;
		Date ddate;
		SimpleDateFormat format1 = new SimpleDateFormat("ddMMyy");
		SimpleDateFormat format2 = new SimpleDateFormat("ddMMyyyy");
		
		//Check date
		day = Integer.parseInt(check.substring(0,2));
		month = Integer.parseInt(check.substring(2,4));
		try {
			ddate = format1.parse(check);	
			year = format2.format(ddate).substring(4);
		} catch (ParseException e) {e.printStackTrace();}
		LocalDate checkdate = java.time.LocalDate.of(Integer.parseInt(year), month, day);
		
		//Start date
		day = Integer.parseInt(start.substring(0,2));
		month = Integer.parseInt(start.substring(2,4));
		try {
			ddate = format1.parse(start);	
			year = format2.format(ddate).substring(4);
		} catch (ParseException e) {e.printStackTrace();}
		LocalDate startdate = java.time.LocalDate.of(Integer.parseInt(year), month, day);
		
		//End date
		day = Integer.parseInt(end.substring(0,2));
		month = Integer.parseInt(end.substring(2,4));
		try {
			ddate = format1.parse(end);	
			year = format2.format(ddate).substring(4);
		} catch (ParseException e) {e.printStackTrace();}
		LocalDate enddate = java.time.LocalDate.of(Integer.parseInt(year), month, day);
		
		if (!checkdate.isAfter(enddate) && !checkdate.isBefore(startdate)) return true;
		else return false;
	}
	
	//Logging method
	public synchronized void logging(String infoMsg) {
		System.out.println("Inside logging of " +this.serverName +" Server, with Msg= "+infoMsg);		
		String datetime = new java.util.Date().toString();
		infoMsg = datetime + " - " +infoMsg;
		
		logger.addAppender(appender);
		logger.setLevel((Level)Level.INFO);
		logger.info(infoMsg);
		
		// Write eventDB
		File file = new File("C:/Java/EclipseWorkspace/COMP6231_Project/src/ReplicaManager03/" + this.serverName + "_eventDB.txt");

		try (BufferedWriter br = new BufferedWriter(new FileWriter(file))) {
			for(String keyM: eventDB.keySet()) {
				for(String keyN: eventDB.get(keyM).keySet()) {
					br.write(keyM);
					br.write(" ");
					br.write(keyN);
					br.write("--");
					int vacancy = eventDB.get(keyM).get(keyN).bookingCapacity - eventDB.get(keyM).get(keyN).currentlyBooked;
					br.write(Integer.toString(vacancy));
					br.newLine();
				}
			}
		} catch (IOException e) {
			System.out.println("unable to read the file" + file.toString());
		}
		// Write Customer DB
	}
	
	/////////////////////////////// Remote methods /////////////////////////////////////////
	
	//bookRemote Event
	public synchronized String bookRemoteEvent(String customerID, String eventID, String eventType) {
		System.out.println("\nInside bookRemoteEvent of " +this.serverName +" Server. " +customerID +" - " +eventType +" - " +eventID + " - Event DB before bookRemoteEvent:" +eventDB + " - Cust DB before bookRemoteEvent:" +custDB);
		String bookResult = null;
		
		//Do Validations
		if(eventDB.get(eventType) == null) {
			bookResult = "eventType " +eventType +" doesn't exist";
			logging("Book Event- " + customerID +" - " +eventType +" - " +eventID +" - " +bookResult);
			return bookResult; 
		}
		if(eventDB.get(eventType).get(eventID) == null) {
			bookResult =  "eventID " +eventID +" doesn't exist";
			logging("Book Event- " + customerID +" - " +eventType +" - " +eventID +" - " +bookResult);
			return bookResult;
		}
		
		synchronized(this) {
			for(String cID: eventDB.get(eventType).get(eventID).customersBooked) {
				if (cID.equals(customerID)) {
					bookResult = "Customer " +customerID +" has already booked the event- " +eventType +" - "+eventID;
					logging("Book Event- " + customerID +" - " +eventType +" - " +eventID +" - " +bookResult);
					return bookResult;
				}
			}
			if(eventDB.get(eventType).get(eventID).bookingCapacity ==  eventDB.get(eventType).get(eventID).currentlyBooked) {
				bookResult = "The event - " +eventType +" - "+eventID +" is full";
				logging("Book Event- " + customerID +" - " +eventType +" - " +eventID +" - " +bookResult);
				return bookResult;
			}
			
			//Passed validations- book it
			eventDB.get(eventType).get(eventID).currentlyBooked += 1;
			eventDB.get(eventType).get(eventID).customersBooked.add(customerID);
		}
		
		System.out.println("Event DB after bookRemoteEvent: " +eventDB + " - Cust DB after bookRemoteEvent: " +custDB);
		logging("Book Event- " + customerID +" - " +eventType +" - " +eventID +" - " +"Success");
		return "Success";
	}
	
	//cancelRemote Event
	public String cancelRemoteEvent(String customerID, String eventID, String eventType) {
		System.out.println("\nInside cancelRemoteEvent of " +this.serverName +" Server. " +customerID +" - " +eventType +" - " +eventID + " - Event DB before cancelRemoteEvent:" +eventDB + " - Cust DB before cancelRemoteEvent:" +custDB);
		String cancelResult;
		
		//Do validations
		char cIDfound = 'N';
		synchronized(this) {
			for(String cID: eventDB.get(eventType).get(eventID).customersBooked) {
				if (cID.equals(customerID)) {
					cIDfound = 'Y';
					break;
				}
			}
		}
		
		if (cIDfound == 'N') {
			cancelResult = "Customer "+ customerID +" has not booked the event -" +eventType +" - " +eventID;
			logging("Cancel Event- " + customerID +" - " +eventType +" - " +eventID +" - " +cancelResult);
			return cancelResult;
		}
		
		//Passed validations - update eventDB
		synchronized(this) {
			eventDB.get(eventType).get(eventID).customersBooked.remove(customerID);
			eventDB.get(eventType).get(eventID).currentlyBooked -= 1;
		}
		
		System.out.println("Event DB after cancelRemoteEvent: " +eventDB + " - Cust DB after cancelRemoteEvent: " +custDB);
		logging("Cancel Event- " + customerID +" - " +eventType +" - " +eventID +" - " +"Success");
		return "Success";
	}
	
	//listRemoteEventAvailability
	public String listRemoteEventAvailability(String eventType) {
		System.out.println("\nInside listRemoteEventAvailability of " +this.serverName +" Server - " +eventType + " - Event DB:" +eventDB + " - Cust DB:" +custDB);
		StringBuilder eventList = new StringBuilder();
		int seatsAvailable;
		
		//Do validations
		if ((eventDB.get(eventType) == null) || (eventDB.get(eventType).isEmpty())) {
			eventList.append("No events from " +this.serverName + " for the eventType- "+eventType);
		}
		else {
			for (String keyN : eventDB.get(eventType).keySet()) {
				eventList.append("[").append(keyN).append(" - ");
				seatsAvailable = eventDB.get(eventType).get(keyN).bookingCapacity - eventDB.get(eventType).get(keyN).currentlyBooked;
				eventList.append(seatsAvailable).append("]");
			}
			eventList.append("\n");
		}
		
		logging("List event Availability- " + eventType +" - "+ "Success");
		return eventList.toString();
	}
	
	//UDP Server
	public void UDPServer(String args[]){
		DatagramSocket aSocket = null;
		System.out.println("Concurrent - " + args[0] +" UDP Server Started at Port- "+args[1]);
		
		//Main Logic
		try {
			DatagramPacket request;
			aSocket = new DatagramSocket(Integer.parseInt(args[1]));
			byte[] buffer = new byte[1000];
			ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
			int count = 1;
			
			while(true) {
				request = new DatagramPacket(buffer, buffer.length);
				System.out.println(this.serverName +" UDP Server is Waiting to receive Packet # " +count +" ..");
				aSocket.receive(request);
				System.out.println("Request#"+count +" Received = " +new String(request.getData()).trim() +" - from Port no." +request.getPort());
				count++;
				ServerResponder SP = new ServerResponder(aSocket,request,this);
				executor.execute(SP);
			}
		}
		catch (SocketException e){System.out.println("Socket error in UDPServer: " + e.getMessage());}
		catch (IOException e) {System.out.println("IO error in UDPServer: " + e.getMessage());}
		finally {
			if (aSocket !=null) aSocket.close();
		}
	}
}

//Define ClientResponder class
class ClientResponder implements Runnable{
	DatagramSocket aSocket;
	DatagramPacket request;
	String result = null;
	String replystr;
	
	public ClientResponder(DatagramSocket aSocket, DatagramPacket request) {
		this.aSocket = aSocket;
		this.request = request;
	}
	public void run() {
		try {
			System.out.println("Sending Request data: " + new String(request.getData()) +" From Thread " +Thread.currentThread().getName());
			aSocket.send(request);
			
			byte[] buffer = new byte[1000];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
			aSocket.receive(reply);
			
			System.out.println("Reply from Server: " + new String(reply.getData()).trim() +" For Thread " +Thread.currentThread().getName());
			replystr = new String(reply.getData()).trim();
			
			this.result = replystr;
			
			//Sleep once you are done.
			Thread.sleep(4000);
		}
		catch (IOException e1) {System.out.println("Socket Exception in ClientResponder of UPDClientStart: Server" + e1.getMessage());} 
		catch (InterruptedException e) {
			System.out.println("I am "+ Thread.currentThread().getName() +" Returning from run");
		}
	}
}

class ServerResponder implements Runnable{
	DatagramSocket aSocket = null;
	DatagramPacket request = null;
	String[] requestdata;
	String requeststr;
	EventManagement obj;
	/* 
	 * [0] - Server name to call
	 * [1] - Method name to call
	 * [2] - Customer ID
	 * [3] - event Type
	 * [4] - event ID
	 * [5] - bookRemove flag
	 */
	
	String result;
	byte [] m;
	
	public ServerResponder(DatagramSocket aSocket, DatagramPacket request, EventManagement obj) {
		this.aSocket = aSocket;
		this.request = request;
		this.requeststr = new String(request.getData()).trim();
		this.requestdata = requeststr.split("\\|");
		this.obj = obj;
	}

	public void run(){
		//Find out what to do with the request.
		System.out.println("Thread running now in " +requestdata[0] +" UDP Server is - " +Thread.currentThread().getName());
		//System.out.println("Request received to " +requestdata[0] +" UDP Server is - " +new String(request.getData()).trim() +" - from Port no. " +request.getPort());
		System.out.println("Request received to " +requestdata[0] +" UDP Server is - " +requeststr);
		
		try { 
			//Call the required method.
			switch(requestdata[1]) {
			case "bookRemoteEvent":
				result = obj.bookRemoteEvent(requestdata[2], requestdata[4], requestdata[3]); // bookRemoteEvent (customerID, eventID, eventType)
				break;
				
			case "cancelRemoteEvent":
				result = obj.cancelRemoteEvent(requestdata[2], requestdata[4], requestdata[3]); //cancelRemoteEvent (customerID, eventID, eventType)
				break;
			
			case "listRemoteEventAvailability":
				result = obj.listRemoteEventAvailability(requestdata[3]);  //listRemoteEventAvailability (eventType)
				break;
			
			case "updateCustDB":
				result =  obj.updateCustDB(requestdata[2], requestdata[4], requestdata[3], requestdata[5].charAt(0));  //updateCustDB (customerID, eventID, eventType, bookorRemove)
				break;
			}	
		}catch (Exception e) { System.out.println("Exception in UDP server Thread " +Thread.currentThread().getName() +"- " + " in UDPServer- " +requestdata[0] +e.getMessage());}
		
		//Send the result to the invoked thread.
		m = result.getBytes();
		
		DatagramPacket reply = new DatagramPacket(m, m.length, request.getAddress(), request.getPort());
		System.out.println("Response= " +result);
		
		try {
			aSocket.send(reply);
		} catch (IOException e) { System.out.println("IO Exception in Thread "+Thread.currentThread().getName() + " in UDPServer- " +requestdata[0] +e.getMessage());}
	}
}

class MyComparator implements Comparator<String>
{
    public int compare(String s1,String s2)
    {
    	SimpleDateFormat format1 = new SimpleDateFormat("ddMMyy");
    	int result1, result2;
		Date date1 = null, date2 = null;
		try {
			date1 = format1.parse(s1.substring(4));
			date2 = format1.parse(s2.substring(4));
		} catch (ParseException e) {e.printStackTrace();}
		result1 = date1.compareTo(date2);
		
		//Sort on M,A,E
		if (result1 == 0) {
			if (((s1.charAt(3) == 'M') && (s2.charAt(3) == 'A')) || ((s1.charAt(3) == 'M') && (s2.charAt(3) == 'E')) || ((s1.charAt(3) == 'A') && (s2.charAt(3) == 'E'))) result2 = -1;
			else if (((s1.charAt(3) == 'A') && (s2.charAt(3) == 'M')) || ((s1.charAt(3) == 'E') && (s2.charAt(3) == 'A')) || ((s1.charAt(3) == 'E') && (s2.charAt(3) == 'M'))) result2 = 1;
			else result2 = 0;
			
			return result2;
		}
		else return result1;
    }
}