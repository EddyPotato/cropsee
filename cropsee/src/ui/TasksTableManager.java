package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class TasksTableManager {
    public static void addTasksTable(JPanel tableContainer) {
        tableContainer.setLayout(new BorderLayout());
        
        String[] columnNames = { "Task ID", "Task Name", "Assigned To", "Due Date", "Status" };
        
        Object[][] data = {
            { "1", "Plant Seeds", "John", "2023-05-10", "Pending" },
            { "2", "Water Plants", "Jane", "2023-05-12", "In Progress" },
            { "3", "Harvest Crops", "Doe", "2023-06-15", "Completed" },
        };

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable tasksTable = new JTable(model);
        tasksTable.setAutoCreateRowSorter(true);
        tasksTable.setFillsViewportHeight(true);

        JScrollPane tableScrollPane = new JScrollPane(tasksTable);
        tableScrollPane.setPreferredSize(new Dimension(700, 400));
        
        // Styling
        tasksTable.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 14));
        tasksTable.setRowHeight(25);
        tasksTable.setIntercellSpacing(new Dimension(0, 0));
        
        tableContainer.add(tableScrollPane, BorderLayout.CENTER);
    }
}