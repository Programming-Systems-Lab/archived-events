/*
 * GraphPanel.java
 *
 * Created on September 3, 2001, 5:41 PM
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import siena.*;

/** Displays and manages changes in the graph
 * @author Ricky Chin
 * @version 1.0
 */
public class GraphPanel extends Panel implements MouseListener, MouseMotionListener {
    
    /** Server where notifications are published and from which are recieved*/
    HierarchicalDispatcher server; 
    
    /**graph is stored internally in this data structure*/
    Node[] nodes = new Node[3];
    
    //see how many are painted and sent
    long numGot = 0;
    long numSent = 0;
    long numPainted = 0;
    
    long start;
    
    /**used for versioning control and throttling of notification updating*/
    long version = 0;
    
    long startDelta = 0;
    
    JLabel status;
        
    /** Creates new GraphPanel
     * @param pub whether it is the originator copy
     * @param s siena server
     */
    public GraphPanel(boolean pub, HierarchicalDispatcher s, JLabel counters) {
        super();
        
        status = counters;
        start = System.currentTimeMillis();
        server = s;
        
        //initialize graph
        if (pub) {
            nodes[0] = new Node("One", 10 + 100*Math.random(), 10 + 150*Math.random());
            nodes[1] = new Node("Two", 10 + 100*Math.random(), 10 + 150*Math.random());
            nodes[2] = new Node("Three", 10 + 100*Math.random(), 10 + 150*Math.random());
            repaint();
        }else {//or wait till receive notification
            nodes[0] = nodes[1] = nodes[2] = null;
        
            //ask for updated graph
            try {
                Notification n = new Notification();
                n.putAttribute("Title", "demo");
                n.putAttribute("Redraw", "Redraw");
                server.publish(n);
            } catch (SienaException t) {
                JOptionPane.showMessageDialog(null, "Cannot publish: " + t.toString(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        addMouseListener(this);
        repaint();
    }

    //node user is dragging with mouse
    Node pick;
    
    //useful for double buffering
    Image offscreen;
    Dimension offscreensize;
    Graphics offgraphics;

    //some nice constants
    final Color selectColor = Color.pink;
    final Color edgeColor = Color.black;
    final Color nodeColor = new Color(250, 220, 100);
    final Color stressColor = Color.darkGray;
    final Color arcColor1 = Color.black;
    final Color arcColor2 = Color.pink;
    final Color arcColor3 = Color.red;

    /** paint a node
     * @param g graphics
     * @param n node to paint
     * @param fm font metrics
     */
    public void paintNode(Graphics g, Node n, FontMetrics fm) {
        //the uninitialized
        if (n == null) {
            return;
        }
        
	int x = (int)n.x;
	int y = (int)n.y;
        
        Color tempColor = (n == pick) ? selectColor : nodeColor;
	
        int w = fm.stringWidth(n.label) + 10;
	int h = fm.getHeight() + 4;       
        
        //do fill box here
        g.setColor(tempColor);
        g.fillRect(x - w/2, y - h / 2, w, h);
        //do outline of box
        g.setColor(Color.black);
        g.drawRect(x - w/2, y - h / 2, w-1, h-1);
        //do node label
        g.drawString(n.label, x - (w-10)/2, (y - (h-4)/2) + fm.getAscent());
    }
    
    void drawEdges(Node n1, Node n2, Graphics og) {
        if (n1 == null || n2 == null) {
            return;
        }
        
        int x1 = (int)n1.x;
	    int y1 = (int)n1.y;
	    int x2 = (int)n2.x;
	    int y2 = (int)n2.y;
            
            //calculate weight
	    int len = (int)Math.abs(Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2)) - 50);
	    og.setColor((len < 10) ? arcColor1 : (len < 20 ? arcColor2 : arcColor3)) ;
	    
            offgraphics.drawLine(x1, y1, x2, y2);
            
            String lbl = String.valueOf(len);
            
            offgraphics.setColor(stressColor);
            offgraphics.drawString(lbl, x1 + (x2-x1)/2, y1 + (y2-y1)/2);
            offgraphics.setColor(edgeColor);
    }

    /** update the panel displaying the graph
     * @param g graphics
     */
    public synchronized void update(Graphics g) {
	Dimension d = getSize();
	if ((offscreen == null) || (d.width != offscreensize.width) || (d.height != offscreensize.height)) {
            
            //used for double buffering 
	    offscreen = createImage(d.width, d.height);
	    offscreensize = d;
	    if (offgraphics != null) {
	        offgraphics.dispose();
	    }
	    offgraphics = offscreen.getGraphics();
	    offgraphics.setFont(getFont());
	}
      
	offgraphics.setColor(getBackground());
	offgraphics.fillRect(0, 0, d.width, d.height);
        
        
	drawEdges(nodes[0], nodes[1], offgraphics);
        drawEdges(nodes[2], nodes[1], offgraphics);
        drawEdges(nodes[2], nodes[0], offgraphics);
        
        FontMetrics fm = offgraphics.getFontMetrics();
        for(int i = 0; i < 3; i++) {
            paintNode(offgraphics, nodes[i], fm);
        }
        
	g.drawImage(offscreen, 0, 0, null);
    }

    public void paint(Graphics g) {
        //hope this creates less flickering
        update(g);
    }
    
    public void mouseReleased(MouseEvent e) {
        //for uninitialized
        if (nodes[0] == null) {
            try {
                Notification n = new Notification();
                n.putAttribute("Title", "demo");
                n.putAttribute("Redraw", "Redraw");
                server.publish(n);
            } catch (SienaException t) {
                JOptionPane.showMessageDialog(null, "Cannot publish: " + t.toString(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }
        
        removeMouseMotionListener(this);
        if (pick != null) {
            pick.x = e.getX();
            pick.y = e.getY();
            pick = null;
        }
        
        try {
            server.publish(getNotification(false));
        } catch (SienaException t) {
            JOptionPane.showMessageDialog(null, "Cannot publish: " + t.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        
	repaint();
	e.consume();
    }
    
    public void mouseEntered(final java.awt.event.MouseEvent p1) {
    }
    public void mouseClicked(MouseEvent p1) {}
    
    public void mousePressed(MouseEvent e) {
        //for uninitialized
        if (nodes[0] == null) {
            try {
                Notification n = new Notification();
                n.putAttribute("Title", "demo");
                n.putAttribute("Redraw", "Redraw");
                server.publish(n);
            } catch (SienaException t) {
                JOptionPane.showMessageDialog(null, "Cannot publish: " + t.toString(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }
        
        addMouseMotionListener(this);
	double bestdist = Double.MAX_VALUE;
	int x = e.getX();
	int y = e.getY();
        
	for (int i = 0 ; i < 3 ; i++) {
	    Node n = nodes[i];
	    double dist = (n.x - x) * (n.x - x) + (n.y - y) * (n.y - y);
	    if (dist < bestdist) {
		pick = n;
		bestdist = dist;
	    }
	}
        
        //picked node will change color for user but not for anybody else
	pick.x = x;
	pick.y = y;
        
        try {
            server.publish(getNotification(false));
        } catch (SienaException t) {
            JOptionPane.showMessageDialog(null, "Cannot publish: " + t.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        
	repaint();
	e.consume();
    }
    
    public void mouseExited(final java.awt.event.MouseEvent p1) {}
    
    public void mouseDragged(MouseEvent e) {
        //for uninitialized
        if (nodes[0] == null) {
            try {
                Notification n = new Notification();
                n.putAttribute("Title", "demo");
                n.putAttribute("Redraw", "Redraw");
                server.publish(n);
            } catch (SienaException t) {
                JOptionPane.showMessageDialog(null, "Cannot publish: " + t.toString(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }
        
        pick.x = e.getX();
	pick.y = e.getY();
        
        try {
            server.publish(getNotification(false));
        } catch (SienaException t) {
            JOptionPane.showMessageDialog(null, "Cannot publish: " + t.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        
	repaint();
	e.consume();
    }
    
    public void mouseMoved(final java.awt.event.MouseEvent p1) {}
    
    /** Process notification of change in graph in underlying
     * graph data structure and this paint the change on the
     * panel
     * @param n notification of graph change
     */
    public synchronized void updateGraph(Notification n, long delta) {
        numGot++;
        status.setText("S" + numSent + " : R" + numGot + " : P" + numPainted + " : T" + ((System.currentTimeMillis() - start) / 1000));
        AttributeValue v = n.getAttribute("Redraw");
        if (v == null) {
            startDelta = delta;

            //since Siena sends in parallel need to 
            //progressively process notifications
            v = n.getAttribute("Time");
            if (v.longValue() - version > 0) {
                version = v.longValue();

                v = n.getAttribute("OneX");
                double x1 = v.doubleValue();

                v = n.getAttribute("OneY");
                double y1 = v.doubleValue();

                v = n.getAttribute("TwoX");
                double x2 = v.doubleValue();

                v = n.getAttribute("TwoY");
                double y2 = v.doubleValue();

                v = n.getAttribute("ThreeX");
                double x3 = v.doubleValue();

                v = n.getAttribute("ThreeY");
                double y3 = v.doubleValue();

                nodes[0] = new Node("One", x1, y1);
                nodes[1] = new Node("Two", x2, y2);
                nodes[2] = new Node("Three", x3, y3);

                repaint();
                numPainted++;
                status.setText("S" + numSent + " : R" + numGot + " : P" + numPainted + " : T" + ((System.currentTimeMillis() - start) / 1000));
            }
        }
    }
    
    /** Produce notification of the properties of the graph
     * Specifically, the coordinates of the nodes of the
     * graph to be published.
     * @return notification to published
     */
    public Notification getNotification(boolean rd) {
        if (rd) {
            Notification n = new Notification();
            n.putAttribute("Title", "demo");
            n.putAttribute("Redraw", "Redraw");
            numSent++;
            status.setText("S" + numSent + " : R" + numGot + " : P" + numPainted + " : T" + ((System.currentTimeMillis() - start) / 1000));
            return n;
        }
        
        Notification n = new Notification();
        n.putAttribute("Title", "demo");
        version += (System.currentTimeMillis() - startDelta);
        n.putAttribute("Time", version);
        n.putAttribute("OneX", nodes[0].x);
        n.putAttribute("OneY", nodes[0].y);
        n.putAttribute("TwoX", nodes[1].x);
        n.putAttribute("TwoY", nodes[1].y);
        n.putAttribute("ThreeX", nodes[2].x);
        n.putAttribute("ThreeY", nodes[2].y);
        numSent++;
        status.setText("S" + numSent + " : R" + numGot + " : P" + numPainted + " : T" + ((System.currentTimeMillis() - start) / 1000));
        return n;
    }
        
        
        
    
    /** main for testing
     */
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
        contentPane.add(new GraphPanel(true, new HierarchicalDispatcher(), new JLabel()), BorderLayout.CENTER);
        contentPane.add(new JButton("Test"), BorderLayout.SOUTH);
        app.show();
        app.repaint();
    }
}