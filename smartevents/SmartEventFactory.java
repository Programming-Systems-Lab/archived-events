/*
 * SmartEventFactory.java
 *
 * Created on December 18, 2002, 10:33 AM
 */

import java.util.ArrayList;

/**
 *
 * @author  Ian Zilla -- PSL Group Fall 2002
 */
public class SmartEventFactory {
    
    private ArrayList smartEvents;
    
    /** Creates a new instance of SmartEventFactory */
    public SmartEventFactory() {
        smartEvents = new ArrayList();
    }
    
    public SmartEvent buildMethodCallEvent(String fileName, String c_ip, int c_port, String c_timeStamp,
                                            long c_seqNum, String c_version, String m_name, String m_pckg,
                                             String m_rvName, String m_rvType, String m_rvVal, String[] m_params) {
        SmartEvent se = new SmartEvent(fileName);
        Context context = new Context(se.getFileName(), c_ip, c_port, c_timeStamp, c_seqNum, c_version);
        MethodCall mc = new MethodCall(se.getFileName(), m_name, m_pckg, m_rvName, m_rvType, m_rvVal, m_params);
        se.setHeader((Context)context);
        se.addBodyBlock((MethodCall)mc);
        smartEvents.add((SmartEvent)se);
        return se;
    }
    
    public SmartEvent buildArchMutationEvent() {
        return null;
    }
    
    public SmartEvent buildComponentStatusEvent(String fileName, String c_ip, int c_port, String c_timeStamp,
                                                 long c_seqNum, String c_version, String cs_type, String cs_app,
                                                  String cs_description ) {
        SmartEvent se = new SmartEvent(fileName);
        Context context = new Context(se.getFileName(), c_ip, c_port, c_timeStamp, c_seqNum, c_version);
        ComponentStatus cs = new ComponentStatus(se.getFileName(), cs_type, cs_app, cs_description);
        se.setHeader(context);
        se.addBodyBlock(cs);
        smartEvents.add(se);
        return se;
    }
    
    public void addMethodCallBlock(SmartEvent se, String m_name, String m_pckg, String m_rvName, 
                                    String m_rvType, String m_rvVal, String[] m_params) {
        MethodCall mc = new MethodCall(se.getFileName(), m_name, m_pckg, m_rvName, m_rvType, m_rvVal, m_params);
        se.addBodyBlock(mc);
    }
    
    public void addArchMutationBlock(SmartEvent se) {
        ;
    }
    
    public void addComponentStatusBlock(SmartEvent se, String cs_type, String cs_app, String cs_description) {
        ComponentStatus cs = new ComponentStatus(se.getFileName(), cs_type, cs_app, cs_description);
        se.addBodyBlock(cs);
    }
    
    public ArrayList getSmartEvents() {
        return smartEvents;
    }
    
}
