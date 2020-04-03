package com.web.server;
import java.util.*;
import java.util.logging.Handler;
import java.util.logging.Level;

import com.web.server.Constants;
import com.web.server.ServerCenterLocation;
import com.web.service.impl.ServerImplementation;

import java.io.*;
import java.net.MalformedURLException;
import java.rmi.RemoteException;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.ws.Endpoint;


@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)

public class EventMainServer   {
	
	static ServerImplementation serverMTL,serverQUE,serverSHE;
	public static HashMap<String, ServerImplementation> serverRepo;

	
	public static void main(String[] args) {

		try {
			initiate();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		 //LogManager logManager = new LogManager("ServerMain");
		 //logManager.logger.log(Level.INFO, "Server Ready! Listening on port :: " + com.Config.Constants.RMI_PORT_NO_MTL);
		
		
	}
	//Initiate method to set the variables
		public static void initiate() throws RemoteException{
			serverMTL = new ServerImplementation(ServerCenterLocation.MTL);
			serverQUE = new ServerImplementation(ServerCenterLocation.QUE);
			serverSHE = new ServerImplementation(ServerCenterLocation.SHE);
			Endpoint endpoint1 = Endpoint.publish("http://localhost:"+Constants.WS_PORT_NO_MTL+"/Service", serverMTL);
			Endpoint endpoint2 = Endpoint.publish("http://localhost:"+Constants.WS_PORT_NO_QUE+"/Service", serverQUE);
			Endpoint endpoint3= Endpoint.publish("http://localhost:"+Constants.WS_PORT_NO_SHE+"/Service", serverSHE);								
			serverRepo = new HashMap<>();
			serverRepo.put("MTL", serverMTL);
			serverRepo.put("QUE", serverQUE);
			serverRepo.put("SHE", serverSHE);
			System.out.println(serverRepo.toString());
			System.out.println(" Server Started...");
			
			boolean isMTLDir = new File(Constants.LOG_DIR+ServerCenterLocation.MTL.toString()).mkdir();
			boolean isQUEDir = new File(Constants.LOG_DIR+ServerCenterLocation.QUE.toString()).mkdir();
			boolean isSHEDir = new File(Constants.LOG_DIR+ServerCenterLocation.SHE.toString()).mkdir();
			boolean globalDir = new File(Constants.LOG_DIR+"ServerGlobal").mkdir();
		}
}
