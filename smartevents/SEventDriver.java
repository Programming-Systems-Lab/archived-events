/*
 * SEventDriver.java
 *
 * Created on December 18, 2002, 9:43 PM
 */

/**
 *
 * @author  Ian Zilla -- PSL Group Fall 2002
 */
public class SEventDriver {
    
    /** Creates a new instance of SEventDriver */
    public SEventDriver() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // build a MethodCallEvent
        long seq_num = 0;
        String[] params = new String[2];
        params[0] = "boo, int, 10";
        params[1] = "ooh, long, 553345";
        SmartEventFactory fact = new SmartEventFactory();
        // create a MethodCall Event
        SmartEvent methodCall = fact.buildMethodCallEvent("C:\\psl\\events\\smartevents\\meth-call-event.txt", "128.14.59.132",
                                            4321, "12/25/2002", seq_num++, "1.0.0", "setFoo", "myPackage", "true",
                                             "boolean", "1", params);
        
        // create a ComponentStatus Event
        SmartEvent compStatus = fact.buildComponentStatusEvent("C:\\psl\\events\\smartevents\\comp-status-event.txt", "128.14.59.132",
                                            5432, "12/25/2002", seq_num++, "1.0.0", "ERROR", "java",
                                            "version 1.4.1");
        
        // Add another BodyBlock to the ComponentStatus Event.
        // In this case it is another ComponentStatus Event, but it
        // could have been another type of BodyBlock.
        fact.addComponentStatusBlock(compStatus, "OK", "java", "version 1.4.1");
    }
}
