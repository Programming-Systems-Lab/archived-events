import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import java.util.*;
import siena.*;

/** This is the constructor for the subscribe panel. This panel
 * lets you create a filter with some constraints and send it
 * to the server to subscribe. It also displays the toString of
 * each notification received. Notice it is a JSplitPane.
 * @author Ricky Chin
 * @version 1.0
 */
class SubscribePanel extends JSplitPane implements ActionListener, Notifiable {
    
    private HierarchicalDispatcher server;
    
    //used to display constraints in JTable table
    private DefaultTableModel filterBody;
    private JTable table;
    
    private JButton addButton;
    private JButton removeButton;
    private JButton subscribeButton;
    private JButton unsubscribeButton;
    
    //used to display notifications
    private JList notesDisplay;
    private DefaultListModel notes;
    private JButton clearButton;
    
    /** Creates a SubscribePanel
     * @param s properly configured Siena HierarchicalDispatcher
     */
    public SubscribePanel(HierarchicalDispatcher s) {
        super(JSplitPane.VERTICAL_SPLIT);
        
        server = s;
        
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new BorderLayout());
        
        filterBody = new DefaultTableModel();
        filterBody.addColumn("Attribute Name");
        filterBody.addColumn("Constraint");
        filterBody.addColumn("Value");
        
        
        table = new JTable(filterBody) {
            public boolean isCellEditable(int row, int col) {return false;}
        };
        table.getTableHeader().setReorderingAllowed(false);
        table.setCellSelectionEnabled(false);
        table.setColumnSelectionAllowed(false);
        table.setRowSelectionAllowed(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        filterPanel.add(new JScrollPane(table), BorderLayout.CENTER);
             
        
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 2));
        
        addButton = new JButton("Add");
        addButton.setToolTipText("Add a constraint");
        addButton.addActionListener(this);
        buttonPanel.add(addButton);
        
        removeButton = new JButton("Remove");
        removeButton.setToolTipText("Remove selected constraint");
        removeButton.addActionListener(this);
        buttonPanel.add(removeButton);
        
        subscribeButton = new JButton("Subscribe");
        subscribeButton.setToolTipText("Subscribe using current filter");
        subscribeButton.addActionListener(this);
        buttonPanel.add(subscribeButton);
        
        unsubscribeButton = new JButton("Unsubscribe");
        unsubscribeButton.setToolTipText("Unsubscribe to server");
        unsubscribeButton.addActionListener(this);
        buttonPanel.add(unsubscribeButton);        
        
        filterPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        this.setTopComponent(filterPanel);
        
        JPanel notePanel = new JPanel();
        notePanel.setLayout(new BorderLayout());
        
        notes = new DefaultListModel();
        notesDisplay = new JList(notes);
        notePanel.add(new JScrollPane(notesDisplay), BorderLayout.CENTER);
        
        clearButton = new JButton("Clear");
        clearButton.setToolTipText("Clear all received notifications");
        clearButton.addActionListener(this);
        notePanel.add(clearButton, BorderLayout.SOUTH);
        
        this.setBottomComponent(notePanel);
        
        //divider in middle
        this.setDividerLocation(130);
        
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
                        String temp = (String)(attr.get(2));
                        try {
                            f.addConstraint((String)(attr.get(0)), new AttributeConstraint(Op.op((String)attr.get(1)), Integer.parseInt(temp)));
                        }catch (NumberFormatException ex) {
                            f.addConstraint((String)(attr.get(0)), new AttributeConstraint(Op.op((String)attr.get(1)), temp));
                        }
                    }
                    
                    server.subscribe(f, this);
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
                    if (source == unsubscribeButton) {
                        server.unsubscribe(new Filter(), this);
                    }else {
                        if (source == clearButton) {
                            notes.clear();
                            notesDisplay.validate();
                        }
                    }
                }
            }   
        }
    }
    
    
    /** add notifications to JList to display
     */
    public void notify(siena.Notification[] p1) throws siena.SienaException {
        for(int i = 0; i < p1.length; i++ ) {
            notes.addElement(p1[i]);
        }
        notesDisplay.validate();
    }
    
    /** add notification to JList to display
     */
    public void notify(final siena.Notification p1) throws siena.SienaException {
        notes.addElement(p1);
        notesDisplay.validate();
    }
}
    
