package psl.events.siena.tests.ordering;

import siena.*;

/**
 * Ordering test for two HD's (one publisher, one subscriber).
 *
 * @author Janak J Parekh
 * @version $Revision$
 */
public class TwoHD implements Notifiable {
  public static final int numNotifications = 5000;
  public long lastReceived = -1;
  
  public static void main(String[] args) {
    if(args.length == 0) {
      System.err.println("usage: java psl.events.siena.tests.ordering.TwoHD " +
      "<-p SENP URL of subscriber|-s port>");
      System.err.println("\t-p implies publisher");
      System.err.println("\t-s implies subscriber");
      System.exit(-1);
    }
    
    if(args[0].equals("-p")) {
      new TwoHD(args[1]);
    } else {
      new TwoHD(Short.parseShort(args[1]));
    }
  }
  
  /**
   * CTOR for publisher mode
   */
  public TwoHD(String subscriberHost) {
    HierarchicalDispatcher hd = null;
    try {
      hd = new HierarchicalDispatcher();
      hd.setReceiver(new TCPPacketReceiver(0));
      hd.setMaster(subscriberHost);
    } catch(Exception e) { e.printStackTrace(); }
    
    Notification n = null;
    for(long i = 0; i < 5000; i++) {
      n = new Notification();
      n.putAttribute("Count", i);
      n.putAttribute("Junk", "The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog.The quick brown fox jumps over the lazy dog.The quick brown fox jumps over the lazy dog.The quick brown fox jumps over the lazy dog.The quick brown fox jumps over the lazy dog.");
      try { hd.publish(n); } catch(Exception e) { e.printStackTrace(); }
    }
  }
  
  /**
   * CTOR for subscriber mode
   */
  public TwoHD(short port) {
    final HierarchicalDispatcher hd = new HierarchicalDispatcher();
    
    try {
      // NB: we are using 1 receiver thread below, doesn't work otherwise
      hd.setReceiver(new TCPPacketReceiver(port),1);
    } catch(Exception e) { e.printStackTrace(); }

    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        hd.shutdown();
      }
    });
    
    Filter f = new Filter();
    try { hd.subscribe(f, this); } catch(Exception e) { e.printStackTrace(); }
    System.err.println("Listening on port " + port);
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
