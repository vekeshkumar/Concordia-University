package ReplicaManager01;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;

import Dependencies.LogData;

public class Test {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		HashMap<String, ArrayList<HashMap<String, String>>> QUE_eventDB = readDataBase("C:/Java/EclipseWorkspace/COMP6231_Project/src/ReplicaManager01/QUE_eventDB.txt");
		HashMap<String, ArrayList<HashMap<String, String>>> MTL_eventDB = readDataBase("C:/Java/EclipseWorkspace/COMP6231_Project/src/ReplicaManager01/MTL_eventDB.txt");
		HashMap<String, ArrayList<HashMap<String, String>>> SHE_eventDB = readDataBase("C:/Java/EclipseWorkspace/COMP6231_Project/src/ReplicaManager01/SHE_eventDB.txt");
		
		HashMap<String, String> QUE_custDB = readLOGFILE("C:/Java/EclipseWorkspace/COMP6231_Project/src/ReplicaManager01/QUE_custDB.txt");
		HashMap<String, String> MTL_custDB = readLOGFILE("C:/Java/EclipseWorkspace/COMP6231_Project/src/ReplicaManager01/MTL_custDB.txt");
		HashMap<String, String> SHE_custDB = readLOGFILE("C:/Java/EclipseWorkspace/COMP6231_Project/src/ReplicaManager01/SHE_custDB.txt");
		
		HashMap<String, String> custDB = new HashMap<String, String>();
		
		custDB.putAll(QUE_custDB);
		custDB.putAll(MTL_custDB);
		custDB.putAll(SHE_custDB);
		
		//Put in Object..
		LogData logdataobj = new LogData();
		
		logdataobj.setQUE_eventDB(QUE_eventDB);
		logdataobj.setMTL_eventDB(MTL_eventDB);
		logdataobj.setSHE_eventDB(SHE_eventDB);
		
		logdataobj.setCustDB(custDB);
		
//		Pass the object..
//		System.out.println("Sending Log file..." +logdataobj);
//		System.out.println("QUE_eventDB= "+ logdataobj.getQUE_eventDB());
//		System.out.println("**************************************************");
//		System.out.println("MTl_eventDB= "+ logdataobj.getMTL_eventDB());
//		System.out.println("**************************************************");
//		System.out.println("SHE_eventDB= "+ logdataobj.getSHE_eventDB());
//		System.out.println("**************************************************");
//		System.out.println("CUST_DB= "+ logdataobj.getCustDB());
		
		UDPUniCastSend(logdataobj, "127.0.0.1", 9985);
		//UDPUniCastSend("String Test..", "127.0.0.1", 9985);
		//UDPUniCastSend(12.456, "127.0.0.1", 9985);
		
	}
	
	public static HashMap<String, ArrayList<HashMap<String, String>>> readDataBase(String filename) throws IOException {
		FileReader fileReader = new FileReader(filename);
		@SuppressWarnings("resource")
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		HashMap<String, ArrayList<HashMap<String, String>>> records = new HashMap<String, ArrayList<HashMap<String, String>>>();
		
		String line = null;
		ArrayList<HashMap<String, String>> conferenceDetails = new ArrayList<HashMap<String, String>>();
		ArrayList<HashMap<String, String>> seminarDetails = new ArrayList<HashMap<String, String>>();
		ArrayList<HashMap<String, String>> tradeShowDetails = new ArrayList<HashMap<String, String>>();
		
		while ((line = bufferedReader.readLine()) != null && line.length() > 0) {
			System.out.println("line= " + line);
			String[] str = line.split(" ");
			String key = str[0];
			HashMap<String, String> eventDetails = new HashMap<String, String>();
			String[] str2 = str[1].split("--");
			eventDetails.put(str2[0], str2[1]);

			if (key.equals("Conference")) {
				conferenceDetails.add(eventDetails);
			} else if (key.equals("TradeShow")) {
				tradeShowDetails.add(eventDetails);
			} else if (key.equals("Seminar")) {
				seminarDetails.add(eventDetails);
			}

		}
		records.put("Conference", conferenceDetails);
		records.put("TradeShow", tradeShowDetails);
		records.put("Seminar", seminarDetails);
		return records;
	}
	
	//Reads all LOG Files..
		public static HashMap<String, String> readLOGFILE(String filename){
			
			String textline;
			String[] textfields = new String[2];
			
			HashMap<String, String> DB = new HashMap<String, String>();
			
			File file = new File(filename);
			try(BufferedReader br = new BufferedReader(new FileReader(file))){
				while((textline = br.readLine()) != null){ //assign and check
					
					textfields = textline.split(" ");
					DB.put(textfields[0], textfields[1].substring(0, (textfields[1].length() - 1)));
				}
			} catch (FileNotFoundException e1) {System.out.println("Can't open the file" +file.toString());}
			  catch (IOException e1) {System.out.println("unable to read the file" +file.toString());}		
			
			return DB; 
		}
		
		
		// Send to FE
		public static void UDPUniCastSend(Object ResultObj, String ipAddress, int Port) {
			ByteArrayOutputStream bStream = new ByteArrayOutputStream();

			// get the byte array of the object
			DatagramSocket aSocket = null;
			InetAddress aHost;
			int serverPort;
			try {
				aSocket = new DatagramSocket(8003);
				aHost = InetAddress.getByName(ipAddress);
				serverPort = Port;

				ObjectOutputStream oo = new ObjectOutputStream(bStream);
				System.out.println("Sending = " +ResultObj);
				oo.writeObject(ResultObj);
				oo.close();

				byte[] serializedMessage = new byte[10000];
				
				serializedMessage = bStream.toByteArray();
				DatagramPacket request = new DatagramPacket(serializedMessage, serializedMessage.length, aHost, serverPort);
				aSocket.send(request);

				System.out.println("Response = " + ResultObj + " - sent to the FE.");
			} catch (SocketException e) {System.out.println("Socket: " + e.getMessage());}
			catch (IOException e) {System.out.println("IO: " + e.getMessage());}
			finally {
				if (aSocket != null)
					aSocket.close();
			}
		}	
}
