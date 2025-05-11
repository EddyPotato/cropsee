package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
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

	/*_____________________ ADD TABLE _____________________*/
	public static void addInventoryTable(JPanel panel) {
		panel.setLayout(new BorderLayout());
		String[] columnNames = {"Item ID", "Item Name", "Quantity", "Condition"};

		model = new DefaultTableModel(columnNames, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}

			/*_____________________ CONVERSION OF DATA _____________________*/
			@Override
			public Class<?> getColumnClass(int column) {
				return switch (column) {
				case 0 -> Integer.class;
				case 2 -> Integer.class;
				case 3 -> String.class;
				default -> String.class;
				};
			}


		};

		inventoryTable = new JTable(model);

		/*_____________________ JSPINNER CONDITION _____________________*/
		// CREATES CLASS OBJECT FOR JSPINNER
		SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 0, 9999999, 1);

		// MAKES THE SPINNER VARIABLE USING THE CLASS OBJECT
		JSpinner spinner = new JSpinner(spinnerModel);

		// CREATE EDITOR VARIABLE USING SPINNER VARIABLE
		JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spinner.getEditor();
		editor.getTextField().setHorizontalAlignment(SwingConstants.CENTER); // CENTER ALIGN

		// MAKES THE 3RD COLUMN TO A SPINNER
		inventoryTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(editor.getTextField()));

		/*_____________________ FONT COLOR CONDITION _____________________*/
		DefaultTableCellRenderer conditionRenderer = new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				String condition = value != null ? value.toString().toLowerCase() : "";

				setFont(new Font("Roboto", Font.BOLD, 14));
				setHorizontalAlignment(SwingConstants.CENTER);

				if ("Good".equalsIgnoreCase(condition)) {
					setForeground(Color.decode("#2ECC71")); // Green
				} else if ("Bad".equalsIgnoreCase(condition)) {
					setForeground(Color.decode("#E74C3C")); // Red
				} else {
					setForeground(Color.BLACK); // Default
				}
				return c;
			}
		};
		inventoryTable.getColumnModel().getColumn(3).setCellRenderer(conditionRenderer);

		// Center all columns
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		for (int i = 0; i < inventoryTable.getColumnCount(); i++) {
		    if (i != 3) { // Skip the "Status" column // IT OVERRIDES THE "COLORED" STATUS
		        inventoryTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
		    }
		}

		inventoryTable.getTableHeader().setReorderingAllowed(false);
		refreshInventoryTable();

		JScrollPane scrollPane = new JScrollPane(inventoryTable);
		scrollPane.setPreferredSize(new Dimension(700, 400));

		inventoryTable.getTableHeader().setFont(new Font("Roboto", Font.BOLD, 16));
		inventoryTable.setRowHeight(30);

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
		String query = "SELECT item_id, item_name, quantity, `condition` FROM inventory";

		try (Connection conn = DBConnection.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query)) {

			while (rs.next()) {
				items.add(new Object[]{
						rs.getInt("item_id"),
						rs.getString("item_name"),
						rs.getInt("quantity"),
						rs.getString("condition")
				});
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error loading inventory: " + e.getMessage());
		}
		return items;
	}

	public static void addItem(String name, int quantity, String condition) {
		String query = "INSERT INTO inventory (item_name, quantity, `condition`) VALUES (?, ?, ?)";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(query)) {

			pstmt.setString(1, name);
			pstmt.setInt(2, quantity);
			pstmt.setString(3, condition);
			pstmt.executeUpdate();
			refreshInventoryTable();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error adding item: " + e.getMessage());
		}
	}

	public static void updateItem(int id, String name, int quantity, String condition) {
		String query = "UPDATE inventory SET item_name=?, quantity=?, `condition`=? WHERE item_id=?";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(query)) {

			pstmt.setString(1, name);
			pstmt.setInt(2, quantity);
			pstmt.setString(3, condition);
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
		String query = "SELECT item_name, quantity AS value FROM inventory";

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