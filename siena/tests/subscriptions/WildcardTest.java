package psl.events.siena.tests.subscriptions;

import siena.Notification;
import siena.HierarchicalDispatcher;
import siena.Filter;
import siena.Op;
import siena.Notifiable;

/**
 * Simple test to see how wildcards work.
 *
 * @author Janak J Parekh
 * @version $Revision$
 */
public class WildcardTest implements Notifiable {
  private String which;
  
  public static void main(String[] args) {
    try {
      HierarchicalDispatcher hd = new HierarchicalDispatcher();
      Filter f1 = new Filter();
      f1.addConstraint("a", Op.ANY, (String)null);
      hd.subscribe(f1, new WildcardTest("1"));
      Filter f2 = new Filter();
      f2.addConstraint("b", Op.ANY, "");
      hd.subscribe(f2, new WildcardTest("2"));
      Filter f3 = new Filter();
      hd.subscribe(f3, new WildcardTest("all"));
      
      // Now send out some goodies
      Notification n1 = new Notification();
      n1.putAttribute("a", "first");
      hd.publish(n1);
      Notification n2 = new Notification();
      n2.putAttribute("b", "second");
      hd.publish(n2);
      Notification n3 = new Notification();
      n3.putAttribute("a", "third");
      n3.putAttribute("b", "third");
      hd.publish(n3);
      Notification n4 = new Notification();
      n4.putAttribute("foo", "bar");
      hd.publish(n4);
      
    } catch(Exception e) { 
      e.printStackTrace();
      System.exit(-1);
    }
  }
  
  public WildcardTest(String which) {
    this.which = which;
  }

  public void notify(Notification n) {
    System.out.println("Instance " + which + ": received notification " + n);
  }
  
  public void notify(Notification[] n) { ; }
}