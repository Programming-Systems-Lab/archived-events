import siena.*;
import java.io.*;

public class BenchPub {

  public static final int RECV_PORT = 8888;
  public static final int NUM_EVENTS = 10000;

  Siena s = null;

  public BenchPub(Siena siena) {
    s = siena;
  }

  public static void main(String args[]) {

    String master = null;
    if (args.length == 1)
      master = args[0];

    HierarchicalDispatcher hd = new HierarchicalDispatcher();
    try {
      hd.setReceiver(new TCPPacketReceiver(RECV_PORT));
      if (master != null) {
	hd.setMaster(master);
	System.out.println("using siena master " + master);
      } else {
	System.out.println("local siena is master on port " + RECV_PORT);
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(-1);
    }

    BenchPub bp = new BenchPub(hd);
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    System.out.println("Hit enter to publish " + NUM_EVENTS +" events");
    try {
      in.readLine();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(-1);
    }
    long before = System.currentTimeMillis();
    bp.publish(); 
    long after = System.currentTimeMillis();
    System.out.println("Done - elapsed publish time = " 
	+ (after - before) + "ms");
  }

  void publish() {
    Notification n = new Notification();
    n.putAttribute("dummy", "dummy");
    try {
      for (int i=0; i<NUM_EVENTS; ++i) {
	s.publish(n);
      }
    } catch (SienaException se) {
      se.printStackTrace();
      System.exit(-1);
    }
  }
}
