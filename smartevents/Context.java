/*
 * Context.java
 *
 * Created on December 18, 2002, 9:58 AM
 */

import java.io.BufferedWriter;
import java.io.FileWriter;

/**
 *
 * @author  Ian Zilla -- PSL Group Fall 2002
 */
public class Context extends HeaderBlock {
    
    // source info
    private String source_ip;
    private int source_port;
    
    // ordering info
    private String ordering_timeStamp;
    private long ordering_seqNum;
    
    // version info
    private String version;
    
    /** Creates a new instance of Context */
    public Context(String fileName, String ip, int port, String timeStamp, long seqNum, String version) {
        setSourceIP(ip);
        setSourcePort(port);
        setOrderingTimeStamp(timeStamp);
        setOrderingSeqNumber(seqNum);
        setVersion(version);
        buildXML(fileName);
    }
    
    public void buildXML(String fileName) {
        // write the Context XML here
        System.out.println("Building Context XML for file: " + fileName);
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
            out.write("<dasada-context>\n");
            out.write("\t<source>\n");
            out.write("\t\t<host>\n");
            out.write("\t\t\t<ip-address>" + this.getSourceIP() + "</ip-address>\n");
            out.write("\t\t\t<ip-port>" + this.getSourcePort() + "</ip-port>\n");
            out.write("\t\t</host>\n");
            out.write("\t</source>\n");
            out.write("\t<ordering>\n");
            out.write("\t\t<timestamp>" + this.getOrderingTimeStamp() + "</timestamp>\n");
            out.write("\t\t<seqNum>" + this.getOrderingSeqNumber() + "</seqNum>\n");
            out.write("\t</ordering>\n");
            out.write("\t<version>" + this.getVersion() + "</version>\n");
            out.write("</dasada-context>\n");
            out.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void setSourceIP(String ip) {
        source_ip = ip;
    }
    
    public String getSourceIP() {
        return source_ip;
    }
    
    public void setSourcePort(int port) {
        source_port = port;
    }
    
    public int getSourcePort() {
        return source_port;
    }
    
    public void setOrderingTimeStamp(String timeStamp) {
        ordering_timeStamp = timeStamp;
    }
    
    public String getOrderingTimeStamp() {
        return ordering_timeStamp;
    }
    
    public void setOrderingSeqNumber(long seqNum) {
        ordering_seqNum = seqNum;
    }
    
    public long getOrderingSeqNumber() {
        return ordering_seqNum;
    }
    
    public void setVersion(String s_version) {
        version = s_version;
    }
    
    public String getVersion() {
        return version;
    }
    
}
