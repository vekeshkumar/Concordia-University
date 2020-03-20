package MiddleWare;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Sequencer {
	/*
	 * 1. Receive Uni cast request from FE.
	 * 2. Add Sequence number to the request.
	 * 3. Multi-cast to all RMs
	 */
	static int counter = 1000;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Sequencer is Ready and waiting..");
		
		//UDP Uni cast receive
		DatagramSocket aSocket = null;
		String strRequest = null;
		
		try {
			DatagramPacket request;
			aSocket = new DatagramSocket(8001);
			//byte[] buffer = new byte[1024];
			
			while(true) {
				//Receive Message
				byte[] buffer = new byte[1024];
				request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);
				
				//Determine the type of request received..
				strRequest = new String(request.getData()).trim();
				System.out.println("Request received at - " +new java.util.Date().toString() +" = " +strRequest);
				
				switch(strRequest.substring(0, strRequest.indexOf('|'))) {
				case "CLIENT_REQUEST":
					strRequest = strRequest +"|" + Integer.toString(generateSeq());
					break;
				case "PROCESS_CRASH":
					break;
				case "SOFTWARE_FAILURE":
					break;
				}
				UDPMulticastSend(strRequest,"230.1.0.0", 8002);
			}
		}catch (SocketException e) { System.out.println("Socket: " + e.getMessage()); }
		catch (IOException e) {System.out.println("IO: " + e.getMessage());} 
		finally {
			if (aSocket != null)
				aSocket.close();
		}
	}
	
	//Generate Sequence numbers.
	public static int generateSeq() {
		return ++counter;
	}
	
	//Multi cast to all RMs
	public static void UDPMulticastSend(String message,String ipAddress, int port) {
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket();
			InetAddress group = InetAddress.getByName(ipAddress);
			byte[] msg = message.getBytes();
			DatagramPacket packet = new DatagramPacket(msg, msg.length,group, port);
			socket.send(packet);
			
			System.out.println("Request - " +message +" has been Multicasted to the Group..(" +ipAddress +"/" +port+")");
		} catch (SocketException e) { System.out.println("Socket: " + e.getMessage());} 
		  catch (UnknownHostException e) {System.out.println("Unknown Host: " + e.getMessage());} 
		  catch (IOException e) { System.out.println("IO: " + e.getMessage());}
		finally{
			socket.close();
		}
	}
}