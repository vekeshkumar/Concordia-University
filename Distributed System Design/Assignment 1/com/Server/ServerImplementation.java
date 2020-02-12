package com.Server;

import java.beans.EventSetDescriptor;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.stream.Stream;

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
		
		clientRecordMTL = new HashMap<>();
		clientRecordQUE = new HashMap<>();
		clientRecordSHE = new HashMap<>();
		//Start udp server with location.	
		udpServer = new UDPServer(scloc, this);
		udpServer.start();
		location = scloc.toString();
		setIPAddress(scloc);
	}


	@SuppressWarnings("null")
	public String addEventRecHashMap(String eventTypeKey, Event eventDetails) {
		String msg = "Error!. Please try again";
		//Storing the event details with the Key
		String eventServLocation = eventDetails.getId().substring(0,3);
		String eventID = eventDetails.getId();
		
		switch (eventServLocation) {
		case "MTL":			
			if(eventTypeKey!=null && eventDetails!=null) { 
				 HashMap<String, List<Event>> innerRec = new HashMap<>();
				 List<Event> records = new ArrayList<Event>();
				 records.add(eventDetails);					 
				 innerRec.put(eventID, records);				
				 eventRecordMTL.put(eventTypeKey, innerRec);
				 //Get list of eventRecord and then 
				 //go for another loop  
				 printMap(eventRecordMTL);
				 msg= "Record added!! Success";
			}
			break;
		case "QUE":
			if(eventTypeKey!=null && eventDetails!=null) { 
				 HashMap<String, List<Event>> innerRec = new HashMap<>();
				 List<Event> records = new ArrayList<Event>();
				 records.add(eventDetails);
				 innerRec.put(eventID, records);
				 eventRecordQUE.put(eventTypeKey, innerRec);
				 //Get list of eventRecord and then 
				 //go for another loop  
				 printMap(eventRecordQUE);
				 msg= "Record added!! Success";
			}

			break;
		case "SHE":
			if(eventTypeKey!=null && eventDetails!=null) { ;
				 HashMap<String, List<Event>> innerRec =  new HashMap<>();
					 List<Event> records = new ArrayList<Event>();
					 records.add(eventDetails);
					 innerRec.put(eventID, records);
				 eventRecordSHE.put(eventTypeKey, innerRec);
				 printMap(eventRecordSHE);
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
		//Check the server location
		//find the event 
		// remove it from the hashmap
		String msg = "Error";
		String eventServLocation = eventID.substring(0,3);
		
		switch (eventServLocation) {
			case "MTL":	
				//Traverse Event MTL hashmap check eventId exists	
				eventRecordMTL.entrySet().removeIf(e -> e.getValue().toString().contains(eventID));
				//eventRecordMTL.entrySet().removeIf(e ->System.out.println( e.getValue().toString()));
				 printMap(eventRecordMTL);
				 msg = "Record removed";
				break;
			case "QUE":
				eventRecordQUE.entrySet().removeIf(e -> e.getValue().toString().contains(eventID));
				 printMap(eventRecordMTL);
				 msg = "Record removed";
				break;
			case "SHE":
				eventRecordSHE.entrySet().removeIf(e -> e.getValue().toString().contains(eventID));
				 printMap(eventRecordMTL);
				 msg = "Record removed";
				break;
			default:
				break;
			}
		return msg;
		// TODO Auto-generated method stub
		
	}

	private String getCurrServerCnt(String location,String eventType){
		String value= null;
		switch(this.location) {
		case "MTL":
			if(this.eventRecordMTL.containsKey(eventType)) {
				value =this.eventRecordMTL.get(eventType).entrySet().iterator().next().getKey();
				System.out.println(this.eventRecordMTL.entrySet().iterator().next().getValue().entrySet().iterator().next().getKey());				
			}
		break;
		case "QUE":
			if(this.eventRecordQUE.containsKey(eventType)) {
				value =this.eventRecordQUE.get(eventType).entrySet().iterator().next().getKey();
				System.out.println(this.eventRecordQUE.entrySet().iterator().next().getValue().entrySet().iterator().next().getKey());
			}
			break;
		case "SHE":
			if(this.eventRecordSHE.containsKey(eventType)) {
				value =this.eventRecordSHE.get(eventType).entrySet().iterator().next().getKey();
				System.out.println(this.eventRecordSHE.get(eventType).entrySet().iterator().next().getKey());
			 	System.out.println(this.eventRecordSHE.entrySet().iterator().next().getValue().entrySet().iterator().next().getKey());
			}
			break;
			default:
				break;
			
		}
		//System.out.println(this.eventRecordMTL.entrySet().iterator().next().getValue().entrySet().iterator().next().getKey());
		/*for (Entry<String, HashMap<String, List<Event>>> entry : this.eventRecordMTL.entrySet()) {
			List<Event> list = (List<Event>) entry.getValue();
			count+=list.size();
			System.out.println(entry.getKey()+" "+list.size());
		}*/
		return value;
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
        ArrayList<String> locList = new ArrayList<>();
        int cntr=0;
        locList.add("MTL");
        locList.add("QUE");
        locList.add("SHE");
        for (String loc : locList) {
            if (loc== this.location) {
                recordCount = loc+","+getCurrServerCnt(this.location,eventType);
            } else {
            	req[cntr] = new UDPIntReqClient(EventMainServer.serverRepo.get(loc));
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
            recordCount += " , " + request.getRemoteListDetails();
        }
        return recordCount;
		
	}

	@Override
	public String bookEvent(String customerId, String eventId, String eventType) {
		String msg = "Error!. Please try again";
		//Storing the event details with the Key
		String eventServLocation = eventId.substring(0,3);
		
		switch (eventServLocation) {
		case "MTL":			
			if(eventRecordMTL.containsKey(eventType) && eventRecordMTL.get(eventType).containsKey(eventId)) {
				int bookingCapacity=0;
				//Event
				Event eve = new Event();
				 List<Event> record = new ArrayList<Event>();
				//Client
				EventClient eveClient = new EventClient();
				List<EventClient> clientRecord = new ArrayList<EventClient>();
				ArrayList<String> bookedEventIds = new ArrayList<String>();

				 record = (List<Event>) eventRecordMTL.get(eventType).get(eventId);
					for (Event event: record) {
						bookingCapacity = event.getBookingCapacity();
						eve.setId(event.getId());
						eve.setEventType(event.getEventType());
					}					
				if(bookingCapacity>=1) {
					bookingCapacity-=1;
					eve.setBookingCapacity(bookingCapacity);
					System.out.println(eve.toString());
					record.add(eve);
					eventRecordMTL.get(eventType).replace(eventType, record);
					
					//Client
					eveClient.setClientId(customerId);
					eveClient.setClientType(customerId.substring(3, 4));
					bookedEventIds.add(eventId);
					eveClient.setBookedEventId(bookedEventIds);
					clientRecord.add(eveClient);
					clientRecordMTL.put(eventType,clientRecord);
					System.out.println("Client----infor");
					printMap(clientRecordMTL);
					
					//eveClient.setBookedEventId()
					msg ="Registered to the event successfully";
					
				}else {
					msg="Event is full";
				}
				
			}else {
				
				msg ="Event is not available in the server";
			}
			 
			
			break;
		case "QUE":
			if(eventRecordQUE.containsKey(eventType) && eventRecordQUE.get(eventType).containsKey(eventId)) {
				int bookingCapacity=0;
				//Event
				Event eve = new Event();
				 List<Event> record = new ArrayList<Event>();
				//Client
				EventClient eveClient = new EventClient();
				List<EventClient> clientRecord = new ArrayList<EventClient>();
				ArrayList<String> bookedEventIds = new ArrayList<String>();

				 record = (List<Event>) eventRecordQUE.get(eventType).get(eventId);
					for (Event event: record) {
						bookingCapacity = event.getBookingCapacity();
						eve.setId(event.getId());
						eve.setEventType(event.getEventType());
					}					
				if(bookingCapacity>=1) {
					bookingCapacity-=1;
					eve.setBookingCapacity(bookingCapacity);
					System.out.println(eve.toString());
					record.add(eve);
					eventRecordQUE.get(eventType).replace(eventType, record);
					
					//Client
					eveClient.setClientId(customerId);
					eveClient.setClientType(customerId.substring(3, 4));
					bookedEventIds.add(eventId);
					eveClient.setBookedEventId(bookedEventIds);
					clientRecord.add(eveClient);
					clientRecordQUE.put(eventType,clientRecord);
					System.out.println("Client----infor");
					printMap(clientRecordMTL);
					
					//eveClient.setBookedEventId()
					msg ="Registered to the event successfully";
					
				}else {
					msg="Event is full";
				}
				
			}else {
				
				msg ="Event is not available in the server";
			}
			 
			break;
		case "SHE":
			if(eventRecordSHE.containsKey(eventType) && eventRecordSHE.get(eventType).containsKey(eventId)) {
				int bookingCapacity=0;
				//Event
				Event eve = new Event();
				 List<Event> record = new ArrayList<Event>();
				//Client
				EventClient eveClient = new EventClient();
				List<EventClient> clientRecord = new ArrayList<EventClient>();
				ArrayList<String> bookedEventIds = new ArrayList<String>();

				 record = (List<Event>) eventRecordSHE.get(eventType).get(eventId);
					for (Event event: record) {
						bookingCapacity = event.getBookingCapacity();
						eve.setId(event.getId());
						eve.setEventType(event.getEventType());
					}					
				if(bookingCapacity>=1) {
					bookingCapacity-=1;
					eve.setBookingCapacity(bookingCapacity);
					System.out.println(eve.toString());
					record.add(eve);
					eventRecordSHE.get(eventType).replace(eventType, record);
					
					//Client
					eveClient.setClientId(customerId);
					eveClient.setClientType(customerId.substring(3, 4));
					bookedEventIds.add(eventId);
					eveClient.setBookedEventId(bookedEventIds);
					clientRecord.add(eveClient);
					clientRecordSHE.put(eventType,clientRecord);
					System.out.println("Client----infor");
					printMap(clientRecordSHE);
					
					//eveClient.setBookedEventId()
					msg ="Registered to the event successfully";
					
				}else {
					msg="Event is full";
				}
				
			}else {
				
				msg ="Event is not available in the server";
			}
			 
			break;
		default:
			break;
		}
		return msg;
	}

	@Override
	public String getBookingSchedule(String customerID) {
		String eventServLocation = customerID.substring(0,3);
		return null;
	}

	@Override
	public String cancelEvent(String customerId, String eventId, String eventType) {
		String msg = "Error!. Please try again";
		//Storing the event details with the Key
		String eventServLocation = eventId.substring(0,3);
		
		switch (eventServLocation) {
		case "MTL":			
			if(eventRecordMTL.containsKey(eventType) && eventRecordMTL.get(eventType).containsKey(eventId)) {
				int bookingCapacity=0;
				//Event
				Event eve = new Event();
				List<Event> record = new ArrayList<Event>();
				 record = (List<Event>) eventRecordMTL.get(eventType).get(eventId);
					for (Event event: record) {
						bookingCapacity = event.getBookingCapacity();
						eve.setId(event.getId());
						eve.setEventType(event.getEventType());
					}					
					eve.setBookingCapacity(bookingCapacity+1);
					record.add(eve);
					eventRecordMTL.get(eventType).replace(eventType, record);
					
					System.out.println("Client----infor");
					printMap(clientRecordMTL);
					
					//eveClient.setBookedEventId()
					msg ="Cancelled from the event successfully";
				
			}else {
				
				msg ="Event is not available in the server";
			}
			 
			
			break;
		case "QUE":
			if(eventRecordQUE.containsKey(eventType) && eventRecordQUE.get(eventType).containsKey(eventId)) {
				int bookingCapacity=0;
				//Event
				Event eve = new Event();
				 List<Event> record = new ArrayList<Event>();
				//Client
				EventClient eveClient = new EventClient();
				List<EventClient> clientRecord = new ArrayList<EventClient>();
				ArrayList<String> bookedEventIds = new ArrayList<String>();

				 record = (List<Event>) eventRecordQUE.get(eventType).get(eventId);
					for (Event event: record) {
						bookingCapacity = event.getBookingCapacity();
						eve.setId(event.getId());
						eve.setEventType(event.getEventType());
					}					
				if(bookingCapacity>=1) {
					bookingCapacity-=1;
					eve.setBookingCapacity(bookingCapacity);
					System.out.println(eve.toString());
					record.add(eve);
					eventRecordQUE.get(eventType).replace(eventType, record);
					
					//Client
					eveClient.setClientId(customerId);
					eveClient.setClientType(customerId.substring(3, 4));
					bookedEventIds.add(eventId);
					eveClient.setBookedEventId(bookedEventIds);
					clientRecord.add(eveClient);
					clientRecordQUE.put(eventType,clientRecord);
					System.out.println("Client----infor");
					printMap(clientRecordMTL);
					
					//eveClient.setBookedEventId()
					msg ="Registered to the event successfully";
					
				}else {
					msg="Event is full";
				}
				
			}else {
				
				msg ="Event is not available in the server";
			}
			 
			break;
		case "SHE":
			if(eventRecordSHE.containsKey(eventType) && eventRecordSHE.get(eventType).containsKey(eventId)) {
				int bookingCapacity=0;
				//Event
				Event eve = new Event();
				 List<Event> record = new ArrayList<Event>();
				//Client
				EventClient eveClient = new EventClient();
				List<EventClient> clientRecord = new ArrayList<EventClient>();
				ArrayList<String> bookedEventIds = new ArrayList<String>();

				 record = (List<Event>) eventRecordSHE.get(eventType).get(eventId);
					for (Event event: record) {
						bookingCapacity = event.getBookingCapacity();
						eve.setId(event.getId());
						eve.setEventType(event.getEventType());
					}					
				if(bookingCapacity>=1) {
					bookingCapacity-=1;
					eve.setBookingCapacity(bookingCapacity);
					System.out.println(eve.toString());
					record.add(eve);
					eventRecordSHE.get(eventType).replace(eventType, record);
					
					//Client
					eveClient.setClientId(customerId);
					eveClient.setClientType(customerId.substring(3, 4));
					bookedEventIds.add(eventId);
					eveClient.setBookedEventId(bookedEventIds);
					clientRecord.add(eveClient);
					clientRecordSHE.put(eventType,clientRecord);
					System.out.println("Client----infor");
					printMap(clientRecordSHE);
					
					//eveClient.setBookedEventId()
					msg ="Registered to the event successfully";
					
				}else {
					msg="Event is full";
				}
				
			}else {
				
				msg ="Event is not available in the server";
			}
			 
			break;
		default:
			break;
		}
		return msg;

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
		String msg = addEventRecHashMap(eventType, eventDetails);
		return msg;
	}


	@Override
	public String checkEventExists(String eventID, String eventType) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}
	 public static void printMap(Map mp) {
		    Iterator it = mp.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry pair = (Map.Entry)it.next();
		        System.out.println(pair.getKey() + " = " + pair.getValue().toString());
		        //it.remove(); // avoids a ConcurrentModificationException
		    }
		}
	 public static String printMapValues(Map mp) {
		 	String value= null;
		    Iterator it = mp.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry pair = (Map.Entry)it.next();
		        value.concat(pair.getKey() + " = " + pair.getValue().toString());
		        //it.remove(); // avoids a ConcurrentModificationException
		    }
		    return value;
		}



}

