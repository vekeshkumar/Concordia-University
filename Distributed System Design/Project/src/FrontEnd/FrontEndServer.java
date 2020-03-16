package FrontEnd;

import java.util.HashMap;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import DEMS_FrontEnd.DEMSInterface;
import DEMS_FrontEnd.DEMSInterfaceHelper;
import FrontEnd.FrontEndImpl;

public class FrontEndServer {
	public static void main(String[] args) {
		try {
			//Setup ORB and get Object references.
			String[] ORBargs = new String[4];
			ORBargs[0] = "-ORBInitialPort";
			ORBargs[1] = "1050";
			ORBargs[2] = "-ORBInitialHost";
			ORBargs[3] = "localhost";
			// create and initialize the ORB //
			ORB orb = ORB.init(ORBargs, null);
			
			//start orbd -ORBInitialPort 1050
			
			// get reference to rootpoa &amp; activate
			POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			rootpoa.the_POAManager().activate();

			// create servant and register it with the ORB
			//For 3 servers

			FrontEndImpl feServer = new FrontEndImpl();
			feServer.setORB(orb);
			
			// get object reference from the servant
			org.omg.CORBA.Object ref1 = rootpoa.servant_to_reference(feServer);

			// and cast the reference to a CORBA reference
			DEMSInterface href1 = DEMSInterfaceHelper.narrow(ref1);

			// get the root naming context
			// NameService invokes the transient name service
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			
			// Use NamingContextExt, which is part of the
			// Interoperable Naming Service (INS) specification.
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
			// bind the Object Reference in Naming
			NameComponent path1[] = ncRef.to_name("FrontEnd");
			ncRef.rebind(path1, ref1);			
			 System.out.println("FE Server"+path1+ref1);
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
