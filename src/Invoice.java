import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

public class Invoice extends JPanel {
    // Widgets
    JLabel product_barcode_lbl;
    JTextField product_barcode_field;

    JLabel customer_name_lbl;
    JTextField customer_name_field;

    JButton addCartBtn;
    JButton removeFromCarBtn;
    JButton confirmBtn;

    JButton saveInvoice;

    public JLabel totalPriceLbl;

    // Cart Table
    JTable jTable;
    DefaultTableModel dt;

    //Cart
    ArrayList<String> selectedItems;

    // Database
    DB database;

    int totalPrice = 0;

    public Invoice() {
        database = new DB();
        selectedItems = new ArrayList<>();

        // Initializing the widgets
        product_barcode_lbl = new JLabel("Product Barcode");
        product_barcode_field = new JTextField();
        product_barcode_field.setOpaque(false);

        customer_name_lbl = new JLabel("Customer Name");
        customer_name_field = new JTextField();
        customer_name_field.setOpaque(false);

        addCartBtn = new JButton("Add to Cart");
        removeFromCarBtn = new JButton("Remove from Cart");

        confirmBtn = new JButton("Confirm");

        saveInvoice = new JButton("Save Invoice");

        totalPriceLbl = new JLabel("Total Price: ");
        totalPriceLbl.setFont(new Font("Serif", Font.PLAIN, 18));

        // left panel
        JPanel leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(200, 500));
        leftPanel.setLayout(new GridLayout(10, 2));

        leftPanel.add(product_barcode_lbl);
        leftPanel.add(product_barcode_field);

        leftPanel.add(customer_name_lbl);
        leftPanel.add(customer_name_field);

        leftPanel.add(addCartBtn);
        leftPanel.add(removeFromCarBtn);

        leftPanel.add(confirmBtn);

        leftPanel.add(saveInvoice);

        leftPanel.add(totalPriceLbl);

        // Right Panel: Table
        JPanel rightPanel = new JPanel();
        rightPanel.setPreferredSize(new Dimension(600, 500));
        rightPanel.setLayout(new FlowLayout());

        // Handling the click events on the buttons
        addCartBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String product_barcode = product_barcode_field.getText();
                selectedItems.add(product_barcode);
                displaySelectedItemsInTable(selectedItems);
                product_barcode_field.setText("");
                // when the user click in here it will be resetted.
                // this is just for fasting things.
            }
        });
        removeFromCarBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String product_barcode = product_barcode_field.getText();
                selectedItems.remove(product_barcode);
                displaySelectedItemsInTable(selectedItems);
            }
        });

        confirmBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // The PreparedStatement interface extends the Statement interface
                // it represents a precompiled SQL statement which can be executed
                // multiple times.

                // This accepts parameterized SQL queries and you can pass 0
                // or more parameters to this query.

                // Initially this statement uses placeholder "?"

                // Instead of parameters , later on you can pass arguments
                // to these dynamically using the setXXX() methods
                // of the PreparedStatement interface.

                dt.setRowCount(0);

                try {
                    String query = "SELECT product_name, price FROM products WHERE bar_code = ?";
                    PreparedStatement statement = database.myCon().prepareStatement(query);


                    // Replacing the ? by the barcode from the arraylist
                    for (String barcode : selectedItems) {
                        statement.setString(1, barcode);

                        ResultSet rs = statement.executeQuery();

                        while (rs.next()) {
                            System.out.println(rs.getString(1));
                            System.out.println(rs.getString(2));

                            Vector v = new Vector();
                            // get the barcode from the local cart
                            v.add(barcode);
                            // get the name and the price of the item from DB
                            v.add(rs.getString(1));
                            v.add(rs.getString(2));
                            dt.addRow(v);

                        }
                    }
                    // Calculate total price
                    calculateTotalPrice();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        // Initializing the table
        dt = new DefaultTableModel();
        jTable = new JTable(dt);
        jTable.setPreferredSize(new Dimension(650, 500));
        jTable.setOpaque(false);


        //loading the table
        dt.addColumn("Product Barcode");
        dt.addColumn("Product Name");
        dt.addColumn("Product Price");

        saveInvoice.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String cus_name = customer_name_field.getText();

                // Getting an array of selected products

                //Although an array is one of the most common data types
                // in the world of programming.
                //MYSQL actually doesn't support saving an array type directly.

                // You can't create a table column of array type in MYSQL.
                // The easiest way to store array type data in mysql is
                // to use JSON data type.

                // The JSON data type was first added in MYSQL version 5.7.8
                // and you can use the type for storing JSON arrays and objects.

                // 1- getting the names of the products in form of array
                ArrayList<String> products_names = new ArrayList<>();
                        // we are starting with declaring an arrayList
                // but we will transform it into an array later cause it will
                // allow us to add things dynamically.

                for(int i=0; i<jTable.getRowCount(); i++){
                    products_names.add(jTable.getValueAt(i,1).toString());
                }

                // 2- Converting arrayList into String
              //  Object[] names_of_products_array = products_names.toArray();

                StringBuilder sb = new StringBuilder();
                for (String s : products_names){
                    sb.append(s);
                    sb.append(" , ");
                }
                // Getting the current date
                Timestamp timestamp = new Timestamp(new Date().getTime());

                // Total Price
                int totalprice = calculateTotalPrice();

                // Query to insert the data into the table
                String insertInvoice = "INSERT INTO invoice " +
                        "( customer_name , products_sold,date,total_price) VALUES "
                        +"('"+cus_name+"','"+sb+"'," +
                        "'"+timestamp+"','"+totalPrice+"')";

                try{
                    if (!cus_name.equals("") && !selectedItems.isEmpty()) {
                        Statement s = database.myCon().createStatement();
                        s.executeUpdate(insertInvoice);
                    }else{
                        JOptionPane.showMessageDialog(null,
                                "One or more fields are empty");
                    }
                } catch (SQLException ex) {
                }
            }
        });


        // adding widgets to the main panel
        // make the table scrollable

        JScrollPane sp = new JScrollPane(jTable);
        rightPanel.add(sp);

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);
    }

    private void displaySelectedItemsInTable(ArrayList<String> selectedItems) {
        try {

            //resetting the table

            dt.setRowCount(0);
            // Inserting items from Arraylist to Jtable
            for (int i = 0; i < selectedItems.size(); i++) {

                dt.addRow(new Object[]{selectedItems.get(i)});
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int calculateTotalPrice() {
        for (int i = 0; i < jTable.getRowCount(); i++) {
            totalPrice = totalPrice + Integer.parseInt(jTable.getValueAt(i, 2).toString());

        }
        totalPriceLbl.setText("Total Price:" +totalPrice+ " $");
        return totalPrice;
    }


}
