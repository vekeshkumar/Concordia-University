package Dependencies;

import java.io.Serializable;

public class Request implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String requestType;   //CLIENT_REQUEST, PROCESS_CRASH, SOFTWARE_FAILURE
	private String source;
	private String requestMsg;
	private boolean isConsecutiveswFailure;
	private int seqNo;
	
	//Constructor..
	public Request() {
	}
	
	public Request(String requestType, String source, String requestMsg, boolean isConsecutiveswFailure, int seqNo) {
		this.requestType = requestType;
		this.source = source;
		this.requestMsg = requestMsg;
		this.isConsecutiveswFailure = isConsecutiveswFailure;
		this.seqNo = seqNo;
	}

	//Getters and Setters
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

	public int getSeqNo() {
		return seqNo;
	}
	public void setSeqNo(int seqNo) {
		this.seqNo = seqNo;
	}

	//toString
	@Override
	public String toString() {
		return " [requestType=" + requestType + ", source=" + source + ", requestMsg=" + requestMsg
				+ ", isConsecutiveswFailure=" + isConsecutiveswFailure + ", seqNo=" + seqNo + "]";
	}
}