/*
 * RemoteGraph.java
 *
 * Created on August 30, 2001, 5:27 PM
 */


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import siena.*;

/** This class is the Frame of the RemoteGraph applications.
 * It contains the main class. RemoteGraph knows how to draw
 * the graph so it only needs notification about the coordinates
 * of the nodes.
 * @author Ricky Chin
 * @version 1.0
 */
public class RemoteGraph extends JFrame {
    
    /** Server where notifications are published and from which are recieved*/
    private HierarchicalDispatcher server;

    /** Creates new RemoteGraph */
    public RemoteGraph() {
        
        /*when the application starts up we must initialize a graph but if two 
          applications initialize two different graphs sending and receiving updates
          about the graph will not make much sense. */
        
        //ask if the graph should be initialized by this instance
        int p = JOptionPane.showConfirmDialog(null, "Are you the originator of the graph?", "", JOptionPane.YES_NO_OPTION);
        boolean publisher = true;
        if (p == 1) {
            publisher = false;
        }
        
        //commented code replaced by setupPanel and Graph GUI
        /*//setup Siena server
        server = new HierarchicalDispatcher();
        while (true) {
            try {
                String master = JOptionPane.showInputDialog(null, "Enter master:");
                String port = JOptionPane.showInputDialog(null, "Enter port:");
                
                server.setMaster("senp://" + master + ":" + Integer.parseInt(port));
                JOptionPane.showMessageDialog(null, "Connected.", "Success", JOptionPane.INFORMATION_MESSAGE);
                break;
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Cannot connect to specified Siena server!\nPlease try again!\n" + e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        try {         
            //create filter here 
            Filter f = new Filter();
            f.addConstraint("Title", "demo");

            server.subscribe(f, this);
            JOptionPane.showMessageDialog(null, "Subscribed to server!");
        }catch (SienaException e) { 
            JOptionPane.showMessageDialog(null, "Cannot subscribe: " + e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        }*/
        
        //frame info
        setTitle("Remote Graph 1.0");
        setSize(240, 320);        
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                
                //shutdown Siena service
                server.shutdown(); 
                
                System.exit(0);
            }
        });
        
        //place the panels on the frame
        Container contentPane = getContentPane();
        SetupPanel settings = new SetupPanel("","");
        
        server = settings.getServer();
        
        GraphGUI canvas = new GraphGUI(server, publisher);
        JTabbedPane menu = new JTabbedPane();
        menu.add("Setup", settings);
        menu.add("Graph", canvas);
        
        contentPane.add(menu, BorderLayout.CENTER);
    }
   
    /** Main used to run the application
     * @param args the command line arguments
     */
    public static void main (String args[]) {
        RemoteGraph app = new RemoteGraph();
        app.show();
    }

}