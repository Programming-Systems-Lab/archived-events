/*
 * ComponentStatus.java
 *
 * Created on December 18, 2002, 10:29 AM
 */

import java.io.BufferedWriter;
import java.io.FileWriter;

/**
 *
 * @author  Ian Zilla -- PSL Group Fall 2002
 */
public class ComponentStatus extends BodyBlock implements Buildable {
    
    private String statusCodeType;
    private String sourceApplication;
    private String description;
    
    /** Creates a new instance of ComponentStatus */
    public ComponentStatus(String fileName, String type, String application, String description) {
        setStatusCodeType(type);
        setSourceApp(application);
        setDescription(description);
        buildXML(fileName);
    }
    
    public void buildXML(String fileName) {
        // write ComponentStatus XML here
        System.out.println("Building ComponentStatus XML for file: " + fileName);
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(fileName, true));
            out.write("<dasada-component-status>\n");
            out.write("\t<stCodeType>" + this.getStatusCodeType() + "</stCodeType>\n");
            out.write("\t<sourceApp>" + this.getSourceApp() + "</sourceApp>\n");
            out.write("\t<description>" + this.getDescription() + "</description>\n");
            out.write("</dasada-component-status>\n");
            out.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void setStatusCodeType(String codeType) {
        statusCodeType = codeType;
    }
    
    public String getStatusCodeType() {
        return statusCodeType;
    }
    
    public void setSourceApp(String application) {
        sourceApplication = application;
    }
    
    public String getSourceApp() {
        return sourceApplication;
    }
    
    public void setDescription(String s_description) {
        description = s_description;
    }
    
    public String getDescription() {
        return description;
    }
    
}