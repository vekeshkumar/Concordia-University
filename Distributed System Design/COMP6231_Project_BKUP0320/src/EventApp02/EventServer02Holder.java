package EventApp02;

/**
* EventApp02/EventServer02Holder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from C:/Java/EclipseWorkspace/COMP6231_Project/Event03.idl
* Thursday, March 19, 2020 10:27:37 AM EDT
*/

public final class EventServer02Holder implements org.omg.CORBA.portable.Streamable
{
  public EventApp02.EventServer02 value = null;

  public EventServer02Holder ()
  {
  }

  public EventServer02Holder (EventApp02.EventServer02 initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = EventApp02.EventServer02Helper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    EventApp02.EventServer02Helper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return EventApp02.EventServer02Helper.type ();
  }

}