package Unicast;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;



public class UDPUnicastServer{

public static void main(String args[]){
DatagramSocket aSocket = null;
try{
aSocket = new DatagramSocket(8000);
byte[] buffer = new byte[1000];
while(true){
DatagramPacket request = new DatagramPacket(buffer, buffer.length);
aSocket.receive(request);

 System.out.println(new String(buffer) + " " + request.getPort());
 DatagramPacket reply = new DatagramPacket(request.getData(),request.getLength(), request.getAddress(), request.getPort());
 aSocket.send(reply);
   }
   }
catch (SocketException e){
System.out.println("Socket: " + e.getMessage());
}
catch (IOException e) {
System.out.println("IO: " + e.getMessage());
}
finally {
if(aSocket != null) aSocket.close();
}
}

}


