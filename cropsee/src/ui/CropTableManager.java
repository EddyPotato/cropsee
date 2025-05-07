package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import app.DBConnection;

public class CropTableManager {
    public static DefaultTableModel model;
    public static JTable cropTable;

    public static void addCropManagementTable(JPanel tableListofCrops) {
        tableListofCrops.setLayout(new BorderLayout());
        String[] columnNames = {"Crop ID", "Crop Name", "Planting Date", "Harvest Date", "Status"};
        
        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        cropTable = new JTable(model);
        refreshCropTable();
        
        JScrollPane tableScrollPane = new JScrollPane(cropTable);
        tableScrollPane.setPreferredSize(new Dimension(700, 400));
        
        cropTable.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 14));
        cropTable.setRowHeight(25);
        tableListofCrops.add(tableScrollPane, BorderLayout.CENTER);
    }

    public static void refreshCropTable() {
        model.setRowCount(0); // Clear existing data
        List<Object[]> crops = fetchCropsFromDatabase();
        for (Object[] crop : crops) {
            model.addRow(crop);
        }
    }

    private static List<Object[]> fetchCropsFromDatabase() {
        List<Object[]> crops = new ArrayList<>();
        String query = "SELECT * FROM crops";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("crop_id"),
                    rs.getString("crop_name"),
                    rs.getDate("planting_date"),
                    rs.getDate("harvest_date"),
                    rs.getString("status")
                };
                crops.add(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading crops: " + e.getMessage());
        }
        return crops;
    }

    public static void addCrop(String name, Date plantingDate, Date harvestDate, String status) {
        String query = "INSERT INTO crops (crop_name, planting_date, harvest_date, status) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, name);
            pstmt.setDate(2, plantingDate);
            pstmt.setDate(3, harvestDate);
            pstmt.setString(4, status);
            pstmt.executeUpdate();
            refreshCropTable();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error adding crop: " + e.getMessage());
        }
    }

    public static void updateCrop(int id, String name, Date plantingDate, Date harvestDate, String status) {
        String query = "UPDATE crops SET crop_name = ?, planting_date = ?, harvest_date = ?, status = ? WHERE crop_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, name);
            pstmt.setDate(2, plantingDate);
            pstmt.setDate(3, harvestDate);
            pstmt.setString(4, status);
            pstmt.setInt(5, id);
            pstmt.executeUpdate();
            refreshCropTable();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error updating crop: " + e.getMessage());
        }
    }

    public static void deleteCrop(int id) {
        String query = "DELETE FROM crops WHERE crop_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            refreshCropTable();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error deleting crop: " + e.getMessage());
        }
    }
}