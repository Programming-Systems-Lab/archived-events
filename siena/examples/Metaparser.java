import java.io.*;
import siena.*;

/**
 * Example "stub" metaparser that (a) listens to the Siena for results
 * from the "Oracle"; and (b) listens from the bus about SmartEvents
 * to "parse".
 *
 * @author Phil Gross (png3@cs.columbia.edu), with modifications by
 * Janak J Parekh (jjp32@cs.columbia.edu)
 * @version 1.0
 */
class Metaparser implements Runnable, Notifiable {
  public static final String me = "Metaparser";
  Siena s = null;
 
  public Metaparser(Siena s) {
    this.s = s;
  }

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

    Metaparser m = new Metaparser(h);
    Thread t = new Thread(m);
    t.start();
  }

  public void run() {
    Filter f = new Filter();
    f.addConstraint("source", "Oracle");
    f.addConstraint("type", "queryResult");
    try {
      // create a listener (implementation of Notifiable) to
      // specifically handle queryResults.
      s.subscribe(f, new Notifiable() {
	  public void notify(Notification n) {
	    // if we get here, it means that source=Oracle and
	    // type=queryResult, as per the filter.
	    AttributeValue av = n.getAttribute("queryResult");
	    if (av != null) {
	      String res = av.stringValue();
	      System.out.println(me + ": got queryResult " + res);
	      processOracleReply(res);
	    } else {
	      System.out.println(me + " Error: oracle w/o queryResult");
	    }
	  }

	  // We don't care about sequences here.
	  public void notify(Notification[] n) { ; }
	});
    } catch (siena.SienaException se) {
      se.printStackTrace();
    }
    System.out.println(me + " subscribed to " + f);

    f.clear();
    f.addConstraint("source", "Smart");
    try {
      // handle this one internally.  Why not?
      s.subscribe(f, this);
    } catch (siena.SienaException se) {
      se.printStackTrace();
    }
    System.out.println(me + " subscribed to " + f);
  }

  public void processOracleReply(String reply) {
    System.out.println(me + ": I'm processing this oracle reply.  Whoopee!");
    System.out.println(me + ": reply was " + reply);
    Notification n = new Notification();
    n.putAttribute("source", "Metaparser");
    n.putAttribute("type", "finalResult");
    n.putAttribute("finalResult", "Happy Happy Joy Joy");
    try {
      s.publish(n);
    } catch (siena.SienaException se) {
      se.printStackTrace();
    }
    System.out.println(me + " published " + n);
  }

  public void processSmartEvent(String xmlFrag) {
    System.out.println(me + "processSmartEvent deciding to ask Oracle");
    Notification n = new Notification();
    n.putAttribute("source", "Metaparser");
    n.putAttribute("type", "query");
    n.putAttribute("query", xmlFrag);
    try {
      s.publish(n);
    } catch (siena.SienaException se) {
      se.printStackTrace();
    }
    System.out.println(me + " published " + n);
  }

  public void notify(Notification n) {
    // Smart event.  Pass fragment on to Oracle
    AttributeValue av = n.getAttribute("xmlFrag");
    if (av != null) {
      String frag = av.stringValue();
      System.out.println(me + " got fragment " + frag);
      processSmartEvent(frag);

    } else {
      System.out.println(me + " Error: Smart without fragment");
    }
  }

  public void notify(Notification[] e) { ; }
}
