package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CropTableManager {

    public static void addCropManagementTable(JPanel tableListofCrops) {
        // Set up the layout to ensure proper expansion
        tableListofCrops.setLayout(new BorderLayout());
        
        String[] columnNames = { "Crop ID", "Crop Name", "Planting Date", "Harvest Date", "Status" };

        Object[][] data = {
            { "1", "Tomato", "2023-03-15", "2023-06-15", "Growing" },
            { "2", "Corn", "2023-02-20", "2023-05-20", "Harvested" },
            { "3", "Wheat", "2023-04-10", "2023-07-10", "Growing" },
        };

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable like the tasks panel
            }
        };
        
        JTable cropTable = new JTable(model);
        cropTable.setAutoCreateRowSorter(true); // Add sorting capability
        cropTable.setFillsViewportHeight(true); // Fill available space

        JScrollPane tableScrollPane = new JScrollPane(cropTable);
        tableScrollPane.setPreferredSize(new Dimension(700, 400));
        
        // Add the scroll pane to the panel
        tableListofCrops.add(tableScrollPane, BorderLayout.CENTER);
        
        // Optional: Add table header styling
        cropTable.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 14));
        cropTable.setRowHeight(25);
    }
}