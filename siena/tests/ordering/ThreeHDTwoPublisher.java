package psl.events.siena.tests.ordering;

import siena.*;

/**
 * Ordering test for four HD's (two publishers, one subscriber, one master).
 *
 * @author Janak J Parekh
 * @version $Revision$
 */
public class ThreeHDTwoPublisher implements Notifiable, Runnable {
  public static final int numNotifications = 500;
  private HierarchicalDispatcher hd = null;
  private long publisherID = -2;
  private long lastReceived[] = {-1, -1};
  
  public static void main(String[] args) {
    if(args.length != 2) {
      System.err.println("usage: java psl.events.siena.tests.ordering.TwoHD " +
      "<-p SENP URL of master|-s SENP URL of master>");
      System.err.println("\t-p implies publishers");
      System.err.println("\t-s implies subscriber");
      System.exit(-1);
    }
    
    if(args[0].equals("-p")) {
      Thread t1 = new Thread(new FourHDTwoPublisher(args[1], 0));
      Thread t2 = new Thread(new FourHDTwoPublisher(args[1], 1));
      t1.start();
      t2.start();
    } else { // Subscriber
      new FourHDTwoPublisher(args[1], -1);
    }
  }
  
  /**
   * CTOR.
   */
  public ThreeHDTwoPublisher(String master, long publisherID) {
    try {
      hd = new HierarchicalDispatcher();
      hd.setReceiver(new TCPPacketReceiver(0),1);
      hd.setMaster(master);
    } catch(Exception e) { e.printStackTrace(); }
    
    this.publisherID = publisherID;
    
    if(publisherID == -1) {
      subscribe();
    } /* Else the run method for the publisher */
  }
    
  /**
   * Publisher mode
   */
  public void run() {
    Notification n = null;
    for(long i = 0; i < numNotifications; i++) {
      n = new Notification();
      n.putAttribute("Source", publisherID);
      n.putAttribute("Count", i);
      n.putAttribute("Junk", "The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog.The quick brown fox jumps over the lazy dog.The quick brown fox jumps over the lazy dog.The quick brown fox jumps over the lazy dog.The quick brown fox jumps over the lazy dog.");
      try { hd.publish(n); } catch(Exception e) { e.printStackTrace(); }
    }
  }
  
  /**
   * Subscriber mode
   */
  public void subscribe() {
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
    int index = (int)n.getAttribute("Source").longValue();
    // get the count
    if(n.getAttribute("Count").longValue() != (++lastReceived[index])) {
      System.err.println("FAIL: Received " +
      n.getAttribute("Count").longValue() + " for source " + index + 
      ", was expecting " + (lastReceived[index] - 1));
      System.exit(-1);
    }
    // if a multiple of 100 print
    if(n.getAttribute("Count").longValue() % 10 == 0) {
      System.out.println("Received " + n.getAttribute("Count").longValue() +
      "th event from source " + index);
    }
  }
  
  public void notify(Notification[] n) { ; }
  
}
