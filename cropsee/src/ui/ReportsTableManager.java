package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ReportsTableManager {
    public static void addReportsTable(JPanel reportsPanel) {
    	/*_____________________ TABLE DATA _____________________*/
        String[] columnNames = { "Report ID", "Report Type", "Generated On", "Details" };

        Object[][] data = {
            { "1", "Weekly Crop Report", "2023-05-05", "Details about crop growth" },
            { "2", "Monthly Harvest Report", "2023-04-30", "Details about harvested crops" },
            { "3", "Inventory Report", "2023-05-01", "Details about inventory levels" },
        };

        /*_____________________ DATA DECLARATION _____________________*/
        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        
        /*_____________________ TABLE WITH THE DATA _____________________*/
        JTable reportsTable = new JTable(model);

        /*_____________________ ADDING TABLE TO SCROLL PANE _____________________*/
        JScrollPane tableScrollPane = new JScrollPane(reportsTable);
        
        /*_____________________ ADD TO THE PANEL (CONTAINER) _____________________*/
        reportsPanel.add(tableScrollPane);
    }
}
