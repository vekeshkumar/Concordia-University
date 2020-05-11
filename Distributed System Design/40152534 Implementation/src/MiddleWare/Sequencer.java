package MiddleWare;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Dependencies.Network;
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
		Map<String, List<Network>> NetworkMap =  new HashMap<String, List<Network>>();
		String textline;
		String text[];
		List<Network> list = new ArrayList<Network>();
		
		//Read the Network file.
		File file = new File("Network.txt");
		try(BufferedReader br = new BufferedReader(new FileReader(file))){
			while((textline = br.readLine()) != null){ //assign and check
				if (textline.charAt(0) == '*') continue;
				text = textline.split("\\,");
				
				list = NetworkMap.get(text[0]);
				if (list == null) {
					list= new ArrayList<Network>();
					list.add(new Network(text[1],text[2],Integer.parseInt(text[3])));
				}
				else list.add(new Network(text[1],text[2],Integer.parseInt(text[3])));
				
				NetworkMap.put(text[0], list);
			}
		} catch (FileNotFoundException e1) {System.out.println("Can't open the file" +file.toString());}
		catch (IOException e1) {System.out.println("unable to read the file" +file.toString());}

		System.out.println("Sequencer is Ready and waiting..");
		
		//UDP Uni cast receive
		DatagramSocket aSocket = null;
		
		try {
			DatagramPacket request;
			aSocket = new DatagramSocket(NetworkMap.get("SEQUENCER").get(0).getRunningPort());
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
				
				UDPMulticastSend(requestObj,NetworkMap.get("SEQUENCER").get(1).getRunningIP(), NetworkMap.get("SEQUENCER").get(1).getRunningPort());
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