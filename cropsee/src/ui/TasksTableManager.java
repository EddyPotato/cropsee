package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class TasksTableManager {
    public static void addTasksTable(JPanel tasksPanel) {
        // Column names for the tasks table
        String[] columnNames = { "Task ID", "Task Name", "Assigned To", "Due Date", "Status" };

        // Sample data for the tasks table (replace with database data later)
        Object[][] data = {
            { "1", "Plant Seeds", "John", "2023-05-10", "Pending" },
            { "2", "Water Plants", "Jane", "2023-05-12", "In Progress" },
            { "3", "Harvest Crops", "Doe", "2023-06-15", "Completed" },
        };

        // Creating a table model with the data
        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        JTable tasksTable = new JTable(model);

        // Setting the table in a JScrollPane for scrolling
        JScrollPane tableScrollPane = new JScrollPane(tasksTable);
        tasksPanel.add(tableScrollPane);
    }
}