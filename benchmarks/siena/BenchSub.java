import siena.*;
import java.io.*;

public class BenchSub implements Notifiable {

  public static final int RECV_PORT = 7777;
  public static final int NUM_EVENTS = 10000;

  Siena s = null;
  static int numReceived = 0;
  static long before=0, after=0;

  public BenchSub(Siena siena) {
    s = siena;
    Filter f = new Filter();
    f.addConstraint("dummy", Op.ANY, "");
    try {
      s.subscribe(f, this);
    } catch (SienaException se) { se.printStackTrace(); }
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

    BenchSub bs = new BenchSub(hd);
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    System.out.println("Hit enter to quit at any time");
    try {
      in.readLine();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(-1);
    }
    long after = System.currentTimeMillis();
    System.out.println("Done - elapsed subscribe time = " 
	+ (after - before) + "ms");
    System.out.println(numReceived + " events received");
  }

  public void notify(Notification e) {
    if (numReceived == 0)
      before = System.currentTimeMillis();
    ++numReceived;
    if ((numReceived % 500) == 0) {
      after = System.currentTimeMillis();
      System.out.println(numReceived + " events received in " + 
	  (after - before));
      // System.exit(0);
    }
  }

  public void notify(Notification s[]) {}
}
