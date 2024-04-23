import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

public class ProductPanel extends JPanel {

    // Widgets
    JLabel search_lbl;
    JLabel product_name_lbl;
    JLabel barcode_lbl;
    JLabel price_lbl;
    JLabel quantity_lbl;

    JTextField search_field;
    JTextField product_name_field;
    JTextField barcode_field;
    JTextField price_field;
    JTextField quantity_field;

    JButton save_Button;
    JButton search_Button;
    JButton update_Button;
    JButton delete_Button;

    // Table
    JTable table;
    DefaultTableModel tb;

    // Database
    DB database;
    public ProductPanel(){
        /************************WIDGETS************************/
        search_lbl = new JLabel("Search Barcode : ");
        product_name_lbl = new JLabel("Product Name: ");
        barcode_lbl = new JLabel("Bar Code");
        price_lbl = new JLabel(" Price: ");
        quantity_lbl = new JLabel(" Quantity: ");

        product_name_field = new JTextField();
        search_field = new JTextField();
        quantity_field = new JTextField();
        barcode_field = new JTextField();
        price_field = new JTextField();

        save_Button = new JButton("Save");
        update_Button = new JButton("Update");
        delete_Button = new JButton("Delete");
        search_Button = new JButton("Search");


        // Left Panel : Grid

        JPanel leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(200,300));

        leftPanel.setLayout(new GridLayout(8,2));

        leftPanel.add(search_lbl);
        leftPanel.add(search_field);
        leftPanel.add(product_name_lbl);
        leftPanel.add(product_name_field);
        leftPanel.add(barcode_lbl);
        leftPanel.add(barcode_field);
        leftPanel.add(quantity_lbl);
        leftPanel.add(quantity_field);
        leftPanel.add(price_lbl);
        leftPanel.add(price_field);

        leftPanel.add(save_Button);
        leftPanel.add(search_Button);
        leftPanel.add(update_Button);
        leftPanel.add(delete_Button);

        // Handling the click events








        // Right Panel: Table
        JPanel rightPanel = new JPanel();
        rightPanel.setPreferredSize(new Dimension(600,500));
        rightPanel.setLayout(new FlowLayout());

        // Initialing the JTable
        table = new JTable();
        table.setPreferredSize(new Dimension(650,500));

        loadTable();

        // Handle the click events on table rows
        save_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertProductIntoDB();
            }
        });

        update_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateProductInDB();
            }
        });

        delete_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteProductFromDB();
            }
        });

        search_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchProductInDB();
            }
        });

        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {

                int r = table.getSelectedRow();

                String barCode = table.getValueAt(r,0).toString();
                String product_name = table.getValueAt(r,1).toString();
                String price  = table.getValueAt(r,2).toString();
                String quantity = table.getValueAt(r,3).toString();

                search_field.setText(barCode);
                product_name_field.setText(product_name);
                barcode_field.setText(barCode);
                quantity_field.setText(quantity);
                price_field.setText(price);
            }
        });

        // Adding panels to the main productsPanel

        JScrollPane sp = new JScrollPane(table);
        rightPanel.add(sp);

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel,BorderLayout.EAST);

    }

    private void searchProductInDB() {

        String bcode_search = search_field.getText();

        try{
            Statement s = database.myCon().createStatement();
            ResultSet rs = s.executeQuery(
                    "SELECT * FROM products WHERE bar_code = '"+bcode_search+"' ");

            if (rs.next()){
                product_name_field.setText(rs.getString("product_name"));
                barcode_field.setText(rs.getString("bar_code"));
                price_field.setText(rs.getString("price"));
                quantity_field.setText(rs.getString("quantity"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        loadTable();


    }

    private void deleteProductFromDB() {
        String bcode = search_field.getText();
        try{
            Statement s = database.myCon().createStatement();
            s.executeUpdate("DELETE FROM products WHERE bar_code = '"+bcode+"' ");
            JOptionPane.showMessageDialog(null,"Product Deleted!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        loadTable();
    }

    private void insertProductIntoDB() {
        String bcode = barcode_field.getText();
        String quantity = quantity_field.getText();
        String price = price_field.getText();
        String product_name = product_name_field.getText();

        try{
            Statement s = database.myCon().createStatement();
            s.executeUpdate(
                    "INSERT INTO products ( bar_code, product_name, price, quantity ) VALUES ('"+bcode+"','"+product_name+"','"+price+"','"+quantity+"')");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }
    private void updateProductInDB(){
        String bcode = barcode_field.getText();
        String quantity = quantity_field.getText();
        String price = price_field.getText();
        String product_name = product_name_field.getText();
        String bar_code_to_search = search_field.getText();

        try{
            Statement s = database.myCon().createStatement();
            s.executeUpdate("UPDATE products SET bar_code = '"+bcode+"', product_name = '"+product_name+"',price = '"+price+"', quantity = '"+quantity+"' WHERE bar_code = '"+bcode+"' ");
            JOptionPane.showMessageDialog(null,"Product Updated!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    private void loadTable() {
        try{
            DefaultTableModel dt = new DefaultTableModel();

            // table columns
            table = new JTable(dt);
            dt.addColumn("BarCode");
            dt.addColumn("Product Name");
            dt.addColumn("Price");
            dt.addColumn("Quantity");

            Statement s = database.myCon().createStatement();
            ResultSet rs = s.executeQuery("SELECT * FROM products");

            while(rs.next()){
                Vector v = new Vector();
                v.add(rs.getString(1));
                v.add(rs.getString(2));
                v.add(rs.getInt(3));
                v.add(rs.getInt(4));

                // display the data into table
                dt.addRow(v);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }


}
