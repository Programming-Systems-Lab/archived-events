/*
 * SmartEvent.java
 *
 * Created on December 18, 2002, 9:48 AM
 */

import java.util.ArrayList;

/**
 *
 * @author  Ian Zilla -- PSL Group Fall 2002
 */
public class SmartEvent implements Buildable {
    
    private String fileName;
    private HeaderBlock header;
    private ArrayList bodyBlocks;
    
    /** Creates a new instance of SmartEvent */
    public SmartEvent(String file_name) {
        fileName = file_name;
        bodyBlocks = new ArrayList();
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setHeader(HeaderBlock h_header) {
        header = h_header;
    }
    
    public HeaderBlock getHeader() {
        return header;
    }
    
    public void addBodyBlock(BodyBlock bodyBlock) {
        bodyBlocks.add(bodyBlock);
    }
    
    public BodyBlock getBodyBlock(int i) {
        return (BodyBlock)bodyBlocks.get(i);
    }
    
    public ArrayList getAllBodyBlocks() {
        return bodyBlocks;
    }
    
    public void buildXML(String fileName) {}
    
}
