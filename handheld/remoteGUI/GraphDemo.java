/*
 * GraphDemo.java
 * based on GraphLayout from Demos included in JDK1.3
 * Created on July 25, 2001, 8:44 PM
 */

import siena.*;

import java.io.*;

import java.util.*;
import java.awt.*;
import java.applet.Applet;
import javax.swing.*;
import java.awt.event.*;

class Node {
    double x;
    double y;

    boolean fixed;

    String lbl;
}


class Edge {
    int from;
    int to;

    double len;
}


class GraphPanel extends Panel
    implements MouseListener, MouseMotionListener {
        
    HierarchicalDispatcher server;
        
    Panel graph;
    int nnodes;
    Node nodes[] = new Node[100];

    int nedges;
    Edge edges[] = new Edge[200];

    GraphPanel(Panel graph, HierarchicalDispatcher s) {
        server = s;
	this.graph = graph;
	addMouseListener(this);
        repaint();
    }

    int findNode(String lbl) {
	for (int i = 0 ; i < nnodes ; i++) {
	    if (nodes[i].lbl.equals(lbl)) {
		return i;
	    }
	}
	return addNode(lbl);
    }
    int addNode(String lbl) {
	Node n = new Node();
	n.x = 10 + 150*Math.random();
	n.y = 10 + 200*Math.random();
	n.lbl = lbl;
	nodes[nnodes] = n;
	return nnodes++;
    }
    void addEdge(String from, String to, int len) {
	Edge e = new Edge();
	e.from = findNode(from);
	e.to = findNode(to);
	e.len = len;
	edges[nedges++] = e;
    }

    Node pick;
    boolean pickfixed;
    Image offscreen;
    Dimension offscreensize;
    Graphics offgraphics;

    final Color fixedColor = Color.red;
    final Color selectColor = Color.pink;
    final Color edgeColor = Color.black;
    final Color nodeColor = new Color(250, 220, 100);
    final Color stressColor = Color.darkGray;
    final Color arcColor1 = Color.black;
    final Color arcColor2 = Color.pink;
    final Color arcColor3 = Color.red;

    public String paintNode(Graphics g, Node n, FontMetrics fm) {
	int x = (int)n.x;
	int y = (int)n.y;
        
        Color tempColor = (n == pick) ? selectColor : (n.fixed ? fixedColor : nodeColor);
	
        int w = fm.stringWidth(n.lbl) + 10;
	int h = fm.getHeight() + 4;
        
        //publish draw filled box
        String note = " FillRect " +  tempColor.getRGB() + " " + (x - w/2) + " " + (y - h/2) + " " + w + " " + h + " ";        
        
        //publish outline of box
        note += " DrawRect " + (Color.black).getRGB() + " " + (x - w/2) + " " + (y - h/2) + " " + (w - 1) + " " + (h - 1) + " ";
        
        //publish node label
        note += " DrawString " + (Color.black).getRGB() + " " + n.lbl + " " + (x - (w-10)/2) + " " + ((y - (h-4)/2) + fm.getAscent() + " ");       
        
        
        
        //hope by publishing first to give the remote GUI a head start
        
        //do fill box here
        g.setColor(tempColor);
        g.fillRect(x - w/2, y - h / 2, w, h);
        //do outline of box
        g.setColor(Color.black);
        g.drawRect(x - w/2, y - h / 2, w-1, h-1);
        //do node label
        g.drawString(n.lbl, x - (w-10)/2, (y - (h-4)/2) + fm.getAscent());
        
        return note;
    }

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

        Notification note = new Notification();
	  note.putAttribute("title", "demo");
        
        StringBuffer temp = new StringBuffer(" New FillRect " + getBackground().getRGB() + " " + 0 + " " + 0 + " " + d.width + " " + d.height + " ");
        
	offgraphics.setColor(getBackground());
	offgraphics.fillRect(0, 0, d.width, d.height);
        
        
	for (int i = 0 ; i < nedges ; i++) {
	    Edge e = edges[i];
	    int x1 = (int)nodes[e.from].x;
	    int y1 = (int)nodes[e.from].y;
	    int x2 = (int)nodes[e.to].x;
	    int y2 = (int)nodes[e.to].y;
	    int len = (int)Math.abs(Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2)) - e.len);
	    offgraphics.setColor((len < 10) ? arcColor1 : (len < 20 ? arcColor2 : arcColor3)) ;
            
            //publish edges
            temp.append(" DrawLine " + offgraphics.getColor().getRGB() + " " + x1 + " " + y1 + " " + x2 + " " + y2 + " ");
            
            
            //draw edges
            offgraphics.drawLine(x1, y1, x2, y2);
            
	    
            String lbl = String.valueOf(len);

            temp.append(" DrawString " + stressColor.getRGB() + " " + lbl + " " + (x1 + (x2-x1)/2) + " " + (y1 + (y2-y1)/2) + " ");
            

            offgraphics.setColor(stressColor);
            offgraphics.drawString(lbl, x1 + (x2-x1)/2, y1 + (y2-y1)/2);
            offgraphics.setColor(edgeColor);

	}

	FontMetrics fm = offgraphics.getFontMetrics();
	for (int i = 0 ; i < nnodes ; i++) {
	    temp.append(paintNode(offgraphics, nodes[i], fm));
	}
        
        temp.append(" End");
        note.putAttribute("Commands", temp.toString());
        try {
            server.publish(note);
        } catch (SienaException e) {
            JOptionPane.showMessageDialog(null, "Cannot publish: " + e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        
	g.drawImage(offscreen, 0, 0, null);
    }

    //1.1 event handling
    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        addMouseMotionListener(this);
	double bestdist = Double.MAX_VALUE;
	int x = e.getX();
	int y = e.getY();
	for (int i = 0 ; i < nnodes ; i++) {
	    Node n = nodes[i];
	    double dist = (n.x - x) * (n.x - x) + (n.y - y) * (n.y - y);
	    if (dist < bestdist) {
		pick = n;
		bestdist = dist;
	    }
	}
	pickfixed = pick.fixed;
	pick.fixed = true;
	pick.x = x;
	pick.y = y;
	repaint();
	e.consume();
    }

    public void mouseReleased(MouseEvent e) {
        removeMouseMotionListener(this);
        if (pick != null) {
            pick.x = e.getX();
            pick.y = e.getY();
            pick.fixed = pickfixed;
            pick = null;
        }
	repaint();
	e.consume();
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
	pick.x = e.getX();
	pick.y = e.getY();
	repaint();
	e.consume();
    }

    public void mouseMoved(MouseEvent e) {
    }
}


public class GraphDemo extends Applet {
    HierarchicalDispatcher server;
    SetupPanel settings;
    GraphPanel panel;

    public void init() {
        
        setSize(240, 320);
        setLayout(new BorderLayout());
        
	settings = new SetupPanel("", "");
        
        
        server = settings.getServer();
            
        JTabbedPane menu = new JTabbedPane();
        menu.add("Settings", settings);
                
        
	Panel temp = new Panel();
	
	temp.setLayout(new BorderLayout());

	panel = new GraphPanel(temp, server);
	temp.add("Center", panel);

	String edges = getParameter("edges");
	for (StringTokenizer t = new StringTokenizer(edges, ",") ; t.hasMoreTokens() ; ) {
	    String str = t.nextToken();
	    int i = str.indexOf('-');
	    if (i > 0) {
		int len = 50;
		int j = str.indexOf('/');
		if (j > 0) {
		    len = Integer.valueOf(str.substring(j+1)).intValue();
		    str = str.substring(0, j);
		}
		panel.addEdge(str.substring(0,i), str.substring(i+1), len);
	    }
	}
	Dimension d = temp.getSize();
	String center = getParameter("center");
	if (center != null){
	    Node n = panel.nodes[panel.findNode(center)];
	    n.x = d.width / 2;
	    n.y = d.height / 2;
	    n.fixed = true;
	}

        menu.add("Graph", temp);
	add(menu, BorderLayout.CENTER);
    }

    public void destroy() {
	server.shutdown();
        remove(panel);
    }

    public String getAppletInfo() {
	return "Title: Graph Demo for Remote GUI \nAuthor: Ricky Chin";
    }

    public String[][] getParameterInfo() {
	String[][] info = {
	    {"edges", "delimited string", "A comma-delimited list of all the edges.  It takes the form of 'C-N1,C-N2,C-N3,C-NX,N1-N2/M12,N2-N3/M23,N3-NX/M3X,...' where C is the name of center node (see 'center' parameter) and NX is a node attached to the center node.  For the edges connecting nodes to eachother (and not to the center node) you may (optionally) specify a length MXY separated from the edge name by a forward slash."},
	    {"center", "string", "The name of the center node."}
	};
	return info;
    }

}
