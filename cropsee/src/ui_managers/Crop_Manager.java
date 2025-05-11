package ui_managers;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import app.DBConnection;

@SuppressWarnings("serial")
public class Crop_Manager {
	/*________________________ CLASS-LEVEL ________________________*/
	public static DefaultTableModel model;
	public static JTable cropTable;

	/*________________________ METHODS ________________________*/
	public static void addCropManagementTable(JPanel referencedPanel) {
		referencedPanel.setLayout(new BorderLayout());

		/*________________________ DISPLAY NAMES ________________________*/
		String[] columnNames = {
				"Crop ID",
				"Crop Name", 
				"Planting Date", 
				"Harvest Date", 
				"Water Schedule", 
				"Fertilizer Schedule", 
				"Growth Stage", 
				"Status"
		};

		model = new DefaultTableModel(columnNames, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		/*________________________ TABLE ________________________*/
		cropTable = new JTable(model);
		cropTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS); // FIT CONTENT
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer(); // CENTERS
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		for (int i = 0; i < cropTable.getColumnCount(); i++) {
			cropTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
		}

		cropTable.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value,
					boolean isSelected, boolean hasFocus, int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

				/*________________________ TABLE STYLE ________________________*/
				setOpaque(true);
				setBackground(Color.decode("#27AE60")); 
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

		cropTable.getTableHeader().setPreferredSize(new Dimension(0, 40));
		cropTable.getTableHeader().setOpaque(false);
		cropTable.getTableHeader().setReorderingAllowed(false);
		refreshCropTable();
		JScrollPane tableScrollPane = new JScrollPane(cropTable);
		tableScrollPane.setPreferredSize(new Dimension(700, 400));

		/*________________________ HEADER SIZE ________________________*/
		cropTable.getTableHeader().setFont(new Font("Roboto", Font.BOLD, 16));
		cropTable.setRowHeight(30);

		/*________________________ ADD TABLE --> PANEL ________________________*/
		referencedPanel.add(tableScrollPane, BorderLayout.CENTER);
	}

	/*________________________ REFRESH TABLE ________________________*/
	public static void refreshCropTable() {
		model.setRowCount(0); // Clear existing data
		List<Object[]> crops = fetchCropsFromDatabase();
		for (Object[] crop : crops) {
			model.addRow(crop);
		}
	}

	/*________________________ MECHANISM ________________________*/
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
						rs.getString("water_schedule"),
						rs.getString("fertilizer_schedule"),
						rs.getString("growth_stage"),
						rs.getString("status")
				};
				crops.add(row);
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error loading crops: " + e.getMessage());
		}
		return crops;
	}


	/*________________________ ADD CROP ________________________*/
	public static void addCrop(String name, Date plantingDate, Date harvestDate, String waterSchedule, 
			String fertilizerSchedule, 
			String growthStage, String status) 
	{
		String query = "INSERT INTO crops (crop_name, planting_date, harvest_date, water_schedule, "
				+ "fertilizer_schedule, growth_stage, status)"
				+ "VALUES (?, ?, ?, ?, ?, ?, ?)";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setString(1, name); // 1
			pstmt.setDate(2, plantingDate); // 2
			pstmt.setDate(3, harvestDate); // 3
			pstmt.setString(4, waterSchedule); // 4
			pstmt.setString(5, fertilizerSchedule); // 5
			pstmt.setString(6, growthStage); // 6
			pstmt.setString(7, status); // 7
			pstmt.executeUpdate();
			refreshCropTable();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error adding crop: " + e.getMessage());
		}
	}

	/*________________________ UPDATE ________________________*/
	public static void updateCrop(int id, String name, Date plantingDate, Date harvestDate, String waterSchedule,
			String fertilizerSchedule, String growthStage, String status) {

		String query = "UPDATE crops SET crop_name = ?, planting_date = ?, harvest_date = ?, " +
				"water_schedule = ?, fertilizer_schedule = ?, growth_stage = ?, status = ? " +
				"WHERE crop_id = ?";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(query)) {

			pstmt.setString(1, name);
			pstmt.setDate(2, plantingDate);
			pstmt.setDate(3, harvestDate);
			pstmt.setString(4, waterSchedule);
			pstmt.setString(5, fertilizerSchedule);
			pstmt.setString(6, growthStage);
			pstmt.setString(7, status);
			pstmt.setInt(8, id); // ID goes last, for the WHERE clause

			pstmt.executeUpdate();
			refreshCropTable();

		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error updating crop: " + e.getMessage());
		}
	}


	/*________________________ DELETE ________________________*/
	public static void deleteCrop(int id) {
		String query = "DELETE FROM crops WHERE crop_id = ?";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setInt(1, id);
			pstmt.executeUpdate();
			refreshCropTable();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error: Existing tasks associated with the crop.");
		}
	}

	/*________________________ CROP STATUS (FOR REPORTS) ________________________*/
	public static Map<String, Integer> getCropStatusData() {
		Map<String, Integer> data = new LinkedHashMap<>();
		String query = "SELECT status, COUNT(*) AS count FROM crops GROUP BY status";

		try (Connection conn = DBConnection.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query)) {

			while (rs.next()) {
				data.put(rs.getString("status"), rs.getInt("count"));
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error loading status data: " + e.getMessage());
		}
		return data;
	}
}
