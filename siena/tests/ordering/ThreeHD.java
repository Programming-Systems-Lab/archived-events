package psl.events.siena.tests.ordering;

import siena.*;

/**
 * Ordering test for three HD's (one publisher, one subscriber, one master).
 *
 * @author Janak J Parekh
 * @version $Revision$
 */
public class ThreeHD implements Notifiable {
  public static final int numNotifications = 5000;
  public long lastReceived = -1;
  
  public static void main(String[] args) {
    if(args.length != 2) {
      System.err.println("usage: java psl.events.siena.tests.ordering.TwoHD " +
      "<-p SENP URL of master|-s SENP URL of master>");
      System.err.println("\t-p implies publisher");
      System.err.println("\t-s implies subscriber");
      System.exit(-1);
    }
    
    if(args[0].equals("-p")) {
      new ThreeHD(args[1], true);
    } else { // Subscriber
      new ThreeHD(args[1], false);
    }
  }
  
  /**
   * CTOR.
   */
  public ThreeHD(String master, boolean publisher) {
    HierarchicalDispatcher hd = null;
    try {
      hd = new HierarchicalDispatcher();
      hd.setReceiver(new TCPPacketReceiver(0),1);
      hd.setMaster(master);
    } catch(Exception e) { e.printStackTrace(); }
    
    if(publisher == true) {
      publish(hd);
    } else {
      subscribe(hd);
    }
  }
    
  /**
   * Publisher mode
   */
  public void publish(HierarchicalDispatcher hd) {
    Notification n = null;
    for(long i = 0; i < 5000; i++) {
      n = new Notification();
      n.putAttribute("Count", i);
      n.putAttribute("Junk", "The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog.The quick brown fox jumps over the lazy dog.The quick brown fox jumps over the lazy dog.The quick brown fox jumps over the lazy dog.The quick brown fox jumps over the lazy dog.");
      try { hd.publish(n); } catch(Exception e) { e.printStackTrace(); }
    }
  }
  
  /**
   * Subscriber mode
   */
  public void subscribe(final HierarchicalDispatcher hd) {
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        hd.shutdown();
      }
    });
    
    Filter f = new Filter();
    try { hd.subscribe(f, this); } catch(Exception e) { e.printStackTrace(); }
    System.err.println("Listening.");
  }
  
  /**
   * Receive mechanism
   */
  public void notify(Notification n) {
    // get the count
    if(n.getAttribute("Count").longValue() != (++lastReceived)) {
      System.err.println("FAIL: Received " +
      n.getAttribute("Count").longValue() + ", was expecting " +
      (lastReceived - 1));
      System.exit(-1);
    }
    // if a multiple of 100 print
    if(n.getAttribute("Count").longValue() % 100 == 0) {
      System.out.println("Received " + n.getAttribute("Count").longValue() +
      "th event");
    }
  }
  
  public void notify(Notification[] n) { ; }
  
}
