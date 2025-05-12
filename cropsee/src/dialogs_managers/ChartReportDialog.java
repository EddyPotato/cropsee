package dialogs_managers;

import app.DBConnection;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
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
	
	public ChartReportDialog(JFrame referenceFrame) {
		this.mainFrame = referenceFrame;
	}
	
	/*--------------------- CREATE CROP REPORT ---------------------*/
	@SuppressWarnings("unused")
	public JPanel createCropReportTab() {
		JPanel panel = new JPanel(new BorderLayout());

		// Chart
		Map<String, Integer> cropData = Crop_Manager.getCropStatusData();
		BarChart_Manager chart = new BarChart_Manager(cropData);

		// Controls
		JPanel controls = new JPanel(new BorderLayout());
		controls.setBackground(Color.white);
		controls.setPreferredSize(new Dimension(200, 100));

		JButton refresh = new JButton("Refresh Crop");
		refresh.setFont(new Font("Roboto", Font.BOLD, 20));
		refresh.setFocusPainted(false);
		refresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
		refresh.setPreferredSize(new Dimension(200, 100));
		refresh.setBackground(Color.decode("#F9A825"));
		refresh.setForeground(Color.decode("#FFFFFF"));
		refresh.addActionListener(e -> refreshChart(chart, Crop_Manager::getCropStatusData));

		controls.add(refresh, BorderLayout.EAST);

		panel.add(controls, BorderLayout.SOUTH);
		panel.add(chart, BorderLayout.CENTER);
		return panel;
	}

	/*--------------------- CREATE TASKS REPORT ---------------------*/
	@SuppressWarnings("unused")
	public JPanel createTaskReportTab() {
		JPanel panel = new JPanel(new BorderLayout());

		Map<String, Integer> taskData = Task_Manager.getTaskStatusData();
		BarChart_Manager chart = new BarChart_Manager(taskData);

		JPanel controls = new JPanel(new BorderLayout());
		controls.setBackground(Color.white);
		controls.setPreferredSize(new Dimension(200, 100));

		JButton refresh = new JButton("Refresh Report");
		refresh.setFont(new Font("Roboto", Font.BOLD, 20));
		refresh.setFocusPainted(false);
		refresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
		refresh.setMargin(new Insets(10, 10, 10, 10));
		refresh.setPreferredSize(new Dimension(200, 100));
		refresh.setBackground(Color.decode("#F9A825"));
		refresh.setForeground(Color.decode("#FFFFFF"));
		refresh.addActionListener(e -> refreshChart(chart, Task_Manager::getTaskStatusData));

		controls.add(refresh, BorderLayout.EAST);

		panel.add(controls, BorderLayout.SOUTH);
		panel.add(chart, BorderLayout.CENTER);
		return panel;
	}

	/*--------------------- CREATE INVENTORY REPORT ---------------------*/
	@SuppressWarnings("unused")
	public JPanel createInventoryReportTab() {
		JPanel panel = new JPanel(new BorderLayout());

		// Convert to String-Integer map for BarChartPanel
		Map<String, Integer> inventoryData = new LinkedHashMap<>();
		Inventory_Manager.getInventoryValueData().forEach((k,v) -> 
		inventoryData.put(k, v.intValue())
				);

		BarChart_Manager chart = new BarChart_Manager(inventoryData);

		JPanel controls = new JPanel(new BorderLayout());
		controls.setBackground(Color.white);
		controls.setPreferredSize(new Dimension(200, 100));

		JButton refresh = new JButton("Refresh Inventory");
		refresh.setFont(new Font("Roboto", Font.BOLD, 20));
		refresh.setFocusPainted(false);
		refresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
		refresh.setMargin(new Insets(10, 10, 10, 10));
		refresh.setPreferredSize(new Dimension(200, 100));
		refresh.setBackground(Color.decode("#F9A825"));
		refresh.setForeground(Color.decode("#FFFFFF"));
		refresh.addActionListener(e -> refreshChart(chart, () -> {
			Map<String, Integer> newData = new LinkedHashMap<>();
			Inventory_Manager.getInventoryValueData().forEach((k,v) -> 
			newData.put(k, v.intValue())
					);
			return newData;
		}));

		controls.add(refresh, BorderLayout.EAST);

		panel.add(controls, BorderLayout.SOUTH);
		panel.add(chart, BorderLayout.CENTER);
		return panel;
	}

	public void exportCropData() {
		String query = "SELECT * FROM crops";
		exportToCSV("Crops", query, new String[]{"Crop ID", "Crop Name", "Planting Date", "Harvest Date", "Status"});
	}

	public void exportTaskData() {
		String query = "SELECT * FROM tasks";
		exportToCSV("Tasks", query, new String[]{"Task ID", "Task Name", "Assigned To", "Due Date", "Crop ID", "Priority", "Status"});
	}

	public void exportInventoryData() {
		String query = "SELECT * FROM inventory";
		exportToCSV("Inventory", query, new String[]{"Item ID", "Item Name", "Quantity", "Condition"});
	}

	// Generic CSV export method
	private void exportToCSV(String reportType, String query, String[] headers) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Save " + reportType + " Report");
		fileChooser.setSelectedFile(new File(reportType + "_Report.csv"));

		int userSelection = fileChooser.showSaveDialog(mainFrame);

		if (userSelection == JFileChooser.APPROVE_OPTION) {
			File fileToSave = fileChooser.getSelectedFile();

			try (Connection conn = DBConnection.getConnection();
					Statement stmt = conn.createStatement();
					ResultSet rs = stmt.executeQuery(query);
					BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {

				// Write CSV headers
				writer.write(String.join(",", headers));
				writer.newLine();

				// Write data rows
				while (rs.next()) {
					List<String> row = new ArrayList<>();
					for (int i = 1; i <= headers.length; i++) {
						row.add(rs.getString(i));
					}
					writer.write(String.join(",", row));
					writer.newLine();
				}

				JOptionPane.showMessageDialog(mainFrame, 
						reportType + " data exported successfully to:\n" + fileToSave.getAbsolutePath());

			} catch (Exception ex) {
				JOptionPane.showMessageDialog(mainFrame, 
						"Export failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	// Generic refresh helper
	private void refreshChart(BarChart_Manager chartPanel, Supplier<Map<String, Integer>> dataSupplier) {
		chartPanel.setData(dataSupplier.get());
		chartPanel.revalidate();
		chartPanel.repaint();
	}

	public void styleTable(JTable table) {
		// Center all columns
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		for (int i = 0; i < table.getColumnCount(); i++) {
			table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
		}

		// Style header
		table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value,
					boolean isSelected, boolean hasFocus, int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

				// Match navigation button color (#27AE60)
				setOpaque(true);
				setBackground(Color.decode("#27AE60")); 
				setForeground(Color.WHITE);
				setFont(new Font("Roboto", Font.BOLD, 16));
				setBorder(BorderFactory.createCompoundBorder(
						BorderFactory.createMatteBorder(0, 0, 2, 0, Color.decode("#2C3E50")), // Dark blue border
						BorderFactory.createEmptyBorder(5, 10, 5, 10)
						));
				setHorizontalAlignment(SwingConstants.CENTER);
				return this;
			}
		});

		table.getTableHeader().setPreferredSize(new Dimension(0, 40));
		table.getTableHeader().setReorderingAllowed(false);
		table.setRowHeight(30);
		table.setFont(new Font("Roboto", Font.PLAIN, 14));
	}
}