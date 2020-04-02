package FrontEnd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandles.Lookup;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import DEMS_FrontEnd.DEMSInterface;
import DEMS_FrontEnd.DEMSInterfaceHelper;
import Servers.MultiThreadingImp;

public class EventMainClient {
	static final Logger log=Logger.getLogger(EventMainClient.class.getName());
	static FileHandler file1=null;	
	static String path="E:\\EclipseWorkspace\\DEMSCorba\\src\\Data\\C";
	static String filename;
	static SimpleFormatter formatter = new SimpleFormatter();
	
	public static void main(String[] args) throws IOException, NotBoundException, ParseException, NotFound, CannotProceed, InvalidName {
		Scanner input=new Scanner(System.in);
		try 
		{
			String option=null;
			String uid = null;
			System.out.println("Enter the operation");
			option=input.nextLine();
			System.out.println(option);
			uid  = input.nextLine();

			
			File folder = new File("E:\\EclipseWorkspace\\DEMSCorba\\src\\Data\\"+option+"\\"+uid);
			File[] listOfFiles = folder.listFiles();
			ArrayList<Thread> threads=new ArrayList<Thread>();
			ArrayList<MultiThreadingImp> messages=new ArrayList<MultiThreadingImp>();
			
			for(File file:listOfFiles)
			{
				ArrayList<String> lines = new ArrayList<String>();
				
				 BufferedReader br = new BufferedReader(new FileReader(file));
				 String st; 
				 while ((st = br.readLine()) != null) 
				  {
				    lines.add(st); 
				  }
				String[] datas = lines.toArray(new String[lines.size()]);	
				
				ORB orb = ORB.init(args, null);
				//-ORBInitialPort 1050 -ORBInitialHost localhost
				org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
				NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
				DEMSInterface obj = null;
	
				if(datas[0].contains("MTL"))
				{
					
					obj = (DEMSInterface) DEMSInterfaceHelper.narrow(ncRef.resolve_str("MTL"));
				}
				else if(datas[0].contains("QUE"))
				{
					obj = (DEMSInterface) DEMSInterfaceHelper.narrow(ncRef.resolve_str("QUE"));
				}
				else if(datas[0].contains("SHE"))
				{
					obj = (DEMSInterface) DEMSInterfaceHelper.narrow(ncRef.resolve_str("SHE"));
				}					
				
				MultiThreadingImp threadobj=new MultiThreadingImp(obj,datas);
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
}
