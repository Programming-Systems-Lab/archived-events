import java.io.*;
import siena.*;

/**
 * Sample "Oracle" class that answers queries from the Metaparser.
 *
 * @author Phil Gross (png3@cs.columbia.edu), with modifications by
 * Janak J Parekh (jjp32@cs.columbia.edu)
 * @version 1.0
 */
class Oracle implements Runnable, Notifiable {

  public static final String me = "Oracle";
  Siena s = null;

 
  public Oracle(Siena s) {
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

    Oracle m = new Oracle(h);
    Thread t = new Thread(m);
    t.start();

  }

  public void run() {

    Filter f = new Filter();
    f.addConstraint("source", "Metaparser");
    f.addConstraint("type", "query");
    try {
      // handle internally
      s.subscribe(f, this);
    } catch (siena.SienaException se) {
      se.printStackTrace();
    }
    System.out.println(me + " subscribed to " + f);

    f.clear();
    f.addConstraint("source", "WGC");
    try {
      // handle externally
      s.subscribe(f, new Notifiable() {
	  public void notify(Notification n) {
	    // if we get here, it means that source=WGC and type=cacheResult,
	    // as per the filter.
	    AttributeValue av = n.getAttribute("cacheResult");
	    if (av != null) {
	      boolean res = av.booleanValue();
	      System.out.println(me + " got cacheResult " + res);
	      
	      if (res) { // we got a cache hit
		av = n.getAttribute("value");
		if (av != null) {
		  String val = av.stringValue();
		  System.out.println(me + " got value " + val);
		  processCacheResult(val);
		} else {
		  System.out.println(me + " Error: true result without value");
		}
	      } else { // result was false, no hit
		processCacheResult("Too bad, so sad");
	      }
	    } else {
	      System.out.println(me + " Error: cacheResult w/o payload");
	    }
	  }

	  public void notify(Notification[] n) { }
	});
    } catch (siena.SienaException se) {
      se.printStackTrace();
    }
    System.out.println(me + " subscribed to " + f);
  }

  public void processMPQuery(String xmlFrag) {
    System.out.println(me + ": I'm processing this XML Fragment.  Yahoo!");
    System.out.println(me + ": Deciding to ask WGC");
    Notification n = new Notification();
    n.putAttribute("source", "Oracle");
    n.putAttribute("type", "cacheLookup");
    n.putAttribute("key", "GoPatGo");
    try {
      s.publish(n);
    } catch (siena.SienaException se) {
      se.printStackTrace();
    }
    System.out.println(me + " published " + n);
  }

  public void processCacheResult(String xmlFrag) {
    System.out.println(me + " got a Cache Result. I'm so excited!");
    Notification n = new Notification();
    n.putAttribute("source", "Oracle");
    n.putAttribute("type", "queryResult");
    n.putAttribute("queryResult", "the Answer to Life, the Universe, etc.");
    try {
      s.publish(n);
    } catch (siena.SienaException se) {
      se.printStackTrace();
    }
    System.out.println(me + " published " + n);
  }

  public void notify(Notification n) {

    // Metaparser query
    AttributeValue av = n.getAttribute("query");
    if (av != null) {
      String query = av.stringValue();
      System.out.println(me + " got query " + query);
      processMPQuery(query);

    } else {
      System.out.println(me + " Error: metaparser query without query");
    }
  }

  public void notify(Notification[] e) {}

}
