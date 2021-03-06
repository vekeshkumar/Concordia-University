package EventApp01;


/**
* EventApp01/EventServer01Operations.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from C:/Java/EclipseWorkspace/COMP6231_Project/Event01.idl
* Thursday, March 19, 2020 10:15:53 AM EDT
*/

public interface EventServer01Operations 
{
  String addEvent (String eventID, String eventType, int bookingCapacity);
  String removeEvent (String eventID, String eventType);
  String listEventAvailability (String eventType);
  String bookEvent (String customerID, String eventID, String eventType);
  String getBookingSchedule (String customerID);
  String cancelEvent (String customerID, String eventID, String eventType);
  String swapEvent (String customerID, String newEventID, String newEventType, String oldEventID, String oldEventType);
  void shutdown ();
} // interface EventServer01Operations
