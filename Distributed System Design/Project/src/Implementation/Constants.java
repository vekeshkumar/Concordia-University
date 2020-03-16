package Implementation;

public class Constants {

	//RMI server Ports
	
	//Server Names of MONTREAL,QUEBEC,SHERBOOKE
	public static int RMI_PORT_NO_MTL =6123;
	public static int RMI_PORT_NO_QUE =6124;
	public static int RMI_PORT_NO_SHE =6125;
	
	//Server Addresses of MONTREAL,QUEBEC,SHERBOOKE
	public static String RMI_SERV_ADDR_MTL ="localhost";
	public static String RMI_SERV_ADDR_QUE ="localhost";
	public static String RMI_SERV_ADDR_SHE ="localhost";
	
	// UDP Server Ports of MONTREAL,QUEBEC,SHERBOOKE
	public static int UDP_PORT_NO_MTL =6666;
	public static int UDP_PORT_NO_QUE =6667;
	public static int UDP_PORT_NO_SHE =6668;
	
	//log directory
	public static String PROJECT_DIR = System.getProperty("user.dir");; //"E:\\EclipseWorkspace\\DistributedEventManagementSystem\\";
	public static String LOG_DIR = PROJECT_DIR+"\\Logs\\";
	
	
}
