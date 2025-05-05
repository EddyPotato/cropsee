package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class InventoryTableManager {
    public static void addInventoryTable(JPanel inventoryPanel) {
        // Column names for the inventory table
        String[] columnNames = { "Item ID", "Item Name", "Quantity", "Price" };

        // Sample data for the inventory table (replace with database data later)
        Object[][] data = {
            { "1", "Seeds", "100", "$50" },
            { "2", "Fertilizer", "200", "$150" },
            { "3", "Pesticide", "50", "$80" },
        };

        // Creating a table model with the data
        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        JTable inventoryTable = new JTable(model);

        // Setting the table in a JScrollPane for scrolling
        JScrollPane tableScrollPane = new JScrollPane(inventoryTable);
        inventoryPanel.add(tableScrollPane);
    }
}