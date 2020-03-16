package Implementation;

import java.util.ArrayList;
import java.util.HashMap;

public class EventClient {
		//Client id, client type and Hashmap for storing details
	private String clientId;
	private String clientType;
	private ArrayList<String> bookedEventId;
	
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public String getClientType() {
		return clientType;
	}
	public void setClientType(String clientType) {
		this.clientType = clientType;
	}
	public ArrayList<String> getBookedEventId() {
		return bookedEventId;
	}
	public void setBookedEventId(ArrayList<String> bookedEventId) {
		this.bookedEventId = bookedEventId;
	}
	@Override
	public String toString() {
	// TODO Auto-generated method stub
	return "Client id"+clientId+"; Client Type "+clientType +" Booked Event id "+ bookedEventId.toString();
	}
}
