package com.Server;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.stream.Stream;
import java.text.DateFormat;
import java.text.ParseException;
import com.Config.*;

import com.Beans.Event;
import com.Beans.EventClient;
import com.Config.Constants;
import com.Config.ServerCenterLocation;

@SuppressWarnings("serial")
public class ServerImplementation implements ICentralizedServer {
	com.Config.LogManager logManager;
	UDPServer udpServer;
	String IPAddress;
	int bookingCount;
	//Stores event details
	HashMap<String,HashMap<String, List<Event>>> eventRecordMTL;
	HashMap<String,HashMap<String, List<Event>>> eventRecordQUE;
	HashMap<String,HashMap<String, List<Event>>> eventRecordSHE;
	//Store client details

	
	//clientRecord Whole List
	static HashMap<String, List<EventClient>> clientRecord;	
	String location;

	protected ServerImplementation(ServerCenterLocation scloc)
			throws RemoteException {
		eventRecordMTL = new HashMap<>();
		eventRecordQUE = new HashMap<>();
		eventRecordSHE = new HashMap<>();

		clientRecord = new HashMap<>(); 
		//Start udp server with location.	
		logManager = new com.Config.LogManager(scloc.toString());
		udpServer = new UDPServer(scloc,logManager.logger,this);
		udpServer.start();
		location = scloc.toString();
		setIPAddress(scloc);
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
		//String msg = addEventRecHashMap(eventType, eventDetails);
		String msg = "Error!. Please try again";
		//Storing the event details with the Key
		String eventServLocation = eventID.substring(0,3);
		if(this.location.equalsIgnoreCase(eventServLocation)){
			if(this.location.equals("MTL")) {			
				//loop through to get ids
				msg = addEventRecHashMap(this.eventRecordMTL, eventType, eventDetails, eventServLocation);
			}else if(this.location.equals("SHE")) {
				msg = addEventRecHashMap(this.eventRecordSHE,eventType, eventDetails, eventServLocation);				
			}else {
				msg = addEventRecHashMap(this.eventRecordQUE,eventType, eventDetails, eventServLocation);
			}
		}
		logManager.logger.log(Level.INFO, msg);
		return msg;
		
	}

	public String addEventRecHashMap(HashMap<String, HashMap<String, List<Event>>> servLocation, String eventTypeKey, Event eventDetails , String eventLoc) {
		String msg = "Error!. Please try again";
		//Storing the event details with the Key
		String eventServLocation = eventDetails.getId().substring(0,3);
		String eventID = eventDetails.getId();
		boolean putValue = false;
		boolean updateValue = false;
		HashMap<String,List<Event>> mtlList  =new HashMap<>(); 
		List<Event> records = new ArrayList<Event>();
		if(eventTypeKey!=null && eventDetails!=null) { 
			 if(servLocation.isEmpty() || servLocation.get(eventTypeKey)== null) {
				//When no record is present				 
				 mtlList = new HashMap<>();				
				 records.add(eventDetails);
				 mtlList.put(eventID,records);
				 putValue = true;				
			 }else {	
				 //When some record present				 
				 if(servLocation.get(eventTypeKey)!= null) {					 
					 if( servLocation.get(eventTypeKey).get(eventID)!=null) {
						 if(servLocation.get(eventTypeKey).get(eventID).listIterator().next().getId().equals(eventID)) {
							 if(servLocation.get(eventTypeKey).get(eventID).listIterator().next().getBookingCapacity()<eventDetails.getBookingCapacity()) 
							 updateValue = true;							
							else 							
							 msg ="Event already exists";
						 }
					 }else {
						 mtlList = servLocation.get(eventTypeKey);
						 records.add(eventDetails);	
						 mtlList.put(eventID,records);
						 putValue = true;	
					 }
				 }else {
					 mtlList = servLocation.get(eventTypeKey);
					 records.add(eventDetails);	
					 mtlList.put(eventID,records);
					 putValue = true;						
				 }
			 }	 			 
			 if(eventLoc.equalsIgnoreCase("mtl")) {
				 if(putValue)					 
				 eventRecordMTL.put(eventTypeKey,mtlList);
				 if(updateValue)
					 eventRecordMTL.get(eventTypeKey).get(eventID).listIterator().next().setBookingCapacity(eventDetails.getBookingCapacity());
				 printMapValues(eventRecordMTL);
				 msg= "Record added!! Success";
			}else if(eventLoc.equalsIgnoreCase("she")) {
				if(putValue)
				 eventRecordSHE.put(eventTypeKey,mtlList);
				if(updateValue)
					 eventRecordSHE.get(eventTypeKey).get(eventID).listIterator().next().setBookingCapacity(eventDetails.getBookingCapacity());
				 printMapValues(eventRecordSHE);
				 msg= "Record added!! Success";
			}else if(eventLoc.equalsIgnoreCase("que")){			
				if(putValue)
				 eventRecordQUE.put(eventTypeKey,mtlList);
				 if(updateValue)
					 eventRecordQUE.get(eventTypeKey).get(eventID).listIterator().next().setBookingCapacity(eventDetails.getBookingCapacity());
				 printMapValues(eventRecordQUE);
				 msg= "Record added!! Success";
			}
		}		
		return msg;
	}	
	
	//Remove Event from server, cancel all the booking, and book for next available event
	@Override
	public String removeEvent(String eventID, String eventType) throws ParseException {
		//Check the server location
		//find the event 
		// remove it from the hashmap
		System.out.println(clientRecord);
		String msg = "Error";
		String eventServLocation = eventID.substring(0,3);		
		ArrayList<String>  customerList = new ArrayList<>();
		String neweventID = null;
		String nexteventID=null;
		TreeSet<Date> nexteventsdates=new TreeSet<Date>();
		
		//changing the eventid for comparing during next event finding
		if(eventID.charAt(3)=='M') { neweventID=eventID.substring(4)+"09";}
		if(eventID.charAt(3)=='A') { neweventID=eventID.substring(4)+"13";}
		if(eventID.charAt(3)=='E') { neweventID=eventID.substring(4)+"17";}
		System.out.println("changed eventid: "+neweventID);
		Date newdate=new SimpleDateFormat("ddMMyyhh").parse(neweventID); 

		
		switch (eventServLocation) {
			case "MTL":	
				//Traverse Event MTL hashmap check eventId exists	
				for(String keyi:eventRecordMTL.get(eventType).keySet())
				{
					String newvalue = null;
					if(keyi.charAt(3)=='M') {newvalue=keyi.substring(4)+"09";}
					if(keyi.charAt(3)=='A') {newvalue=keyi.substring(4)+"13";}
					if(keyi.charAt(3)=='E') {newvalue=keyi.substring(4)+"17";}
					Date nextdate=new SimpleDateFormat("ddMMyyhh").parse(newvalue);
					nexteventsdates.add(nextdate);
				}
				customerList = eventRecordMTL.get(eventType).get(eventID).listIterator().next().getBookedCustomerIds();		
				//remove the record from the eventRecord 
								
				
				System.out.println("After finding and removing the record");
				System.out.println("Event record");
				 printMapValues(eventRecordMTL);
				 msg = "Record removed";
				 if(!customerList.isEmpty())
					{
						for(String bookedcust:customerList)
						{
							if(!nexteventsdates.isEmpty())
							{
							for(Date nexteventdate:nexteventsdates)
							{
								if(nexteventdate.compareTo(newdate)<0)
								{
									continue;
								}
								if(nexteventdate.equals(newdate))
								{
									continue;
								}
								if(nexteventdate.compareTo(newdate)>0)
								{
									
									Date nedate=nexteventdate;
									DateFormat dateFormat = new SimpleDateFormat("ddMMyyhh");  
									String nextevent = dateFormat.format(nedate);  
									
									String nextevent1 = null;
									
									if(nextevent.endsWith("09")) {nextevent1=this.location.substring(0,3)+"M"+nextevent.substring(0,nextevent.length()-2);}
									if(nextevent.endsWith("01")) {nextevent1=this.location.substring(0,3)+"A"+nextevent.substring(0,nextevent.length()-2);}
									if(nextevent.endsWith("05")) {nextevent1=this.location.substring(0,3)+"E"+nextevent.substring(0,nextevent.length()-2);}
									System.out.println("next eventid: "+nextevent1);
									System.out.println(clientRecord);
									String result=bookEvent(bookedcust,nextevent1,eventType);
									if(result.trim().equalsIgnoreCase("Event registered successfully"))
									{
										
										break;
									}
								}
							  }
							} System.out.println("Before removing client record :"+clientRecord);
							clientRecord.get(bookedcust).listIterator().next().getBookedEventId().removeIf(e -> e.contains(eventID));
							System.out.println("afer removing  event"+clientRecord);
							
						}
					}
				 eventRecordMTL.get(eventType).remove(eventID);
				 System.out.println("After removing the event");
				 printMapValues(eventRecordMTL);				 
				 msg = "Record removed";
				break;
			case "QUE":
				for(String keyi:eventRecordQUE.get(eventType).keySet())
				{
					String newvalue = null;
					if(keyi.charAt(3)=='M') {newvalue=keyi.substring(4)+"09";}
					if(keyi.charAt(3)=='A') {newvalue=keyi.substring(4)+"13";}
					if(keyi.charAt(3)=='E') {newvalue=keyi.substring(4)+"17";}
					Date nextdate=new SimpleDateFormat("ddMMyyhh").parse(newvalue);
					nexteventsdates.add(nextdate);
				}
				customerList = eventRecordQUE.get(eventType).get(eventID).listIterator().next().getBookedCustomerIds();
				 System.out.println("After finding and removing the record");
				 if(!customerList.isEmpty())
					{
						for(String bookedcust:customerList)
						{
							if(!nexteventsdates.isEmpty())
							{
							for(Date nexteventdate:nexteventsdates)
							{
								if(nexteventdate.compareTo(newdate)<0)
								{
									continue;
								}
								if(nexteventdate.equals(newdate))
								{
									continue;
								}
								if(nexteventdate.compareTo(newdate)>0)
								{
									
									Date nedate=nexteventdate;
									DateFormat dateFormat = new SimpleDateFormat("ddMMyyhh");  
									String nextevent = dateFormat.format(nedate);  
									
									String nextevent1 = null;
									
									if(nextevent.endsWith("09")) {nextevent1=this.location.substring(0,3)+"M"+nextevent.substring(0,nextevent.length()-2);}
									if(nextevent.endsWith("01")) {nextevent1=this.location.substring(0,3)+"A"+nextevent.substring(0,nextevent.length()-2);}
									if(nextevent.endsWith("05")) {nextevent1=this.location.substring(0,3)+"E"+nextevent.substring(0,nextevent.length()-2);}
									System.out.println("next eventid: "+nextevent1);
									String result=bookEvent(bookedcust,nextevent1,eventType);
									if(result.trim().equalsIgnoreCase("Event registered successfully"))
									{										
										break;
									}
								}
							  }
							}
							
						}
					}
				 eventRecordQUE.get(eventType).remove(eventID);
				 printMapValues(eventRecordQUE);
				 msg = "Record removed";
				break;
			case "SHE":
				for(String keyi:eventRecordMTL.get(eventType).keySet())
				{
					String newvalue = null;
					if(keyi.charAt(3)=='M') {newvalue=keyi.substring(4)+"09";}
					if(keyi.charAt(3)=='A') {newvalue=keyi.substring(4)+"13";}
					if(keyi.charAt(3)=='E') {newvalue=keyi.substring(4)+"17";}
					Date nextdate=new SimpleDateFormat("ddMMyyhh").parse(newvalue);
					nexteventsdates.add(nextdate);
				}
				customerList = eventRecordSHE.get(eventType).get(eventID).listIterator().next().getBookedCustomerIds();
				System.out.println("After finding and removing the record");
				if(!customerList.isEmpty())
				{
					for(String bookedcust:customerList)
					{
						if(!nexteventsdates.isEmpty())
						{
						for(Date nexteventdate:nexteventsdates)
						{
							if(nexteventdate.compareTo(newdate)<0)
							{
								continue;
							}
							if(nexteventdate.equals(newdate))
							{
								continue;
							}
							if(nexteventdate.compareTo(newdate)>0)
							{
								
								Date nedate=nexteventdate;
								DateFormat dateFormat = new SimpleDateFormat("ddMMyyhh");  
								String nextevent = dateFormat.format(nedate);  
								
								String nextevent1 = null;
								
								if(nextevent.endsWith("09")) {nextevent1=this.location.substring(0,3)+"M"+nextevent.substring(0,nextevent.length()-2);}
								if(nextevent.endsWith("01")) {nextevent1=this.location.substring(0,3)+"A"+nextevent.substring(0,nextevent.length()-2);}
								if(nextevent.endsWith("05")) {nextevent1=this.location.substring(0,3)+"E"+nextevent.substring(0,nextevent.length()-2);}
								System.out.println("next eventid: "+nextevent1);
								String result=bookEvent(bookedcust,nextevent1,eventType);
								if(result.trim().equalsIgnoreCase("Event registered successfully"))
								{									
									break;
								}
							}
						  }
						}
						
					}
				}
				eventRecordSHE.get(eventType).remove(eventID);
				printMapValues(eventRecordSHE);
				 msg = "Record removed";
				break;
			default:
				System.out.println("Error, Couldn't find the record");
				break;
			}
		logManager.logger.log(Level.INFO, msg);
		return msg;
		// TODO Auto-generated method stub
	}
	//Seminars: MTLE130320 3, SHEA060220 6, QUEM180230 0, MTLE190320 2
	@Override
	public String listEventAvailability(String eventType) {
		//Check all 3 servers
		// get events from each servers of the given type
		//capacity is available 
		//current date
		String msgResult ="Error";
        String recordCount = null;
        UDPIntReqClient[] req = new UDPIntReqClient[2];
        ArrayList<String> locList = new ArrayList<>(Arrays.asList("MTL","QUE","SHE"));
        int cntr=0;
        //loop the server and get the values

        for (String loc : locList) {
            if (loc== this.location) {            	
                recordCount = getCurrServerCnt(this.location,eventType);
            } else {
            	req[cntr] = new UDPIntReqClient(EventMainServer.serverRepo.get(loc),eventType,"listing");
                req[cntr].start();
                cntr++;
            }
        }
        for (UDPIntReqClient request : req) {
        	
            try {
				request.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            recordCount += " , " + request.getRemoteListDetails().trim();
        }
        logManager.logger.log(Level.INFO, "Record Count"+recordCount);
        return recordCount;
		
	}
	public String getCurrServerCnt(String location,String eventType){
		//Get all values from current server whose capacity is not full
		String value= null;
		if(this.location.equals("MTL")) {			
			//loop through to get ids
			value = getEventIds(this.eventRecordMTL, eventType);
		}else if(this.location.equals("QUE")) {
			value = getEventIds(this.eventRecordQUE, eventType);
			
		}else {
			value = getEventIds(this.eventRecordSHE, eventType);
		}
		return value;
	}
	 //fn to get list of events from a server
	 public static String getEventIds(HashMap<String, HashMap<String, List<Event>>> servLocation, String eventType) {
		 ArrayList<String> value = new  ArrayList<>();
		 String strValue =  "No event is available";
		 
		 if(!servLocation.isEmpty()) {
			 for (Entry<String, HashMap<String, List<Event>>> eachEntry : servLocation.entrySet()) {
				if(eachEntry.getKey().equalsIgnoreCase(eventType)) {					
					for(Entry<String, List<Event>> eachEvent : eachEntry.getValue().entrySet()) {
						if(eachEvent.getValue().listIterator().next() != null) {
							value.add(eachEvent.getKey()+"-"+eachEvent.getValue().get(0).getBookingCapacity());
						 }
					}
					strValue = value.toString();
				}
			 }
			 System.out.println("3"+strValue);
		 }else {
			 strValue ="No event is available";
			}
		return strValue;
	 }
	
	
	public String bookCurrEvent(HashMap<String, HashMap<String, List<Event>>> servLocation,String customerId,String eventId, String eventType, String eventLoc){
		//Get details - check if same customer login is booking the event
		//Check the customer has already booked the event
		//Update should be done in ArrayList of EventRecord and also in ClientRecord
		//bookedCustomerIds[],  bookedEventIds[]  
		String msg = null;
		if(servLocation.containsKey(eventType) && servLocation.get(eventType).containsKey(eventId)) {
			int bookingCapacity=0;
			String customerLoc = customerId.substring(0, 3);
			boolean isRegistrationSuccessful =false;
			System.out.println(clientRecord);
			//Event
			Event eve = new Event();
			List<Event> record = new ArrayList<Event>();		
			record = (List<Event>) servLocation.get(eventType).get(eventId);
			for (Event event: record) {
				bookingCapacity = event.getBookingCapacity();
				eve.setId(event.getId());
				eve.setEventType(event.getEventType());
				eve.setBookedCustomerIds(event.getBookedCustomerIds());
			}		
			//Client
			//Update booking capacity
			//Update Customer Id in Event and client record
			if(bookingCapacity>=1) {
				bookingCapacity-=1;
				eve.setBookingCapacity(bookingCapacity);				
				if(eve.getBookedCustomerIds()==null) {
   					ArrayList<String> arrList = new ArrayList<>();
					arrList.add(customerId);
					eve.setBookedCustomerIds(arrList);
				}else {
					if(eve.getBookedCustomerIds().contains(customerId)) {
						msg="Already the event is booked by the customer";
					}else {
						ArrayList<String> arrList = eve.getBookedCustomerIds();
						arrList.add(customerId);
						eve.setBookedCustomerIds(arrList);
					}
				}
				
				//Client 
				if(eventLoc.equalsIgnoreCase("mtl")) {
					eventRecordMTL.get(eventType).get(eventId).listIterator().next().setBookingCapacity(eve.getBookingCapacity());
					eventRecordMTL.get(eventType).get(eventId).listIterator().next().setBookedCustomerIds(eve.getBookedCustomerIds());
					System.out.println(eventRecordMTL);
					
				}else if(eventLoc.equalsIgnoreCase("she")) {
					eventRecordSHE.get(eventType).get(eventId).listIterator().next().setBookingCapacity(eve.getBookingCapacity());
					eventRecordSHE.get(eventType).get(eventId).listIterator().next().setBookedCustomerIds(eve.getBookedCustomerIds());
					System.out.println(eventRecordSHE);
					
				}else {					
					eventRecordQUE.get(eventType).get(eventId).listIterator().next().setBookingCapacity(eve.getBookingCapacity());
					eventRecordQUE.get(eventType).get(eventId).listIterator().next().setBookedCustomerIds(eve.getBookedCustomerIds());
					System.out.println(eventRecordQUE);
				}	
				//Add event Id's to client record
				
			printMapValues(clientRecord);
			if(!isRegistrationSuccessful)
					msg ="Event registered successfully";
			}else
				msg="Event is full";			
			
		}
		else
			msg ="Event is not available in the server";
		logManager.logger.log(Level.INFO, msg);
		return msg;
	}
	
	@Override
	public String bookEvent(String customerId, String eventId, String eventType) throws ParseException {
		String msg = "Error!. Please try again";
		//Storing the event details with the Key
		String eventServLocation = eventId.substring(0,3);
		boolean isBookingAllowed = false;
		if(this.location.equalsIgnoreCase(eventServLocation)){
			if(this.location.equals("MTL")) {
				System.out.println(eventRecordMTL.toString());
				//loop through to get ids
				msg = bookCurrEvent(this.eventRecordMTL,customerId, eventId, eventType, eventServLocation);
			}else if(this.location.equals("SHE")) {
				msg = bookCurrEvent(this.eventRecordSHE, customerId, eventId, eventType, eventServLocation);
				
			}else {
				msg = bookCurrEvent(this.eventRecordQUE, customerId, eventId, eventType, eventServLocation);
			}
			
		}else {		
			if(clientRecord.isEmpty()) {
				isBookingAllowed = true;				
			}else {
				isBookingAllowed = checkThreeEventsOrMore(customerId, eventId, eventServLocation);
			}
			if(isBookingAllowed) {
				String functionType="book";
				UDPIntReqClient req = new UDPIntReqClient(EventMainServer.serverRepo.get(eventServLocation),eventType,eventId,customerId,functionType);
				req.start();
				try {
					req.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				msg  = req.returnBookEventMsg();
			}else {
				msg="You cannot book more than events from other servers";
			}
		}
		
		//Client
		EventClient eveClient = new EventClient();			
		ArrayList<EventClient> clientRecordArr = new ArrayList<EventClient>();
		ArrayList<String> bookedEventIds = new ArrayList<String>();	
		eveClient.setClientId(customerId);
		eveClient.setClientType(customerId.substring(3, 4));
		
		if(msg.trim().equalsIgnoreCase("Event registered successfully")) {
			if(clientRecord.isEmpty()) {
				bookedEventIds.add(eventId);
				eveClient.setBookedEventId(bookedEventIds);
				clientRecordArr.add(eveClient);
				clientRecord.put(customerId,clientRecordArr);
				System.out.println("client record"+clientRecord);
			}
			else{	
				if(clientRecord.containsKey(customerId)) {				
					bookedEventIds = clientRecord.get(customerId).listIterator().next().getBookedEventId();		
					if(bookedEventIds.contains(eventId)) {
						msg= "Sorry, This event is already registered by the customer";
					}else {
						bookedEventIds.add(eventId);
						eveClient.setBookedEventId(bookedEventIds);		
						clientRecord.get(customerId).listIterator().next().setBookedEventId(eveClient.getBookedEventId());
						System.out.println(clientRecord);
					}
				}else {
					bookedEventIds.add(eventId);
					eveClient.setBookedEventId(bookedEventIds);
					clientRecordArr.add(eveClient);							
					clientRecord.put(customerId,clientRecordArr);
				}
			}
		}
		logManager.logger.log(Level.INFO, msg);
		System.out.println("Client Record---"+clientRecord);
		return msg;
	}
	public boolean checkThreeEventsOrMore(String customerId, String eventId, String loc) throws ParseException {
		String cusLoc = customerId.substring(0,3);
		boolean isBookingAllowed = false;
		ArrayList<String> otherServerList = new ArrayList<>();
		
		if(!clientRecord.isEmpty()) {
			otherServerList = clientRecord.get(customerId).listIterator().next().getBookedEventId();
			Calendar cal= Calendar.getInstance();
			String tempdate1=eventId.substring(4);
			Date date1=new SimpleDateFormat("ddMMyy").parse(tempdate1); 
		    cal.setTime(date1);
		    int week1 =cal.get(Calendar.WEEK_OF_YEAR);
		    int otherBookingForEvent =0;
		    for(String eachEvent : otherServerList) {
		    	String dateStr = eachEvent.substring(4);	    	
		    	Date date2=new SimpleDateFormat("ddMMyy").parse(dateStr); 
				cal.setTime(date2);
			    int week2 =cal.get(Calendar.WEEK_OF_YEAR);
			    if(week1==week2)
			    {
			    	otherBookingForEvent++;
			    }		    	
		    }
		    if(otherBookingForEvent>=3) {
		    	isBookingAllowed = false;
		    }else {
		    	isBookingAllowed = true;
		    }
		}else {
			isBookingAllowed = true;

		}
		return isBookingAllowed;
	}

	
	@Override
	public String cancelEvent(String customerId, String eventId, String eventType) {
		String msg = "Error!. Please try again";
		//Storing the event details with the Key
		String eventServLocation = eventId.substring(0,3);
		if(this.location.equalsIgnoreCase(eventServLocation)){
			System.out.println(customerId+ eventId+ eventType+ eventServLocation);
			if(this.location.equals("MTL")) {			
				//loop through to get ids
				msg = cancelCurrEvent(this.eventRecordMTL, customerId, eventId, eventType, eventServLocation);
				
				
			}else if(this.location.equals("SHE")) {
				msg = cancelCurrEvent(this.eventRecordSHE, customerId, eventId, eventType, eventServLocation);				
			}else {
				msg = cancelCurrEvent(this.eventRecordQUE, customerId, eventId, eventType, eventServLocation);
			}
		}else {	String functionType = "cancel";
			UDPIntReqClient req = new UDPIntReqClient(EventMainServer.serverRepo.get(eventServLocation),eventType,eventId,customerId,functionType);
			req.start();
			try {
				req.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			msg  = req.returnBookEventMsg();
			if(msg.trim().equalsIgnoreCase("Event cancelled successfully.")) {
				clientRecord.get(customerId).listIterator().next().getBookedEventId().removeIf(e -> e.contains(eventId));
				System.out.println(clientRecord);
			}
		}
		return msg;
	}
	public String cancelCurrEvent(HashMap<String, HashMap<String, List<Event>>> servLocation, String customerId, String eventId, String eventType, String eventLocation) {
		String msg = "";
		boolean isCancelSuccess = false;
		//increment capacity of event
		//removee the cliend ids in two places	
		System.out.println(eventLocation);
		if(servLocation.containsKey(eventType) && servLocation.get(eventType).containsKey(eventId)) {
				//Client 
				if(eventLocation.equalsIgnoreCase("mtl")) {
					if(eventRecordMTL.get(eventType).get(eventId).listIterator().next().getBookedCustomerIds().contains(customerId)) {
						System.out.println("Thhaa variya");
						int bookingCapacityValue = eventRecordMTL.get(eventType).get(eventId).listIterator().next().getBookingCapacity()+1;
						eventRecordMTL.get(eventType).get(eventId).listIterator().next().setBookingCapacity(bookingCapacityValue);
						eventRecordMTL.get(eventType).get(eventId).listIterator().next().getBookedCustomerIds().removeIf(e -> e.contains(customerId));
						System.out.println("dfnkdf"+clientRecord);
						
						isCancelSuccess = true;
						printMapValues(eventRecordMTL);
						printMapValues(clientRecord);
						
					}else {
						System.out.println("Sorry, This event is not booked by this customer and It cannot be cancelled");
						msg="Sorry, This event is not booked by this customer and It cannot be cancelled";
						isCancelSuccess= false;
					}
				}else if(eventLocation.equalsIgnoreCase("she")) {

					if(eventRecordSHE.get(eventType).get(eventId).listIterator().next().getBookedCustomerIds().contains(customerId)) {
						int bookingCapacityValue = eventRecordMTL.get(eventType).get(eventId).listIterator().next().getBookingCapacity()+1;
						eventRecordSHE.get(eventType).get(eventId).listIterator().next().setBookingCapacity(bookingCapacityValue);						
						eventRecordSHE.get(eventType).get(eventId).listIterator().next().getBookedCustomerIds().removeIf(e -> e.contains(eventId));						
						//clientRecord.get(customerId).listIterator().next().getBookedEventId().removeIf(e -> e.contains(eventId));
						isCancelSuccess = true;
						printMapValues(eventRecordSHE);
						printMapValues(clientRecord);
					}else {						

						System.out.println("Sorry, This event is not booked by this customer and It cannot be cancelled");
						msg="Sorry, This event is not booked by this customer and It cannot be cancelled";
						isCancelSuccess= false;
					}
				}else {
					if(eventRecordQUE.get(eventType).get(eventId).listIterator().next().getBookedCustomerIds().contains(customerId)) {
						int bookingCapacityValue = eventRecordMTL.get(eventType).get(eventId).listIterator().next().getBookingCapacity()+1;
						eventRecordQUE.get(eventType).get(eventId).listIterator().next().setBookingCapacity(bookingCapacityValue);						
						eventRecordQUE.get(eventType).get(eventId).listIterator().next().getBookedCustomerIds().removeIf(e -> e.contains(customerId));
						//clientRecord.get(customerId).listIterator().next().getBookedEventId().removeIf(e -> e.contains(eventId));
						isCancelSuccess = true;
						
					}else {						
						System.out.println("Sorry, This event is not booked by this customer and It cannot be cancelled");
						msg="Sorry, This event is not booked by this customer and It cannot be cancelled";
						isCancelSuccess= false;
					}
				}
				if(isCancelSuccess)
					msg ="Event cancelled successfully.";
				else
					msg = "Sorry, Event cancelling is unsuccessful";
		}else {
			
			msg ="Event is not available in the server";
		}
		logManager.logger.log(Level.INFO, msg);
		
		return msg;
	
	}
	@Override
	public String getBookingSchedule(String customerID) {
		String eventsBooked = null;
		ArrayList<String> eventsList = new ArrayList<>();
		//loop through to get ids
		System.out.println("Event Schedule"+clientRecord.toString());
		if(clientRecord.containsKey(customerID)){
			eventsList  = clientRecord.get(customerID).listIterator().next().getBookedEventId();
		}		
		System.out.println("Event booked:"+eventsList.toString());
		logManager.logger.log(Level.INFO, eventsList.toString());
		return eventsList.toString();
	}

	 public static void printMap(Map mp) {
		    Iterator it = mp.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry pair = (Map.Entry)it.next();
		        System.out.println(pair.getKey() + " = " + pair.getValue().toString());
		    }
	}
	 public static String printMapValues(Map mp) {
		 String result = "";
		    Iterator it = mp.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry pair = (Map.Entry)it.next();
		        String value =(pair.getKey()+"="+pair.getValue().toString());
		        result += value+"\n";
		    }
		    System.out.println("Print records : "+result+"\n");
		    return result;
		   
	}

}


