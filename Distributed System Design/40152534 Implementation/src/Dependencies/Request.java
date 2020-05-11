package Dependencies;

import java.io.Serializable;

public class Request implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int seqNo;
	private String requestType;   //CLIENT_REQUEST, PROCESS_CRASH, SOFTWARE_FAILURE, GET_LOGFILE
	private String source;
	private String requestMsg;
	private boolean isConsecutiveswFailure;
	private boolean implProcessCrash;
	
	//Constructor..
	public Request() {
	}
	
	public Request(int seqNo, String requestType, String source, String requestMsg, boolean isConsecutiveswFailure, boolean implProcessCrash) {
		this.seqNo = seqNo;
		this.requestType = requestType;
		this.source = source;
		this.requestMsg = requestMsg;
		this.isConsecutiveswFailure = isConsecutiveswFailure;
		this.implProcessCrash = implProcessCrash;
	}

	//Getters and Setters
	public int getSeqNo() {
		return seqNo;
	}
	public void setSeqNo(int seqNo) {
		this.seqNo = seqNo;
	}
	
	public String getRequestType() {
		return requestType;
	}
	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}

	public String getRequestMsg() {
		return requestMsg;
	}
	public void setRequestMsg(String requestMsg) {
		this.requestMsg = requestMsg;
	}

	public boolean isConsecutiveswFailure() {
		return isConsecutiveswFailure;
	}
	public void setConsecutiveswFailure(boolean isConsecutiveswFailure) {
		this.isConsecutiveswFailure = isConsecutiveswFailure;
	}
	
	public boolean isImplProcessCrash() {
		return implProcessCrash;
	}
	public void setImplProcessCrash(boolean implProcessCrash) {
		this.implProcessCrash = implProcessCrash;
	}

	//toString
	@Override
	public String toString() {
		return " [seqNo=" + seqNo + ", requestType=" + requestType + ", source=" + source + ", requestMsg="
				+ requestMsg + ", isConsecutiveswFailure=" + isConsecutiveswFailure + ", implProcessCrash="
				+ implProcessCrash + "]";
	}
}