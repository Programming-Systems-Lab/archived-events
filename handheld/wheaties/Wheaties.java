/*
 * Wheaties.java
 *
 * Created on July 2, 2001, 2:56 AM
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.io.*;

import siena.*;


/** This class holds all the GUI components for Wheaties.
 * It also contains the main method for Wheaties as a
 * stand alone application.
 * @author Ricky Chin
 * @version 1.0
 */
public class Wheaties extends JFrame {

    private HierarchicalDispatcher server;
    private SetupPanel settings;
    
    /** Creates new Wheaties */
    public Wheaties() {
        setTitle("Wheaties Version 1.0");
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
        menu.add("Publish", new PublishPanel(settings.getServer()));
        menu.add("Subscribe", new SubscribePanel(settings.getServer()));
        
        Container contentPane = getContentPane();
        contentPane.add(menu);
        
    }    
    
    /** Run Wheaties the application
     * @param args should be empty
     */
    public static void main(String[] args) {
        Wheaties app = new Wheaties();
        app.show();
    }

}