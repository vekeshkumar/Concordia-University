package MiddleWare;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;

import FrontEndApp.FrontEndServer;
import FrontEndApp.FrontEndServerHelper;

public class FrontEnd {
	/*
	 * Front End CORBA Server..
	 */
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Hello in DEMS FrontEnd Server...");
		FrontEndManagement obj = null;
		ORB orb = null;
		
		//Set up ORB
		try{ 
			// create and initialize the ORB
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
			obj = new FrontEndManagement();
			obj.setORB(orb);
			
			// get object reference from the servant
			org.omg.CORBA.Object EMobjref = rootpoa.servant_to_reference(obj);
			
			// and cast the reference to a JAVA reference
			FrontEndServer objhref = FrontEndServerHelper.narrow(EMobjref);
			
			// get the root naming context
			// NameService invokes the transient name service 
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			
			// Use NamingContextExt, which is part of the Inter-operable Naming Service (INS) specification
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef); 
			
			// bind the Object Reference in Naming
			NameComponent pathQUE[] = ncRef.to_name("FrontEnd");
			
			ncRef.rebind(pathQUE, objhref);
			System.out.println("FrontEnd is ready and waiting ...");
			
			// wait for invocations from clients
			while(true) {
				orb.run();
			}
		}
		catch(Exception e) { System.out.println("Something went wrong in FrontEnd Server.. " +e.getMessage());}	
	}
}