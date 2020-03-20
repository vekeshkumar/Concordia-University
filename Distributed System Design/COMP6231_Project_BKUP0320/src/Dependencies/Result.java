package Dependencies;

import java.io.Serializable;

public class Result implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	String RMID;
	String request;
	String response;
	
	//Constructor
	public Result(String RMID, String request, String response) {
		this.RMID = RMID;
		this.request = request;
		this.response = response;
	}

	//Getters and Setters
	public String getRMID() {
		return RMID;
	}

	public void setRMID(String rMID) {
		RMID = rMID;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	//toString
	@Override
	public String toString() {
		return "[RMID=" + RMID + ", request=" + request + ", response=" + response + "]";
	}
}