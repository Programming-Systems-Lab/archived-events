/*
 * GraphGUI.java
 *
 * Created on August 30, 2001, 5:45 PM
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import siena.*;

/**
 *
 * @author  Ricky Chin
 * @version 1.0
 */
public class GraphGUI extends JPanel implements ActionListener, Notifiable {
    
    /**The originator of the graph or not. We can't have to different initial graphs*/
    boolean publisher;
      
    /** Server where notifications are published and from which are recieved*/
    HierarchicalDispatcher server;
    
    /**panel where graph will be displayed*/
    GraphPanel canvas;

    JLabel status = new JLabel("");
    
    /**button to notify that graph panel should be updated*/
    JButton reDraw;
    
    /** Creates new GraphGUI */
    public GraphGUI(HierarchicalDispatcher s, boolean pub) {
        super();
        
        server = s;
        try {         
            //create filter here 
            Filter f = new Filter();
            f.addConstraint("Title", "demo");

            server.subscribe(f, this);
            //JOptionPane.showMessageDialog(null, "Subscribed to server!");
        }catch (SienaException e) { 
            JOptionPane.showMessageDialog(null, "Cannot subscribe: " + e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        publisher = pub;
        
        setLayout(new BorderLayout());
        
        add(status, BorderLayout.NORTH);
        
        canvas = new GraphPanel(pub, server, status);
        add(canvas, BorderLayout.CENTER);
        
        reDraw = new JButton("ReDraw");
        reDraw.addActionListener(this);
        add(reDraw, BorderLayout.SOUTH);
    }
    
    

    /** sends a <code>Notification</code> to this <code>Notifable</code>
     *
     * Since version 1.0.1 of the Siena API it is safe to modify the
     * Notification object received through this method.  Note that:
     * <ol>
     *
     * <li><em>any</em> previous version of the Siena API assumes that
     *    clients <em>do not modify</em> these notifications;
     *
     * <li>the current solution incurrs in an unnecessary cost by
     *    having to duplicate every notification.  Therefore, it
     *    is a <em>temporary solution</em>.  The plan is to
     *    implement <em>immutable</em> notifications and to pass
     *    those to subscribers.
     *
     * </ol>
     * necessary duplication of notifications can be expensive,
     * especially if the same notification must be copied to numerous
     * subscribers.
     * @see Siena#subscribe(Filter,Notifiable)
     * @param n notification passed to the notifiable
     * @throws SienaException  */
    public void notify(Notification n) throws SienaException {
        AttributeValue v = n.getAttribute("Redraw");
        //a normal notification
        canvas.updateGraph(n, System.currentTimeMillis());
        

        //only the originator can send info about "the" graph
        if (publisher && (v != null)) {
            try {
                server.publish(canvas.getNotification(false));
            } catch (SienaException t) {
                JOptionPane.showMessageDialog(null, "Cannot publish: " + t.toString(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }//else do nothing because you might have outdated version anyway

    }
    
    /** sends a sequence of <code>Notification</code>s to this
     * <code>Notifable</code>
     *
     * Since version 1.0.1 of the Siena API it is safe to modify the
     * Notification objects received through this method.  Please
     * read the notes in the above documentation of {@link
     * #notify(Notification)}, which apply to this method as well.
     *
     * @param s sequence of notifications passed to the notifiable
     * @see Siena#subscribe(Pattern,Notifiable)
     */
    public void notify(Notification[] s) throws SienaException {
        for(int i = 0; i < s.length; i++) {
            notify(s[i]);
        }
    }
    
    /** A hit to the Redraw button sends a "Redraw" notification.
     */
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        
        if (source == reDraw && !publisher) {
            try {
                server.publish(canvas.getNotification(true));
            } catch (SienaException t) {
                JOptionPane.showMessageDialog(null, "Cannot publish: " + t.toString(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**main for testing purposes*/
    public static void main(String[] args) {
        JFrame app = new JFrame();
        app.setTitle("Test 1.0");
        app.setSize(240, 320);
        
        app.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        
        Container contentPane = app.getContentPane();
        JTabbedPane menu = new JTabbedPane();
        menu.add("Graph", new GraphGUI(new HierarchicalDispatcher(), true));
        contentPane.add(menu);
        app.show();
    }
}