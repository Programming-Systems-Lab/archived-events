import siena.*;
import siena.comm.*;

/** Benchmarking program for subscriber with Siena 1.3 in mind */

public class BenchSubscriber implements Notifiable {
    int got = 0;
    int total = 0;
    int limit;
    long start = 0;
    Siena server;

    public BenchSubscriber(int num, Siena h, int repeat) {
	limit = num;
	server = h;
	total = repeat * num;
    }
    
    public void notify(Notification event) throws SienaException {
	got++;
	if (got == 1) {
	    start = System.currentTimeMillis();
	}

	if ((got % limit) == 0) {
	    long result = System.currentTimeMillis() - start;
	    System.out.println("Elapsed time: " + result + " Receive speed: " + ((limit * 1000.0)/result));
	    start = System.currentTimeMillis();
	}

	if (got >= total) {
	    server.shutdown();
	    try {
		Thread.sleep(1000);
	    }catch (Exception e) {}
	    System.exit(0);
	}
    }

    public void notify(Notification event[]) throws SienaException {
	if (got == 0) {
	    start = System.currentTimeMillis();
	}
	
	got += event.length;
	
	if ((got % limit) == 0) {
	    long result = System.currentTimeMillis() - start;
	    System.out.println("Elapsed time: " + result + " Receive speed: " + ((limit * 1000.0)/result));
	    start = System.currentTimeMillis();
	}

	if (got >= total) {
	    server.shutdown();
	    try {
		Thread.sleep(1000);
	    }catch (Exception ex) {}
	    System.exit(0);
	}
    }
    
    public static void main(String args[]) throws Exception {
	if (args.length != 4) {
	    System.out.println("Usage: BenchConsumer [h|t] [siena server] [number of events] [repeat]");
	    System.out.println("h = HierarchicalDispatcher");
	    System.out.println("t = ThinClient");
	    return;
	}
	
	String option = args[0];
	String serverName = args[1];
	Siena client = new HierarchicalDispatcher();
	if (option.equals("h")) {
	    HierarchicalDispatcher h = new HierarchicalDispatcher();
	    h.setMaster(serverName);

	    //set the correct PacketReceiver 
	    //must correspond to the StartServer option set
	    if (serverName.startsWith("ka")) {
		//keep alive is the one that is fastest
		h.setReceiver(new KAPacketReceiver(5555));
	    }else if (serverName.startsWith("udp")) {
		//udp is prone to packet lost
		h.setReceiver(new UDPPacketReceiver(0));
	    }else {//tcp is default for Siena
		h.setReceiver(new TCPPacketReceiver(0));
	    }

	    client = h;

	}else if (option.equals("t")) {
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

	int repetition = Integer.parseInt(args[3]);

	BenchSubscriber bs = new BenchSubscriber(numOfEvents, client, repetition);
	
	Filter f = new Filter();
	f.addConstraint("dummy", "dummy");
	client.subscribe(f, bs);
	
	System.out.println("waiting for events...");
    }
}









