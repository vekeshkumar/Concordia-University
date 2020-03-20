package Replica03;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;

import EventApp03.EventServer03;
import EventApp03.EventServer03Helper;
import Replica03.EventManagement;

public class ReplicaServer {
	/*
	 * [0] - Server name
	 * [1] - UDP port
	 */
	
	public static void main(String[] args) {
		EventManagement obj = null;
		ORB orb = null;
		
		try{ 
			// create and initialize the ORB
			System.out.println("Hello in " +args[0] +" DEMS Event Server...");
			String[] ORBargs = new String[4];
			ORBargs[0] = "-ORBInitialPort";
			ORBargs[1] = "1050";
			ORBargs[2] = "-ORBInitialHost";
			ORBargs[3] = "localhost";
			
			orb = ORB.init(ORBargs, null); 
			
			// get reference to root poa & activate the POAManager
			POA rootpoa = (POA)orb.resolve_initial_references("RootPOA");
			rootpoa.the_POAManager().activate(); 
			
			// create servant and register it with the ORB
			obj = new EventManagement(args[0]);
			obj.setORB(orb);
			
			// get object reference from the servant
			org.omg.CORBA.Object EMobjref = rootpoa.servant_to_reference(obj);
			
			// and cast the reference to a JAVA reference
			EventServer03 objhref = EventServer03Helper.narrow(EMobjref);
			
			// get the root naming context
			// NameService invokes the transient name service 
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			
			// Use NamingContextExt, which is part of the Inter-operable Naming Service (INS) specification
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef); 
			
			// bind the Object Reference in Naming
			NameComponent pathQUE[] = ncRef.to_name(args[0]);
			
			ncRef.rebind(pathQUE, objhref);
			System.out.println(args[0] +" Server is ready and waiting ..."); 
		}
		catch(Exception e) { System.out.println("Something went wrong.. " +e.getMessage());}
		
		//Start UDP Servers
		Thread t = new Thread(new UDPServerStart(args, obj));
		t.start();

		//Wait till Threads finish
		try {t.join();} 
		catch (InterruptedException e) {System.out.println("Exception on Join in Main Server.." +e.getMessage());}
		
		System.out.println("After join..");
		// wait for invocations from clients
		while(true) {
			orb.run();
		}
	}
}

class UDPServerStart implements Runnable{
	String[] udpargs = new String[2];
	EventManagement obj;

	public UDPServerStart(String[] args, EventManagement obj) {
		udpargs[0] = args[0];
		udpargs[1] = args[1];
		this.obj = obj;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		obj.UDPServer(udpargs);
	}
}