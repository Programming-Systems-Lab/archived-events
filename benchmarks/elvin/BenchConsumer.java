import org.elvin.je4.*;

public class BenchConsumer implements NotificationListener {
    int got = 0;
    int limit;
    long start = 0;

    public BenchConsumer(int num) {
	limit = num;
    }
    
    public void notificationAction(Notification event) {
	got++;
	if (got == 1) {
	    start = System.currentTimeMillis();
	}
	if ((got % limit) == 0) {
	    long result = System.currentTimeMillis() - start;
	    System.out.println("Elapsed time: " + result + " Receive speed: " + ((limit * 1000.0)/result));
	    start = System.currentTimeMillis();
	    //System.exit(0);
	}
	    
	
    }

    public static void main(String args[]) throws Exception {
	if (args.length != 2) {
	    System.out.println("Usage: BenchConsumer [elvin server] [number of events]");
	    return;
	}

	ElvinURL serverName = new ElvinURL(args[0]);

	Consumer cons = new Consumer(serverName);

	int numOfEvents = Integer.parseInt(args[1]);

	Subscription sub = new Subscription("require(dummy)");

	sub.addNotificationListener(new BenchConsumer(numOfEvents));

	cons.addSubscription(sub);

	System.out.println("waiting for events...");
    }
}





