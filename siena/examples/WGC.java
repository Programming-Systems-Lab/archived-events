import java.io.*;
import siena.*;

/**
 * Sample stub workgroup cache.  Expects "requests" from the "Oracle".
 *
 * @author Phil Gross (png3@cs.columbia.edu)
 * @version 1.0
 */
class WGC implements Runnable, Notifiable {

  public static final String me = "WGC";
  Siena s = null;
  boolean success = false;

 
  public WGC(Siena s, boolean success) {
    this.s = s;
    this.success = success;
  }

  /** if two parameters given, lookup will succeed. */
  public static void main(String args[]) {
  
    String master = "senp://localhost:31337";

    if (args.length > 0) {
      master = args[0];
    }

    HierarchicalDispatcher h = new HierarchicalDispatcher();
    try {
      h.setMaster(master);
      System.out.println(me + ": master is " + master);
    } catch (siena.InvalidHandlerException ihe) {
      ihe.printStackTrace();
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }

    WGC m = new WGC(h, (args.length > 1) ? true : false);

    Thread t = new Thread(m);
    t.start();

  }

  public void run() {

    Filter f1 = new Filter();
    f1.addConstraint("source", "Oracle");
    f1.addConstraint("type", "cacheLookup");
    try {
      // handle internally
      s.subscribe(f1, this);
    } catch (siena.SienaException se) {
      se.printStackTrace();
    }
    System.out.println(me + " subscribed to " + f1);

  }

  public void notify(Notification n) {

    // Metaparser query
    AttributeValue av = n.getAttribute("key");
    if (av != null) {
      String key = av.stringValue();
      System.out.println(me + " got key " + key);
      System.out.println(me + " success = " + success);

      // sophisticated cache lookup scheme
      /*
      java.util.Random r = new java.util.Random();
      if (r.nextBoolean()) {
      */
      // only generate event if success
      if (success) {
	n = new Notification();
	n.putAttribute("source", "WGC");
	n.putAttribute("type", "cacheResult");
	n.putAttribute("cacheResult", true);
	n.putAttribute("value", "Secret Egg Salad Recipe");
	try {
	  s.publish(n);
	} catch (siena.SienaException se) {
	  se.printStackTrace();
	}
	System.out.println(me + " published " + n);
      } 

    } else {
      System.out.println(me + " Error: Oracle lookup without key");
    }
  }

  public void notify(Notification[] e) {}

}
