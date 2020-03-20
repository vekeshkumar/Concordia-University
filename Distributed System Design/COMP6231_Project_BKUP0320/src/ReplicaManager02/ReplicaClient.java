package ReplicaManager02;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import EventApp02.EventServer02;
import EventApp02.EventServer02Helper;

public class ReplicaClient {
	static EventServer02 QUEImpl, MTLImpl, SHEImpl;
			
	public static String processRequest(String request) {
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
		System.out.println("Processing Request= " +request);
		
		EventServer02 obj;
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
			String name = "QUE";			
			QUEImpl = EventServer02Helper.narrow(ncRef.resolve_str(name));
			System.out.println("Obtained a handle on QUE Server object: "+QUEImpl);

			//Resolve the object reference in naming
			name = "MTL";			
			MTLImpl = EventServer02Helper.narrow(ncRef.resolve_str(name));
			System.out.println("Obtained a handle on MTL Server object: "+MTLImpl);
			
			//Resolve the object reference in naming
			name = "SHE";			
			SHEImpl = EventServer02Helper.narrow(ncRef.resolve_str(name));
			System.out.println("Obtained a handle on SHE Server object: "+SHEImpl);
		}
		catch(Exception e) {System.out.println("Exception in Event Client " +e.getMessage());}
	}
	
	//Call Replica..
	public static String callReplica(String[] args, EventServer02 obj) {
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
			if (args[6] != null) bookingCapacity = Integer.parseInt(args[6]);
			break;
		case 8:
			eventType = args[2];
			eventID = args[3];
			oldEventType = args[4];
			oldEventID = args[5];
			if (args[6] != null) bookingCapacity = Integer.parseInt(args[6]);
			CID = args[7];
			break;
		}
		
		//Call
		switch(methodName) {
		case "addEvent":
			result = obj.addEvent(eventID, eventType, bookingCapacity);
			break;
		case "removeEvent":
			result = obj.removeEvent(eventID, eventType);
			break;
			
		case "listEventAvailability":
			result = obj.listEventAvailability(eventType);
			break;
		
		case "bookEvent":
			if (CID != null) ID = CID;
			result = obj.bookEvent(ID, eventID, eventType);
			break;
		
		case "getBookingSchedule":
			if (CID != null) ID = CID;
			result = obj.getBookingSchedule(ID);
			break;
		
		case "cancelEvent":
			if (CID != null) ID = CID;
			result = obj.cancelEvent(ID, eventID, eventType);
			break;
			
		case "swapEvent":
			if (CID != null) ID = CID;
			result = obj.swapEvent(ID, eventID, eventType, oldEventID, oldEventType);
			break;
		}
		
		//return output..
		return result;
	}
}