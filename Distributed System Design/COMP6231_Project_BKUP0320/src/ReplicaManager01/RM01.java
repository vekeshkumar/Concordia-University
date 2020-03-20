package ReplicaManager01;

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
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;

import Dependencies.LogData;
import Dependencies.Result;
import Replica01.ReplicaServer;

public class RM01 {
	/*
	 * Replica Manager.. 1. Receive request from the Sequencer. 2. Receive request
	 * from other RMs. 3. Multi-Cast to other RMs. 4. Total Ordering - Add Unique
	 * requests to queue. 5. Call Replica for every request.
	 */

	PriorityBlockingQueue<Integer> requestQueue = new PriorityBlockingQueue<Integer>(10000, new SortRequests());
	Map<Integer, String> requestMap = new LinkedHashMap<Integer, String>();
	Map<Integer, Object> executedMap = new LinkedHashMap<Integer, Object>();

	//To store the data in case of Process crash..
	HashMap<String, ArrayList<HashMap<String, String>>> QUE_eventDB;
	HashMap<String, ArrayList<HashMap<String, String>>> MTL_eventDB; 
	HashMap<String, ArrayList<HashMap<String, String>>> SHE_eventDB; 
	HashMap<String, String> custDB;
			
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		RM01 obj = new RM01();
		String RMID = "RM01";
		int startingSeqNo = 1001;

		System.out.println(RMID + " is Ready and Running...");
		
		new Thread(new ReceiveRequest(RMID, obj)).start();
		new Thread(new UDPUniCastReceive(RMID, obj)).start();
		new Thread(new ExecuteRequest(RMID, obj, startingSeqNo)).start();

		new Thread(new StartServer("QUE", "8070")).start();
		new Thread(new StartServer("MTL", "8080")).start();
		new Thread(new StartServer("SHE", "8090")).start();
	}
}

//1. Receives Requests Thread - Puts in wait Queue...
class ReceiveRequest implements Runnable {
	String RMID;
	RM01 obj;

	public ReceiveRequest(String RMID, RM01 obj) {
		this.RMID = RMID;
		this.obj = obj;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		MulticastSocket socket = null;
		InetAddress group = null;
		String request = null;

		try {
			socket = new MulticastSocket(8002);
			group = InetAddress.getByName("230.1.0.0");
			socket.joinGroup(group);
			int seqNo;

			while (true) {
				System.out.println(RMID + " is waiting for Multicast message...");
				byte[] buffer = new byte[1024];
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

				// Receive Request
				socket.receive(packet);
				request = new String(packet.getData()).trim();

				System.out.println("Multicast UDP message received>> " + request);

				// Determine whether received from Sequencer or RM.
				if (request.substring(0, 2).equals("RM")) {
					// From Other RM. Remove RM ID and Insert in Wait Queue if not already exists.
					request = request.substring(5);
					if (!isDuplicateRequest(request)) {
						seqNo = Integer.parseInt(request.substring(request.indexOf('|') + 1));
						obj.requestQueue.put(seqNo);
						synchronized (obj) {
							obj.requestMap.put(seqNo, request.substring(0, request.indexOf('|')));
						}
					}
				} 
				else {
					// From Sequencer.. Determine what kind of request is it..

					switch (request.substring(0, request.indexOf('|'))) {
					case "CLIENT_REQUEST":
						request = request.substring(request.indexOf('|') + 1);

						// Insert in Wait Queue if not already exists.
						if (!isDuplicateRequest(request)) {
							seqNo = Integer.parseInt(request.substring(request.indexOf('|') + 1));
							obj.requestQueue.put(seqNo);
							synchronized (obj) {
								obj.requestMap.put(seqNo, request.substring(0, request.indexOf('|')));
							}
						}

						// Attach RM ID and send to other two RMs.
						request = RMID + "|" + request;
						UDPMultiCastSend(request, "230.1.0.0", 8002);
						break;
					case "PROCESS_CRASH":
						obj.requestQueue.put(9999);
						synchronized (obj) {
							obj.requestMap.put(9999, request.substring(request.indexOf('|'))+1);
						}
						break;
					case "SOFTWARE_FAILURE":
						obj.requestQueue.put(9998);
						synchronized (obj) {
							obj.requestMap.put(9998, request.substring(request.indexOf('|'))+1);
						}
						break;
					case "GET_LOGFILE":
						obj.requestQueue.put(9997);
						synchronized (obj) {
							obj.requestMap.put(9997, request.substring(request.indexOf('|'))+1);
						}
						break;
					}
				}

				// Display Queue Contents..
				System.out.println("requestQueue contents = " + obj.requestMap);
			}
		} catch (IOException e) {
			System.out.println("Exception in ReceiveRequest- " + e.getMessage());
		} finally {
			try {
				socket.leaveGroup(group);
				socket.close();
			} catch (IOException e) {
				System.out.println("Exception while leaving the Socket group in ReceiveRequest- " + e.getMessage());
			}
		}
	}

	// UDPMultiCastSend to other RMs..
	public void UDPMultiCastSend(String message, String ipAddress, int port) throws IOException {
		DatagramSocket socket = new DatagramSocket();
		InetAddress group = InetAddress.getByName(ipAddress);
		byte[] msg = message.getBytes();
		DatagramPacket packet = new DatagramPacket(msg, msg.length, group, port);
		System.out.println("Message has been sent to other RMs");
		socket.send(packet);
		socket.close();
	}

	// Check whether the request is a duplicate request..
	public boolean isDuplicateRequest(String request) {
		int seqNo;
		seqNo = Integer.parseInt(request.substring(request.indexOf('|') + 1));

		if (obj.requestMap.get(seqNo) != null || obj.executedMap.get(seqNo) != null) return true;
		else return false;
	}
}

//2. UDPUniCastReceive Thread to receive LogData object..
class UDPUniCastReceive implements Runnable{
	String RMID;
	RM01 obj;

	public UDPUniCastReceive(String RMID, RM01 obj) {
		this.RMID = RMID;
		this.obj = obj;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			DatagramSocket datagramsocket = null;
			try {
				datagramsocket = new DatagramSocket(9985);
				byte[] barray1 = new byte[10000];
				DatagramPacket packet = new DatagramPacket(barray1, barray1.length);
				datagramsocket.receive(packet);

				ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(barray1));
				LogData logdataobj = (LogData) iStream.readObject();
				iStream.close();
				
				System.out.println("Log file received.. " + logdataobj);
				
				//Put whatever received from other RM..
				synchronized(obj) {
					obj.QUE_eventDB = logdataobj.getQUE_eventDB();
					obj.MTL_eventDB = logdataobj.getMTL_eventDB();
					obj.SHE_eventDB = logdataobj.getSHE_eventDB();
					obj.custDB = logdataobj.getCustDB();
				}
				
			} catch (IOException | ClassNotFoundException e) {
				System.out.println("Exception in UDPUniCastReceive.." +e.getMessage());
			} finally {
				datagramsocket.close();
			}
		}
	}
}

//3. Execute Requests Thread -- Read from requestQueue...
class ExecuteRequest implements Runnable {
	String RMID;
	RM01 obj;
	int startingSeqNo;
	String response;

	public ExecuteRequest(String RMID, RM01 obj, int startingSeqNo) {
		this.RMID = RMID;
		this.obj = obj;
		this.startingSeqNo = startingSeqNo;
	}

	@Override
	public void run() {
		String request;
		int seqNo = 0;
		Result ResultObj;

		while (true) {
			System.out.println(RMID + " is waiting to Execute a request...");
			
			// Read from Request Queue and Request Map..
			try {
				seqNo = obj.requestQueue.take();
				System.out.println("Removed the Head of the queue = " + seqNo);
			} catch (InterruptedException e) {System.out.println("take Interrupted while waiting.." + e.getMessage());}
			
			synchronized (obj) {
				request = obj.requestMap.get(seqNo);
			}
			
			//Determine what request it is..
			switch(seqNo) {
			case 9999:
				/*
				 * PROCESS_CRASH
				 * 1. Check which RM is crashed.
				 * 2. If it is current RM, 
				 *   a. Kill all the current Implementation Servers.
				 *   b. Restart the servers.
				 *   c. Read log file from other RMs.
				 * 3. If it is other RM, Ignore.  
				 */
				String[] crashedRMs = request.split("\\,");

				for (int i=0; i< crashedRMs.length; i++) {
					if (crashedRMs[i].equals(RMID)) {
						//a. Kill all the current Implementation Servers.
						//b. Restart the servers.
						
						//c. Read log file from any other RM...
						try {
							UDPMultiCastSend("GET_LOGFILE", "230.1.0.0", 8002);
						} catch (IOException e) {System.out.println("IO Exception in ExecuteRequest.. ");}						
						break;
					}
				}
				//ResultObj = new Result(RMID, request, "PROCESS_CRASH received");
				//UDPUniCastSend(ResultObj);
				break;
			case 9998:
				//SOFTWARE_FAILURE
				ResultObj = new Result(RMID, request, "SOFTWARE_FAILURE received");
				//UDPUniCastSend(ResultObj);
				break;
			case 9997:
				//GET_LOGFILE
				try {
					HashMap<String, ArrayList<HashMap<String, String>>> QUE_eventDB = readDataBase("QUE_eventDB.txt");
					HashMap<String, ArrayList<HashMap<String, String>>> MTL_eventDB = readDataBase("MTL_eventDB.txt");
					HashMap<String, ArrayList<HashMap<String, String>>> SHE_eventDB = readDataBase("SHE_eventDB.txt");
					
					HashMap<String, String> QUE_custDB = readLOGFILE("QUE_custDB.txt");
					HashMap<String, String> MTL_custDB = readLOGFILE("MTL_custDB.txt");
					HashMap<String, String> SHE_custDB = readLOGFILE("SHE_custDB.txt");
					
					HashMap<String, String> custDB = new HashMap<String, String>();
					
					//Merge all custDB..
					custDB.putAll(QUE_custDB);
					custDB.putAll(MTL_custDB);
					custDB.putAll(SHE_custDB);
					
					//Put in Object..
					LogData logdataobj = new LogData();
					
					logdataobj.setQUE_eventDB(QUE_eventDB);
					logdataobj.setMTL_eventDB(MTL_eventDB);
					logdataobj.setSHE_eventDB(SHE_eventDB);
					logdataobj.setCustDB(custDB);
					
					//Pass the object..
					System.out.println("Sending Log file..." +logdataobj);
					UDPUniCastSend(logdataobj, "127.0.0.1", 8006);
					obj.executedMap.put(9997, logdataobj);
					
				} catch (IOException e1) { System.out.println("IO Exception while reading Log files.." + e1.getMessage());}
				break;
			default:
				//CLIENT_REQUEST -- Check whether the request is already executed..
				if (obj.executedMap.get(seqNo) != null) {
					System.out.println("Request " + request + " is already executed..");
				} else {
					/*
					 * Check whether previous requests are executed or not. 1. Previous requests in
					 * Executed Queue - Execute the current request. 2. Previous requests Not in
					 * Executed Queue but in Request Queue - Bring them and Execute. 3. Previous
					 * requests Not in Executed Queue and Not in Request Queue - Just wait..
					 */

					// Check whether previous requests are executed or not.
					char allPrevExectd = 'Y';
					for (int i = seqNo - 1; i >= startingSeqNo; i--) {
						if (obj.executedMap.get(i) == null) {
							allPrevExectd = 'N';
							break;
						}
					}

					// 1. Previous requests in Executed Queue - Execute the current request.
					if (allPrevExectd == 'Y') {
						System.out.println("Sending the message -" + request + " from ExecuteRequest to Replica..");
						response = ReplicaClient.processRequest(request);

						ResultObj = new Result(RMID, request, response);
						obj.executedMap.put(seqNo, ResultObj);
						UDPUniCastSend(ResultObj, "127.0.0.1", 8006);

					} else {
						char allPrevRcvd = 'Y';
						for (int i = seqNo - 1; i >= startingSeqNo; i--) {
							if (obj.requestMap.get(i) == null) {
								allPrevRcvd = 'N';
								break;
							}
						}

						// 2. Previous requests Not in Executed Queue but in Request Queue - Bring them
						// and Execute.
						if (allPrevRcvd == 'Y') {
							for (int i = seqNo - 1; i >= startingSeqNo; i--) {
								if (obj.requestMap.get(i) != null) {
									System.out.println("Sending the message -" + request + " from ExecuteRequest to Replica..");
									response = ReplicaClient.processRequest(obj.requestMap.get(i));

									ResultObj = new Result(RMID, obj.requestMap.get(i), response);
									obj.executedMap.put(i, ResultObj);
									UDPUniCastSend(ResultObj, "127.0.0.1", 8006);
								}
							}
						} else {
							// 3. Previous requests Not in Executed Queue and Not in Request Queue - Insert
							// back and Just wait..
							obj.requestQueue.put(seqNo);
							try {
								Thread.sleep(3000);
							} catch (InterruptedException e) {
								System.out.println("Interrupted while sleeping.. " + e.getMessage());
							}
						}
					} // end of else
				} // end of else
			}//end of Switch
		} // end of while
	}// end of run

	// Send to FE
	public void UDPUniCastSend(Object ResultObj, String ipAddress, int Port) {
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
			oo.writeObject(ResultObj);
			oo.close();

			byte[] serializedMessage = bStream.toByteArray();
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
	
	// UDPMultiCastSend to other RMs..
	public void UDPMultiCastSend(String message, String ipAddress, int port) throws IOException {
		DatagramSocket socket = new DatagramSocket();
		InetAddress group = InetAddress.getByName(ipAddress);
		byte[] msg = message.getBytes();
		DatagramPacket packet = new DatagramPacket(msg, msg.length, group, port);
		System.out.println("Message has been sent to other RMs");
		socket.send(packet);
		socket.close();
	}
	
	//Reads all custDB LOG Files..
	public HashMap<String, String> readLOGFILE(String filename){
		
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
	
	//Reads all eventDB LOG Files..
	public HashMap<String, ArrayList<HashMap<String, String>>> readDataBase(String filename) throws IOException {
		FileReader fileReader = new FileReader(filename);
		@SuppressWarnings("resource")
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		HashMap<String, ArrayList<HashMap<String, String>>> records = new HashMap<String, ArrayList<HashMap<String, String>>>();
		
		String line = null;
		ArrayList<HashMap<String, String>> conferenceDetails = new ArrayList<HashMap<String, String>>();
		ArrayList<HashMap<String, String>> seminarDetails = new ArrayList<HashMap<String, String>>();
		ArrayList<HashMap<String, String>> tradeShowDetails = new ArrayList<HashMap<String, String>>();
		
		while ((line = bufferedReader.readLine()) != null && line.length() > 0) {
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
	
}// end of ExecuteRequest class

//Start Servers..
class StartServer implements Runnable {
	String[] sendargs = new String[2];

	public StartServer(String server, String port) {
		this.sendargs[0] = server;
		this.sendargs[1] = port;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		ReplicaServer.main(sendargs);
	}
}

//Comparator to compare Integers 
class SortRequests implements Comparator<Integer> {
	public int compare(Integer request1, Integer request2) {
		return request1.compareTo(request2);
	}
}