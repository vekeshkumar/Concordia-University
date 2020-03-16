package FrontEnd;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import DEMS_FrontEnd.DEMSInterface;
import DEMS_FrontEnd.DEMSInterfaceHelper;


public class FEClient {
	static DEMSInterface FrontEndObj;
	/*static final Logger logger = Logger.getLogger(Logger.class.getName());
	//Logging
	static SimpleLayout layout = new SimpleLayout();
	static RollingFileAppender appender =null;
	static String filename;
	static String path = "C:/Java/EclipseWorkspace/COMP6231_Assignment2/src/Client/";*/
			
	public static void main(String[] args) {
		System.out.println("Welcome to DEMS Events..");
		
		List<String> input = new ArrayList<String>();
		List<String> output = new ArrayList<String>();
		String[] inputargs;
		String textline, validRequest;
		List<Thread> ClientThreads = new LinkedList<Thread>();
		List<ProcessRequest> ProcessRequestors = new ArrayList<ProcessRequest>(); 
		
		//Logging
		//appender = new RollingFileAppender();
		
		//Setup ORB and get Object references.
		String[] ORBargs = new String[4];
		ORBargs[0] = "-ORBInitialPort";
		ORBargs[1] = "1050";
		ORBargs[2] = "-ORBInitialHost";
		ORBargs[3] = "localhost";
		
		setupORB(ORBargs);

		//Read the Input file.
		File file = new File("E:\\EclipseWorkspace\\DistributedProject_FE\\src\\Input.txt");
		try(BufferedReader br = new BufferedReader(new FileReader(file))){
			while((textline = br.readLine()) != null){ //assign and check
				if (textline.charAt(0) == 'I') continue;
				input.add(textline);
			}
		} catch (FileNotFoundException e1) {System.out.println("Can't open the file" +file.toString());}
		  catch (IOException e1) {System.out.println("unable to read the file" +file.toString());}
		
		//For every input line..
		System.out.println();
		for (String line : input) {
			inputargs = line.split("\\,");
			System.out.println("Processing Request= " +line);
			
			//Validate line
			validRequest = validateRequest(inputargs);
			
			//If not success -> Add it to output.
			if (!validRequest.equals("Success")) {
				line += " : " +validRequest;
				output.add(line);
			}
			else { //Success. Create a thread to process the request.
				DEMSInterface obj;
				obj =  FrontEndObj;
				
				ProcessRequest PR = new ProcessRequest(line, inputargs, obj);
				ProcessRequestors.add(PR);
				
				Thread t = new Thread(PR);
				ClientThreads.add(t);
				
				//Start thread
				t.start();
			}
		}
		
		//Wait till all threads died.
		for(int k=0; k < ClientThreads.size() ; k++) {
			try {
				ClientThreads.get(k).join();
			} catch (InterruptedException e) { System.out.println("Exception on Join in Main Client.." +e.getMessage());}
		}
		
		//Threads finished processing. Add the result to output and write the logs.
		for(int k=0; k < ClientThreads.size() ; k++) {
			if (ProcessRequestors.get(k).result != null) {
				output.add(ProcessRequestors.get(k).result);		
				//logging(ProcessRequestors.get(k).result.substring(0, 8), ProcessRequestors.get(k).infoMsg);
			}
		}
		
		//Display Output
		System.out.println();
		for(String outline: output) {
			System.out.println(outline);
		}
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
			String name = "FrontEnd";			
			FrontEndObj = DEMSInterfaceHelper.narrow(ncRef.resolve_str(name));
			System.out.println("Obtained a handle on QUE Server object: "+FrontEndObj);
		}
		catch(Exception e) {System.out.println("Exception in Event Client " +e.getMessage());}
	}
	
	//validate Line
	public static String validateRequest(String[] args) {
		String ID = args[0];
		
		if ((ID.length() > 8) || (!ID.substring(0,3).equals("QUE") && !ID.substring(0,3).equals("MTL") && !ID.substring(0,3).equals("SHE")) || 
				((ID.charAt(3) != 'C') && (ID.charAt(3) != 'M'))) {
				return "Invalid ID";
		}
		
		if (ID.charAt(3) == 'C' && (!args[1].equals("bookEvent") && !args[1].equals("getBookingSchedule") && !args[1].equals("cancelEvent") && !args[1].equals("swapEvent"))) {
				return "Unauthorized operation for Customer";
		}
		return "Success";
	}
	
	//Logger
	/*public static void logging(String ID, String infoMsg) {
		
		if (ID.charAt(3) == 'M') filename  = path + "EventManagerLogs/" +ID +".txt";
		else filename  = path +"CustomerLogs/" +ID +".txt";
		
		appender.setFile(filename);
		appender.setLayout(layout);
		appender.setAppend(true);
		appender.activateOptions();
		
		String datetime = new java.util.Date().toString();
		
		infoMsg = datetime + " - " +infoMsg;
		logger.addAppender(appender);
		logger.setLevel((Level)Level.INFO);
		logger.info(infoMsg);
	}*/
}

class ProcessRequest implements Runnable{
	String ID, methodName, eventType, eventID, oldEventType, oldEventID, result, line, infoMsg, CID;
	int bookingCapacity;
	DEMSInterface obj;
	
	public ProcessRequest(String line, String[] args, DEMSInterface obj) {
		this.line = line;
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
		this.obj = obj;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		switch(methodName) {
		case "addEvent":
			result = obj.addEvent(ID,eventID, eventType, bookingCapacity);
			infoMsg = "Add event - " +ID +" - "+ eventType +" - " +eventID +" - " +bookingCapacity +" - " +result;
			break;
		
		case "removeEvent":
			result = obj.removeEvent(ID,eventID, eventType);
			infoMsg = "Remove event - " +ID +" - "+ eventType +" - " +eventID +" - " +result;
			break;
			
		case "listEventAvailability":
			result = obj.listEventAvailability(ID,eventType);
			infoMsg = "List event Availability- " +ID + " - " + eventType +" - "+ result;
			break;
		
		case "bookEvent":
			if (CID != null) ID = CID;
			result = obj.bookEvent(ID, eventID, eventType);
			infoMsg = "Book Event- " + ID +" - " +eventType +" - " +eventID +" - " +result;
			break;
		
		case "getBookingSchedule":
			if (CID != null) ID = CID;
			result = obj.getBookingSchedule(ID);
			infoMsg = "Get Booking Schedule- " + ID +" - "+ result;
			break;
		
		case "cancelEvent":
			if (CID != null) ID = CID;
			result = obj.cancelEvent(ID, eventID, eventType);
			infoMsg = "Cancel Event- " + ID +" - " +eventType +" - " +eventID +" - " +result;
			break;
			
		case "swapEvent":
			if (CID != null) ID = CID;
			result = obj.swapEvent(ID, eventID, eventType, oldEventID, oldEventType);
			infoMsg = "Swap Event- " + ID +" - " +oldEventType +" - " +oldEventID +" - " +eventType +" - " +eventID +" - " +result;
			break;
		}
		result = line +" : " +result.trim();
	}
}
