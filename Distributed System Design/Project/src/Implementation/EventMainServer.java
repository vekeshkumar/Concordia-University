package Implementation;
import java.io.File;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.logging.Level;



public class EventMainServer   {
	

	static ServerImplementation serverMTL,serverQUE,serverSHE;
	static HashMap<String, ServerImplementation> serverRepo;
	static ICentralizedServer stubMTL,stubQUE,stubSHE;
	
	public static void main(String[] args) {
				
		//Initiate the required ports and names
		 initiate();
		 createRemoteObjects();
		 registerServersToRMI();
		 //LogManager logManager = new LogManager("ServerMain");
		 //logManager.logger.log(Level.INFO, "Server Ready! Listening on port :: " + Constants.RMI_PORT_NO_MTL);
		 System.out.println("Server started for "+ServerCenterLocation.MTL+" at the Port :"+Constants.RMI_PORT_NO_MTL);
		 System.out.println("Server started for "+ServerCenterLocation.QUE+" at the Port :"+Constants.RMI_PORT_NO_QUE);
		 System.out.println("Server started for "+ServerCenterLocation.SHE+" at the Port :"+Constants.RMI_PORT_NO_SHE);
	
	}

	//Initiate method to set the variables
	public static void initiate(){
		//Initiate the loggers
		try {
			serverMTL = new ServerImplementation(ServerCenterLocation.MTL);
			serverQUE = new ServerImplementation(ServerCenterLocation.QUE);
			serverSHE = new ServerImplementation(ServerCenterLocation.SHE);
			
			
			serverRepo = new HashMap<>();
			serverRepo.put("MTL", serverMTL);
			serverRepo.put("QUE", serverQUE);
			serverRepo.put("SHE", serverSHE);
			
			boolean isMTLDir = new File(Constants.LOG_DIR+ServerCenterLocation.MTL.toString()).mkdir();
			boolean isQUEDir = new File(Constants.LOG_DIR+ServerCenterLocation.QUE.toString()).mkdir();
			boolean isSHEDir = new File(Constants.LOG_DIR+ServerCenterLocation.SHE.toString()).mkdir();
			boolean globalDir = new File(Constants.LOG_DIR+"ServerGlobal").mkdir();
			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//Method to create removeObjects
	private static void createRemoteObjects() {
		try {
			stubMTL = (ICentralizedServer) UnicastRemoteObject.exportObject(serverMTL, Constants.RMI_PORT_NO_MTL);
			stubQUE = (ICentralizedServer) UnicastRemoteObject.exportObject( serverQUE, Constants.RMI_PORT_NO_QUE);
			stubSHE = (ICentralizedServer) UnicastRemoteObject.exportObject( serverSHE, Constants.RMI_PORT_NO_SHE);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
	}
	
	private static void registerServersToRMI()  {
		//Bind the remote objects to the RMI Registry
		Registry registryMTL,registryQUE,registrySHE;
		try {
			registryMTL = LocateRegistry.createRegistry(Constants.RMI_PORT_NO_MTL);
			registryQUE = LocateRegistry.createRegistry(Constants.RMI_PORT_NO_QUE);
			registrySHE = LocateRegistry.createRegistry(Constants.RMI_PORT_NO_SHE);
			
			registryMTL.bind("MTL", stubMTL);
			registryQUE.bind("QUE", stubQUE);
			registrySHE.bind("SHE", stubSHE);
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
