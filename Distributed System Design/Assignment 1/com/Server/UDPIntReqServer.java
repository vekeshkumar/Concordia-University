package com.Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.Config.ServerCenterLocation;
import com.Beans.Event;

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
	@Override
	public void run() {
		byte[] responseData;
		try {
			String  inputPacket =  new String(receivePacket.getData()).trim();
			if(inputPacket.equals("GET_EVENT_LIST")) {
				System.out.println("Got record count pkt");
			}
			responseData = getListEventAvailability().getBytes();
			serverSocket.send(new DatagramPacket(responseData, responseData.length,
				receivePacket.getAddress(),receivePacket.getPort()));
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	//List all events available for given eventType
	//Seminars: MTLE130320 3, SHEA060220 6, QUEM180230 0, MTLE190320 2 - based on eventType
	private String getListEventAvailability() {
		//get events not empty from all servers of same eventType
		
		String  eventList = null;
		printMap(server.eventRecordMTL);
		printMap(server.eventRecordQUE);
		printMap(server.eventRecordSHE);
		
		/*
		 * for (Entry<String, HashMap<String, List<Event>>> entry :
		 * server.eventRecordMTL.entrySet()) { HashMap<String,List<Event>> list =
		 * entry.getValue(); list.entrySet()
		 * System.out.println(entry.getKey()+" "+list.size()); }
		 */
		return "OKAKAKAKAKA";
		
		
		
	}
	
	public static void printMap(Map mp) {
	    Iterator it = mp.entrySet().iterator();
	    while (it.hasNext()) {	    	
	        Map.Entry pair = (Map.Entry)it.next();
	        System.out.println(pair.getKey()+",");
	       
	    }
	}
	


	
	
}
