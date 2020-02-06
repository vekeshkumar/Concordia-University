package com.Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;

import com.Config.ServerCenterLocation;

public class UDPIntReqServer extends Thread{
	
	DatagramSocket serverSocket;
	ServerCenterLocation location;
	private DatagramPacket receivePacket;
	private ServerImplementation server;
	
	

	public UDPIntReqServer(DatagramPacket dgPacket, ServerImplementation serverImpl) {
		// TODO Auto-generated constructor stub
		receivePacket  = dgPacket;
		server = serverImpl;
		try {
			serverSocket = new DatagramSocket();
		} catch (SocketException se) {
			// TODO: handle exception
			se.printStackTrace();
		}
	}
	
	public void run() {
		byte[] responseData;
		try {
			String  inputPacket =  new String(receivePacket.getData()).trim();
			responseData = getListEventAvailability().getBytes();
			serverSocket.send(new DatagramPacket(responseData, responseData.length,
				receivePacket.getAddress(),receivePacket.getPort()));
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	//List all events available for given eventType
	private String getListEventAvailability() {
		return null;
		
	}
	


	
	
}
