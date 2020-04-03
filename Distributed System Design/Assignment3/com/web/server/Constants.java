package com.web.server;

public class Constants {

	//RMI server Ports
	
	//Server Names of MONTREAL,QUEBEC,SHERBOOKE
	public static int WS_PORT_NO_MTL =6666;
	public static int WS_PORT_NO_QUE =7777;
	public static int WS_PORT_NO_SHE =8888;
	
	//Server Addresses of MONTREAL,QUEBEC,SHERBOOKE
	public static String WS_SERV_ADDR_MTL ="localhost";
	public static String WS_SERV_ADDR_QUE ="localhost";
	public static String WS_SERV_ADDR_SHE ="localhost";
	
	// UDP Server Ports of MONTREAL,QUEBEC,SHERBOOKE
	public static int UDP_PORT_NO_MTL =6000;
	public static int UDP_PORT_NO_QUE =7000;
	public static int UDP_PORT_NO_SHE =8000;
	
	//log directory
	public static String PROJECT_DIR = System.getProperty("user.dir");; //"E:\\EclipseWorkspace\\\WebServicesDEMS\\";
	public static String LOG_DIR = PROJECT_DIR+"\\Logs\\";
	
	
}
