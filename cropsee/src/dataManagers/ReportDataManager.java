package dataManagers;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;  // Using java.util.Date for display
import app.DBConnection;

@SuppressWarnings("serial")
public class ReportDataManager {
	/*_____________________ CLASS-LEVEL _____________________*/
	private static DefaultTableModel model;
	private static JTable reportsTable;

	/*_____________________ METHODS _____________________*/
	public static void addReportsTable(JPanel reportsPanel) {
		String[] columnNames = {
				"Report Type", 
				"Generated On", 
				"Details"
		};

		model = new DefaultTableModel(columnNames, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		/*_____________________ GRAPH TABLE _____________________*/
		reportsTable = new JTable(model);

		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer(); // CENTER
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		for (int i = 0; i < reportsTable.getColumnCount(); i++) {
			reportsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
		}

		reportsTable.getTableHeader().setReorderingAllowed(false);
		refreshReports();
		JScrollPane scrollPane = new JScrollPane(reportsTable);
		reportsPanel.add(scrollPane);
	}

	/*_____________________ REFRESH REPORTS _____________________*/
	public static void refreshReports() {
		model.setRowCount(0);
		addReportRow(
				"Harvest Summary", 
				new Date(System.currentTimeMillis()),
				"Total harvested crops: " + getHarvestedCropCount()
				);
	}

	/*_____________________ ADD _____________________*/
	private static void addReportRow(String type, Date generatedOn, String details) {
		model.addRow(new Object[]{type, generatedOn, details});
	}

	/*_____________________ AMOUNT OF HARVESTED CROP _____________________*/
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