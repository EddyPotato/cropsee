package ui;

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

public class CropTableManager {
	// CREATION OF A CLASS-LEVEL VARIABLES
	public static DefaultTableModel model;
	public static JTable cropTable;
	
	// ADDS THE TABLE
	// THE PARAMETER IS THE JPANEL WHERE THE TABLE WILL RESIDE INSIDE WITH ITS LAYOUT BEING BORDERLAYOUT
	//SO IT IS IN THE CENTER FILLS TILL THE END OF THE JPANEL'S DIMENSION
	@SuppressWarnings("serial")
	public static void addCropManagementTable(JPanel tableListofCrops) {
		// ADDS THE TABLE TO THE WHOLE TABLE USING BORDERLAYOUT
		tableListofCrops.setLayout(new BorderLayout());

		// ALL COLUMN NAMES FOR THE CROP TABLE
		String[] columnNames = {"Crop ID", "Crop Name", "Planting Date", "Harvest Date", "Status"};

		// CREATING THE TABLE USING THE COLUMN NAMES
		model = new DefaultTableModel(columnNames, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false; // MAKES THE CELLS NOT EDITABLE DIRECTLY ON THE TABLE ITSELF THAN IN A BUTTON
			}
		};

		// MAKING THE JAVA TABLE USING CLASS-LEVEL VARIABLE WHEREIN THE TABLE VALUES ARE ON THE MODEL ABOVE
		cropTable = new JTable(model);
		
		// Center all columns
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		for (int i = 0; i < cropTable.getColumnCount(); i++) {
			cropTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
		}
		
		cropTable.getTableHeader().setReorderingAllowed(false);
		refreshCropTable(); // REFRESHES THE TABLE EACH TIME

		// MAKES THE TABLE SCROLLABLE ESPECIALLY IF MANY
		JScrollPane tableScrollPane = new JScrollPane(cropTable);
		tableScrollPane.setPreferredSize(new Dimension(700, 400));

		// ADDING FONT STYLES TO THE HEADER OF THE TABLE
		cropTable.getTableHeader().setFont(new Font("Roboto", Font.BOLD, 16));

		// ADDING HEIGHT PER ROW
		cropTable.setRowHeight(30);

		// ADDS THE FINISHED RENDERED AND WITH COLUMN-READY DATA TO THE ACTUAL JPANEL WHERE THE TABLE WILL RESIDE, IN THIS CASE THE TABLE CONTAINERS
		tableListofCrops.add(tableScrollPane, BorderLayout.CENTER);
	}

	// REFRESH METHOD WITH USING ANOTHER METHOD BELOW (THIS IS WAY TO REFRESH EVERY CELL OR MORE LIKE EVERY ROW -- IT IS THE ITERATION OF REFRESHING)
	public static void refreshCropTable() {
		model.setRowCount(0); // Clear existing data
		List<Object[]> crops = fetchCropsFromDatabase();
		for (Object[] crop : crops) {
			model.addRow(crop);
		}
	}

	// THE ACTUAL MECHANISM FOR THE REFRESH METHOD
	private static List<Object[]> fetchCropsFromDatabase() {
		// I DONT KNOW WHAT THIS IS BUT IT'S AN ARRAY YES BUT THE STRUCTURE I DON'T KNOW
		List<Object[]> crops = new ArrayList<>();

		// AH... THE QUERY FOR GETTING ALL THE CROPS IN THE TABLE INSIDE THE DATABASE
		String query = "SELECT * FROM crops";

		// MAKING A CONNECTION
		try (Connection conn = DBConnection.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query)) {

			// BASTA YUN HINAHANAP LAHAT HAHAHAHAHA
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
			// THIS IS USED TO CATCH INSTEAD OF A RED ERROR
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error loading crops: " + e.getMessage());
		}
		// WILL RETURN THE CROPS WHICH IS THE LIST OF OBJECTS
		return crops;
	}

	// THE ACTUAL MECHANISM FOR THE ADDING OF CROPS TO THE MYSQL DATABASE SERVER
	// THIS USES THE QUERY AND THE PREPAREDSTATEMENT
	// THIS INITIATES AFTER BEING CLICKED BY THE USER
	public static void addCrop(String name, Date plantingDate, Date harvestDate, String status) {

		// THE QUERY FOR THE ACTUAL CROP BUTTON ACTIONS
		// MAKES THEM TYPE THE QUERY COMMAND JUST BY CLICKING IT
		// THE QUESTION MARKS ARE THE INDEXED STARTING FROM 1
		String query = "INSERT INTO crops (crop_name, planting_date, harvest_date, status) VALUES (?, ?, ?, ?)";

		// FIRST, CONNECT TO THE DATABASE
		try (Connection conn = DBConnection.getConnection();
				// PUTS THE INPUT TO THE QUERY USING PREPARED STATEMENT
				PreparedStatement pstmt = conn.prepareStatement(query)) {

			pstmt.setString(1, name); // FOR FIRST ?
			pstmt.setDate(2, plantingDate); // FOR SECOND ?
			pstmt.setDate(3, harvestDate); // FOR THIRD ?
			pstmt.setString(4, status); // FOR FOURTH ?
			pstmt.executeUpdate(); // WILL EXECUTE THE QUERY
			refreshCropTable(); // REFRESHES THE TABLE AFTERWARDS FOR IT TO SHOW

		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error adding crop: " + e.getMessage()); // IF ERROR
		}
	}

	// THE SAME PROCESS BUT FOR UPDATING CROPS
	public static void updateCrop(int id, String name, Date plantingDate, Date harvestDate, String status) {
		
		// THIS IS THE QUERY
		String query = "UPDATE crops SET crop_name = ?, planting_date = ?, harvest_date = ?, status = ? WHERE crop_id = ?";

		// GET CONNECTION
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(query)) {

			// ADD THE DATA COLLECTED FROM USER TO QUERY
			// MIDDLE MAN FOR USER AND DATABASE
			pstmt.setString(1, name);
			pstmt.setDate(2, plantingDate);
			pstmt.setDate(3, harvestDate);
			pstmt.setString(4, status);
			pstmt.setInt(5, id);
			pstmt.executeUpdate(); // EXECUTE THE COMMAND
			refreshCropTable(); // REFRESH AFTER

		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error updating crop: " + e.getMessage()); // THE MESSAGE IF ERROR
		}
	}

	// THIS WILL DELETE IT, THOUGH USING ID ONLY
	public static void deleteCrop(int id) {
		// THE QUERY COMMAND
		String query = "DELETE FROM crops WHERE crop_id = ?";

		// GET CONNECTION AGAIN
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(query)) {

			pstmt.setInt(1, id);
			pstmt.executeUpdate(); // EXECUTE THE COMMAND
			refreshCropTable(); // REFRESH AFTERWARDS

		} catch (SQLException e) {
			// ERROR MESSAGE ATTACHMENT TO CROP_ID AND TASK_ID
			JOptionPane.showMessageDialog(null, "Error: " + "There are still tasks associated with the crop.\n" + e.getMessage());
		}
	}
	
	// THIS GETS THE DATA FOR THE STATUS OF THE CROPS
	public static Map<String, Integer> getCropStatusData() {
		Map<String, Integer> data = new LinkedHashMap<>();
		String query = "SELECT status, COUNT(*) AS count FROM crops GROUP BY status";

		// GETS CONNECTION FIRST
		try (Connection conn = DBConnection.getConnection();
				// MAKES THE MYSQL PREPAREDSTATEMENT AND RESULTSET
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query)) { // WILL EXECUTE USING THE RESULTS

			// WHILE THERE IS STILL RESULT
			while (rs.next()) {
				// I DON'T GET THIS NOW
				data.put(rs.getString("status"), rs.getInt("count"));
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error loading status data: " + e.getMessage()); // ERROR MESSAGE
		}
		return data; // UPDATES THE DATA FOR THE STATUS FOR SOME REASON
	}
}