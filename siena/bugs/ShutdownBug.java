
package psl.events.siena.bugs;

import siena.*;

/**
 * Stupid shutdown bug.  Works with Siena 1.2 and 1.3.
 *
 * @author Janak J Parekh <janak@cs.columbia.edu>
 * @version $Revision$
 */
public class ShutdownBug {
  private HierarchicalDispatcher hd = null;
  
  public static void main(String[] args) {
    if(args.length != 1) {
      System.err.println("usage: java ShutdownBug <SENP URL>");
      System.exit(-1);
    }
    new ShutdownBug(args[0]);
  }

  public ShutdownBug(String sienaHost) {
    hd = new HierarchicalDispatcher();
    try {
      hd.setReceiver(new TCPPacketReceiver(0));
      hd.setMaster(sienaHost);
    } catch(Exception e) {
      e.printStackTrace();
    }

    // Cleanup
    hd.shutdown();
    // Wait for a couple seconds
    try {
      Thread.currentThread().sleep(2000);
    } catch(Exception e) { ; }
  }
}  
