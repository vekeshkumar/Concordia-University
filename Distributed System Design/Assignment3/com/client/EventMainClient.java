package com.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandles.Lookup;
import java.net.URL;
import java.rmi.NotBoundException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import com.web.server.Constants;
import com.web.server.EventMainServer;
import com.web.server.MultiThreadingImp;
import com.web.service.WebInterface;


public class EventMainClient {
	static final Logger log=Logger.getLogger(EventMainClient.class.getName());
	static FileHandler file1=null;	
	static String path="E:\\EclipseWorkspace\\WebServicesDEMS\\src\\com\\data";
	static String filename;
	static SimpleFormatter formatter = new SimpleFormatter();
	
	public static void main(String[] args) throws IOException, NotBoundException, ParseException {
		Scanner input=new Scanner(System.in);

		WebInterface serverLoc = null;
		URL url = null;
		QName Qname  = null;
		Service service = null;
		try 
		{
			String option,uid=null;
			System.out.println("Enter the operation");
			option=input.nextLine();
			System.out.println(option);
			uid  = input.nextLine();
			File folder = new File("E:\\EclipseWorkspace\\WebServicesDEMS\\src\\Data\\"+option+"\\"+uid);
			File[] listOfFiles = folder.listFiles();
			ArrayList<Thread> threads=new ArrayList<Thread>();
			ArrayList<MultiThreadingImp> messages=new ArrayList<MultiThreadingImp>();

			for(File file:listOfFiles)
			{
					//System.out.println("file name:"+file);
					ArrayList<String> lines = new ArrayList<String>();;
					
					 BufferedReader br = new BufferedReader(new FileReader(file));
					 String st; 
					 while ((st = br.readLine()) != null) 
					  {
					    lines.add(st); 
					  }
					String[] datas = lines.toArray(new String[lines.size()]);
					Qname = new QName("http://impl.service.web.com/","ServerImplementationService");					
					if(datas[0].contains("QUE"))
					{
						 url = new URL("http://localhost:"+Constants.WS_PORT_NO_QUE+"/Service?wsdl");
						service = Service.create(url, Qname);
						serverLoc = service.getPort(WebInterface.class);
					}
					else if(datas[0].contains("MTL"))
					{ 
						 url = new URL("http://localhost:"+Constants.WS_PORT_NO_MTL+"/Service?wsdl");
						service = Service.create(url, Qname);
						serverLoc = service.getPort(WebInterface.class);
					}
					else if(datas[0].contains("SHE"))
					{
						 url = new URL("http://localhost:"+Constants.WS_PORT_NO_SHE+"/Service?wsdl");
						service = Service.create(url, Qname);
						serverLoc = service.getPort(WebInterface.class);
					}	
					MultiThreadingImp threadobj=new MultiThreadingImp(serverLoc,datas);
					messages.add(threadobj);
					
					Thread t=new Thread(threadobj);
					threads.add(t);
					t.start();
					br.close();
			}
			for(int j=0;j<threads.size();j++)
			{
				while(threads.get(j).isAlive())
				{
					if(messages.get(j).msg!=null)
					{
						threads.get(j).interrupt();
					}
				}
			}
			
			for(int j=0;j<threads.size();j++)
			{
				filename=path+messages.get(j).datas[0]+".txt";
				file1=new FileHandler(filename,true);
				file1.setFormatter(formatter);
				log.addHandler(file1);
				log.setUseParentHandlers(false);
				log.info(messages.get(j).loginfo);
				file1.close();
				
			}
			
			
		}catch (Exception e) {
			System.out.println("Hello Client exception: " + e);
			e.printStackTrace();}
	}	
	public static boolean checkEventIdValidation(String eventId, String clientId) {
		boolean isValidEveId =false;
		Pattern eventPattern = Pattern.compile("([A-Z]{1,3})([M,A,E]{1})([0-9]{5,6})");
		if(eventId.substring(0, 2).equalsIgnoreCase(clientId.substring(0,2))) {
			Matcher eventIdPattern = eventPattern.matcher(eventId);
			if(eventIdPattern.matches()) {
				isValidEveId = true;
			}else {
				System.out.println("Please check your Event id format and re-enter it again");
			}
		}
		return isValidEveId;
	}
	
	
	
}
