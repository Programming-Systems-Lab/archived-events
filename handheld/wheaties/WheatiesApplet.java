/*
 * WheatiesApplet.java
 *
 * Created on July 23, 2001, 11:00 PM
 */

import javax.swing.*;

import siena.*;

/** This is the applet version of Wheaties. Since it uses Swing components,
 * you must have a Java 2 plugin to run it.
 * @author Ricky Chin
 * @version 1.0
 */
public class WheatiesApplet extends java.applet.Applet {
    
    private HierarchicalDispatcher server;
    private SetupPanel settings;

    /** Creates the whole gui
     */
    public void init () {
        
        settings = new SetupPanel("", "");
        
        
        server = settings.getServer();
            
        JTabbedPane menu = new JTabbedPane();
        menu.add("Settings", settings);
        menu.add("Publish", new PublishPanel(settings.getServer()));
        menu.add("Subscribe", new SubscribePanel(settings.getServer()));
        
        add(menu);
    }
    
    /** Destroys the applet and shutdown the Siena service
     *
     */
    public void destroy () {
        super.destroy();
        server.shutdown();
    }

}