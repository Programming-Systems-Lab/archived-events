
package psl.events.siena.utils;

import siena.*;

/**
 * Universal subscription-and-printout service, useful for debugging.
 * Note that even though the class is not a Runnable, it doesn't
 * really matter since Siena will do all the thread work for us.
 *
 * TODO: Convert to a standardized logger
 *
 * @author Janak J Parekh
 * @version 1.0 
 */
public class AllSubscriber implements Notifiable {
  private HierarchicalDispatcher hd = null;
  
  public static void main(String[] args) {
    if(args.length != 1) {
      System.err.println("usage: java AllSubscriber <SENP URL>");
      System.exit(-1);
    }
    new AllSubscriber(args[0]);
  }

  public AllSubscriber(String sienaHost) {
    hd = new HierarchicalDispatcher();
    try {
      hd.setMaster(sienaHost);
    } catch(Exception e) {
      e.printStackTrace();
    }

    // Now super-subscribe
    Filter f = new Filter();
    
    try {
      hd.subscribe(f, this);
    } catch(Exception e) {
      e.printStackTrace();
    }

    // Cleanup
    Runtime.getRuntime().addShutdownHook(new Thread() {
	public void run() {
	  System.err.println("Shutting down..");
	  hd.shutdown();
	  return;
	}
      });

    System.out.println("Ready.");
  }

  /**
   * Get the hierarchicalDispatcher instance we use.
   *
   * @return The Hierarchical Dispatcher we're listening to.
   */
  public HierarchicalDispatcher getSiena() {
    return hd;
  }
  
  public void notify(Notification n) {
    System.out.println("Received event:" + n);
  }

  public void notify(Notification[] s) { ; }
}  
