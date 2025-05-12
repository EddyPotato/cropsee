package dialogs_managers;

import app.DBConnection;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import ui_managers.BarChart_Manager;
import ui_managers.Crop_Manager;
import ui_managers.Inventory_Manager;
import ui_managers.Task_Manager;

public class ChartReportDialog {
	private final JFrame mainFrame;

	// Constructor receives the main application frame to be used in dialogs
	public ChartReportDialog(JFrame referenceFrame) {
		this.mainFrame = referenceFrame;
	}

	/*--------------------- CREATE CROP REPORT ---------------------*/
	public JPanel createCropReportTab() {
		JPanel panel = new JPanel(new BorderLayout());

		// Fetch crop status data from Crop_Manager
		Map<String, Integer> cropData = Crop_Manager.getCropStatusData();

		// Create a bar chart for crop data
		BarChart_Manager cropChart = new BarChart_Manager(cropData);
		cropChart.setPreferredSize(new Dimension(300, 300));

		// Add the chart to the center of the panel
		panel.add(cropChart, BorderLayout.CENTER);

		// Create a button to refresh the chart with new data
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1, 2, 5, 5));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		buttonPanel.setBackground(Color.white);
		
		buttonPanel.add(createRefreshButton("Refresh Crop", cropChart, Crop_Manager::getCropStatusData)); // Refresh button
		buttonPanel.add(createExportButton("Create Image", cropChart)); // Export button

		panel.add(buttonPanel, BorderLayout.SOUTH); // Add button panel to the bottom of the main panel


		return panel;
	}

	/*--------------------- CREATE TASKS REPORT ---------------------*/
	public JPanel createTaskReportTab() {
		JPanel panel = new JPanel(new BorderLayout());
		

		// Fetch task status data from Task_Manager
		Map<String, Integer> taskData = Task_Manager.getTaskStatusData();

		// Create a bar chart for task data
		BarChart_Manager taskChart = new BarChart_Manager(taskData);
		taskChart.setPreferredSize(new Dimension(300, 300));

		// Add chart and refresh button
		panel.add(taskChart, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1, 2, 5, 5));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		buttonPanel.setBackground(Color.white);
		
		buttonPanel.add(createRefreshButton("Refresh Crop", taskChart, Crop_Manager::getCropStatusData));
		buttonPanel.add(createExportButton("Create Image", taskChart));

		panel.add(buttonPanel, BorderLayout.SOUTH);

		return panel;
	}

	/*--------------------- CREATE INVENTORY REPORT ---------------------*/
	public JPanel createInventoryReportTab() {
		JPanel panel = new JPanel(new BorderLayout());

		// Retrieve inventory values and convert to Integer type
		Map<String, Integer> inventoryData = new LinkedHashMap<>();
		Inventory_Manager.getInventoryValueData().forEach((k, v) -> inventoryData.put(k, v.intValue()));

		// Create a bar chart for inventory data
		BarChart_Manager inventoryChart = new BarChart_Manager(inventoryData);
		inventoryChart.setPreferredSize(new Dimension(300, 300));

		// Add chart to the panel
		panel.add(inventoryChart, BorderLayout.CENTER);

		// Add refresh button to update chart with latest inventory values
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1, 2, 5, 5));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		buttonPanel.setBackground(Color.white);
		
		JButton refresh = createRefreshButton("Refresh Inventory", inventoryChart, () -> {
			Map<String, Integer> newInventoryData = new LinkedHashMap<>();
			Inventory_Manager.getInventoryValueData().forEach((k, v) -> newInventoryData.put(k, v.intValue()));
			return newInventoryData;
		});
		buttonPanel.add(refresh);
		buttonPanel.add(createExportButton("Create Image", inventoryChart));

		panel.add(buttonPanel, BorderLayout.SOUTH);
		return panel;
	}

	/*--------------------- HELPER METHODS ---------------------*/

	// Creates a styled refresh button that updates a chart using a provided data supplier
	@SuppressWarnings("unused")
	private JButton createRefreshButton(String label, BarChart_Manager chart, Supplier<Map<String, Integer>> dataSupplier) {
		JButton refresh = new JButton(label);
		refresh.setFont(new Font("Roboto", Font.BOLD, 20));
		refresh.setFocusPainted(false);
		refresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
		refresh.setPreferredSize(new Dimension(200, 50));
		refresh.setBackground(Color.decode("#F9A825"));
		refresh.setForeground(Color.decode("#FFFFFF"));

		// Action listener that refreshes the chart when clicked
		refresh.addActionListener(e -> refreshChart(chart, dataSupplier));
		return refresh;
	}

	// Creates a styled export button that allows the user to save the chart as an image
	@SuppressWarnings("unused")
	private JButton createExportButton(String label, BarChart_Manager chart) {
		JButton export = new JButton(label);
		export.setFont(new Font("Roboto", Font.BOLD, 20));
		export.setFocusPainted(false);
		export.setCursor(new Cursor(Cursor.HAND_CURSOR));
		export.setPreferredSize(new Dimension(200, 50));
		export.setBackground(Color.decode("#00897B"));
		export.setForeground(Color.WHITE);

		export.addActionListener(e -> {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setDialogTitle("Export Chart as Image");
			fileChooser.setSelectedFile(new File("chart.png"));

			int choice = fileChooser.showSaveDialog(mainFrame);
			if (choice == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				if (!file.getName().endsWith(".png")) {
					file = new File(file.getAbsolutePath() + ".png");
				}
				chart.exportAsImage(file);
			}
		});
		return export;
	}

	// Exports crop data to a CSV file
	public void exportCropData() {
		String query = "SELECT * FROM crops";
		exportToCSV("Crops", query, new String[]{"Crop ID", "Crop Name", "Planting Date", "Harvest Date", "Status"});
	}

	// Exports task data to a CSV file
	public void exportTaskData() {
		String query = "SELECT * FROM tasks";
		exportToCSV("Tasks", query, new String[]{"Task ID", "Task Name", "Assigned To", "Due Date", "Crop ID", "Priority", "Status"});
	}

	// Exports inventory data to a CSV file
	public void exportInventoryData() {
		String query = "SELECT * FROM inventory";
		exportToCSV("Inventory", query, new String[]{"Item ID", "Item Name", "Quantity", "Condition"});
	}

	// Generic method to export any report data to CSV
	private void exportToCSV(String reportType, String query, String[] headers) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Save " + reportType + " Report");
		fileChooser.setSelectedFile(new File(reportType + "_Report.csv"));

		int userSelection = fileChooser.showSaveDialog(mainFrame);

		if (userSelection == JFileChooser.APPROVE_OPTION) {
			File fileToSave = fileChooser.getSelectedFile();

			try (
					Connection conn = DBConnection.getConnection();
					Statement stmt = conn.createStatement();
					ResultSet rs = stmt.executeQuery(query);
					BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))
					) {
				// Write CSV column headers
				writer.write(String.join(",", headers));
				writer.newLine();

				// Write each row of data
				while (rs.next()) {
					List<String> row = new ArrayList<>();
					for (int i = 1; i <= headers.length; i++) {
						row.add(rs.getString(i));
					}
					writer.write(String.join(",", row));
					writer.newLine();
				}

				// Show success dialog
				JOptionPane.showMessageDialog(mainFrame, 
						reportType + " data exported successfully to:\n" + fileToSave.getAbsolutePath());

			} catch (Exception ex) {
				// Show error dialog if something goes wrong
				JOptionPane.showMessageDialog(mainFrame, 
						"Export failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	// Updates a bar chart with fresh data and repaints it
	private void refreshChart(BarChart_Manager chartPanel, Supplier<Map<String, Integer>> dataSupplier) {
		chartPanel.setData(dataSupplier.get());
		chartPanel.revalidate();
		chartPanel.repaint();
	}

	// Styles a given JTable: centers text, customizes headers and fonts
	@SuppressWarnings("serial")
	public void styleTable(JTable table) {
		// Center-align all cell content
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		for (int i = 0; i < table.getColumnCount(); i++) {
			table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
		}

		// Customize the table header appearance
		table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value,
					boolean isSelected, boolean hasFocus, int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				setOpaque(true);
				setBackground(Color.decode("#27AE60")); // Green color
				setForeground(Color.WHITE);
				setFont(new Font("Roboto", Font.BOLD, 16));
				setBorder(BorderFactory.createCompoundBorder(
						BorderFactory.createMatteBorder(0, 0, 2, 0, Color.decode("#2C3E50")), // Blue bottom border
						BorderFactory.createEmptyBorder(5, 10, 5, 10)
						));
				setHorizontalAlignment(SwingConstants.CENTER);
				return this;
			}
		});

		// Set header and row sizes and fonts
		table.getTableHeader().setPreferredSize(new Dimension(0, 40));
		table.getTableHeader().setReorderingAllowed(false);
		table.setRowHeight(30);
		table.setFont(new Font("Roboto", Font.PLAIN, 14));
	}
}