/*
 * BodyBlock.java
 *
 * Created on December 18, 2002, 10:00 AM
 */

/**
 *
 * @author  Ian Zilla
 */
public abstract class BodyBlock implements Buildable {
    
    /** Creates a new instance of BodyBlock */
    public BodyBlock() {
    }
    
    public abstract void buildXML(String fileName);
    
    
}