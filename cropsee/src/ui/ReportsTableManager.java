package ui;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;  // Using java.util.Date for display
import app.DBConnection;

public class ReportsTableManager {
    private static DefaultTableModel model;
    private static JTable reportsTable;

    public static void addReportsTable(JPanel reportsPanel) {
        String[] columnNames = {"Report Type", "Generated On", "Details"};
        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        reportsTable = new JTable(model);
        refreshReports(); // Load initial data

        JScrollPane scrollPane = new JScrollPane(reportsTable);
        reportsPanel.add(scrollPane);
    }

    public static void refreshReports() {
        model.setRowCount(0);  // Clear existing data
        
        // Harvest Report
        addReportRow(
            "Harvest Summary", 
            new Date(System.currentTimeMillis()),  // java.util.Date
            "Total harvested crops: " + getHarvestedCropCount()
        );
        
        // Add more report types here
    }

    private static void addReportRow(String type, Date generatedOn, String details) {
        model.addRow(new Object[]{type, generatedOn, details});
    }

    private static int getHarvestedCropCount() {
        String query = "SELECT COUNT(*) AS count FROM crops WHERE status='Harvested'";
        int count = 0;
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            if (rs.next()) {
                count = rs.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching harvest count: " + e.getMessage());
        }
        return count;
    }
}