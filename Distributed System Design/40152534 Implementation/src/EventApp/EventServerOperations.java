package EventApp;


/**
* EventApp/EventServerOperations.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from C:/Java/EclipseWorkspace/COMP6231_Project/Event.idl
* Wednesday, March 25, 2020 5:36:11 PM EDT
*/

public interface EventServerOperations 
{
  String addEvent (String eventID, String eventType, int bookingCapacity);
  String removeEvent (String eventID, String eventType);
  String listEventAvailability (String eventType);
  String bookEvent (String customerID, String eventID, String eventType);
  String getBookingSchedule (String customerID);
  String cancelEvent (String customerID, String eventID, String eventType);
  String swapEvent (String customerID, String newEventID, String newEventType, String oldEventID, String oldEventType);
  void shutdown ();
} // interface EventServerOperations