/*
 * ArchMutation.java
 *
 * Created on December 18, 2002, 10:20 AM
 */

/**
 *
 * @author  Ian Zilla -- PSL Group Fall 2002
 */
public class ArchMutation extends BodyBlock implements Buildable {
    
    private boolean created = false;
    private boolean deleted = false;
    private boolean attached = false;
    private boolean detached = false;
    private boolean changed = false;
    
    /** Creates a new instance of ArchMutation */
    public ArchMutation() {
    }
    
    public void buildXML(String fileName) {
    }
    
    public boolean isCreated() {
        return false;
    }
    
    public boolean isDeleted() {
        return false;
    }
    
    public boolean isAttached() {
        return false;
    }
    
    public boolean isDetached() {
        return false;
    }
    
    public boolean isChanged() {
        return false;
    }
    
}
