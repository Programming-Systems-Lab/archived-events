/*
 * HeaderBlock.java
 *
 * Created on December 18, 2002, 9:53 AM
 */

/**
 *
 * @author  Ian Zilla -- PSL Group Fall 2002
 */
public abstract class HeaderBlock implements Buildable {
    
    /** Creates a new instance of HeaderBlock */
    public HeaderBlock() {
    }
    
    public abstract void buildXML(String fileName);
    
}
