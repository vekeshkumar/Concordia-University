package Replicas;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import Implementation.EventClient;
import Implementation.EventMainClient;
import Implementation.ServerCenterLocation;
import Implementation.ServerImplementation;
 
public class RM2 implements Runnable {
	//Replica Manager
	//1.Receive request from the sequencer
	//2.Total Ordering - we need to add it in a queue
	//3.Multicast to other RMS
	
	public static ServerImplementation serverMTL,serverQUE,serverSHE;
	public static HashMap<String, ServerImplementation> serverRepo;
	//clientRecord Whole List
	String result;
	static HashMap<String, List<EventClient>> clientRecord;	
	String RMId = "RM02";
	/*
	 * RM ID
	 * Wait/Receiving Queue 
	 * Execution Queue -
	 * RM to RM Side
	 * 
	 */
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		RM2 obj = new RM2();
		ReceiveRequests RR = new ReceiveRequests(obj);
		Thread tRR = new Thread(RR);
		tRR.start();
		
		ExecuteRequests ER = new ExecuteRequests(obj);
		Thread tER = new Thread(ER);
		tER.start();
		
		Thread t= new Thread(obj);
	    t.start();
	}
	
	public void receiveUDPMessage(String ip, int port) throws IOException,NotBoundException, ParseException {
		byte[] buffer=new byte[1024];
		MulticastSocket socket=new MulticastSocket(port);
		InetAddress group=InetAddress.getByName(ip);
		socket.joinGroup(group);	
		Queue<String>  orderedMsg = new LinkedList<>();
		try {
			while(true){
				System.out.println("Waiting for multicast message...");
				DatagramPacket packet=new DatagramPacket(buffer,buffer.length);
				socket.receive(packet);
				
				String msg=new String(packet.getData());	
				if(msg.substring(0,2)!="RM") {
					//Cut the RMId
					msg = msg.substring(4);
					
					if(!orderedMsg.contains(msg)) {
						 orderedMsg.add(msg);
					 }					
				}else {
					//Coming from the sequencer
					 if(!orderedMsg.contains(msg)) { 
						  orderedMsg.add(msg);
						  //Sending to Other RM's
						  msg= RMId+msg;						  
						  sendUDPMessage(msg, "231.2.0.0",8001);
					  }		
				}			
				System.out.println("[Multicast UDP message received]>> "+msg);
				System.out.println("Waiting Queue - values");
				orderedMsg.stream().forEach(s->System.out.println(s));
				String[] msgSplit = msg.split("\\|");
				result=  EventMainClient.mainClient(msgSplit[0]);
				if(result!=null) {
					//Send result to the FE
					sendUDPMessage(orderedMsg.remove().toString(), "127.0.0.1",7421);
				}
				
				if("OK".equals(msg)) {
					System.out.println("No more message. Exiting : "+msg);				
				}
			}		
		}finally {
			socket.leaveGroup(group);
			socket.close();
		}
	}
	public static void sendUDPMessage(String message,String ipAddress, int port) throws IOException {
		DatagramSocket socket = new DatagramSocket();
		InetAddress group = InetAddress.getByName(ipAddress);
		byte[] msg = message.getBytes();
		DatagramPacket packet = new DatagramPacket(msg, msg.length,group, port);
		System.out.println("Message has been sent to other RM's");
		socket.send(packet);
		socket.close();
	}

	public void run(){
	   try {
		   //sequencer
	      receiveUDPMessage("230.1.0.0", 8000);	      
	   }catch(IOException  | ParseException | NotBoundException ex){ex.printStackTrace();}
	}
}


class ReceiveRequests implements Runnable{
	RM2 obj;
	
	public ReceiveRequests(RM2 obj) {
		this.obj = obj;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			obj.receiveUDPMessage("230.1.0.0", 8000);
		} catch (IOException | NotBoundException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
	}
}

class ExecuteRequests implements Runnable{
	RM2 obj;
	
	public ExecuteRequests(RM2 obj) {
		this.obj = obj;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
}