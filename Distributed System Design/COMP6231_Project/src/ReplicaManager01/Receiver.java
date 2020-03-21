package ReplicaManager01;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import Dependencies.LogData;


public class Receiver {

	public static void main(String[] args) {
		while (true) {
			DatagramSocket datagramsocket = null;
			try {
				datagramsocket = new DatagramSocket(9985);
				byte[] barray1 = new byte[10000];
				DatagramPacket packet = new DatagramPacket(barray1, barray1.length);
				datagramsocket.receive(packet);

				ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(barray1));
				System.out.println("iStream.getClass()= " +iStream.getClass());

				LogData logdataobj = (LogData) iStream.readObject();
				//String logdataobj = (String) iStream.readObject();
				//Integer logdataobj = (Integer) iStream.readObject();
				//Double logdataobj = (Double) iStream.readObject();
				
				//System.out.println("Log file received.. " + logdataobj);
				System.out.println("QUE_eventDB= "+ logdataobj.getQUE_eventDB());
				System.out.println("**************************************************");
				System.out.println("MTl_eventDB= "+ logdataobj.getMTL_eventDB());
				System.out.println("**************************************************");
				System.out.println("SHE_eventDB= "+ logdataobj.getSHE_eventDB());
				System.out.println("**************************************************");
				System.out.println("CUST_DB= "+ logdataobj.getCustDB());

				iStream.close();
			} catch (IOException | ClassNotFoundException e) {
				System.out.println("Exception in UDPUNIcast Receive..");
				e.printStackTrace();
			} finally {
				datagramsocket.close();
			}
		}
	}
}