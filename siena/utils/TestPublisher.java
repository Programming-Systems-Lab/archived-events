
package psl.events.siena.utils;

import siena.*;

/**
 * Simple test publisher, useful for testing a Siena.
 *
 * @author Janak J Parekh
 * @version 1.0
 */
public class TestPublisher {
  private HierarchicalDispatcher hd = null;
  
  public static void main(String[] args) {
    new TestPublisher(args);
  }

  public TestPublisher(String[] args) {
    if(args.length==0) {
      System.err.println("usage: java TestPublisher <SENP URL>");
      System.exit(-1);
    }
    hd = new HierarchicalDispatcher();
    try {
      hd.setReceiver(new TCPPacketReceiver(0));
      hd.setMaster(args[0]);
    } catch(Exception e) {
      e.printStackTrace();
    }

    System.out.println("publishing...");

    // Now publish
    Notification n = new Notification();
    n.putAttribute("foo","bar");

    try {
      hd.publish(n);
    } catch(Exception e) { e.printStackTrace(); }

    System.out.println("shutting down...");

    // If we don't sleep, Siena throws an exception... blargh
    try { Thread.sleep(2000); } catch(Exception e) { ; }

    hd.shutdown();
  }
}  
