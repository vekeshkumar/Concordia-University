package Unicast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Scanner;
public class UDPUnicast {
	
	public static void main(String args[]){
		DatagramSocket aSocket = null;
		Scanner s = new Scanner(System.in);

		while(true) {
		try {
		String dataString = s.nextLine();
		aSocket = new DatagramSocket(8000);

		byte [] m = dataString.getBytes();
		InetAddress aHost = InetAddress.getByName("192.168.43.71");
		int serverPort = 8000;
		DatagramPacket request = new DatagramPacket(m, dataString.length(), aHost, serverPort);
		aSocket.send(request);

		byte[] buffer = new byte[1000];
		DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
		aSocket.receive(reply);
		System.out.println(new String(reply.getData()));

		}
		catch (SocketException e){
		System.out.println("Socket: " + e.getMessage());
		}
		catch (IOException e){
		System.out.println("IO: " + e.getMessage());
		}
		finally {
		if(aSocket != null)  aSocket.close();
		   }
		}


		   }
		}









