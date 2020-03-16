package Multicast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPMulticastServer {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			sendUDPMessage("This is a multicast messge", "230.2.0.0",8000);
			sendUDPMessage("This is the second multicast messge","230.2.0.0", 8000);
			sendUDPMessage("This is the third multicast messge","230.2.0.0", 8000);
			sendUDPMessage("OK", "230.2.0.0", 8000);
		} 
		catch (IOException e) {e.printStackTrace();}
	}
	
	public static void sendUDPMessage(String message,String ipAddress, int port) throws IOException {
		DatagramSocket socket = new DatagramSocket();
		InetAddress group = InetAddress.getByName(ipAddress);
		byte[] msg = message.getBytes();
		DatagramPacket packet = new DatagramPacket(msg, msg.length,group, port);
		socket.send(packet);
		socket.close();
	}
}