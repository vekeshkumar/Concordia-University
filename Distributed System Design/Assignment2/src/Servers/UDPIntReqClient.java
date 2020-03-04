package Servers;
import java.util.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPIntReqClient extends Thread {
		private String  listEventAvailability="";
		private ServerImplementation serverImp;
		private String requestedEventType;
		private String customerId;
		private String eventId;
		private String eventType;
		private String msg;
		private String functionType;
		private String customerEvents;
		public UDPIntReqClient(ServerImplementation serIp, String eventType,String functionType) {
			this.serverImp = serIp;
			this.requestedEventType = eventType;
			this.functionType= functionType;
		}
		public  UDPIntReqClient(ServerImplementation serverImplementation, String eventType, String eventId,
				String customerId, String   functionType) {
			// TODO Auto-generated constructor stub
			this.serverImp = serverImplementation;
			this.customerId = customerId;
			this.serverImp = serverImplementation;
			this.eventId = eventId;
			this.eventType = eventType;
			this.functionType = functionType;
			
		}
		public String returnBookEventMsg() {
			return msg;
		}
		
		public String getRemoteListDetails() {
			// TODO Auto-generated method stub
			return listEventAvailability;
		}
		public String returnCustomerMsg(){
			return customerEvents;
		}
		
		@Override
		public void run() {
			DatagramSocket socket = null;
			try {
				socket = new DatagramSocket();
				String typeLoc = null;
				//For List event availability
				if(functionType.equalsIgnoreCase("listing")) {
					typeLoc = (requestedEventType+","+serverImp.location+","+functionType);
				}else if(functionType.equalsIgnoreCase("book") || functionType.equalsIgnoreCase("cancel") ) {
					typeLoc =(eventType+","+customerId+","+eventId+","+serverImp.location+","+functionType);
				}else if(functionType.equalsIgnoreCase("customerBookedEvents")){
					typeLoc =(serverImp.location+","+requestedEventType+","+functionType);
				}				
				//For Book event
				byte[] data = typeLoc.getBytes();
				DatagramPacket  packet = new DatagramPacket(data, data.length, InetAddress.getByName(serverImp.IPAddress), serverImp.udpServer.portNumUDP);
				socket.send(packet);
				data = new byte[1000];
				socket.receive(new DatagramPacket(data, data.length));
  				String value = new String(data);
  				System.out.println("UDP Client"+value);
  				listEventAvailability += serverImp.location+"-"+value+"\n";	
  				System.out.println("UDP Client response-"+value);
  				System.out.println(serverImp.location+"--"+value);
  				msg = value;
  				
				
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
