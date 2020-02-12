package com.Beans;

import java.io.Serializable;
import java.util.*;
public class Event implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String eventId;
	private String eventType;
	private Date eventDate;
	private int bookingCapacity;
	private String bookingTime;
	private ArrayList<String> bookedEvent;
	
	private HashMap<ArrayList<String>, HashMap<ArrayList<String>, ArrayList<String>>> cityHashMap = new HashMap<>();
	
	public String getId() {
		return eventId;
	}
	public void setId(String id) {
		this.eventId = id;
	}
	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	public Date getEventDate() {
		return eventDate;
	}
	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}
	public int getBookingCapacity() {
		return bookingCapacity;
	}
	public void setBookingCapacity(int bookingCapacity) {
		this.bookingCapacity = bookingCapacity;
	}
	public HashMap<ArrayList<String>, HashMap<ArrayList<String>, ArrayList<String>>> getCityHashMap() {
		return cityHashMap;
	}
	public void setCityHashMap(HashMap<ArrayList<String>, HashMap<ArrayList<String>, ArrayList<String>>> cityHashMap) {
		this.cityHashMap = cityHashMap;
	}
	public ArrayList<String> getBookedEvent() {
		return bookedEvent;
	}
	public void setBookedEvent(ArrayList<String> bookedEvent) {
		this.bookedEvent = bookedEvent;
	}
	public String getBookingTime() {
		return bookingTime;
	}
	public void setBookingTime(String bookingTime) {
		this.bookingTime = bookingTime;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Event Id="+eventId+"Event Type="+eventType+",Event Capacity"+bookingCapacity;
	}
}
