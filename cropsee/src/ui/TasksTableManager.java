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

public class TasksTableManager {
	public static DefaultTableModel model;
	public static JTable tasksTable;
	
	public static void addTasksTable(JPanel tableContainer) {
		tableContainer.setLayout(new BorderLayout());
		String[] columnNames = { "Task ID", "Task Name", "Assigned To", "Due Date", "Crop ID", "Priority", "Status" };

		model = new DefaultTableModel(columnNames, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		tasksTable = new JTable(model);

		// Center all columns
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		for (int i = 0; i < tasksTable.getColumnCount(); i++) {
			tasksTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
		}
		
		tasksTable.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
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
        tasksTable.getTableHeader().setPreferredSize(new Dimension(0, 40));
        tasksTable.getTableHeader().setOpaque(false);

		tasksTable.getTableHeader().setReorderingAllowed(false);
		refreshTaskTable();

		JScrollPane tableScrollPane = new JScrollPane(tasksTable);
		tableScrollPane.setPreferredSize(new Dimension(700, 400));

		tasksTable.setRowHeight(30);

		tableContainer.add(tableScrollPane, BorderLayout.CENTER);
	}

	public static void refreshTaskTable() {
		model.setRowCount(0);
		List<Object[]> tasks = fetchTasksFromDatabase();
		for (Object[] task : tasks) {
			model.addRow(task);
		}
	}

	private static List<Object[]> fetchTasksFromDatabase() {
		List<Object[]> tasks = new ArrayList<>();
		String query = "SELECT task_id, task_name, assigned_to, due_date, crop_id, priority, status FROM tasks";

		try (Connection conn = DBConnection.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query)) {

			while (rs.next()) {
				Object[] row = {
						rs.getInt("task_id"),
						rs.getString("task_name"),
						rs.getString("assigned_to"),
						rs.getDate("due_date"),
						rs.getObject("crop_id"),  // Handle NULL values
						rs.getString("priority"),
						rs.getString("status")
				};
				tasks.add(row);
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error loading tasks: " + e.getMessage());
		}
		return tasks;
	}

	public static void addTask(String taskName, String assignedTo, Date dueDate, 
			Integer cropId, String priority, String status) {
		String query = "INSERT INTO tasks (task_name, assigned_to, due_date, crop_id, priority, status) " +
				"VALUES (?, ?, ?, ?, ?, ?)";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(query)) {

			pstmt.setString(1, taskName);
			pstmt.setString(2, assignedTo);
			pstmt.setDate(3, dueDate);
			pstmt.setObject(4, cropId, Types.INTEGER);
			pstmt.setString(5, priority);
			pstmt.setString(6, status);
			pstmt.executeUpdate();
			refreshTaskTable();

		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error adding task: " + e.getMessage());
		}
	}

	public static void updateTask(int taskId, String taskName, String assignedTo, Date dueDate, 
			Integer cropId, String priority, String status) {
		String query = "UPDATE tasks SET task_name=?, assigned_to=?, due_date=?, " +
				"crop_id=?, priority=?, status=? WHERE task_id=?";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(query)) {

			pstmt.setString(1, taskName);
			pstmt.setString(2, assignedTo);
			pstmt.setDate(3, dueDate);
			pstmt.setObject(4, cropId, Types.INTEGER);
			pstmt.setString(5, priority);
			pstmt.setString(6, status);
			pstmt.setInt(7, taskId);
			pstmt.executeUpdate();
			refreshTaskTable();

		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error updating task: " + e.getMessage());
		}
	}

	public static void deleteTask(int taskId) {
		String query = "DELETE FROM tasks WHERE task_id = ?";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(query)) {

			pstmt.setInt(1, taskId);
			pstmt.executeUpdate();
			refreshTaskTable();

		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error deleting task: " + e.getMessage());
		}
	}

	public static void markTaskComplete(int taskId) {
		String query = "UPDATE tasks SET status = 'Completed' WHERE task_id = ?";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(query)) {

			pstmt.setInt(1, taskId);
			pstmt.executeUpdate();
			refreshTaskTable();

		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error marking task complete: " + e.getMessage());
		}
	}
	public static void removeCompletedTasks() {
		String query = "DELETE FROM tasks WHERE status = 'Completed'";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(query)) {

			int deletedRows = pstmt.executeUpdate();
			refreshTaskTable();
			JOptionPane.showMessageDialog(null, 
					"Removed " + deletedRows + " completed tasks", 
					"Cleanup Complete", JOptionPane.INFORMATION_MESSAGE);

		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error removing tasks: " + e.getMessage());
		}
	}
	public static Map<String, Integer> getTaskStatusData() {
		Map<String, Integer> data = new LinkedHashMap<>();
		String query = "SELECT status, COUNT(*) AS count FROM tasks GROUP BY status";

		try (Connection conn = DBConnection.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query)) {

			while (rs.next()) {
				data.put(rs.getString("status"), rs.getInt("count"));
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error loading task data: " + e.getMessage());
		}
		return data;
	}
}