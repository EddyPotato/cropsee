package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class CropTableManager {

    public static void addCropManagementTable(JPanel tableListofCrops) {
        String[] columnNames = { "Crop ID", "Crop Name", "Planting Date", "Harvest Date", "Status" };

        Object[][] data = {
            { "1", "Tomato", "2023-03-15", "2023-06-15", "Growing" },
            { "2", "Corn", "2023-02-20", "2023-05-20", "Harvested" },
            { "3", "Wheat", "2023-04-10", "2023-07-10", "Growing" },
        };

        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        JTable cropTable = new JTable(model);

        JScrollPane tableScrollPane = new JScrollPane(cropTable);
        tableListofCrops.add(tableScrollPane);
    }
}
