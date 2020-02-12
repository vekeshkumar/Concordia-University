package com.Server;
import java.util.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPIntReqClient extends Thread {
		private String  listEventAvailability;
		private ServerImplementation serverImp;
		
		public UDPIntReqClient(ServerImplementation serIp) {
			this.serverImp = serIp;
		}
		public String getRemoteListDetails() {
			// TODO Auto-generated method stub
			return listEventAvailability;
		}
		
		@Override
		public void run() {
			DatagramSocket socket = null;
			try {
				socket = new DatagramSocket();
				byte[] data = "GET_EVENT_LIST".getBytes();
				System.out.println(serverImp.location);
				DatagramPacket  packet = new DatagramPacket(data, data.length, InetAddress.getByName(serverImp.IPAddress), serverImp.udpServer.portNumUDP);
				socket.send(packet);
				data = new byte[1000];
				socket.receive(new DatagramPacket(data, data.length));
				listEventAvailability = serverImp.location+","+new String(data);	
				System.out.println(listEventAvailability);
			} catch (Exception e) {
				// TODO: handle exception
			}
			finally {
	            if (socket != null) {
	                socket.close();
	            }
	        }
		}
	
		
		

}
