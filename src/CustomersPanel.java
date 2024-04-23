import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

public class CustomersPanel extends JPanel {
    // Widgets

    JLabel search_lbl;
    JLabel name_lbl;
    JLabel tp_number_lbl;

    JTextField search_Field;
    JTextField name_Field;
    JTextField tpNum_Field;
    JTextField searchTable_Field;

    JButton save_Btn;
    JButton search_Btn;
    JButton update_Btn;
    JButton delete_Btn;
    JButton search_table_Btn;

    // Table

    JTable table;
    DefaultTableModel defaultTableModel;

    //Database
    DB database;

    public CustomersPanel(){  //constructor
        database = new DB();

        // Widgets initialization

        search_lbl = new JLabel("Search by mobile ");
        name_lbl = new JLabel("Name: ");
        tp_number_lbl = new JLabel("Mobile Number: ");

        name_Field = new JTextField();
        search_Field = new JTextField();
        tpNum_Field = new JTextField();
        searchTable_Field = new JTextField();

        save_Btn = new JButton("Save");
        update_Btn = new JButton("Update");
        delete_Btn = new JButton("Delete");
        search_Btn = new JButton("Search");
        search_table_Btn = new JButton("Search Table");

        // Left Panel : Grid
        JPanel leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(200,300));

        leftPanel.setLayout(new GridLayout(8,2));

        leftPanel.add(search_lbl);
        leftPanel.add(search_Field);

        leftPanel.add(name_lbl);
        leftPanel.add(name_Field);

        leftPanel.add(tp_number_lbl);
        leftPanel.add(tpNum_Field);

        leftPanel.add(save_Btn);
        leftPanel.add(search_Btn);

        leftPanel.add(update_Btn);
        leftPanel.add(delete_Btn);

        leftPanel.add(searchTable_Field);
        leftPanel.add(search_table_Btn);

        // Handling the click events on the buttons
        save_Btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertCustomerIntoDB();
            }
        });
        update_Btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateCustomerInDB();
            }
        });

        delete_Btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteCustomerFromDB();
            }
        });


        search_table_Btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchTableByName();
            }
        });

        search_Btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchCustomerByMobileNumber();
            }
        });

        // Right Panel: Table
        JPanel rightPanel = new JPanel();
        rightPanel.setPreferredSize(new Dimension(600,500));
        rightPanel.setLayout(new FlowLayout());

        // Initializing the table

        table = new JTable();
        table.setPreferredSize(new Dimension(650 , 500));

        // Loading the table
        loadTable();

        // adding click events

        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int r = table.getSelectedRow();
                String name =table.getValueAt(r,0).toString();
                String mobile_num = table.getValueAt(r,1).toString();

                search_Field.setText(mobile_num);
                name_Field.setText(name);
                tpNum_Field.setText(mobile_num);



            }
        });




        // make the table scrollable
        JScrollPane sp = new JScrollPane(table);
        rightPanel.add(sp);

        add(leftPanel , BorderLayout.WEST);
        add(rightPanel , BorderLayout.EAST);





    }

    private void searchCustomerByMobileNumber() {
        String search = search_Field.getText();

        try{
            Statement s = database.myCon().createStatement();
            ResultSet rs = s.executeQuery(
                    "SELECT * FROM customers WHERE mobile_num = '"+search+"'");

            if (rs.next()){
                name_Field.setText(rs.getString("customer_name"));
                tpNum_Field.setText(rs.getString("mobile_num"));

            }



        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void searchTableByName() {
        String name= searchTable_Field.getText();
        try{
            defaultTableModel.setRowCount(0);
            Statement s = database.myCon().createStatement();

            ResultSet rs = s.executeQuery(
                    "SELECT * FROM customers WHERE customer_name LIKE '%"+name+"%'");

            // defaultTable model only accepts vectors.

            while (rs.next()){
                Vector v = new Vector();
                v.add(rs.getString(1));
                v.add(rs.getString(2));

                defaultTableModel.addRow(v);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteCustomerFromDB() {
        String mobile_num = search_Field.getText();
        try{
            Statement s = database.myCon().createStatement();
            s.executeUpdate("DELETE FROM customers WHERE mobile_num = '"+mobile_num+"'");
            JOptionPane.showMessageDialog(null,"Customer Deleted");
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        loadTable();
    }

    private void updateCustomerInDB() {
        String name = name_Field.getText();
        String tp = tpNum_Field.getText();

        try{
            Statement s = database.myCon().createStatement();
            s.executeUpdate("UPDATE customers SET customer_name = '"+name+"' , mobile_num = '"+tp+"' WHERE mobile_num = '"+tp+"' ");

            JOptionPane.showMessageDialog(null, "customer updated");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        loadTable();
    }

    private void insertCustomerIntoDB() {
        String cus_name = name_Field.getText();
        String mobile_num = tpNum_Field.getText();

        try{
            Statement s = database.myCon().createStatement();
            s.executeUpdate("INSERT INTO customers (customer_name , mobile_num) VALUES ('"+cus_name+"','"+mobile_num+"')");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        loadTable();
    }


    public  void loadTable() {
        try {
            defaultTableModel = new DefaultTableModel();

            // Table Columns
            table = new JTable(defaultTableModel);
            defaultTableModel.addColumn("Customer Name");
            defaultTableModel.addColumn("Mobile Number");

            // Getting all the data from database
            Statement s = database.myCon().createStatement();
            // Whenever you have a table or bunch of data that you receive from database
            // you need to store all of these received data intı a result set variable
            ResultSet rs = s.executeQuery("SELECT * FROM customers");


            // Inserting all received records into table

            // when you have custom table you need to add rows dynamically
            // in the coding ; during runtime always use defaultTableModel

            while (rs.next()) {
                Vector v = new Vector();
                v.add(rs.getString(1));
                v.add(rs.getString(2));

                defaultTableModel.addRow(v);

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
