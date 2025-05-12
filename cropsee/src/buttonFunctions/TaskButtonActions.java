package buttonFunctions;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import com.toedter.calendar.JDateChooser;

import app.DBConnection;
import dataManagers.TaskDataManager;

import java.awt.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TaskButtonActions {
	/*========================================== CLASS-LEVEL ==========================================*/
	private JFrame mainFrame;
	public TaskButtonActions(JFrame mainFrame) {
		this.mainFrame = mainFrame;
	}
	private String lastUsedPriority = "Medium";
	private String lastUsedStatus = "Pending";

	/*========================================== ADD DIALOG ==========================================*/
	/*========================================== ADD DIALOG ==========================================*/
	/*========================================== ADD DIALOG ==========================================*/
	public void showAddTaskButtonActions() {
		/*_______________________________ CREATE DIALOG _______________________________*/
		JDialog dialog = new JDialog(mainFrame, "Add New Task", true);
		dialog.setPreferredSize(new Dimension(500, 400));
		
		/*_______________________________ CREATE CONTENT PANEL _______________________________*/
		JPanel contentPanel = new JPanel(new GridLayout(7, 2, 5, 5)); // (6 FIELDS + 1 SUBMIT) X 2
		contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

		/*_______________________________ TEXT FIELDS _______________________________*/
		JTextField taskNameField = new JTextField();
		taskNameField.setDocument(new PlainDocument() {
			@Override
			public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
				if (str != null && (getLength() + str.length()) <= 50) {
					super.insertString(offs, str, a);
				}
			}
		});
		JTextField assignedToField = new JTextField();
		JDateChooser dueDateField = new JDateChooser();
		JComboBox<Integer> cropIdCombo = new JComboBox<>(fetchCropIds()); // ADDS ALL EXISTING CROP ID
		cropIdCombo.insertItemAt(null, 0);
		cropIdCombo.setSelectedIndex(0);
		JComboBox<String> priorityCombo = new JComboBox<>(new String[]{"Low", "Medium", "High"});
		JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Pending", "In Progress", "Completed"});

		/*___________________ VALIDATION ___________________*/
		priorityCombo.setSelectedItem(lastUsedPriority);
		statusCombo.setSelectedItem(lastUsedStatus);

		Border defaultBorder = taskNameField.getBorder();
		Border errorBorder = BorderFactory.createLineBorder(Color.RED);

		Runnable liveValidate = () -> {
			taskNameField.setBorder(taskNameField.getText().trim().isEmpty() ? errorBorder : defaultBorder);
			assignedToField.setBorder(assignedToField.getText().trim().isEmpty() ? errorBorder : defaultBorder);
			dueDateField.setBorder(dueDateField.getDate() == null ? errorBorder : defaultBorder);
		};

		taskNameField.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) { liveValidate.run(); }
			public void removeUpdate(DocumentEvent e) { liveValidate.run(); }
			public void changedUpdate(DocumentEvent e) { liveValidate.run(); }
		});

		assignedToField.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) { liveValidate.run(); }
			public void removeUpdate(DocumentEvent e) { liveValidate.run(); }
			public void changedUpdate(DocumentEvent e) { liveValidate.run(); }
		});

		dueDateField.getDateEditor().addPropertyChangeListener("date", e -> liveValidate.run());

		/*___________________ INSERTION TO CONTENT PANEL ___________________*/
		String[][] labels = {
				{"Task Name: ", "Enter the task title"},
				{"Assigned To: ", "Enter the assignee"},
				{"Due Date: ", "Pick a due date"},
				{"Crop ID (Optional): ", "Link to a crop if applicable"},
				{"Priority: ", "Select task priority"},
				{"Status: ", "Current progress state"}
		};

		JComponent[] fields = {
				taskNameField, assignedToField, dueDateField,
				cropIdCombo, priorityCombo, statusCombo
		};

		for (int i = 0; i < labels.length; i++) {
			contentPanel.add(new JLabel(labels[i][0])); // FIXED TO THE 1ST COLUMN
			fields[i].setToolTipText(labels[i][1]); // FIXED TO THE 2ND COLUMN
			contentPanel.add(fields[i]);
		}

		/*___________________ SUBMIT ___________________*/
		JButton submitButton = new JButton("ADD TASKS");
		submitButton.setFocusPainted(false);

		submitButton.addActionListener(e -> {
			try {
				/*___________________ VALIDATE ___________________*/
				boolean hasError = false;
				if (taskNameField.getText().trim().isEmpty()) {
					taskNameField.setBorder(errorBorder);
					hasError = true;
				}
				if (assignedToField.getText().trim().isEmpty()) {
					assignedToField.setBorder(errorBorder);
					hasError = true;
				}
				if (dueDateField.getDate() == null) {
					dueDateField.setBorder(errorBorder);
					hasError = true;
				}
				if (hasError) throw new IllegalArgumentException("Please fill in all required fields.");


				Date dueDate = new java.sql.Date(dueDateField.getDate().getTime());
				Integer cropId = (Integer) cropIdCombo.getSelectedItem();
				lastUsedPriority = (String) priorityCombo.getSelectedItem();
				lastUsedStatus = (String) statusCombo.getSelectedItem();

				/*___________________ FUNCTION ___________________*/
				/*___________________ FUNCTION ___________________*/
				/*___________________ FUNCTION ___________________*/
				TaskDataManager.addTask(
						taskNameField.getText().trim(),
						assignedToField.getText().trim(),
						dueDate,
						cropId,
						lastUsedPriority,
						lastUsedStatus
						);

				dialog.dispose();
			} catch (IllegalArgumentException ex) {
				JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Input Error", JOptionPane.WARNING_MESSAGE);
			}
		});

		contentPanel.add(new JPanel());
		contentPanel.add(submitButton);
		dialog.setContentPane(contentPanel);
		dialog.pack();
		dialog.setLocationRelativeTo(mainFrame);
		dialog.setVisible(true);
	}

	/*========================================== UPDATE DIALOG ==========================================*/
	/*========================================== UPDATE DIALOG ==========================================*/
	/*========================================== UPDATE DIALOG ==========================================*/
	public void showEditTaskDialog() {
		/*________________________ SELECTED ROW ________________________*/
		int selectedRow = TaskDataManager.tasksTable.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(mainFrame, "Please select a task to edit!");
			return;
		}

		/*________________________ PREVIOUS DATA ________________________*/
		int taskId = (int) TaskDataManager.model.getValueAt(selectedRow, 0);
		String taskName = (String) TaskDataManager.model.getValueAt(selectedRow, 1);
		String assignedTo = (String) TaskDataManager.model.getValueAt(selectedRow, 2);
		Date dueDate = (Date) TaskDataManager.model.getValueAt(selectedRow, 3);
		Integer cropId = (Integer) TaskDataManager.model.getValueAt(selectedRow, 4);
		String priority = (String) TaskDataManager.model.getValueAt(selectedRow, 5);
		String status = (String) TaskDataManager.model.getValueAt(selectedRow, 6);

		/*________________________ DIALOG ________________________*/
		JDialog dialog = new JDialog(mainFrame, "Add New Task", true);
		dialog.setPreferredSize(new Dimension(500, 400));
		JPanel contentPanel = new JPanel(new GridLayout(7, 2, 5, 5)); // (6 FIELDS + 1 SUBMIT) X 2
		contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

		/*________________________ COMPONENTS ________________________*/
		JTextField taskNameField = new JTextField();
		taskNameField.setDocument(new PlainDocument() { // LIMIT LENGTH
			@Override
			public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
				if (str != null && (getLength() + str.length()) <= 50) {
					super.insertString(offs, str, a);
				}
			}
		});
		taskNameField.setText(taskName);
		JTextField assignedToField = new JTextField(assignedTo);
		JDateChooser dueDateField = new JDateChooser(dueDate);
		JComboBox<Integer> cropIdCombo = new JComboBox<>(fetchCropIds());
		cropIdCombo.insertItemAt(null, 0);
		cropIdCombo.setSelectedItem(cropId); // SET PREVIOUS DATA
		JComboBox<String> priorityCombo = new JComboBox<>(new String[]{"Low", "Medium", "High"});
		priorityCombo.setSelectedItem(priority); // SET PREVIOUS DATA
		JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Pending", "In Progress", "Completed"});
		statusCombo.setSelectedItem(status); // SET PREVIOUS DATA

		/*________________________ REQUIRED_MAKER ________________________*/
		Border defaultBorder = taskNameField.getBorder();
		Border errorBorder = BorderFactory.createLineBorder(Color.RED);
		Runnable liveValidate = () -> {
			taskNameField.setBorder(taskNameField.getText().trim().isEmpty() ? errorBorder : defaultBorder);
			assignedToField.setBorder(assignedToField.getText().trim().isEmpty() ? errorBorder : defaultBorder);
			dueDateField.setBorder(dueDateField.getDate() == null ? errorBorder : defaultBorder);
		};
		taskNameField.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) { liveValidate.run(); }
			public void removeUpdate(DocumentEvent e) { liveValidate.run(); }
			public void changedUpdate(DocumentEvent e) { liveValidate.run(); }
		});
		assignedToField.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) { liveValidate.run(); }
			public void removeUpdate(DocumentEvent e) { liveValidate.run(); }
			public void changedUpdate(DocumentEvent e) { liveValidate.run(); }
		});
		dueDateField.getDateEditor().addPropertyChangeListener("date", e -> liveValidate.run());

		/*________________________ INSERTION TO CONTENT PANEL ________________________*/
		String[][] labels = {
				{"Task Name: ", "Enter the name of the task (max 50 characters"}, // Text labal --- Tooltip
				{"Assigned To: ", "Enter the name of the assignee"},
				{"Due Date: ", "Pick a due date for the task"},
				{"Crop ID (Optional): ", "Optionally associate a crop ID"},
				{"Priority: ", "Select the task priority"},
				{"Status: ", "Set the current status of the task"}
		};

		JComponent[] fields = {
				taskNameField, assignedToField, dueDateField,
				cropIdCombo, priorityCombo, statusCombo
		};

		for (int i = 0; i < labels.length; i++) {
			contentPanel.add(new JLabel(labels[i][0])); // FIXED TO THE 1ST COLUMN
			fields[i].setToolTipText(labels[i][1]); // FIXED TO THE 2ND COLUMN
			contentPanel.add(fields[i]);
		}

		/*________________________ SUBMIT ________________________*/
		JButton submitButton = new JButton("UPDATE TASK");
		submitButton.setFocusPainted(false);

		submitButton.addActionListener(e -> {
			boolean hasError = false;
			if (taskNameField.getText().trim().isEmpty()) {
				taskNameField.setBorder(errorBorder);
				hasError = true;
			}
			if (assignedToField.getText().trim().isEmpty()) {
				assignedToField.setBorder(errorBorder);
				hasError = true;
			}
			if (dueDateField.getDate() == null) {
				dueDateField.setBorder(errorBorder);
				hasError = true;
			}
			if (hasError) {
				JOptionPane.showMessageDialog(dialog, "Please fill in all required fields.", "Input Error", JOptionPane.WARNING_MESSAGE);
				return;
			}

			/*___________________ FUNCTION ___________________*/
			/*___________________ FUNCTION ___________________*/
			/*___________________ FUNCTION ___________________*/
			try {
				Date newDueDate = new java.sql.Date(dueDateField.getDate().getTime());
				TaskDataManager.updateTask(
						taskId,
						taskNameField.getText().trim(),
						assignedToField.getText().trim(),
						newDueDate,
						(Integer) cropIdCombo.getSelectedItem(),
						(String) priorityCombo.getSelectedItem(),
						(String) statusCombo.getSelectedItem()
						);
				dialog.dispose();
			} catch (IllegalArgumentException ex) {
				JOptionPane.showMessageDialog(dialog, "Invalid input: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		});

		contentPanel.add(new JPanel());
		contentPanel.add(submitButton);
		dialog.setContentPane(contentPanel);
		dialog.pack();
		dialog.setLocationRelativeTo(mainFrame);
		dialog.setVisible(true);
	}

	/*========================================== DELETE DIALOG ==========================================*/
	/*========================================== DELETE DIALOG ==========================================*/
	/*========================================== DELETE DIALOG ==========================================*/
	public void deleteSelectedTask() {
		/*________________________ SELECTED ROW ________________________*/
		int selectedRow = TaskDataManager.tasksTable.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(mainFrame, "Please select a task to delete!", "No Selection", JOptionPane.WARNING_MESSAGE);
			return;
		}

		/*________________________ PREVIOUS DATA ________________________*/
		int taskId = (int) TaskDataManager.model.getValueAt(selectedRow, 0);
		String taskName = (String) TaskDataManager.model.getValueAt(selectedRow, 1);

		/*________________________ CONFIRMATION ________________________*/
		int confirm = JOptionPane.showConfirmDialog(
				mainFrame,
				"Are you sure you want to delete the task:\n\"" + taskName + "\"?",
				"Confirm Delete",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.WARNING_MESSAGE
				);

		/*________________________ PERMANENT DELETION ________________________*/
		if (confirm == JOptionPane.YES_OPTION) {
			int finalConfirm = JOptionPane.showConfirmDialog(
					mainFrame,
					"This action is irreversible. Do you really want to permanently delete this task?",
					"Confirm Permanent Deletion",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE
					);

			/*___________________ FUNCTION ___________________*/
			/*___________________ FUNCTION ___________________*/
			/*___________________ FUNCTION ___________________*/
			if (finalConfirm == JOptionPane.YES_OPTION) {
				TaskDataManager.deleteTask(taskId);
				JOptionPane.showMessageDialog(mainFrame, "Task \"" + taskName + "\" has been deleted.");
			}
		}
	}

	/*========================================== COMPLETE ==========================================*/
	/*========================================== COMPLETE ==========================================*/
	/*========================================== COMPLETE ==========================================*/
	public void markTaskInProgress() {
		/*________________________ SELECTED ROW ________________________*/
		int selectedRow = TaskDataManager.tasksTable.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(mainFrame, "Please select a task to mark as in progress!", "No Selection", JOptionPane.WARNING_MESSAGE);
			return;
		}

		/*________________________ PREVIOUS DATA ________________________*/
		int taskId = (int) TaskDataManager.model.getValueAt(selectedRow, 0);
		String taskName = (String) TaskDataManager.model.getValueAt(selectedRow, 1);
		String currentStatus = (String) TaskDataManager.model.getValueAt(selectedRow, 6);

		/*________________________ ALDREADY COMPLETED ________________________*/
		if ("In Progress".equalsIgnoreCase(currentStatus)) {
			JOptionPane.showMessageDialog(mainFrame, "The selected task is already marked as in progress.", "Already In Progress", JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		/*________________________ CONFIRMATION ________________________*/
		int confirm = JOptionPane.showConfirmDialog(
				mainFrame,
				"Are you sure you want to mark the task:\n\"" + taskName + "\" as in progress?",
				"Confirm In Progress Status",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE
				);

		/*________________________ MARK AS COMPLETED ________________________*/
		if (confirm == JOptionPane.YES_OPTION) {
			/*___________________ FUNCTION ___________________*/
			/*___________________ FUNCTION ___________________*/
			/*___________________ FUNCTION ___________________*/
			TaskDataManager.markTaskInProgress(taskId);
			JOptionPane.showMessageDialog(mainFrame, "Task \"" + taskName + "\" has been marked as in progress.");
		}
	}
	
	/*========================================== COMPLETE ==========================================*/
	/*========================================== COMPLETE ==========================================*/
	/*========================================== COMPLETE ==========================================*/
	public void markTaskComplete() {
		/*________________________ SELECTED ROW ________________________*/
		int selectedRow = TaskDataManager.tasksTable.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(mainFrame, "Please select a task to mark as complete!", "No Selection", JOptionPane.WARNING_MESSAGE);
			return;
		}

		/*________________________ PREVIOUS DATA ________________________*/
		int taskId = (int) TaskDataManager.model.getValueAt(selectedRow, 0);
		String taskName = (String) TaskDataManager.model.getValueAt(selectedRow, 1);
		String currentStatus = (String) TaskDataManager.model.getValueAt(selectedRow, 6);

		/*________________________ ALDREADY COMPLETED ________________________*/
		if ("Completed".equalsIgnoreCase(currentStatus)) {
			JOptionPane.showMessageDialog(mainFrame, "The selected task is already marked as completed.", "Already Completed", JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		/*________________________ CONFIRMATION ________________________*/
		int confirm = JOptionPane.showConfirmDialog(
				mainFrame,
				"Are you sure you want to mark the task:\n\"" + taskName + "\" as completed?",
				"Confirm Completion",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE
				);

		/*________________________ MARK AS COMPLETED ________________________*/
		if (confirm == JOptionPane.YES_OPTION) {
			/*___________________ FUNCTION ___________________*/
			/*___________________ FUNCTION ___________________*/
			/*___________________ FUNCTION ___________________*/
			TaskDataManager.markTaskComplete(taskId);
			JOptionPane.showMessageDialog(mainFrame, "Task \"" + taskName + "\" has been marked as completed.");
		}
	}

	/*========================================== CROP ID (OPTIONAL) ==========================================*/
	/*========================================== CROP ID (OPTIONAL) ==========================================*/
	/*========================================== CROP ID (OPTIONAL) ==========================================*/
	private Integer[] fetchCropIds() {
		/*________________________ CROP ID ________________________*/
		List<Integer> cropIds = new ArrayList<>(); // Store crop IDs
		String query = "SELECT crop_id FROM crops ORDER BY crop_id";

		/*________________________ DATABASE ________________________*/
		try (Connection conn = DBConnection.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query)) {

			while (rs.next()) {
				cropIds.add(rs.getInt("crop_id")); // Add crop ID to the list
			}

		} catch (SQLException e) {
			JOptionPane.showMessageDialog(mainFrame, "Error loading crop IDs: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}

		/*________________________ NO CROP ID ________________________*/
		if (cropIds.isEmpty()) {
			JOptionPane.showMessageDialog(mainFrame, "No crop IDs found. Add crops first before linking tasks.", "No Crops Found", JOptionPane.INFORMATION_MESSAGE);
		}

		return cropIds.toArray(new Integer[0]); // Convert List to Integer array
	}
}