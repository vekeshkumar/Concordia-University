package FrontEndApp;


/**
* FrontEndApp/FrontEndServerPOA.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from C:/Java/EclipseWorkspace/COMP6231_Project/FrontEnd.idl
* Tuesday, April 7, 2020 2:12:00 PM EDT
*/

public abstract class FrontEndServerPOA extends org.omg.PortableServer.Servant
 implements FrontEndApp.FrontEndServerOperations, org.omg.CORBA.portable.InvokeHandler
{

  // Constructors

  private static java.util.Hashtable _methods = new java.util.Hashtable ();
  static
  {
    _methods.put ("sendtoFE", new java.lang.Integer (0));
    _methods.put ("shutdown", new java.lang.Integer (1));
  }

  public org.omg.CORBA.portable.OutputStream _invoke (String $method,
                                org.omg.CORBA.portable.InputStream in,
                                org.omg.CORBA.portable.ResponseHandler $rh)
  {
    org.omg.CORBA.portable.OutputStream out = null;
    java.lang.Integer __method = (java.lang.Integer)_methods.get ($method);
    if (__method == null)
      throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);

    switch (__method.intValue ())
    {
       case 0:  // FrontEndApp/FrontEndServer/sendtoFE
       {
         String message = in.read_string ();
         String $result = null;
         $result = this.sendtoFE (message);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 1:  // FrontEndApp/FrontEndServer/shutdown
       {
         this.shutdown ();
         out = $rh.createReply();
         break;
       }

       default:
         throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    return out;
  } // _invoke

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:FrontEndApp/FrontEndServer:1.0"};

  public String[] _all_interfaces (org.omg.PortableServer.POA poa, byte[] objectId)
  {
    return (String[])__ids.clone ();
  }

  public FrontEndServer _this() 
  {
    return FrontEndServerHelper.narrow(
    super._this_object());
  }

  public FrontEndServer _this(org.omg.CORBA.ORB orb) 
  {
    return FrontEndServerHelper.narrow(
    super._this_object(orb));
  }


} // class FrontEndServerPOA
