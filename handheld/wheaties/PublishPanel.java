import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import java.util.*;
import siena.*;

/** This publish panel dispalys the attributes in your notification.
 * When you publish it, we start with an empty notification again.
 * @author Ricky Chin
 * @version 1.0
 */
class PublishPanel extends JPanel implements ActionListener {
    
    private HierarchicalDispatcher server;
    
    private DefaultTableModel notificationBody;
    private JTable table;
    
    private JButton addButton;
    private JButton removeButton;
    private JButton publishButton;
    
    /** Creates a PublishPanel
     * @param s properly configured Siena HierarchicalDispatcher
     */
    public PublishPanel(HierarchicalDispatcher s) {
        
        server = s;
        
        setLayout(new BorderLayout());
        
        notificationBody = new DefaultTableModel();
        notificationBody.addColumn("Attribute Name");
        notificationBody.addColumn("Attribute Value");
        
        table = new JTable(notificationBody) {
            public boolean isCellEditable(int row, int col) {return false;}
        };
        table.getTableHeader().setReorderingAllowed(false);
        table.setCellSelectionEnabled(false);
        table.setColumnSelectionAllowed(false);
        table.setRowSelectionAllowed(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        add(new JScrollPane(table), BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 3));
        
        addButton = new JButton("Add");
        addButton.setToolTipText("Add attributes");
        addButton.addActionListener(this);
        
        //need to set buttons to appear on iPaq's screen
        removeButton = new JButton("Remove");
        removeButton.setToolTipText("Remove selected attribute");
        removeButton.setMargin(new Insets(1, 1, 1, 1));
        removeButton.addActionListener(this);
        
        publishButton = new JButton("Publish");
        publishButton.setToolTipText("Publish notification");
        publishButton.setMargin(new Insets(1, 1, 1, 1));
        publishButton.addActionListener(this);
        
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(publishButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    public void actionPerformed(ActionEvent evt) {
        Object source = evt.getSource();
        
        if (source == addButton) {
            String name = JOptionPane.showInputDialog("Enter Attribute name: ");
            
            if (!name.equals("")) {
                
                String value = JOptionPane.showInputDialog("Enter Attribute value: ");
                
                String[] temp = { name, value };
            
                notificationBody.addRow(temp);
            }
            
        }else {
            if (source == publishButton) {
                try {
                    Notification note = new Notification();
                    Vector list = notificationBody.getDataVector();
                    Iterator it = list.iterator();
                    while (it.hasNext()) {
                        Vector attr = (Vector)(it.next());
                        note.putAttribute((String)(attr.get(0)), (String)(attr.get(1)));
                    }
                    
                    server.publish(note);
                    JOptionPane.showMessageDialog(null, "Notification published!");
                    String[] temp = {"Attribute Name", "Attribute Value"};
                    notificationBody.setDataVector(new String[0][0], temp);
                    
                } catch (SienaException e) {
                    JOptionPane.showMessageDialog(null, "Cannot publish: " + e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }else {
                if (source == removeButton) {
                    int delAttr = table.getSelectedRow();
                    if (delAttr < 0) {
                        JOptionPane.showMessageDialog(null, "No attribute selected. Cannot remove!");
                    }else {
                        notificationBody.removeRow(delAttr);
                    }
                }
            }   
        }
    }
}
    