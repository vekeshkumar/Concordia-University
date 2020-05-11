package Dependencies;

public class Network {
	String UDPCall;
	String RunningIP;
	int RunningPort;
	
	//Constructor
	public Network() {
	}
	
	public Network(String uDPCall, String runningIP, int runningPort) {
		UDPCall = uDPCall;
		RunningIP = runningIP;
		RunningPort = runningPort;
	}

	//Getters and Setters..
	public String getUDPCall() {
		return UDPCall;
	}
	public void setUDPCall(String uDPCall) {
		UDPCall = uDPCall;
	}

	public String getRunningIP() {
		return RunningIP;
	}
	public void setRunningIP(String runningIP) {
		RunningIP = runningIP;
	}

	public int getRunningPort() {
		return RunningPort;
	}
	public void setRunningPort(int runningPort) {
		RunningPort = runningPort;
	}

	//toString
	@Override
	public String toString() {
		return " [UDPCall=" + UDPCall + ", RunningIP=" + RunningIP + ", RunningPort=" + RunningPort + "]";
	}
}