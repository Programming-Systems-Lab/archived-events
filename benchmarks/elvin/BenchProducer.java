import org.elvin.je4.*;

//test sending speed of elvin
public class BenchProducer {
    public static void main(String args[]) throws Exception {
	if (args.length != 4) {
	    System.out.println("Usage: BenchProducer [elvin server] [number of events] [sending speed of events] [repeat]");
	    return;
	}

	ElvinURL serverName = new ElvinURL(args[0]);

	Producer prod = new Producer(serverName);

	int numOfEvents = Integer.parseInt(args[1]);
	int speed = Integer.parseInt(args[2]);
	
	int sleepTime = 0;
	if (speed != 0) {
	    sleepTime = (int)Math.ceil(1000 / speed);
	}

	int repetition = Integer.parseInt(args[3]);
	
	for(int j = 0; j < repetition; j++) {

	    long msec = System.currentTimeMillis();
	    
	    for(int i = 0; i < numOfEvents; i++) {
		Notification notif = new Notification();
		notif.put("dummy", i);
		prod.notify(notif);
		Thread.sleep(sleepTime);
	    }
	    
	    long result = System.currentTimeMillis() - msec;
	    
	    System.out.println("Sending Performance: elapse time " + result + " Real Speed: " + (((numOfEvents * 1.0)/ result) * 1000)); 
	}
	    prod.getConnection().close();
	    System.exit(0);
    }
}
