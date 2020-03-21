package MiddleWare;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import Dependencies.Request;

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
		
		try {
			DatagramPacket request;
			aSocket = new DatagramSocket(8001);
			Request requestObj;
			
			while(true) {
				//Receive Message
				byte[] buffer = new byte[10000];
				request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);
				
				ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(buffer));
				requestObj = (Request) iStream.readObject();
				iStream.close();
				
				//Determine the type of request received..
				System.out.println("Request received at - " +new java.util.Date().toString() +" = " +requestObj);
				
				switch(requestObj.getRequestType()) {
				case "CLIENT_REQUEST":
					requestObj.setSeqNo(generateSeq());
					requestObj.setSource("SEQUENCER");
					break;
				case "PROCESS_CRASH":
					requestObj.setSeqNo(9999);
					requestObj.setSource("SEQUENCER");
					break;
				case "SOFTWARE_FAILURE":
					requestObj.setSeqNo(9998);
					requestObj.setSource("SEQUENCER");
					break;
				}
				
				UDPMulticastSend(requestObj,"230.1.0.0", 8002);
			}
		}catch (SocketException e) { System.out.println("Socket: " + e.getMessage()); }
		catch (IOException e) {System.out.println("IO: " + e.getMessage());} 
		catch (ClassNotFoundException e) {System.out.println("ClassNotFoundException in Sequencer.." +e.getMessage());} 
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
	public static void UDPMulticastSend(Request requestObj,String ipAddress, int port) {
		ByteArrayOutputStream bStream = new ByteArrayOutputStream();
		DatagramSocket socket = null;
		try {
			ObjectOutputStream oo = new ObjectOutputStream(bStream);
			oo.writeObject(requestObj);
			oo.close();
			
			byte[] serializedMessage = new byte[10000];
			serializedMessage = bStream.toByteArray();
			
			socket = new DatagramSocket();
			InetAddress group = InetAddress.getByName(ipAddress);
			DatagramPacket packet = new DatagramPacket(serializedMessage, serializedMessage.length, group, port);
			socket.send(packet);
			
			System.out.println("Request - " +requestObj +" has been Multicasted to the Group..(" +ipAddress +"/" +port+")");
			
		} catch (SocketException e) { System.out.println("Socket: " + e.getMessage());} 
		  catch (UnknownHostException e) {System.out.println("Unknown Host: " + e.getMessage());} 
		  catch (IOException e) { System.out.println("IO: " + e.getMessage());}
		finally{
			socket.close();
		}
	}
}