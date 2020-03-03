package com.Server;
import java.util.*;
import java.util.logging.Level;

import com.Config.LogManager;
import com.Config.ServerCenterLocation;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.charset.MalformedInputException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class EventMainServer   {
	
	static ServerImplementation serverMTL,serverQUE,serverSHE;
	static HashMap<String, ServerImplementation> serverRepo;
	static ICentralizedServer stubMTL,stubQUE,stubSHE;
	
	public static void main(String[] args) {
				
		//Initiate the required ports and names
		 initiate();
		 createRemoteObjects();
		 registerServersToRMI();
		 LogManager logManager = new LogManager("ServerMain");
		 logManager.logger.log(Level.INFO, "Server Ready! Listening on port :: " + com.Config.Constants.RMI_PORT_NO_MTL);
		 System.out.println("Server started for "+ServerCenterLocation.MTL+" at the Port :"+com.Config.Constants.RMI_PORT_NO_MTL);
		 System.out.println("Server started for "+ServerCenterLocation.QUE+" at the Port :"+com.Config.Constants.RMI_PORT_NO_QUE);
		 System.out.println("Server started for "+ServerCenterLocation.SHE+" at the Port :"+com.Config.Constants.RMI_PORT_NO_SHE);
	
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
			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//Method to create removeObjects
	private static void createRemoteObjects() {
		try {
			stubMTL = (ICentralizedServer) UnicastRemoteObject.exportObject(serverMTL, com.Config.Constants.RMI_PORT_NO_MTL);
			stubQUE = (ICentralizedServer) UnicastRemoteObject.exportObject(serverQUE, com.Config.Constants.RMI_PORT_NO_QUE);
			stubSHE = (ICentralizedServer) UnicastRemoteObject.exportObject(serverSHE, com.Config.Constants.RMI_PORT_NO_SHE);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
	}
	
	private static void registerServersToRMI()  {
		//Bind the remote objects to the RMI Registry
		Registry registryMTL,registryQUE,registrySHE;
		try {
			registryMTL = LocateRegistry.createRegistry(com.Config.Constants.RMI_PORT_NO_MTL);
			registryQUE = LocateRegistry.createRegistry(com.Config.Constants.RMI_PORT_NO_QUE);
			registrySHE = LocateRegistry.createRegistry(com.Config.Constants.RMI_PORT_NO_SHE);
			
			registryMTL.bind("MTL", stubMTL);
			registryQUE.bind("QUE", stubQUE);
			registrySHE.bind("SHE", stubSHE);
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
