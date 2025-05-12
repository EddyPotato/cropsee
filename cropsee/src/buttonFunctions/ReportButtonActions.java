package buttonFunctions;

import app.DBConnection;
import dataManagers.BarChartDataManager;
import dataManagers.CropDataManager;
import dataManagers.InventoryDataManager;
import dataManagers.TaskDataManager;

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

public class ReportButtonActions {
	/*========================================== CLASS-LEVEL ==========================================*/
	private final JFrame mainFrame;
	public ReportButtonActions(JFrame referenceFrame) {
		this.mainFrame = referenceFrame;
	}
	
	/*========================================== (THIS CREATES THE USER INTERFACE) ==========================================*/
	/*========================================== CREATE CROP REPORT ==========================================*/
	/*========================================== CREATE CROP REPORT ==========================================*/
	public JPanel createCropReportTab() {
		/*_______________________________ MAIN PANEL _______________________________*/
		JPanel panel = new JPanel(new BorderLayout());

		/*_______________________________ GETS CROP DATA _______________________________*/
		Map<String, Integer> cropData = CropDataManager.getCropStatusData();

		/*_______________________________ CREATES CHART _______________________________*/
		BarChartDataManager cropChart = new BarChartDataManager(cropData);
		cropChart.setPreferredSize(new Dimension(300, 300));

		/*_______________________________ ADD CHART TO MAIN PANEL _______________________________*/
		panel.add(cropChart, BorderLayout.CENTER);

		/*_______________________________ BUTTONS _______________________________*/
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1, 2, 5, 5));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		buttonPanel.setBackground(Color.white);
		
		buttonPanel.add(createRefreshButton("Refresh Crop", cropChart, CropDataManager::getCropStatusData)); // Refresh button
		buttonPanel.add(createImageButton("Create Image", cropChart)); // Export button

		/*_______________________________ ADD BUTTONS TO BOTTOM OF MAIN PANEL _______________________________*/
		panel.add(buttonPanel, BorderLayout.SOUTH);
		
		/*_______________________________ RETURN PRODUCED TABLE _______________________________*/
		return panel;
	}

	/*========================================== CREATE TASKS REPORT ==========================================*/
	/*========================================== CREATE TASKS REPORT ==========================================*/
	/*========================================== CREATE TASKS REPORT ==========================================*/
	public JPanel createTaskReportTab() {
		/*_______________________________ MAIN PANEL _______________________________*/
		JPanel panel = new JPanel(new BorderLayout());
		
		/*_______________________________ GETS TASK DATA _______________________________*/
		Map<String, Integer> taskData = TaskDataManager.getTaskStatusData();

		/*_______________________________ CREATES CHART _______________________________*/
		BarChartDataManager taskChart = new BarChartDataManager(taskData);
		taskChart.setPreferredSize(new Dimension(300, 300));

		/*_______________________________ ADD CHART TO MAIN PANEL _______________________________*/
		panel.add(taskChart, BorderLayout.CENTER);

		/*_______________________________ BUTTONS _______________________________*/
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1, 2, 5, 5));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		buttonPanel.setBackground(Color.white);
		
		/*_______________________________ ADD BUTTONS TO BOTTOM OF MAIN PANEL _______________________________*/
		buttonPanel.add(createRefreshButton("Refresh Crop", taskChart, TaskDataManager::getTaskStatusData));
		buttonPanel.add(createImageButton("Create Image", taskChart));

		/*_______________________________ ADD BUTTONS TO BOTTOM OF MAIN PANEL _______________________________*/
		panel.add(buttonPanel, BorderLayout.SOUTH);

		/*_______________________________ RETURN PRODUCED TABLE _______________________________*/
		return panel;
	}

	/*========================================== CREATE TASKS REPORT ==========================================*/
	/*========================================== CREATE TASKS REPORT ==========================================*/
	/*========================================== CREATE TASKS REPORT ==========================================*/
	public JPanel createInventoryReportTab() {
		/*_______________________________ MAIN PANEL _______________________________*/
		JPanel panel = new JPanel(new BorderLayout());

		/*_______________________________ GETS INVENTORY DATA _______________________________*/
		Map<String, Integer> inventoryData = new LinkedHashMap<>();
		InventoryDataManager.getInventoryValueData().forEach((k, v) -> inventoryData.put(k, v.intValue()));

		/*_______________________________ CREATES CHART _______________________________*/
		BarChartDataManager inventoryChart = new BarChartDataManager(inventoryData);
		inventoryChart.setPreferredSize(new Dimension(300, 300));

		/*_______________________________ ADD CHART TO MAIN PANEL _______________________________*/
		panel.add(inventoryChart, BorderLayout.CENTER);

		/*_______________________________ BUTTONS _______________________________*/
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1, 2, 5, 5));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		buttonPanel.setBackground(Color.white);
		
		/*_______________________________ ADD BUTTONS TO BOTTOM OF MAIN PANEL _______________________________*/
		JButton refresh = createRefreshButton("Refresh Inventory", inventoryChart, () -> {
			Map<String, Integer> newInventoryData = new LinkedHashMap<>();
			InventoryDataManager.getInventoryValueData().forEach((k, v) -> newInventoryData.put(k, v.intValue()));
			return newInventoryData;
		});
		buttonPanel.add(refresh);
		buttonPanel.add(createImageButton("Create Image", inventoryChart));

		/*_______________________________ ADD BUTTONS TO BOTTOM OF MAIN PANEL _______________________________*/
		panel.add(buttonPanel, BorderLayout.SOUTH);
		
		/*_______________________________ RETURN PRODUCED TABLE _______________________________*/
		return panel;
	}

	/*========================================== CREATE TASKS REPORT ==========================================*/
	/*========================================== CREATE TASKS REPORT ==========================================*/
	/*========================================== CREATE TASKS REPORT ==========================================*/
	private JButton createRefreshButton(String label, BarChartDataManager chart, Supplier<Map<String, Integer>> dataSupplier) {
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

	/*========================================== CREATE IMAGE ==========================================*/
	/*========================================== CREATE IMAGE ==========================================*/
	/*========================================== CREATE IMAGE ==========================================*/
	private JButton createImageButton(String label, BarChartDataManager chart) {
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
	
	/*========================================== FOR CROPS ==========================================*/
	/*========================================== FOR CROPS ==========================================*/
	/*========================================== FOR CROPS ==========================================*/
	public void exportCropData() {
		String query = "SELECT * FROM crops";
		exportToCSV("Crops", query, new String[]{"Crop ID", "Crop Name", "Planting Date", "Harvest Date", "Status"});
	}
	
	/*========================================== FOR TASKS ==========================================*/
	/*========================================== FOR TASKS ==========================================*/
	/*========================================== FOR TASKS ==========================================*/
	public void exportTaskData() {
		String query = "SELECT * FROM tasks";
		exportToCSV("Tasks", query, new String[]{"Task ID", "Task Name", "Assigned To", "Due Date", "Crop ID", "Priority", "Status"});
	}
	
	/*========================================== FOR INVENTORY ==========================================*/
	/*========================================== FOR INVENTORY ==========================================*/
	/*========================================== FOR INVENTORY ==========================================*/
	public void exportInventoryData() {
		String query = "SELECT * FROM inventory";
		exportToCSV("Inventory", query, new String[]{"Item ID", "Item Name", "Quantity", "Condition"});
	}
	
	/*========================================== EXCEL MECHANISM ==========================================*/
	/*========================================== EXCEL MECHANISM ==========================================*/
	/*========================================== EXCEL MECHANISM ==========================================*/
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
	
	/*========================================== REFRESH ==========================================*/
	/*========================================== REFRESH ==========================================*/
	/*========================================== REFRESH ==========================================*/
	private void refreshChart(BarChartDataManager chartPanel, Supplier<Map<String, Integer>> dataSupplier) {
		chartPanel.setData(dataSupplier.get());
		chartPanel.revalidate();
		chartPanel.repaint();
	}
	
	/*========================================== STYLE TABLE ==========================================*/
	/*========================================== STYLE TABLE ==========================================*/
	/*========================================== STYLE TABLE ==========================================*/
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