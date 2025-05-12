package dataManagers;

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

@SuppressWarnings("serial")
public class InventoryDataManager {
	/*________________________ CLASS-LEVEL ________________________*/
	public static DefaultTableModel model;
	public static JTable inventoryTable;

	/*________________________ METHODS ________________________*/
	public static void addInventoryTable(JPanel referencedPanel) {
		referencedPanel.setLayout(new BorderLayout());
		String[] columnNames = {
				"Item ID", 
				"Item Name", 
				"Quantity", 
				"Condition"
		};

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

		/*_____________________ TABLE _____________________*/
		inventoryTable = new JTable(model);

		/*_____________________ JSPINNER _____________________*/
		SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 0, 9999999, 1);
		JSpinner spinner = new JSpinner(spinnerModel);
		JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spinner.getEditor();
		editor.getTextField().setHorizontalAlignment(SwingConstants.CENTER);

		inventoryTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(editor.getTextField()));

		/*_____________________ CONDITION - COLOR CODE _____________________*/
		DefaultTableCellRenderer conditionRenderer = new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				String condition = value != null ? value.toString().toLowerCase() : "";

				setFont(new Font("Roboto", Font.BOLD, 14));
				setHorizontalAlignment(SwingConstants.CENTER);

				if ("Good".equalsIgnoreCase(condition)) {
					setForeground(Color.decode("#2ECC71")); // GREEN
				} else if ("Bad".equalsIgnoreCase(condition)) {
					setForeground(Color.decode("#E74C3C")); // RED
				} else {
					setForeground(Color.BLACK); // DEFAULT
				}
				return c;
			}
		};

		inventoryTable.getColumnModel().getColumn(3).setCellRenderer(conditionRenderer);
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		for (int i = 0; i < inventoryTable.getColumnCount(); i++) {
			if (i != 3) { // IT OVERRIDES THE "COLORED" STATUS
				inventoryTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
			}
		}

		inventoryTable.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value,
					boolean isSelected, boolean hasFocus, int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

				setOpaque(true);
				setBackground(Color.decode("#27AE60")); // Green like buttons
				setForeground(Color.WHITE);
				setFont(new Font("Roboto", Font.BOLD, 16));
				setBorder(BorderFactory.createCompoundBorder(
						BorderFactory.createMatteBorder(0, 0, 2, 0, Color.decode("#2C3E50")),
						BorderFactory.createEmptyBorder(5, 10, 5, 10)
						));
				setHorizontalAlignment(SwingConstants.CENTER);
				return this;
			}
		});
		inventoryTable.getTableHeader().setPreferredSize(new Dimension(0, 40));
		inventoryTable.getTableHeader().setOpaque(false);
		inventoryTable.getTableHeader().setReorderingAllowed(false);
		refreshInventoryTable();
		JScrollPane scrollPane = new JScrollPane(inventoryTable);
		scrollPane.setPreferredSize(new Dimension(700, 400));
		inventoryTable.getTableHeader().setFont(new Font("Roboto", Font.BOLD, 16));
		inventoryTable.setRowHeight(30);
		referencedPanel.add(scrollPane, BorderLayout.CENTER);
	}

	/*_____________________ REFRESH _____________________*/
	public static void refreshInventoryTable() {
		model.setRowCount(0);
		for (Object[] row : fetchInventoryItems()) {
			model.addRow(row);
		}
	}

	/*_____________________ FETCH DATA _____________________*/
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

	/*_____________________ ADD ITEM _____________________*/
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

	/*_____________________ UPDATE _____________________*/
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

	/*_____________________ DELETE _____________________*/
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

	/*_____________________ INVENTORY STATUS (FOR REPORTING) _____________________*/
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