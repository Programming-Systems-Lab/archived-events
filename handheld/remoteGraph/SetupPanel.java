/*
 * SetupPanel.java
 *
 * Created on July 18, 2001, 6:06 PM
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import siena.*;

/** This setup panel lets the user change the Siena server to connect to
 * by specifying the master and port.
 * @author Ricky Chine
 * @version 1.0
 */
public class SetupPanel extends JPanel implements ActionListener {
    
    String mas;
    String po;
        
    private HierarchicalDispatcher server;
    
    private JTextField master;
    private JTextField port;
    
    private JButton connectButton;
    private JButton cancelButton;
    private JButton disconnectButton;

    /** Creates new SetupPanel
     * @param sm the master name
     * @param sp the port number of the master
     */
    public SetupPanel(String sm, String sp) {
        
        mas = sm;
        po = sp;
        
        server = new HierarchicalDispatcher();
        
        JPanel masterPanel = new JPanel();
        
        masterPanel.add(new JLabel("Master: "));
        master = new JTextField(10);
        master.setText(sm);
        masterPanel.add(master);
        add(masterPanel);

        JPanel portPanel = new JPanel();
        
        portPanel.add(new JLabel("Port: "));
        port = new JTextField(10);
        port.setText(sp);
        portPanel.add(port);
        add(portPanel);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 1));
        
        //connect
        connectButton = new JButton("Connect");
        connectButton.setToolTipText("Connect to Siena server");
        connectButton.addActionListener(this);
        buttonPanel.add(connectButton);
        
        //cancel
        cancelButton = new JButton("Cancel");
        cancelButton.setToolTipText("Abort change of master");
        cancelButton.addActionListener(this);        
        buttonPanel.add(cancelButton);
        
        //disconnect
        disconnectButton = new JButton("Disconnect");
        disconnectButton.setToolTipText("Disconnect from server");
        disconnectButton.addActionListener(this);        
        buttonPanel.add(disconnectButton);
        
        add(buttonPanel);
        
        if (!("".equals(sm))) {
            try {
                if (!("".equals(sp))) {
                    server.setMaster("senp://" + sm + ":" + Integer.parseInt(sp));
                }else {
                    server.setMaster("senp://" + sm);
                }

                JOptionPane.showMessageDialog(null, "Connected to Siena server!", "Success", JOptionPane.INFORMATION_MESSAGE);

            //for whatever reason
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Cannot connect to specified Siena server!\nPlease try again!\n" + e.toString(),
                                                "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /** This is used by other GUI components in a Wheaties application to get the
     * configured HierarchicalDispatcher
     * @return the configured HierarchicalDispatcher
     */
    public HierarchicalDispatcher getServer() {
        return server;
    }
    
    /** Used by Wheaties to save the name of the master
     * @return the name of the master
     *
     */
    public String getHost() {
        return mas;
    }
    
    /** Used by Wheaties to save port setting
     * @return the port number of the master
     */
    public String getPort() {
        return po;
    }
    
    
    public void actionPerformed(ActionEvent evt) {
        Object source = evt.getSource();
        
        if (source == connectButton) {       
            try {
                if (!("".equals(port.getText()))) {
                    server.setMaster("senp://" + master.getText() + ":" + Integer.parseInt(port.getText()));
                }else {
                    server.setMaster("senp://" + master.getText());
                }
                mas = master.getText();
                po = port.getText();
                                
                JOptionPane.showMessageDialog(null, "Connected.", "Success", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Cannot connect to specified Siena server!\nPlease try again!\n" + e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }else {
            if (source == cancelButton) {
                master.setText(mas);
                port.setText(po);
            }else {
                if (source == disconnectButton) {
                    server.shutdown();
                }
            }
        }
    }
}