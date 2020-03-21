package Dependencies;

import java.util.LinkedList;
import java.util.List;

public class FrontEndData {
	private int seqNo;
	private String requestMsg;
	private String requestStatus;
	private String correctResponse;
	private List<Long> timeList = new LinkedList<Long>();
	private String swFailedRMID;
	private List<String> crashedRMIDs = new LinkedList<String>();
	
	//Constructor..
	public FrontEndData() {
	}

	public FrontEndData(int seqNo, String requestMsg, String requestStatus, String correctResponse, List<Long> timeList,
			String swFailedRMID, List<String> crashedRMIDs) {
		this.seqNo = seqNo;
		this.requestMsg = requestMsg;
		this.requestStatus = requestStatus;
		this.correctResponse = correctResponse;
		this.timeList = timeList;
		this.swFailedRMID = swFailedRMID;
		this.crashedRMIDs = crashedRMIDs;
	}
	
	//Getters and Setters..
	public int getSeqNo() {
		return seqNo;
	}
	public void setSeqNo(int seqNo) {
		this.seqNo = seqNo;
	}

	public String getRequestMsg() {
		return requestMsg;
	}
	public void setRequestMsg(String requestMsg) {
		this.requestMsg = requestMsg;
	}

	public String getRequestStatus() {
		return requestStatus;
	}
	public void setRequestStatus(String requestStatus) {
		this.requestStatus = requestStatus;
	}

	public String getCorrectResponse() {
		return correctResponse;
	}
	public void setCorrectResponse(String correctResponse) {
		this.correctResponse = correctResponse;
	}

	public List<Long> getTimeList() {
		return timeList;
	}
	public void setTimeList(List<Long> timeList) {
		this.timeList = timeList;
	}

	public String getSwFailedRMID() {
		return swFailedRMID;
	}
	public void setSwFailedRMID(String swFailedRMID) {
		this.swFailedRMID = swFailedRMID;
	}

	public List<String> getCrashedRMIDs() {
		return crashedRMIDs;
	}
	public void setCrashedRMIDs(List<String> crashedRMIDs) {
		this.crashedRMIDs = crashedRMIDs;
	}
	
	//toString..
	@Override
	public String toString() {
		return " [seqNo=" + seqNo + ", requestMsg=" + requestMsg + ", requestStatus=" + requestStatus
				+ ", correctResponse=" + correctResponse + ", timeList=" + timeList + ", swFailedRMID=" + swFailedRMID
				+ ", crashedRMIDs=" + crashedRMIDs + "]";
	}
}