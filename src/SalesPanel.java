import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

public class SalesPanel extends JPanel {
    JLabel totalSales;
    JButton getSales;

    // Table

    JTable jTable;
    DefaultTableModel dt;


    //Database
    DB database;

    public SalesPanel(){

        totalSales = new JLabel("Total Sales: ");
        totalSales.setFont(new Font("Serif",Font.PLAIN,32));
        getSales = new JButton("Calculate Total Sales");


        // left Panel

        JPanel leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(200,500));

        leftPanel.setLayout(new GridLayout(6,2));

        leftPanel.add(totalSales);
        leftPanel.add(getSales);


        // right panel

        JPanel rightPanel = new JPanel();
        rightPanel.setPreferredSize(new Dimension(600,500));

        rightPanel.setLayout(new FlowLayout());


        //Initializing the table

        dt = new DefaultTableModel();
        jTable = new JTable(dt);
        jTable.setPreferredSize(new Dimension(650,500));
        jTable.setOpaque(false);

        // loading the table

        dt.addColumn("Invoice ID");
        dt.addColumn("Customer Name");
        dt.addColumn("Products Sold");
        dt.addColumn("Date");
        dt.addColumn("Invoice Price");
        
        getAllInvoicesFromDB();

        getSales.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateTheSales();
            }
        });

        JScrollPane sp = new JScrollPane(jTable);
        rightPanel.add(sp);

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);







    }

    private void getAllInvoicesFromDB() {
        String query = "SELECT * FROM invoice";

        try {
            Statement s = database.myCon().createStatement();
            ResultSet rs = s.executeQuery(query);

            while (rs.next()){
                Vector v = new Vector();
                v.add(rs.getInt(1));
                v.add(rs.getString(2));
                v.add(rs.getString(3));
                v.add(rs.getDate(4));
                v.add(rs.getInt(5));
                // display data into table
                dt.addRow(v);
            }




        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    public void calculateTheSales() {
        int totalPrice = 0;

        for (int i=0; i<jTable.getRowCount(); i++){
            totalPrice = totalPrice + Integer
                    .parseInt(jTable.getValueAt(i,4).toString());
        }
        totalSales.setText("Total Price: "+totalPrice+ " $");
    }


}
