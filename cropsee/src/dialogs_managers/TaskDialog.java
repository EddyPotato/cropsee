package dialogs_managers;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import com.toedter.calendar.JDateChooser;

import app.DBConnection;
import ui_managers.Task_Manager;

import java.awt.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TaskDialog {
	private JFrame mainFrame;

	public TaskDialog(JFrame mainFrame) {
		this.mainFrame = mainFrame;
	}

	/*------------------------ ADD TASK DIALOG ------------------------*/
	// Class-level fields to remember last used priority and status
	private String lastUsedPriority = "Medium";
	private String lastUsedStatus = "Pending";

	@SuppressWarnings({ "serial", "unused" })
	public void showAddTaskDialog() {
		JDialog dialog = new JDialog(mainFrame, "Add New Task", true);
		dialog.setPreferredSize(new Dimension(500, 400));
		JPanel contentPanel = new JPanel(new GridLayout(7, 2, 5, 5)); // (6 FIELDS + 1 SUBMIT) X 2
		contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

		// TEXT FIELDS
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

		// SETTING DEFAULT VALUES
		priorityCombo.setSelectedItem(lastUsedPriority);
		statusCombo.setSelectedItem(lastUsedStatus);

		// HELPER METHOD (REQUIRED MAKER)
		Border defaultBorder = taskNameField.getBorder();
		Border errorBorder = BorderFactory.createLineBorder(Color.RED);

		Runnable liveValidate = () -> {
			taskNameField.setBorder(taskNameField.getText().trim().isEmpty() ? errorBorder : defaultBorder);
			assignedToField.setBorder(assignedToField.getText().trim().isEmpty() ? errorBorder : defaultBorder);
			dueDateField.setBorder(dueDateField.getDate() == null ? errorBorder : defaultBorder);
		};

		// REQUIRED-MAKER LISTENERS
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

		/*____________ ADD ____________*/
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

		/*____________ SUBMIT ____________*/
		JButton submitButton = new JButton("ADD TASKS");
		submitButton.setFocusPainted(false);

		submitButton.addActionListener(e -> {
			try {
				/*____________ VALIDATION ____________*/
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

				/*____________ SAVE LAST PRIORITY ____________*/
				lastUsedPriority = (String) priorityCombo.getSelectedItem();
				lastUsedStatus = (String) statusCombo.getSelectedItem();

				Task_Manager.addTask(
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

	/*------------------------ UPDATE TASK DIALOG ------------------------*/
	@SuppressWarnings({ "serial", "unused" })
	public void showEditTaskDialog() {
		int selectedRow = Task_Manager.tasksTable.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(mainFrame, "Please select a task to edit!");
			return;
		}

		/*________________________ PREVIOUS DATA ________________________*/
		int taskId = (int) Task_Manager.model.getValueAt(selectedRow, 0);
		String taskName = (String) Task_Manager.model.getValueAt(selectedRow, 1);
		String assignedTo = (String) Task_Manager.model.getValueAt(selectedRow, 2);
		Date dueDate = (Date) Task_Manager.model.getValueAt(selectedRow, 3);
		Integer cropId = (Integer) Task_Manager.model.getValueAt(selectedRow, 4);
		String priority = (String) Task_Manager.model.getValueAt(selectedRow, 5);
		String status = (String) Task_Manager.model.getValueAt(selectedRow, 6);

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

		/*________________________ ADD ________________________*/
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

			try {
				Date newDueDate = new java.sql.Date(dueDateField.getDate().getTime());
				Task_Manager.updateTask(
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

	/*--------------------- DELETE TASK DIALOG ---------------------*/
	public void deleteSelectedTask() {
		int selectedRow = Task_Manager.tasksTable.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(mainFrame, "Please select a task to delete!", "No Selection", JOptionPane.WARNING_MESSAGE);
			return;
		}

		int taskId = (int) Task_Manager.model.getValueAt(selectedRow, 0);
		String taskName = (String) Task_Manager.model.getValueAt(selectedRow, 1);

		int confirm = JOptionPane.showConfirmDialog(
				mainFrame,
				"Are you sure you want to delete the task:\n\"" + taskName + "\"?",
				"Confirm Delete",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.WARNING_MESSAGE
				);

		if (confirm == JOptionPane.YES_OPTION) {
			int finalConfirm = JOptionPane.showConfirmDialog(
					mainFrame,
					"This action is irreversible. Do you really want to permanently delete this task?",
					"Confirm Permanent Deletion",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE
					);

			if (finalConfirm == JOptionPane.YES_OPTION) {
				Task_Manager.deleteTask(taskId);
				JOptionPane.showMessageDialog(mainFrame, "Task \"" + taskName + "\" has been deleted.");
			}
		}
	}


	/*--------------------- COMPLETE TASK DIALOG ---------------------*/
	public void markTaskComplete() {
		int selectedRow = Task_Manager.tasksTable.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(mainFrame, "Please select a task to mark as complete!", "No Selection", JOptionPane.WARNING_MESSAGE);
			return;
		}

		int taskId = (int) Task_Manager.model.getValueAt(selectedRow, 0);
		String taskName = (String) Task_Manager.model.getValueAt(selectedRow, 1);
		String currentStatus = (String) Task_Manager.model.getValueAt(selectedRow, 6);

		if ("Completed".equalsIgnoreCase(currentStatus)) {
			JOptionPane.showMessageDialog(mainFrame, "The selected task is already marked as completed.", "Already Completed", JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		int confirm = JOptionPane.showConfirmDialog(
				mainFrame,
				"Are you sure you want to mark the task:\n\"" + taskName + "\" as completed?",
				"Confirm Completion",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE
				);

		if (confirm == JOptionPane.YES_OPTION) {
			Task_Manager.markTaskComplete(taskId);
			JOptionPane.showMessageDialog(mainFrame, "Task \"" + taskName + "\" has been marked as completed.");
		}
	}

	/*--------------------- FETCH CROP_ID ---------------------*/
	private Integer[] fetchCropIds() {
		List<Integer> cropIds = new ArrayList<>();
		String query = "SELECT crop_id FROM crops ORDER BY crop_id";

		try (Connection conn = DBConnection.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query)) {

			while (rs.next()) {
				cropIds.add(rs.getInt("crop_id"));
			}

		} catch (SQLException e) {
			JOptionPane.showMessageDialog(mainFrame, "Error loading crop IDs: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();  // Optional: for debugging logs
		}

		if (cropIds.isEmpty()) {
			JOptionPane.showMessageDialog(mainFrame, "No crop IDs found. Add crops first before linking tasks.", "No Crops Found", JOptionPane.INFORMATION_MESSAGE);
		}

		return cropIds.toArray(new Integer[0]);
	}
}