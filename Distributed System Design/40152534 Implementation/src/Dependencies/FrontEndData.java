package Dependencies;

import java.util.LinkedList;
import java.util.List;

public class FrontEndData {
	private int seqNo;
	private String requestStatus;
	private String correctResponse;
	private List<Long> timeList = new LinkedList<Long>();
	private String swFailedRMID;
	private List<String> crashedRMIDs = new LinkedList<String>();
	private List<Result> replyList = new LinkedList<Result>();
	
	//Constructor..
	public FrontEndData() {
	}

	public FrontEndData(int seqNo, String requestStatus, String correctResponse, List<Long> timeList,
			String swFailedRMID, List<String> crashedRMIDs, List<Result> replyList) {
		this.seqNo = seqNo;
		this.requestStatus = requestStatus;
		this.correctResponse = correctResponse;
		this.timeList = timeList;
		this.swFailedRMID = swFailedRMID;
		this.crashedRMIDs = crashedRMIDs;
		this.replyList = replyList;
	}
	
	//Getters and Setters..
	public int getSeqNo() {
		return seqNo;
	}
	public void setSeqNo(int seqNo) {
		this.seqNo = seqNo;
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
	public void setTimeListValue(long value) {
		this.timeList.add(value);
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
	
	public List<Result> getReplyList() {
		return replyList;
	}
	public void setReplyList(List<Result> replyList) {
		this.replyList = replyList;
	}
	public void setReplyListValue(Result value) {
		this.replyList.add(value);
	}
	
	//returns slowest time..
	public long getSlowestTime() {
		return timeList.get(timeList.size()-1);
	}
	
	//returns no.of replies so far..
	public int getCountOfReplies() {
		return replyList.size();
	}

	//toString..
	@Override
	public String toString() {
		return " [seqNo=" + seqNo + ", requestStatus=" + requestStatus
				+ ", correctResponse=" + correctResponse + ", timeList=" + timeList + ", swFailedRMID=" + swFailedRMID
				+ ", crashedRMIDs=" + crashedRMIDs + ", replyList=" + replyList + "]";
	}
}