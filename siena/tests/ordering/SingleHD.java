package psl.events.siena.tests.ordering;

import siena.*;

/**
 * Ordering test for single JVM, one HD, one publisher, one subscriber.
 *
 * @author Janak J Parekh
 * @version $Revision$
 */
public class SingleHD implements Notifiable {
  public static final int numNotifications = 5000;
  public long lastReceived = -1;
  
  public static void main(String[] args) {
    new SingleHD();
  }

  /**
   * CTOR.
   */
  public SingleHD() {
    HierarchicalDispatcher hd = new HierarchicalDispatcher();
    Filter f = new Filter();
    try { hd.subscribe(f, this); } catch(Exception e) { e.printStackTrace(); }
    
    Notification n = null;
    for(long i = 0; i < 5000; i++) {
      n = new Notification();
      n.putAttribute("Count", i);
      n.putAttribute("Junk", "The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog.The quick brown fox jumps over the lazy dog.The quick brown fox jumps over the lazy dog.The quick brown fox jumps over the lazy dog.The quick brown fox jumps over the lazy dog.");
      try { hd.publish(n); } catch(Exception e) { e.printStackTrace(); }
    }
    hd.shutdown();
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
      