import java.io.*;
import siena.*;

/**
 * Example "SmartEvent tester program".
 * 
 * @author Phil Gross (png3@cs.columbia.edu)
 * @version 1.0
 */
class SmartEvent implements Runnable, Notifiable {

  public static final String me = "SmartEvent";
  Siena s = null;

  public SmartEvent(Siena s) {
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
    } catch (siena.InvalidHandlerException ihe) {
      ihe.printStackTrace();
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }

    SmartEvent m = new SmartEvent(h);
    Thread t = new Thread(m);
    t.start();

  }

  public void run() {

    Filter f1 = new Filter();
    f1.addConstraint("source", "Metaparser");
    f1.addConstraint("type", "finalResult");
    try {
      // handle internally.
      s.subscribe(f1, this);
    } catch (siena.SienaException se) {
      se.printStackTrace();
    }
    System.out.println(me + " subscribed to " + f1);

    Notification n = new Notification();
    n.putAttribute("source", "Smart");
    n.putAttribute("xmlFrag", "<We love XML/>");
    try {
      s.publish(n);
    } catch (siena.SienaException se) {
      se.printStackTrace();
    }
    System.out.println(me + " published " + n);
  }

  public void notify(Notification n) {
    // Smart event.  Pass fragment on to Oracle
    AttributeValue av = n.getAttribute("finalResult");
    if (av != null) {
      String res = av.stringValue();
      System.out.println(me + " got finalResult " + res);
      System.out.println(me + ":  Peace on earth, etc.");
    } else {
      System.out.println(me + " Error: finalResult w/o payload");
    }
  }

  public void notify(Notification[] e) {}
}
