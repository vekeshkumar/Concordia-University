package EventApp03;

/**
* EventApp03/EventServer03Holder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from C:/Java/EclipseWorkspace/COMP6231_Project/Event03.idl
* Thursday, March 19, 2020 10:28:00 AM EDT
*/

public final class EventServer03Holder implements org.omg.CORBA.portable.Streamable
{
  public EventApp03.EventServer03 value = null;

  public EventServer03Holder ()
  {
  }

  public EventServer03Holder (EventApp03.EventServer03 initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = EventApp03.EventServer03Helper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    EventApp03.EventServer03Helper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return EventApp03.EventServer03Helper.type ();
  }

}
