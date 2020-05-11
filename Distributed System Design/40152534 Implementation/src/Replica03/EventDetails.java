package Replica03;

import java.util.LinkedList;
import java.util.List;

public class EventDetails {
	int bookingCapacity;
	int currentlyBooked;
	List<String> customersBooked = new LinkedList<String>();
		
	public EventDetails(int bookingCapacity, int currentlyBooked) {
		this.bookingCapacity = bookingCapacity;
		this.currentlyBooked = currentlyBooked;
	}
	
	public int getBookingCapacity() {
		return bookingCapacity;
	}
	public void setBookingCapacity(int bookingCapacity) {
		this.bookingCapacity = bookingCapacity;
	}
	
	public int getCurrentlyBooked() {
		return currentlyBooked;
	}
	public void setCurrentlyBooked(int currentlyBooked) {
		this.currentlyBooked = currentlyBooked;
	}

	@Override
	public String toString() {
		return "[bookingCapacity=" + bookingCapacity + ", currentlyBooked=" + currentlyBooked
				+ ", customersBooked=" + customersBooked + "]";
	}
}