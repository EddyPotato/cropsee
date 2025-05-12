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
public class Task_Manager {
	public static DefaultTableModel model;
	public static JTable tasksTable;

	public static void addTasksTable(JPanel tableContainer) {
		tableContainer.setLayout(new BorderLayout());
		String[] columnNames = {
				"Task ID", 
				"Task Name", 
				"Assigned To", 
				"Due Date", 
				"Crop ID (Optional)", 
				"Priority", 
				"Status"
		};

		model = new DefaultTableModel(columnNames, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		/*_____________________ TABLE _____________________*/
		tasksTable = new JTable(model);

		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer(); // CENTER
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
				setBackground(Color.decode("#27AE60")); // GREEN
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

		/*_____________________ CUSTOMIZATION _____________________*/
		tasksTable.getTableHeader().setPreferredSize(new Dimension(0, 40));
		tasksTable.getTableHeader().setOpaque(false);
		tasksTable.getTableHeader().setReorderingAllowed(false);
		refreshTaskTable();
		JScrollPane tableScrollPane = new JScrollPane(tasksTable);
		tableScrollPane.setPreferredSize(new Dimension(700, 400));
		tasksTable.setRowHeight(30);
		tableContainer.add(tableScrollPane, BorderLayout.CENTER);
	}

	/*_____________________ REFRESH _____________________*/
	public static void refreshTaskTable() {
		model.setRowCount(0);
		List<Object[]> tasks = fetchTasksFromDatabase();
		for (Object[] task : tasks) {
			model.addRow(task);
		}
	}

	/*________________________ GET DATA ________________________*/
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

	/*________________________ ADD TASKS ________________________*/
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

	/*________________________ UPDATE TASKS ________________________*/
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

	/*________________________ DELETE TASKS ________________________*/
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

	/*________________________ MARK COMPLETE ________________________*/
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
	
	/*________________________ REMOVE COMPLETED TASKS ________________________*/
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

	/*________________________ TASK STATUS (FOR REPORTING) ________________________*/
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

	/*________________________ UPCOMING TASKS ________________________*/
	public static List<Object[]> getUpcomingTasksThisWeek() {
		List<Object[]> tasks = new ArrayList<>();
		String query = """
					SELECT task_id, task_name, assigned_to, due_date, crop_id, priority, status
					FROM tasks
					WHERE due_date BETWEEN CURRENT_DATE AND DATE_ADD(CURRENT_DATE, INTERVAL 7 DAY)
					ORDER BY due_date ASC
				""";

		try (Connection conn = DBConnection.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query)) {
			while (rs.next()) {
				Object[] row = {
						rs.getInt("task_id"),
						rs.getString("task_name"),
						rs.getString("assigned_to"),
						rs.getDate("due_date"),
						rs.getObject("crop_id"),
						rs.getString("priority"),
						rs.getString("status")
				};
				tasks.add(row);
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error loading upcoming tasks: " + e.getMessage());
		}
		return tasks;
	}

	/*________________________ OVERDUE TASKS ________________________*/
	public static List<Object[]> getOverdueTasks() {
		List<Object[]> tasks = new ArrayList<>();
		String query = """
					SELECT task_id, task_name, assigned_to, due_date, crop_id, priority, status
					FROM tasks
					WHERE due_date < CURRENT_DATE AND status != 'Completed'
					ORDER BY due_date ASC
				""";

		try (Connection conn = DBConnection.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query)) {
			while (rs.next()) {
				Object[] row = {
						rs.getInt("task_id"),
						rs.getString("task_name"),
						rs.getString("assigned_to"),
						rs.getDate("due_date"),
						rs.getObject("crop_id"),
						rs.getString("priority"),
						rs.getString("status")
				};
				tasks.add(row);
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error loading overdue tasks: " + e.getMessage());
		}
		return tasks;
	}

	/*________________________ GROUP TASKS BY CROP ________________________*/
	@SuppressWarnings("unused")
	public static Map<Integer, List<Object[]>> getTasksGroupedByCrop() {
		Map<Integer, List<Object[]>> cropTasks = new LinkedHashMap<>();
		String query = "SELECT * FROM tasks ORDER BY crop_id, due_date";

		try (Connection conn = DBConnection.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query)) {

			while (rs.next()) {
				int cropId = rs.getInt("crop_id");
				Object[] task = {
						rs.getInt("task_id"),
						rs.getString("task_name"),
						rs.getString("assigned_to"),
						rs.getDate("due_date"),
						cropId,
						rs.getString("priority"),
						rs.getString("status")
				};
				cropTasks.computeIfAbsent(cropId, k -> new ArrayList<>()).add(task);
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error grouping tasks by crop: " + e.getMessage());
		}
		return cropTasks;
	}

	/*________________________ REMINDERS LOG ________________________*/
	public static List<Object[]> getRemindersLog() {
		List<Object[]> logs = new ArrayList<>();
		String query = """
					SELECT r.reminder_id, t.task_name, r.reminder_type, r.reminder_date, r.notes
					FROM reminders r
					JOIN tasks t ON r.task_id = t.task_id
					ORDER BY r.reminder_date ASC
				""";

		try (Connection conn = DBConnection.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query)) {
			while (rs.next()) {
				Object[] row = {
						rs.getInt("reminder_id"),
						rs.getString("task_name"),
						rs.getString("reminder_type"),
						rs.getDate("reminder_date"),
						rs.getString("notes")
				};
				logs.add(row);
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error loading reminders: " + e.getMessage());
		}
		return logs;
	}
}