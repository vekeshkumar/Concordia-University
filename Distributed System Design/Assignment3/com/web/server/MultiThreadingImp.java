package com.web.server;

import java.text.ParseException;
import java.util.Arrays;
import com.web.service.WebInterface;

public class MultiThreadingImp implements Runnable{

	String managerID,customerID,eventID,eventtype,oldeventID,oldeventtype,neweventID,neweventtype,tempID,methodname;
	public String loginfo;
	public String msg;
	int capacity;
	String file;
	public String[] datas;
	static WebInterface obj;
	


	public MultiThreadingImp(WebInterface serverLoc,String[] datas)
	{
		this.obj=(WebInterface) serverLoc;
		this.datas=datas;
	}
	
	@Override
	public void run() 
	{
		if(datas[0].charAt(3)=='M')
		{
			managerID=datas[0];
			switch(datas[1])
			{
				case "addEvent":
					eventtype=datas[2];
					eventID=datas[3];
					capacity=Integer.parseInt(datas[4]);
					if(managerID.substring(0,3).equals(eventID.substring(0,3)))
					{
						msg=managerID+":\n"+obj.addEvent(eventID, eventtype, capacity);
						loginfo="\nEvent Type:"+eventtype+"\nEvent ID:"+eventID+"\nCapacity:"+capacity+"\n"+msg;
						System.out.println(msg);
					}
					else
					{
						msg=managerID+":\n"+"You can add events only to your city servers.";
						loginfo=msg;
						System.out.println(msg);
					}					
					break;
					
				case "removeEvent":
					eventtype=datas[2];
					eventID=datas[3];
					if(managerID.substring(0,3).equals(eventID.substring(0,3)))
					{
						try {
							msg=managerID+":\n"+obj.removeEvent(eventID, eventtype);
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						loginfo="\nEvent Type:"+eventtype+"\nEvent ID:"+eventID+"\n"+msg;
						System.out.println(msg);
					}
					else
					{
						msg=managerID+":\n"+"You can only remove events from your city server";
						loginfo=msg;
						System.out.println(msg);
					}
					
					break;
					
				case "listEventAvailability":
					eventtype=datas[2];
					msg=managerID+":\n"+obj.listEventAvailability(eventtype);
					loginfo="\nEvent Type:"+eventtype+"\nList of Events available";
					System.out.println(msg);
					break;
					
				case "customerOperation":
					customerID=datas[2];
					if(!(customerID.substring(0,3).equals(managerID.substring(0,3))))
					{
						msg=managerID+":\n"+"You can only perform operations for your city customers";
						loginfo=msg;
						System.out.println(msg);
						break;
					}
					else
					{						
						String temp = null;
						String[] data1=Arrays.copyOfRange(datas,2,datas.length+1);
						try {
							temp=customer(data1,obj);
						} catch (ParseException e) {
							
							e.printStackTrace();
						}
						msg=managerID+":"+temp.trim();
						System.out.println(msg.trim());
						loginfo="\nCustomer ID:"+customerID+"\nOperations performed".trim();
					}
			}
		}
		else if(datas[0].trim().charAt(3)=='C')
		{
			try {
				loginfo = customer(datas,obj);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			  
	}
	public static String customer(String[] datas,WebInterface obj) throws ParseException
	{		
		String custID,eventID,eventtype,oldeventID,oldeventtype,neweventID,neweventtype,msg,loginfo;
		custID=datas[0].trim();
		
		switch(datas[1])
		{
		case "bookEvent":
			eventtype=datas[2].trim();
			eventID=datas[3].trim();
			msg= obj.bookEvent(custID,eventID,eventtype);
			loginfo="\nCustomer ID:"+custID+"\nEvent Type:"+eventtype+"\nEvent ID:"+eventID+"\n"+msg.trim();
			System.out.println(custID+":"+msg);			
			return loginfo.trim();
			
		case "getBookingSchedule":
			msg= obj.getBookingSchedule(custID);
			loginfo="\nCustomer ID:"+custID+"\nSchedule";
			System.out.println(custID+":"+msg);
			return loginfo;
			
			
		case "cancelEvent":
			
			eventtype=datas[2];
			eventID=datas[3];
			msg= obj.cancelEvent(custID,eventID, eventtype);
			loginfo="\nCustomer ID:"+custID+"\nEvent Type:"+eventtype+"\nEvent ID:"+eventID+"\n"+msg.trim();
			System.out.println(custID+":"+msg);
			return loginfo;
			
		case "swapEvent":
			
			oldeventtype=datas[2];
			oldeventID=datas[3];
			neweventtype=datas[4];
			neweventID=datas[5];
			msg= obj.swapEvent(custID,neweventID,neweventtype,oldeventID,oldeventtype).trim();
			loginfo="\nCustomer ID:"+custID+"\nold Event Type:"+oldeventtype+"\nold Event ID:"+oldeventID+"\nnew Event Type:"+neweventtype+"\nnew Event ID:"+neweventID+"\n"+msg.trim();
			System.out.println(custID+":"+msg.trim());
			return loginfo.trim();
			
		}
		return "No Operations performed";
	}
}
	






