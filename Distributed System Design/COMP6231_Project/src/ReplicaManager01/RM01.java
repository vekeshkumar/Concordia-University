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
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.PriorityBlockingQueue;

import Dependencies.LogData;
import Dependencies.Request;
import Dependencies.Result;
import Replica01.EventManagement;
import Replica01.ReplicaServer;

public class RM01 {
	/*
	 * Replica Manager.. 1. Receive request from the Sequencer. 2. Receive request
	 * from other RMs. 3. Multi-Cast to other RMs. 4. Total Ordering - Add Unique
	 * requests to queue. 5. Call Replica for every request.
	 */

	PriorityBlockingQueue<Integer> requestQueue = new PriorityBlockingQueue<Integer>(10000, new SortRequests());
	Map<Integer, Request> requestMap = new TreeMap<Integer, Request>();
	Map<Integer, Object> executedMap = new TreeMap<Integer, Object>();

	//Get and store the data in case of Process crash..
	HashMap<String, ArrayList<HashMap<String, String>>> QUE_eventDB;
	HashMap<String, ArrayList<HashMap<String, String>>> MTL_eventDB; 
	HashMap<String, ArrayList<HashMap<String, String>>> SHE_eventDB; 
	HashMap<String, String> custDB;
			
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		RM01 obj = new RM01();
		String RMID = "RM01";
		int startingSeqNo = 1001;
		String RMpath =  "C:/Java/EclipseWorkspace/COMP6231_Project/src/ReplicaManager01/";

		System.out.println(RMID + " is Ready and Running...");
		
		new Thread(new ReceiveRequest(RMID, obj)).start();
		new Thread(new UDPUniCastReceive(RMID, obj)).start();
		new Thread(new ExecuteRequest(RMID, obj, startingSeqNo, RMpath)).start();

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
		Request requestObj;

		try {
			socket = new MulticastSocket(8002);
			group = InetAddress.getByName("230.1.0.0");
			socket.joinGroup(group);

			while (true) {
				System.out.println(RMID + " is waiting for Multicast message...");
				byte[] buffer = new byte[10000];
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

				// Receive Request
				socket.receive(packet);
				ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(buffer));
				requestObj = (Request) iStream.readObject();
				iStream.close();

				//Determine the type of request received..
				System.out.println("Multicast UDP message received>> " + requestObj);
				
				switch(requestObj.getRequestType()) {
				case "CLIENT_REQUEST":
					if (!isDuplicateRequest(requestObj.getSeqNo())) {
						obj.requestQueue.put(requestObj.getSeqNo());
						synchronized (obj) {
							obj.requestMap.put(requestObj.getSeqNo(), requestObj);
						}
					}
					
					// Determine whether received from Sequencer or RM.
					switch(requestObj.getSource()) {
					case "SEQUENCER":	
						requestObj.setSource(RMID);
						UDPMultiCastSendclass UDPMobj = new UDPMultiCastSendclass(requestObj, "230.1.0.0", 8002);
						UDPMobj.UDPMultiCastSend();
						break;
					default:
						//"RM01", "RM02","RM03"
						break;
					}
					break;
				default:
					//"PROCESS_CRASH", "SOFTWARE_FAILURE", "GET_LOGFILE"  
					obj.requestQueue.put(requestObj.getSeqNo());
					synchronized (obj) {
						obj.requestMap.put(requestObj.getSeqNo(), requestObj);
					}
					break;
				}

				// Display Queue Contents..
				System.out.println("requestQueue contents = " + obj.requestMap);
			}
		} catch (IOException | ClassNotFoundException e) {
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

	// Check whether the request is a duplicate request..
	public boolean isDuplicateRequest(int seqNo) {
		if (obj.requestMap.get(seqNo) != null) return true;
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
	String RMpath;

	public ExecuteRequest(String RMID, RM01 obj, int startingSeqNo, String RMpath) {
		this.RMID = RMID;
		this.obj = obj;
		this.startingSeqNo = startingSeqNo;
		this.RMpath = RMpath;
	}

	@Override
	public void run() {
		Request requestObj;
		Result resultObj;
		int seqNo = 0;
		

		while (true) {
			System.out.println(RMID + " is waiting to Execute a request...");
			
			// Read from Request Queue and Request Map..
			try {
				seqNo = obj.requestQueue.take();
				System.out.println("Removed the Head of the queue = " + seqNo);
			} catch (InterruptedException e) {System.out.println("take Interrupted while waiting.." + e.getMessage());}
			
			synchronized (obj) {
				requestObj = obj.requestMap.get(seqNo);
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
				String[] crashedRMs = requestObj.getRequestMsg().split("\\,");

				for (int i=0; i< crashedRMs.length; i++) {
					if (crashedRMs[i].equals(RMID)) {
						//a. Kill all the current Implementation Servers.
						//b. Restart the servers.
						
						//c. Read log file from any other RM...
						try {
							
							Request GET_LOGFILErequestObj = new Request();
							
							GET_LOGFILErequestObj.setRequestType("GET_LOGFILE");
							GET_LOGFILErequestObj.setRequestMsg(RMID);
							GET_LOGFILErequestObj.setSource(RMID);
							GET_LOGFILErequestObj.setSeqNo(9997);
							
							UDPMultiCastSendclass UDPMobj = new UDPMultiCastSendclass(GET_LOGFILErequestObj, "230.1.0.0", 8002);
							UDPMobj.UDPMultiCastSend();
						} catch (IOException e) {System.out.println("IO Exception in ExecuteRequest.. ");}						
						break;
					}
				}
				//resultObj = new Result(RMID, request, "PROCESS_CRASH received");
				//UDPUniCastSend(resultObj);
				break;
				
			case 9998:
				//SOFTWARE_FAILURE
				String failedRM = requestObj.getRequestMsg();
				EventManagement.sendCorrectResult = true;
				
//				if (requestObj.isConsecutiveswFailure() && failedRM.equals(RMID)) {
//					EventManagement.sendCorrectResult = true;
//				}
				
				resultObj = new Result(seqNo, RMID, requestObj.getRequestMsg(), "SOFTWARE_FAILURE received and corrected..");
				UDPUniCastSend(resultObj, "127.0.0.1", 8006);
				break;
				
			case 9997:
				//GET_LOGFILE
				try {
					HashMap<String, ArrayList<HashMap<String, String>>> QUE_eventDB = readDataBase(RMpath + "QUE_eventDB.txt");
					HashMap<String, ArrayList<HashMap<String, String>>> MTL_eventDB = readDataBase(RMpath + "MTL_eventDB.txt");
					HashMap<String, ArrayList<HashMap<String, String>>> SHE_eventDB = readDataBase(RMpath + "SHE_eventDB.txt");
					
					HashMap<String, String> QUE_custDB = readLOGFILE(RMpath + "QUE_custDB.txt");
					HashMap<String, String> MTL_custDB = readLOGFILE(RMpath + "MTL_custDB.txt");
					HashMap<String, String> SHE_custDB = readLOGFILE(RMpath + "SHE_custDB.txt");					
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
					System.out.println("Request " + requestObj + " is already executed..");
				} else {
					/*
					 * Check whether previous requests are executed or not. 
					 * 1. Previous requests in Executed Queue - Execute the current request.
					 * 2. Previous requests Not in Executed Queue but in Request Queue - Bring them and Execute. 
					 * 3. Previous requests Not in Executed Queue and Not in Request Queue - Just wait..
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
						System.out.println("Sending the message -" + requestObj + " from ExecuteRequest to Replica..");
						response = ReplicaClient.processRequest(requestObj.getRequestMsg());

						resultObj = new Result(seqNo, RMID, requestObj.getRequestMsg(), response);
						obj.executedMap.put(seqNo, resultObj);
						UDPUniCastSend(resultObj, "127.0.0.1", 8006);

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
									System.out.println("Sending the message -" + requestObj + " from ExecuteRequest to Replica..");
									response = ReplicaClient.processRequest(obj.requestMap.get(i).getRequestMsg());

									resultObj = new Result(seqNo, RMID, obj.requestMap.get(i).getRequestMsg(), response);
									obj.executedMap.put(i, resultObj);
									UDPUniCastSend(resultObj, "127.0.0.1", 8006);
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
	public void UDPUniCastSend(Object resultObj, String ipAddress, int Port) {
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
			oo.writeObject(resultObj);
			oo.close();
			
			byte[] serializedMessage = new byte[10000];
			serializedMessage = bStream.toByteArray();
			DatagramPacket request = new DatagramPacket(serializedMessage, serializedMessage.length, aHost, serverPort);
			aSocket.send(request);

			System.out.println("Response = " + resultObj + " - sent to the FE.");
		} catch (SocketException e) {System.out.println("Socket: " + e.getMessage());}
		catch (IOException e) {System.out.println("IO: " + e.getMessage());}
		finally {
			if (aSocket != null)
				aSocket.close();
		}
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

//UDPMultiCastSend to other RMs..
class UDPMultiCastSendclass {
	private Request requestObj;
	private String ipAddress;
	private int port;
	
	public UDPMultiCastSendclass(Request requestObj, String ipAddress, int port) {
		this.requestObj = requestObj;
		this.ipAddress = ipAddress;
		this.port = port;
	}

	public void UDPMultiCastSend() throws IOException {
		ByteArrayOutputStream bStream = new ByteArrayOutputStream();
		ObjectOutputStream oo = new ObjectOutputStream(bStream);
		oo.writeObject(requestObj);
		oo.close();
		
		byte[] serializedMessage = new byte[10000];
		serializedMessage = bStream.toByteArray();
		
		DatagramSocket socket = new DatagramSocket();
		InetAddress group = InetAddress.getByName(ipAddress);
		
		DatagramPacket packet = new DatagramPacket(serializedMessage, serializedMessage.length, group, port);
		socket.send(packet);
		socket.close();
		
		System.out.println("Request = " +requestObj +" - has been Multicasted to other RMs.");
	}
}

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