package Servers;
import java.util.*;
import java.util.logging.Level;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;


import java.io.*;
import java.net.MalformedURLException;
import java.nio.charset.MalformedInputException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import DEMS_FrontEnd.DEMSInterface;
import DEMS_FrontEnd.DEMSInterfaceHelper;


public class EventMainServer   {
	
	static ServerImplementation serverMTL,serverQUE,serverSHE;
	static HashMap<String, ServerImplementation> serverRepo;

	
	public static void main(String[] args) {
				
		//Initiate the required ports and names
		
		try {
			// create and initialize the ORB //
			ORB orb = ORB.init(args, null);
			
			// get reference to rootpoa &amp; activate
			POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			rootpoa.the_POAManager().activate();

			// create servant and register it with the ORB
			//For 3 servers

			serverMTL = new ServerImplementation(ServerCenterLocation.MTL);
			serverQUE = new ServerImplementation(ServerCenterLocation.QUE);
			serverSHE = new ServerImplementation(ServerCenterLocation.SHE);
			
			serverMTL.setORB(orb);
			serverQUE.setORB(orb);
			serverSHE.setORB(orb);

			serverRepo = new HashMap<>();
			serverRepo.put("MTL", serverMTL);
			serverRepo.put("QUE", serverQUE);
			serverRepo.put("SHE", serverSHE);					
			
			// get object reference from the servant
			org.omg.CORBA.Object ref1 = rootpoa.servant_to_reference(serverMTL);
			org.omg.CORBA.Object ref2 = rootpoa.servant_to_reference(serverQUE);
			org.omg.CORBA.Object ref3 = rootpoa.servant_to_reference(serverSHE);
			
			// and cast the reference to a CORBA reference
			DEMSInterface href1 = DEMSInterfaceHelper.narrow(ref1);
			DEMSInterface href2 = DEMSInterfaceHelper.narrow(ref2);
			DEMSInterface href3 = DEMSInterfaceHelper.narrow(ref3);

			// get the root naming context
			// NameService invokes the transient name service
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			
			// Use NamingContextExt, which is part of the
			// Interoperable Naming Service (INS) specification.
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
			// bind the Object Reference in Naming
			NameComponent path1[] = ncRef.to_name("MTL");
			ncRef.rebind(path1, ref1);			
			NameComponent path2[] = ncRef.to_name("QUE");
			ncRef.rebind(path2, ref2);
			NameComponent path3[] = ncRef.to_name("SHE");
			ncRef.rebind(path3, ref3);
			 System.out.println("Server started for MTL"+path1+ref1);
			 System.out.println("Server started for QUE "+path2+ref2);
			 System.out.println("Server started for SHE"+path3+ref3);
			for(;;) {
				orb.run();
			}
		 //LogManager logManager = new LogManager("ServerMain");
		 //logManager.logger.log(Level.INFO, "Server Ready! Listening on port :: " + com.Config.Constants.RMI_PORT_NO_MTL);
		
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
