/*
 * GUIPanel.java
 *
 * Created on July 25, 2001, 7:07 PM
 */

import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import siena.*;

/**
 * The "canvas" for RemoteGUI
 * @author  Ricky Chin
 * @version 1.0
 */
public class GUIPanel extends Panel implements Notifiable {

    HierarchicalDispatcher server;

    //Thread drawer;
    
    Image backup;
    Image current;
    
    
    
    //buffer for the drawing commands that come in as notifications
    java.util.List commands;
    
    boolean subscribed;
    
    /** Creates new GUIPanel */
    public GUIPanel(HierarchicalDispatcher s) {
        commands = Collections.synchronizedList(new LinkedList());
        //drawer = new Thread(this);
        server = s;
        subscribed = false;
    }
    
    //this method can access Graphics which is needed to paint picture
    public void paint(Graphics gg) {        
        if (backup == null) {
            gg.setColor(Color.white);
            gg.fillRect(0, 0, 240, 320);
            
            //System.out.println("Hereby null");
        }else {
            gg.drawImage(backup, 0, 0, null);
        }
        
        while (current == null) {
            current = createImage(this.getWidth(), this.getHeight());
            //System.out.println("Here null");
        }
    }
    
    public synchronized void paintNotification(Notification n) {
        
        StringTokenizer st = new StringTokenizer((n.getAttribute("Commands")).stringValue());

        while (st.hasMoreTokens()) {
            String action = st.nextToken();
            
            if (action.equals("DrawLine")) {        
                Graphics g = current.getGraphics();
                //System.out.println(action);
                g.setColor(new Color (Integer.parseInt(st.nextToken())));
                int x1 = Integer.parseInt(st.nextToken());
                int y1 = Integer.parseInt(st.nextToken());
                int x2 = Integer.parseInt(st.nextToken());
                int y2 = Integer.parseInt(st.nextToken());

                g.drawLine(x1, y1, x2, y2);
                }

            if (action.equals("DrawRect")) {
                Graphics g = current.getGraphics();
                //System.out.println(action);
                g.setColor(new Color (Integer.parseInt(st.nextToken())));

                int x = Integer.parseInt(st.nextToken());
                int y = Integer.parseInt(st.nextToken());
                int width = Integer.parseInt(st.nextToken());
                int height = Integer.parseInt(st.nextToken());

                g.drawRect(x, y, width, height);
            }

            if (action.equals("FillRect")) {
                Graphics g = current.getGraphics();
                //System.out.println(action);
                g.setColor(new Color (Integer.parseInt(st.nextToken())));

                int x = Integer.parseInt(st.nextToken());
                int y = Integer.parseInt(st.nextToken());
                int width = Integer.parseInt(st.nextToken());
                int height = Integer.parseInt(st.nextToken());

                g.fillRect(x, y, width, height);
            }

            if (action.equals("DrawString")) {
                Graphics g = current.getGraphics();
                //System.out.println(action);
                //text is black
                g.setColor(new Color (Integer.parseInt(st.nextToken())));

                String text = st.nextToken();
                int x = Integer.parseInt(st.nextToken());
                int y = Integer.parseInt(st.nextToken());

                g.drawString(text, x, y);
            }

            if (action.equals("End")) {
                //System.out.println(action);
                backup = current;
                repaint();
            }

            if (action.equals("New")) {
                //System.out.println(action);
                while (current == null) {
                    repaint();
                    System.out.println("Infinite loop");
                }
            }
        }
         
    }
    
    //know when we unsubscribe
    public void setSubscribed(boolean b) {
        subscribed = b;
    }

    /** sends a <code>Notification</code> to this <code>Notifable</code>
     *
     * Since version 1.0.1 of the Siena API it is safe to modify the
     * Notification object received through this method.  Note that:
     * <ol>
     *
     * <li><em>any</em> previous version of the Siena API assumes that
     *     clients <em>do not modify</em> these notifications;
     *
     * <li>the current solution incurrs in an unnecessary cost by
     *     having to duplicate every notification.  Therefore, it
     *     is a <em>temporary solution</em>.  The plan is to
     *     implement <em>immutable</em> notifications and to pass
     *     those to subscribers.
     *
     * </ol>
     * necessary duplication of notifications can be expensive,
     * especially if the same notification must be copied to numerous
     * subscribers.
     *
     * @param n notification passed to the notifiable
     * @see Siena#subscribe(Filter,Notifiable)
     */
    public synchronized void notify(Notification n) throws SienaException {      
        paintNotification(n);
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
        //System.out.println("cool");
        for(int i = 0; i < s.length; i++) {
            //System.out.println("add");
            commands.add(s[i]);
        }
        if (commands.size() >= 2) {
            while (!commands.isEmpty()) {
                Notification note = (Notification)commands.remove(0);
                paintNotification(note);
            }
        }
    }

}