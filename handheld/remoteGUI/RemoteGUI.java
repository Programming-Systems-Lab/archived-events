/*
 * RemoteGUI.java
 *
 * Created on July 25, 2001, 6:55 PM
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.io.*;

import siena.*;

/**
 * This draws a picture according to notifications from Siena server
 * @author  Ricky Chin
 * @version 1.0
 */
public class RemoteGUI extends JFrame {
    
    private HierarchicalDispatcher server;
    private SetupPanel settings;

    /** Creates new RemoteGUI */
    public RemoteGUI() {
        setTitle("Remote GUI 1.0");
        setSize(240, 320);
        
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                
                //shutdown Siena service
                server.shutdown(); 
                
                System.exit(0);
            }
        });
        
        settings = new SetupPanel("", "");
        
        server = settings.getServer();
            
        //create tabbed pane with setting, publish and subscribe panels
        JTabbedPane menu = new JTabbedPane();
        menu.add("Settings", settings);
        
        GUIPanel temp = new GUIPanel(server);
        menu.add("Siena GUI", new JScrollPane(temp));
        menu.add("Subscribe", new SubscribePanel(server, temp));
        
        Container contentPane = getContentPane();
        contentPane.add(menu);
        
    }

    /**
    * @param args the command line arguments
    */
    public static void main (String args[]) {
        RemoteGUI app = new RemoteGUI();
        app.show();
    }

}