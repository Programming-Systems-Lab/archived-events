/*
 * Node.java
 *
 * Created on September 2, 2001, 1:05 AM
 */

/**
 *
 * @author  Ricky Chin
 * @version 1.0
 */
public class Node {
    //coordinates of node
    double x;
    double y;

    String label;
    
    /** Creates new Node */
    public Node(String name, double x1, double y1) {
        label = name;
        x = x1;
        y = y1;
    }
}