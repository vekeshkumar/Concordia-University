package Replica03;

public class CustDetails {
	String bookedDate;
	String status;
	
	public CustDetails( String bookedDate, String status) {
		this.bookedDate = bookedDate;
		this.status = status;
	}
	
	public String getBookedDate() {
		return bookedDate;
	}
	public void setBookedDate(String bookedDate) {
		this.bookedDate = bookedDate;
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "[bookedDate=" + bookedDate + ", status=" + status + "]";
	}
}