/*
 * MethodCall.java
 *
 * Created on December 18, 2002, 10:10 AM
 */

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.StringTokenizer;

/**
 *
 * @author  Ian Zilla -- PSL Group Fall 2002
 */
public class MethodCall extends BodyBlock implements Buildable{
    
    private String name;
    private String inPackage;
    private String retValName;
    private String retValType;
    private String retValValue;
    private String[] parameters;
    
    /** Creates a new instance of MethodCall */
    public MethodCall(String fileName, String name, String pckg, String rvName, 
                        String rvType, String rvValue, String[] params) {
        setName(name);
        setPackage(pckg);
        setRVName(rvName);
        setRVType(rvType);
        setRVValue(rvValue);
        setParameters(params);
        buildXML(fileName);
    }
    
    public void buildXML(String fileName) {
        // write MethodCall XML
        System.out.println("Building MethodCall XML for file: " + fileName);
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(fileName, true));
            out.write("<dasada-function-call>\n");
            out.write("\t<name>" + this.getName() + "</name>\n");
            out.write("\t<package>" + this.getPackage() + "</package>\n");
            out.write("\t<returnValue>\n");
            out.write("\t\t<name>" + this.getRVName() + "</name>\n");
            out.write("\t\t<type>" + this.getRVType() + "</type>\n");
            out.write("\t\t<value>" + this.getRVValue() + "</value>\n");
            out.write("\t</returnValue>\n");
            out.write("\t<parameters>\n");
            // there could be many parameters so go through them all
            String[] temp = this.getParameters();
            for (int i = 0; i < temp.length; i++) {
                StringTokenizer s = new StringTokenizer(temp[i], "," + " ");
                String t_name = s.nextToken();
                String t_type = s.nextToken();
                String t_val = s.nextToken();
                out.write("\t\t<parameter>\n");
                out.write("\t\t\t<name>" + t_name + "</name>\n");
                out.write("\t\t\t<type>" + t_type + "</type>\n");
                out.write("\t\t\t<value>" + t_val + "</value>\n");
                out.write("\t\t</parameter>\n");
            }
            out.write("\t</parameters>\n");
            out.write("</dasada-function-call>\n");
            out.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void setName(String s_name) {
        name = s_name;
    }
    
    public String getName() {
        return name;
    }
    
    public void setPackage(String s_inPackage) {
        inPackage = s_inPackage;
    }
    
    public String getPackage() {
        return inPackage;
    }
    
    public void setRVName(String rvName) {
        retValName = rvName;
    }
    
    public String getRVName() {
        return retValName;
    }
    
    public void setRVType(String rvType) {
        retValType = rvType;
    }
    
    public String getRVType() {
        return retValType;
    }
    
    public void setRVValue(String rvVal) {
        retValValue = rvVal;
    }
    
    public String getRVValue() {
        return retValValue;
    }
    
    public void setParameters(String[] params) {
        parameters = params;
    }
    
    public String[] getParameters() {
        return parameters;
    }
}