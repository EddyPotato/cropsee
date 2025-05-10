package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import app.DBConnection;

public class InventoryTableManager {
    public static DefaultTableModel model;
    public static JTable inventoryTable;

    public static void addInventoryTable(JPanel panel) {
        panel.setLayout(new BorderLayout());
        String[] columnNames = {"Item ID", "Item Name", "Quantity", "Price"};
        
        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int column) {
                return switch (column) {
                    case 0 -> Integer.class;
                    case 2 -> Integer.class;
                    case 3 -> Double.class;
                    default -> String.class;
                };
            }
            
            
        };
        
        inventoryTable = new JTable(model);
        inventoryTable.getTableHeader().setReorderingAllowed(false);
        refreshInventoryTable();
        
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(JLabel.LEFT);
        
        // Apply to all columns
        for (int i = 0; i < inventoryTable.getColumnCount(); i++) {
            inventoryTable.getColumnModel().getColumn(i).setCellRenderer(leftRenderer);
        }
        
        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        scrollPane.setPreferredSize(new Dimension(700, 400));
        
        inventoryTable.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 14));
        inventoryTable.setRowHeight(25);
        
        panel.add(scrollPane, BorderLayout.CENTER);
    }

    public static void refreshInventoryTable() {
        model.setRowCount(0);
        for (Object[] row : fetchInventoryItems()) {
            model.addRow(row);
        }
    }

    private static List<Object[]> fetchInventoryItems() {
        List<Object[]> items = new ArrayList<>();
        String query = "SELECT item_id, item_name, quantity, price FROM inventory";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                items.add(new Object[]{
                    rs.getInt("item_id"),
                    rs.getString("item_name"),
                    rs.getInt("quantity"),
                    rs.getDouble("price")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading inventory: " + e.getMessage());
        }
        return items;
    }

    public static void addItem(String name, int quantity, double price) {
        String query = "INSERT INTO inventory (item_name, quantity, price) VALUES (?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, name);
            pstmt.setInt(2, quantity);
            pstmt.setDouble(3, price);
            pstmt.executeUpdate();
            refreshInventoryTable();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error adding item: " + e.getMessage());
        }
    }

    public static void updateItem(int id, String name, int quantity, double price) {
        String query = "UPDATE inventory SET item_name=?, quantity=?, price=? WHERE item_id=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, name);
            pstmt.setInt(2, quantity);
            pstmt.setDouble(3, price);
            pstmt.setInt(4, id);
            pstmt.executeUpdate();
            refreshInventoryTable();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error updating item: " + e.getMessage());
        }
    }

    public static void deleteItem(int id) {
        String query = "DELETE FROM inventory WHERE item_id=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            refreshInventoryTable();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error deleting item: " + e.getMessage());
        }
    }
    
    public static Map<String, Double> getInventoryValueData() {
        Map<String, Double> data = new LinkedHashMap<>();
        String query = "SELECT item_name, (quantity * price) AS value FROM inventory";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                data.put(rs.getString("item_name"), rs.getDouble("value"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading inventory data: " + e.getMessage());
        }
        return data;
    }
}