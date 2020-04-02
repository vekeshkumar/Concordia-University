package Servers;

public class Constants {

	//RMI server Ports
	
	//Server Names of MONTREAL,QUEBEC,SHERBOOKE
	public static int RMI_PORT_NO_MTL =6667;
	public static int RMI_PORT_NO_QUE =7778;
	public static int RMI_PORT_NO_SHE =8889;
	
	//Server Addresses of MONTREAL,QUEBEC,SHERBOOKE
	public static String RMI_SERV_ADDR_MTL ="localhost";
	public static String RMI_SERV_ADDR_QUE ="localhost";
	public static String RMI_SERV_ADDR_SHE ="localhost";
	
	// UDP Server Ports of MONTREAL,QUEBEC,SHERBOOKE
	public static int UDP_PORT_NO_MTL =6001;
	public static int UDP_PORT_NO_QUE =7002;
	public static int UDP_PORT_NO_SHE =8003;
	
	//log directory
	public static String PROJECT_DIR = System.getProperty("user.dir");; //"E:\\EclipseWorkspace\\DistributedEventManagementSystem\\";
	public static String LOG_DIR = PROJECT_DIR+"\\Logs\\";
	
	
}
