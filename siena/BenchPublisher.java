import siena.*;
import siena.comm.*;

/** Program to test publishing speed of Siena (made with Siena 1.3 in mind) */
public class BenchPublisher {

    /* The testing method */
    public static void main(String args[]) throws Exception {
	if (args.length != 5) {
	    System.out.println("Usage: BenchProducer [h|t] [siena server] [number of events] [sending speed of events] [repeat]");
	    System.out.println("h = HierarchicalDispatcher");
	    System.out.println("t = ThinClient");
	    return;
	}

	String option = args[0];
	String serverName = args[1];
	Siena client = null;

	if (option.equals("h")) {
	    //using HierachicalDispatcher
	    HierarchicalDispatcher h = new HierarchicalDispatcher();
	    h.setMaster(serverName);

	    //set the correct PacketReceiver 
	    //must correspond to the StartServer option set
	    if (serverName.startsWith("ka")) {
		//keep alive is the one that is fastest
		h.setReceiver(new KAPacketReceiver());
	    }else if (serverName.startsWith("udp")) {
		//udp is prone to packet lost
		h.setReceiver(new UDPPacketReceiver(0));
	    }else {//tcp is default for Siena
		h.setReceiver(new TCPPacketReceiver(0));
	    }
	    client = h;
	}else if (option.equals("t")) {
	    //using ThinClient
	    ThinClient temp = new ThinClient(serverName);
	    if (serverName.startsWith("ka")) {
		//keep alive is the one that is fastest
		temp.setReceiver(new KAPacketReceiver());
	    }else if (serverName.startsWith("udp")) {
		//udp is prone to packet lost
		temp.setReceiver(new UDPPacketReceiver(0));
	    }//tcp is default for Siena
	     
	    client = temp;
	}else {
		System.out.println("Invalid option");
		return;
	}

	
	int numOfEvents = Integer.parseInt(args[2]);

	//events per second
	int speed = Integer.parseInt(args[3]);
	
	int sleepTime = 0;
	if (speed != 0) {
	    //calculate sleep time 
	    //our real speed will be less that specified in reality
	    sleepTime = (int)Math.ceil(1000 / speed);
	}

	//lets run several times just for benchmarking
	int repetition = Integer.parseInt(args[4]);
	
	//do not want effects of running out of memory creating same object
	//we are just interest in the speed
	Notification notif = new Notification();
	notif.putAttribute("dummy", "dummy");

	for(int j = 0; j < repetition; j++) {

	    long msec = System.currentTimeMillis();
	    
	    for(int i = 0; i < numOfEvents; i++) {

		/* previous benchmarks were with creating a new Notification
		 * for each send and we got over 200 events from KA
		 */
		//Notification notif = new Notification();
		//notif.putAttribute("dummy", "dummy");
		client.publish(notif);

		//not an option of 0 for speed means send asap
		Thread.sleep(sleepTime);
	    }
	    
	    long result = System.currentTimeMillis() - msec;
	    
	    System.out.println("Sending Performance: elapse time " + result + " Real Speed: " + (((numOfEvents * 1.0)/ result) * 1000)); 
	}
	    client.shutdown();
	    /* since Siena sends things out of order sometimes 
	     * a BYE can precede some PUBs so we wait
	     * The shutdown can return faster than the Server is prepared 
	     * leading to a non-fatal error of "PacketReceiver is closed"
	     * So we sleep for a while before exiting
	     */
	    Thread.sleep(1000);
	    System.exit(0);
    }
}






