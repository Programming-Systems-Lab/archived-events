import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import java.util.*;
import siena.*;

/** This is the constructor for the subscribe panel. This panel
 * lets you create a filter with some constraints and send it
 * to the server to subscribe.
 * @author Ricky Chin
 * @version 1.0
 */
class SubscribePanel extends JPanel implements ActionListener {
    
    private HierarchicalDispatcher server;
    
    //implements notifiable
    private GUIPanel canvas;
    
    //used to display constraints in JTable table
    private DefaultTableModel filterBody;
    private JTable table;
    
    private JButton addButton;
    private JButton removeButton;
    private JButton subscribeButton;
    private JButton unsubscribeButton;
    
    /** Creates a SubscribePanel
     * @param s properly configured Siena HierarchicalDispatcher
     */
    public SubscribePanel(HierarchicalDispatcher s, GUIPanel c) {
        setLayout(new BorderLayout());
        
        server = s;
        
        canvas = c;
        
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new BorderLayout());
        
        filterBody = new DefaultTableModel();
        filterBody.addColumn("Attribute Name");
        filterBody.addColumn("Constraint");
        filterBody.addColumn("Value");
        
        //Jtable settings
        table = new JTable(filterBody) {
            public boolean isCellEditable(int row, int col) {return false;}
        };
        table.getTableHeader().setReorderingAllowed(false);
        table.setCellSelectionEnabled(false);
        table.setColumnSelectionAllowed(false);
        table.setRowSelectionAllowed(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        filterPanel.add(new JScrollPane(table), BorderLayout.CENTER);
             
        add(filterPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 2));
        
        addButton = new JButton("Add");
        addButton.addActionListener(this);
        removeButton = new JButton("Remove");
        removeButton.addActionListener(this);
        subscribeButton = new JButton("Subscribe");
        subscribeButton.addActionListener(this);
        unsubscribeButton = new JButton("Unsubscribe");
        unsubscribeButton.addActionListener(this);
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(subscribeButton);
        buttonPanel.add(unsubscribeButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    public void actionPerformed(ActionEvent evt) {
        Object source = evt.getSource();
        
        if (source == addButton) {
            String name = JOptionPane.showInputDialog("Enter Attribute name: ");
           
            if (!"".equals(name)) {
                
                String temp1 = "Enter the number of the corresponding option:\n";
		for(int i = 0; i < Op.operators.length; i++) {
		    temp1 += "  " + i + ": " + Op.operators[i] + "\n";
		}
		
		String option = "";
		while (true) {
		    try {
			int choice = Integer.parseInt(JOptionPane.showInputDialog(temp1));
			option = Op.operators[choice];
			break;
		    }catch (NumberFormatException nfe) {
			JOptionPane.showMessageDialog(null,"Invalid choice! Try again.");
			continue;
		    }catch (ArrayIndexOutOfBoundsException aiobe) {
			JOptionPane.showMessageDialog(null, "Invalid choice! Try again.");
			continue;
		    }
                }

     		if (!"".equals(option)) {
                    String value = JOptionPane.showInputDialog("Enter Constraint Value: ");

                    if (!"".equals(value)) {
                        String[] temp = { name, option, value };

                        filterBody.addRow(temp);
                    }
                }

            }
            
        }else {
            if (source == subscribeButton) {
                try {         
                    //create filter here from TableModel data
                    Filter f = new Filter();
                    Vector list = filterBody.getDataVector();
                    Iterator it = list.iterator();
                    while (it.hasNext()) {
                        Vector attr = (Vector)(it.next());
                        f.addConstraint((String)(attr.get(0)), new AttributeConstraint(Op.op((String)attr.get(1)), (String)(attr.get(2))));
                    }
                    
                    server.subscribe(f, canvas);
                    canvas.setSubscribed(true);
                    JOptionPane.showMessageDialog(null, "Subscribed to server!");
                    String[] temp = {"Attribute Name", "Constraint", "Value"};
                    filterBody.setDataVector(new String[0][0], temp);
                }catch (SienaException e) { 
                    JOptionPane.showMessageDialog(null, "Cannot subscribe: " + e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }else {
                if (source == removeButton) {
                    int delAttr = table.getSelectedRow();
                    if (delAttr < 0) {
                        JOptionPane.showMessageDialog(null, "No constraint selected. Cannot remove!");
                    }else {
                        filterBody.removeRow(delAttr);
                    }
                }else {
                    //source == unsubscribeButton
                    server.unsubscribe(new Filter(), canvas);
                    canvas.setSubscribed(false);
                }
            }   
        }
    }
}
    