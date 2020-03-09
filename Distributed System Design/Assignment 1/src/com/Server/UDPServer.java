package com.Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.Config.LogManager;
import com.Config.ServerCenterLocation;

public class UDPServer extends Thread{
	
	DatagramSocket serverSocket = null;
	DatagramPacket receivePacket = null;
	DatagramPacket sendPacket = null;
	int portNumUDP;
	ServerImplementation serverImpl;
	Logger loggerInstance;
	ServerCenterLocation scLocation;
	String recordDetails;
	
	public UDPServer(ServerCenterLocation scloc, Logger logger, ServerImplementation serverImp){
		scLocation = scloc;
		loggerInstance = logger;
		this.serverImpl = serverImp;
		
		try {
			switch(scloc) {
			case MTL:
				 serverSocket = new DatagramSocket(com.Config.Constants.UDP_PORT_NO_MTL);
				 portNumUDP = com.Config.Constants.UDP_PORT_NO_MTL;
				 logger.log(Level.INFO, "MTL UDP Server Started");
				break;
			case QUE:
				 serverSocket = new DatagramSocket(com.Config.Constants.UDP_PORT_NO_QUE);
				 portNumUDP = com.Config.Constants.UDP_PORT_NO_QUE;
				 logger.log(Level.INFO, "QUE UDP Server Started");
				break;
			case SHE:
				 serverSocket = new DatagramSocket(com.Config.Constants.UDP_PORT_NO_SHE);
				 portNumUDP = com.Config.Constants.UDP_PORT_NO_SHE;
				 logger.log(Level.INFO, "SHE UDP Server Started");
				break;
			}
			
		} catch (IOException io) {
			System.out.println(io.getMessage());
		}
	}
		@Override
		public void run() {
			byte[] receiveData;
			while (true) {
				try {
					receiveData = new byte[1024];
					receivePacket = new DatagramPacket(receiveData,receiveData.length);
					serverSocket.receive(receivePacket);
					String inputPckt = new String(receivePacket.getData()).trim();
					new UDPIntReqServer(receivePacket, serverImpl).start();
					//scLocation: should be other two location
					
				} catch (Exception e) {
					// TODO: handle exception
				}
				
			}
			
		}
	
}
