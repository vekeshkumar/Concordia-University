package com.web.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import com.web.service.impl.ServerImplementation;


public class UDPServer extends Thread{
	
	DatagramSocket serverSocket = null;
	DatagramPacket receivePacket = null;
	DatagramPacket sendPacket = null;
	int portNumUDP;
	ServerImplementation serverImpl;	
	ServerCenterLocation scLocation;
	String recordDetails;
	
	public UDPServer(ServerCenterLocation scloc, ServerImplementation serverImp){
		scLocation = scloc;
		this.serverImpl = serverImp;
		
		try {
			switch(scloc) {
			case MTL:
				 serverSocket = new DatagramSocket(Constants.UDP_PORT_NO_MTL);
				 portNumUDP = Constants.UDP_PORT_NO_MTL;
				break;
			case QUE:
				 serverSocket = new DatagramSocket(Constants.UDP_PORT_NO_QUE);
				 portNumUDP = Constants.UDP_PORT_NO_QUE;
				break;
			case SHE:
				 serverSocket = new DatagramSocket(Constants.UDP_PORT_NO_SHE);
				 portNumUDP = Constants.UDP_PORT_NO_SHE;
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
