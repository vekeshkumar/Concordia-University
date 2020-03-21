package Dependencies;

import java.io.Serializable;

public class Result implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int seqNo;
	private String RMID; 
	private String requestMsg;
	private String response;
	
	//Constructor
	public Result(int seqNo, String RMID, String requestMsg, String response) {
		this.seqNo = seqNo;
		this.RMID = RMID;
		this.requestMsg = requestMsg;
		this.response = response;
	}

	//Getters and Setters
	public int getSeqNo() {
		return seqNo;
	}
	public void setSeqNo(int seqNo) {
		this.seqNo = seqNo;
	}

	public String getRMID() {
		return RMID;
	}
	public void setRMID(String rMID) {
		RMID = rMID;
	}

	public String getRequestMsg() {
		return requestMsg;
	}
	public void setRequestMsg(String requestMsg) {
		this.requestMsg = requestMsg;
	}

	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}

	@Override
	public String toString() {
		return " [seqNo=" + seqNo + ", RMID=" + RMID + ", requestMsg=" + requestMsg + ", response=" + response + "]";
	}
}