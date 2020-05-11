package ReplicaManager02;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import EventApp.EventServer;
import EventApp.EventServerHelper;

public class ReplicaClient {
	static EventServer QUEImpl, MTLImpl, SHEImpl;
			
	public static String processRequest(int seqNo, String request) {
		String[] inputargs, ORBargs;
		
		//Setup ORB and get Object references.
		ORBargs = new String[4];
		ORBargs[0] = "-ORBInitialPort";
		ORBargs[1] = "1050";
		ORBargs[2] = "-ORBInitialHost";
		ORBargs[3] = "localhost";
		
		setupORB(ORBargs);
		
		//Split the request..
		inputargs = request.split("\\,");
		System.out.println("Processing Request= "+seqNo +" - " +request);
		
		EventServer obj;
		if (inputargs[0].substring(0, 3).equals("QUE")) obj =  QUEImpl;
		else if (inputargs[0].substring(0, 3).equals("MTL")) obj =  MTLImpl;
		else obj =  SHEImpl;
				
		return callReplica(inputargs, obj);
	}
	
	//Setup ORB
	public static void setupORB(String[] args) {
		try {
			//Create and initialize ORB
			ORB orb = ORB.init(args, null);
			
			//get the root naming context
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			
			//Use NamingContextExt instead of NamingContext
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
			
			//Resolve the object reference in naming
			String name = "QUE02";			
			QUEImpl = EventServerHelper.narrow(ncRef.resolve_str(name));

			//Resolve the object reference in naming
			name = "MTL02";			
			MTLImpl = EventServerHelper.narrow(ncRef.resolve_str(name));		
			
			//Resolve the object reference in naming
			name = "SHE02";			
			SHEImpl = EventServerHelper.narrow(ncRef.resolve_str(name));
		}
		catch(Exception e) {System.out.println("Exception in Event Client " +e.getMessage());}
	}
	
	//Call Replica..
	public static String callReplica(String[] args, EventServer obj) {
		String ID, methodName, eventType, eventID, oldEventType, oldEventID, CID, result;
		ID = methodName = eventType = eventID = oldEventType = oldEventID = CID = result =null;
		int bookingCapacity = 0;
		
		//Take input values..
		ID = args[0];
		methodName = args[1];
		
		switch(args.length) {
		case 3:
			eventType = args[2];
			break;
		case 4:
			eventType = args[2];
			eventID = args[3];
			break;
		case 6:
			eventType = args[2];
			eventID = args[3];
			oldEventType = args[4];
			oldEventID = args[5];
			break;
		case 7:
			eventType = args[2];
			eventID = args[3];
			oldEventType = args[4];
			oldEventID = args[5];
			if (args[6] != null && !args[6].equals("")) bookingCapacity = Integer.parseInt(args[6]);
			break;
		case 8:
			eventType = args[2];
			eventID = args[3];
			oldEventType = args[4];
			oldEventID = args[5];
			if (args[6] != null && !args[6].equals("")) bookingCapacity = Integer.parseInt(args[6]);
			CID = args[7];
			break;
		}
		
		//Call
		switch(methodName) {
		case "addEvent":
			result = obj.addEvent(eventID, eventType, bookingCapacity);
			if (result.equals("Failure")) bookingCapacity += 2;
			if (result.equals("Success") || result.equals("Failure")) result = result + "| " +bookingCapacity;
			break;

		case "removeEvent":
			result = obj.removeEvent(eventID, eventType);
			if (!result.equals("Success")) result = "Failure";
			break;
			
		case "listEventAvailability":
			result = obj.listEventAvailability(eventType);
			
			//Format the result..
			String[] listResultLines = result.split("\\\n");
			int openIndex, count, vacancy;
			String resultEventID;
			Map<String, Integer> listEventResult = new TreeMap<String, Integer>();

			for(String resultLine : listResultLines) {
				resultLine = resultLine.trim();
				if (resultLine.substring(0, 2).equals("No")) continue;
				count = countOccurences('[', resultLine);
				
				for(int i=0; i<=count; i++) {
					openIndex = resultLine.indexOf('[');
					if (openIndex == -1) break;
					
					resultEventID = resultLine.substring(openIndex+1,11);
					vacancy = Integer.parseInt(resultLine.substring(openIndex+14, resultLine.indexOf(']')));
					listEventResult.put(resultEventID, vacancy);
					
					resultLine = resultLine.substring(resultLine.indexOf(']')+1);
				}
			}
			
			//Send the result..
			result = listEventResult.toString();
			break;
		
		case "bookEvent":
			if (CID != null) ID = CID;
			result = obj.bookEvent(ID, eventID, eventType);
			if (!result.equals("Success")) result = "Failure";
			break;
		
		case "getBookingSchedule":
			if (CID != null) ID = CID;
			result = obj.getBookingSchedule(ID);
			
			//Format the result..
			String[] getResultLines = result.split("\\\n");
			//Set<String> getSchdResult = new TreeSet<String>();
			List<String> getSchdResult = new LinkedList<String>();
			
			for(String resultLine : getResultLines) {
				if (resultLine.indexOf('-') == -1) continue;
				getSchdResult.add(resultLine.substring(0, 10));
			}
			
			//Send the result..
			Collections.sort(getSchdResult);
			result = getSchdResult.toString();
			break;
		
		case "cancelEvent":
			if (CID != null) ID = CID;
			result = obj.cancelEvent(ID, eventID, eventType);
			if (!result.equals("Success")) result = "Failure";
			break;
			
		case "swapEvent":
			if (CID != null) ID = CID;
			result = obj.swapEvent(ID, eventID, eventType, oldEventID, oldEventType);
			if (!result.equals("Success")) result = "Failure";
			break;
		}
		
		//return output..
		return result;
	}
	
	//Count occurrences of a char in a String..
	public static int countOccurences(char c, String str) {
		int count = 0;
		for(int i =0; i<str.length(); i++) {
			if (str.charAt(i) == c) count++;
		}
		
		return count;
	}
}