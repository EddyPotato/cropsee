package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ReportsTableManager {
    public static void addReportsTable(JPanel reportsPanel) {
        // Column names for the reports table
        String[] columnNames = { "Report ID", "Report Type", "Generated On", "Details" };

        // Sample data for the reports table (replace with database data later)
        Object[][] data = {
            { "1", "Weekly Crop Report", "2023-05-05", "Details about crop growth" },
            { "2", "Monthly Harvest Report", "2023-04-30", "Details about harvested crops" },
            { "3", "Inventory Report", "2023-05-01", "Details about inventory levels" },
        };

        // Creating a table model with the data
        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        JTable reportsTable = new JTable(model);

        // Setting the table in a JScrollPane for scrolling
        JScrollPane tableScrollPane = new JScrollPane(reportsTable);
        reportsPanel.add(tableScrollPane);
    }
}
