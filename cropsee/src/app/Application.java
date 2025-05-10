package app;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.JComboBox;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;

import java.util.List;
import java.util.ArrayList;

import ui.*; // import all CONTENT OF TABS

public class Application {
	/*_____________________ CLASS-LEVEL _____________________*/
	private JFrame mainFrame;
	private CardLayout cardLayout = new CardLayout();
	@SuppressWarnings("unused")
	private Connection connection;

	/*_____________________ REUSABLE METHODS _____________________*/

	private Component createBorderGap() {
		return Box.createRigidArea(new Dimension(0, 5));
	}

	/*===================== CRUD METHODS =====================*/
	/*_____________________ CRUD - CROP MANAGEMENT _____________________*/
	private void showAddCropDialog() {
		JDialog dialog = new JDialog(mainFrame, "Add New Crop", true);
		dialog.setLayout(new GridLayout(5, 2, 5, 5));

		JTextField nameField = new JTextField();
		JTextField plantingDateField = new JTextField();
		JTextField harvestDateField = new JTextField();
		JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Planning", "Growing", "Harvested"});

		dialog.add(new JLabel("Crop Name:"));
		dialog.add(nameField);
		dialog.add(new JLabel("Planting Date (YYYY-MM-DD):"));
		dialog.add(plantingDateField);
		dialog.add(new JLabel("Harvest Date (YYYY-MM-DD):"));
		dialog.add(harvestDateField);
		dialog.add(new JLabel("Status:"));
		dialog.add(statusCombo);

		JButton submitButton = new JButton("Add Crop");
		submitButton.addActionListener(e -> {
			try {
				Date plantingDate = Date.valueOf(plantingDateField.getText());
				Date harvestDate = Date.valueOf(harvestDateField.getText());
				CropTableManager.addCrop(
						nameField.getText(),
						plantingDate,
						harvestDate,
						(String) statusCombo.getSelectedItem()
						);
				dialog.dispose();
			} catch (IllegalArgumentException ex) {
				JOptionPane.showMessageDialog(dialog, "Invalid date format! Use YYYY-MM-DD");
			}
		});

		dialog.add(submitButton);
		dialog.pack();
		dialog.setLocationRelativeTo(mainFrame);
		dialog.setVisible(true);
	}

	private void showEditCropDialog() {
		int selectedRow = CropTableManager.cropTable.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(mainFrame, "Please select a crop to edit!");
			return;
		}

		int cropId = (int) CropTableManager.model.getValueAt(selectedRow, 0);
		String name = (String) CropTableManager.model.getValueAt(selectedRow, 1);
		Date plantingDate = (Date) CropTableManager.model.getValueAt(selectedRow, 2);
		Date harvestDate = (Date) CropTableManager.model.getValueAt(selectedRow, 3);
		String status = (String) CropTableManager.model.getValueAt(selectedRow, 4);

		JDialog dialog = new JDialog(mainFrame, "Edit Crop", true);
		dialog.setLayout(new GridLayout(5, 2, 5, 5));

		JTextField nameField = new JTextField(name);
		JTextField plantingDateField = new JTextField(plantingDate.toString());
		JTextField harvestDateField = new JTextField(harvestDate.toString());
		JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Planning", "Growing", "Harvested"});
		statusCombo.setSelectedItem(status);

		// Add components to dialog
		dialog.add(new JLabel("Crop Name:"));
		dialog.add(nameField);
		dialog.add(new JLabel("Planting Date (YYYY-MM-DD):"));
		dialog.add(plantingDateField);
		dialog.add(new JLabel("Harvest Date (YYYY-MM-DD):"));
		dialog.add(harvestDateField);
		dialog.add(new JLabel("Status:"));
		dialog.add(statusCombo);

		JButton submitButton = new JButton("Update Crop");
		submitButton.addActionListener(e -> {
			try {
				Date newPlantingDate = Date.valueOf(plantingDateField.getText());
				Date newHarvestDate = Date.valueOf(harvestDateField.getText());
				CropTableManager.updateCrop(
						cropId,
						nameField.getText(),
						newPlantingDate,
						newHarvestDate,
						(String) statusCombo.getSelectedItem()
						);
				dialog.dispose();
			} catch (IllegalArgumentException ex) {
				JOptionPane.showMessageDialog(dialog, "Invalid date format! Use YYYY-MM-DD");
			}
		});

		dialog.add(submitButton);
		dialog.pack();
		dialog.setLocationRelativeTo(mainFrame);
		dialog.setVisible(true);
	}

	private void deleteSelectedCrop() {
		int selectedRow = CropTableManager.cropTable.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(mainFrame, "Please select a crop to delete!");
			return;
		}

		int confirm = JOptionPane.showConfirmDialog(mainFrame, 
				"Are you sure you want to delete this crop?", "Confirm Delete", JOptionPane.YES_NO_OPTION);

		if (confirm == JOptionPane.YES_OPTION) {
			int cropId = (int) CropTableManager.model.getValueAt(selectedRow, 0);
			CropTableManager.deleteCrop(cropId);
		}
	}

	/*_____________________ CRUD - TASKS _____________________*/
	private void showAddTaskDialog() {
		JDialog dialog = new JDialog(mainFrame, "Add New Task", true);
		dialog.setLayout(new GridLayout(7, 2, 5, 5));

		JTextField taskNameField = new JTextField();
		JTextField assignedToField = new JTextField();
		JTextField dueDateField = new JTextField();
		JComboBox<Integer> cropIdCombo = new JComboBox<>(fetchCropIds());
		cropIdCombo.insertItemAt(null, 0);
		JComboBox<String> priorityCombo = new JComboBox<>(new String[]{"Low", "Medium", "High"});
		JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Pending", "In Progress", "Completed"});

		dialog.add(new JLabel("Task Name:"));
		dialog.add(taskNameField);
		dialog.add(new JLabel("Assigned To:"));
		dialog.add(assignedToField);
		dialog.add(new JLabel("Due Date (YYYY-MM-DD):"));
		dialog.add(dueDateField);
		dialog.add(new JLabel("Crop ID (Optional):"));
		dialog.add(cropIdCombo);
		dialog.add(new JLabel("Priority:"));
		dialog.add(priorityCombo);
		dialog.add(new JLabel("Status:"));
		dialog.add(statusCombo);

		JButton submitButton = new JButton("Add Task");
		submitButton.addActionListener(e -> {
			try {
				Date dueDate = Date.valueOf(dueDateField.getText());
				Integer cropId = (Integer) cropIdCombo.getSelectedItem();
				TasksTableManager.addTask(
						taskNameField.getText(),
						assignedToField.getText(),
						dueDate,
						cropId,
						(String) priorityCombo.getSelectedItem(),
						(String) statusCombo.getSelectedItem()
						);
				dialog.dispose();
			} catch (IllegalArgumentException ex) {
				JOptionPane.showMessageDialog(dialog, "Invalid date format! Use YYYY-MM-DD");
			}
		});

		dialog.add(submitButton);
		dialog.pack();
		dialog.setLocationRelativeTo(mainFrame);
		dialog.setVisible(true);
	}

	private void showEditTaskDialog() {
		int selectedRow = TasksTableManager.tasksTable.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(mainFrame, "Please select a task to edit!");
			return;
		}

		int taskId = (int) TasksTableManager.model.getValueAt(selectedRow, 0);
		String taskName = (String) TasksTableManager.model.getValueAt(selectedRow, 1);
		String assignedTo = (String) TasksTableManager.model.getValueAt(selectedRow, 2);
		Date dueDate = (Date) TasksTableManager.model.getValueAt(selectedRow, 3);
		Integer cropId = (Integer) TasksTableManager.model.getValueAt(selectedRow, 4);
		String priority = (String) TasksTableManager.model.getValueAt(selectedRow, 5);
		String status = (String) TasksTableManager.model.getValueAt(selectedRow, 6);

		JDialog dialog = new JDialog(mainFrame, "Edit Task", true);
		dialog.setLayout(new GridLayout(7, 2, 5, 5));

		JTextField taskNameField = new JTextField(taskName);
		JTextField assignedToField = new JTextField(assignedTo);
		JTextField dueDateField = new JTextField(dueDate.toString());
		JComboBox<Integer> cropIdCombo = new JComboBox<>(fetchCropIds());
		cropIdCombo.setSelectedItem(cropId);
		JComboBox<String> priorityCombo = new JComboBox<>(new String[]{"Low", "Medium", "High"});
		priorityCombo.setSelectedItem(priority);
		JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Pending", "In Progress", "Completed"});
		statusCombo.setSelectedItem(status);

		dialog.add(new JLabel("Task Name:"));
		dialog.add(taskNameField);
		dialog.add(new JLabel("Assigned To:"));
		dialog.add(assignedToField);
		dialog.add(new JLabel("Due Date (YYYY-MM-DD):"));
		dialog.add(dueDateField);
		dialog.add(new JLabel("Crop ID (Optional):"));
		dialog.add(cropIdCombo);
		dialog.add(new JLabel("Priority:"));
		dialog.add(priorityCombo);
		dialog.add(new JLabel("Status:"));
		dialog.add(statusCombo);

		JButton submitButton = new JButton("Update Task");
		submitButton.addActionListener(e -> {
			try {
				Date newDueDate = Date.valueOf(dueDateField.getText());
				TasksTableManager.updateTask(
						taskId,
						taskNameField.getText(),
						assignedToField.getText(),
						newDueDate,
						(Integer) cropIdCombo.getSelectedItem(),
						(String) priorityCombo.getSelectedItem(),
						(String) statusCombo.getSelectedItem()
						);
				dialog.dispose();
			} catch (IllegalArgumentException ex) {
				JOptionPane.showMessageDialog(dialog, "Invalid date format! Use YYYY-MM-DD");
			}
		});

		dialog.add(submitButton);
		dialog.pack();
		dialog.setLocationRelativeTo(mainFrame);
		dialog.setVisible(true);
	}

	private void deleteSelectedTask() {
		int selectedRow = TasksTableManager.tasksTable.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(mainFrame, "Please select a task to delete!");
			return;
		}

		int confirm = JOptionPane.showConfirmDialog(mainFrame, 
				"Are you sure you want to delete this task?", "Confirm Delete", JOptionPane.YES_NO_OPTION);

		if (confirm == JOptionPane.YES_OPTION) {
			int taskId = (int) TasksTableManager.model.getValueAt(selectedRow, 0);
			TasksTableManager.deleteTask(taskId);
		}
	}

	private void markTaskComplete() {
		int selectedRow = TasksTableManager.tasksTable.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(mainFrame, "Please select a task to mark complete!");
			return;
		}

		int taskId = (int) TasksTableManager.model.getValueAt(selectedRow, 0);
		TasksTableManager.markTaskComplete(taskId);
	}

	private Integer[] fetchCropIds() {
		List<Integer> cropIds = new ArrayList<>();
		String query = "SELECT crop_id FROM crops";

		try (Connection conn = DBConnection.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query)) {

			while (rs.next()) {
				cropIds.add(rs.getInt("crop_id"));
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error loading crops: " + e.getMessage());
		}
		return cropIds.toArray(new Integer[0]);
	}

	/*_____________________ CRUD - INVENTORY MANAGEMENT _____________________*/
	private void showAddInventoryDialog() {
		JDialog dialog = new JDialog(mainFrame, "Add Inventory Item", true);
		JPanel contentPanel = new JPanel(new GridLayout(4, 2, 5, 5));
		contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20)); 

		// Input fields
		JTextField nameField = new JTextField();
		JTextField quantityField = new JTextField();  // Renamed for clarity
		JTextField priceField = new JTextField();     // Renamed for clarity

		// Add components
		contentPanel.add(new JLabel("ITEM NAME:"));
		contentPanel.add(nameField);
		contentPanel.add(new JLabel("QUANTITY:"));
		contentPanel.add(quantityField);
		contentPanel.add(new JLabel("PRICE:"));
		contentPanel.add(priceField);

		JButton submitButton = new JButton("Add Item");
		submitButton.setFocusPainted(false);
		contentPanel.add(new JLabel()); // empty space for alignment
		contentPanel.add(submitButton);

		// Submit Action
		submitButton.addActionListener(e -> {
			try {
				int quantity = Integer.parseInt(quantityField.getText());
				double price = Double.parseDouble(priceField.getText());
				InventoryTableManager.addItem(
					nameField.getText(),
					quantity,
					price
				);
				dialog.dispose();
			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(dialog, "Invalid number format!");
			}
		});

		dialog.setContentPane(contentPanel);
		dialog.pack();
		dialog.setLocationRelativeTo(mainFrame);
		dialog.setVisible(true);
	}

	private void showEditInventoryDialog() {
		int selectedRow = InventoryTableManager.inventoryTable.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(mainFrame, "Please select an item to edit!");
			return;
		}

		int itemId = (int) InventoryTableManager.model.getValueAt(selectedRow, 0);
		String name = (String) InventoryTableManager.model.getValueAt(selectedRow, 1);
		int quantity = (int) InventoryTableManager.model.getValueAt(selectedRow, 2);
		double price = (double) InventoryTableManager.model.getValueAt(selectedRow, 3);

		JDialog dialog = new JDialog(mainFrame, "Edit Inventory Item", true);
		dialog.setLayout(new GridLayout(4, 2, 5, 5));

		JTextField nameField = new JTextField(name);
		JTextField quantityField = new JTextField(String.valueOf(quantity));
		JTextField priceField = new JTextField(String.valueOf(price));

		dialog.add(new JLabel("Item Name:"));
		dialog.add(nameField);
		dialog.add(new JLabel("Quantity:"));
		dialog.add(quantityField);
		dialog.add(new JLabel("Price:"));
		dialog.add(priceField);

		JButton submitButton = new JButton("Update Item");
		submitButton.addActionListener(e -> {
			try {
				int newQuantity = Integer.parseInt(quantityField.getText());
				double newPrice = Double.parseDouble(priceField.getText());
				InventoryTableManager.updateItem(
						itemId,
						nameField.getText(),
						newQuantity,
						newPrice
						);
				dialog.dispose();
			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(dialog, "Invalid number format!");
			}
		});

		dialog.add(submitButton);
		dialog.pack();
		dialog.setLocationRelativeTo(mainFrame);
		dialog.setVisible(true);
	}

	private void deleteInventoryItem() {
		int selectedRow = InventoryTableManager.inventoryTable.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(mainFrame, "Please select an item to delete!");
			return;
		}

		int confirm = JOptionPane.showConfirmDialog(mainFrame, 
				"Are you sure you want to delete this item?", "Confirm Delete", JOptionPane.YES_NO_OPTION);

		if (confirm == JOptionPane.YES_OPTION) {
			int itemId = (int) InventoryTableManager.model.getValueAt(selectedRow, 0);
			InventoryTableManager.deleteItem(itemId);
		}
	}

	/*_____________________ METHODS _____________________*/
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				// CLASS					METHOD
				Application window = new Application(); 
				window.mainFrame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public Application() {
		initialize();
	}

	private void connectToDatabase() {
		try {
			// Adjust these credentials as needed
			String url = "jdbc:mysql://localhost:3306/cropsee_db";
			String username = "root";
			String password = ""; // Leave blank if no password

			connection = DriverManager.getConnection(url, username, password);
			System.out.println("Database connected successfully!");
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Failed to connect to the database:\n" + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	private void initialize() {
		/*_____________________ DATABASE _____________________*/
		connectToDatabase(); // Establish database connection first

		/*_____________________ FRAME _____________________*/
		mainFrame = new JFrame();
		mainFrame.setResizable(true);
		mainFrame.setAlwaysOnTop(false);
		mainFrame.setTitle("Crop See");
		mainFrame.setSize(1200, 700);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setLocationRelativeTo(null);

		/*_____________________ STAGE _____________________*/
		mainFrame.getContentPane().setLayout(new BorderLayout());
		mainFrame.getContentPane().setBackground(Color.white);

		/*===================== MASTER PANELS =====================*/
		/*_____________________ LAYOUT _____________________*/
		JPanel topPanel = new JPanel(new BorderLayout());
		JPanel sidePanel = new JPanel(new GridLayout(5, 1, 0, 5)); // row, column, gaps-between-rows, gaps-between-columns
		JPanel mainPanel = new JPanel(cardLayout);

		/*_____________________ COLOR _____________________*/
		topPanel.setBackground(Color.magenta);
		sidePanel.setBackground(Color.orange);
		mainPanel.setBackground(Color.yellow);

		/*_____________________ PREFERRED SIZE _____________________*/
		int logo_size = 50;
		int title_size = 30;
		topPanel.setPreferredSize(new Dimension(0, 70)); // width - height
		sidePanel.setPreferredSize(new Dimension(200, 0));
		mainPanel.setPreferredSize(null);

		/*_____________________ MAX SIZE _____________________*/
		topPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		sidePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		mainPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

		/*_____________________ MARGIN _____________________*/
		topPanel.setBorder(null);
		sidePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // top, left, bottom, right
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		/*===================== TOP PANEL =====================*/
		/*_____________________ LOGO _____________________*/
		ImageIcon originalImage = new ImageIcon(getClass().getResource("/resources/Logo.jpg"));
		Image scaledImage = originalImage.getImage().getScaledInstance(logo_size, logo_size, Image.SCALE_SMOOTH);
		ImageIcon resizedImage = new ImageIcon(scaledImage);

		JLabel logo = new JLabel(resizedImage);
		logo.setPreferredSize(new Dimension(logo_size, logo_size));
		logo.setCursor(new Cursor(Cursor.HAND_CURSOR));

		/*_____________________ TITLE _____________________*/
		JLabel title = new JLabel("<html><center>Crop See</center></html>");
		title.setFont(new Font("Tahoma", Font.BOLD, title_size));
		title.setBounds(55, 10, 90, 24);
		title.setForeground(new Color(30, 138, 56)); // DARK GREEN
		title.setCursor(new Cursor(Cursor.HAND_CURSOR));

		/*_____________________ CONTAINER (CENTERS USING GRIDBAG) _____________________*/
		JPanel topContainer = new JPanel(new GridBagLayout());
		topContainer.setOpaque(false);
		topContainer.add(Box.createRigidArea(new Dimension(20, 0)));
		topContainer.add(logo);
		topContainer.add(Box.createRigidArea(new Dimension(20, 0)));
		topContainer.add(title);

		/*_____________________ ADDING CONTAINER TO TOP PANEL (LEFT-ALIGN USING BORDERLAYOUT) _____________________*/
		topPanel.add(topContainer, BorderLayout.WEST);

		/*_____________________ TOP PANEL - EVENT LISTERNERS _____________________*/
		logo.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				System.out.println("Clicked!"); // Debug
				JOptionPane.showMessageDialog(null, "Logo clicked!");
			}
		});

		title.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				System.out.println("Clicked!"); // Debug
				JOptionPane.showMessageDialog(null, "Title clicked!");
			}
		});

		/*===================== SIDE PANEL =====================*/
		/*_____________________ BUTTON TEXT _____________________*/
		JButton dashboardBtn = new JButton("<html>Dashboard</html>");
		JButton manageBtn = new JButton("<html><center>Crop<br>Management</center></html>");
		JButton tasksBtn = new JButton("<html>Tasks</html>");
		JButton monitorBtn = new JButton("<html><center>Inventory Management</center></html>");
		JButton reportsBtn = new JButton("<html>Reports</html>");

		/*_____________________ BUTTON CUSTOMIZATION _____________________*/
		dashboardBtn.setFont(new Font("Tahoma", Font.BOLD, 18));
		dashboardBtn.setFocusPainted(false);
		dashboardBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

		manageBtn.setFont(new Font("Tahoma", Font.BOLD, 18));
		manageBtn.setFocusPainted(false);
		manageBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

		tasksBtn.setFont(new Font("Tahoma", Font.BOLD, 18));
		tasksBtn.setFocusPainted(false);
		tasksBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

		monitorBtn.setFont(new Font("Tahoma", Font.BOLD, 18));
		monitorBtn.setFocusPainted(false);
		monitorBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

		reportsBtn.setFont(new Font("Tahoma", Font.BOLD, 18));
		reportsBtn.setFocusPainted(false);
		reportsBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

		sidePanel.add(dashboardBtn);
		sidePanel.add(manageBtn);
		sidePanel.add(tasksBtn);
		sidePanel.add(monitorBtn);
		sidePanel.add(reportsBtn);

		/*===================== CENTRAL PANEL =====================*/
		// None (Check "MASTER PANEL - INFO")

		/*_____________________ ADD TO MAINFRAME _____________________*/
		mainFrame.getContentPane().add(topPanel, BorderLayout.NORTH);
		mainFrame.getContentPane().add(sidePanel, BorderLayout.WEST);
		mainFrame.getContentPane().add(mainPanel, BorderLayout.CENTER);

		/*===================== PANELS=====================*/
		JPanel dashboardPanel = new JPanel();
		JPanel cropManagementPanel = new JPanel();
		JPanel tasksPanel = new JPanel();
		JPanel InventoryPanel = new JPanel();
		JPanel reportPanel = new JPanel();

		/*_____________________ LAYOUT _____________________*/
		dashboardPanel.setLayout(new BoxLayout(dashboardPanel, BoxLayout.Y_AXIS));
		cropManagementPanel.setLayout(new BoxLayout(cropManagementPanel, BoxLayout.Y_AXIS));
		tasksPanel.setLayout(new BoxLayout(tasksPanel, BoxLayout.Y_AXIS));
		InventoryPanel.setLayout(new BoxLayout(InventoryPanel, BoxLayout.Y_AXIS));
		reportPanel.setLayout(new BoxLayout(reportPanel, BoxLayout.Y_AXIS));

		/*_____________________ COLOR _____________________*/
		dashboardPanel.setBackground(Color.white);
		cropManagementPanel.setBackground(Color.white);
		tasksPanel.setBackground(Color.white);
		InventoryPanel.setBackground(Color.white);
		reportPanel.setBackground(Color.white);

		/*_____________________ MARGIN _____________________*/
		dashboardPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); // top, left, bottom, right
		cropManagementPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); 
		tasksPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		InventoryPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		reportPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

		/*===================== CROP MANAGEMENT =====================*/
		JPanel tableListofCrops = new JPanel(new BorderLayout()); // Use BorderLayout
		tableListofCrops.setPreferredSize(new Dimension(Integer.MAX_VALUE, 400));
		tableListofCrops.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

		// Adding the table to the Crop Management panel
		CropTableManager.addCropManagementTable(tableListofCrops);

		JPanel cropManagementActionPanel = new JPanel();
		cropManagementActionPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 100));
		cropManagementActionPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

		// Create action buttons similar to tasks panel
		JButton addCropBtn = new JButton("Add New Crop");
		JButton editCropBtn = new JButton("Edit Crop");
		JButton deleteCropBtn = new JButton("Delete Crop");

		addCropBtn.addActionListener(e -> showAddCropDialog());
		editCropBtn.addActionListener(e -> showEditCropDialog());
		deleteCropBtn.addActionListener(e -> deleteSelectedCrop());

		// Style buttons
		for (JButton btn : new JButton[]{addCropBtn, editCropBtn, deleteCropBtn}) {
			btn.setFont(new Font("Tahoma", Font.BOLD, 14));
			btn.setFocusPainted(false);
			btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		}

		cropManagementActionPanel.add(addCropBtn);
		cropManagementActionPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		cropManagementActionPanel.add(editCropBtn);
		cropManagementActionPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		cropManagementActionPanel.add(deleteCropBtn);

		cropManagementPanel.add(tableListofCrops);
		cropManagementPanel.add(createBorderGap());
		cropManagementPanel.add(cropManagementActionPanel);

		/*===================== TASK =====================*/
		JPanel tableListofTasks = new JPanel(new BorderLayout());
		tableListofTasks.setPreferredSize(new Dimension(Integer.MAX_VALUE, 400));
		tableListofTasks.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

		// Adding the tasks table
		TasksTableManager.addTasksTable(tableListofTasks);

		JPanel tasksActionPanel = new JPanel();
		tasksActionPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 100));
		tasksActionPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

		// Action buttons
		JButton addTaskBtn = new JButton("Add New Task");
		JButton editTaskBtn = new JButton("Edit Task");
		JButton deleteTaskBtn = new JButton("Delete Task");  
		JButton completeTaskBtn = new JButton("Mark Complete");
		JButton removeCompletedBtn = new JButton("Remove Completed");

		// Buttons listeners
		addTaskBtn.addActionListener(e -> showAddTaskDialog());
		editTaskBtn.addActionListener(e -> showEditTaskDialog());
		deleteTaskBtn.addActionListener(e -> deleteSelectedTask());
		completeTaskBtn.addActionListener(e -> markTaskComplete());
		removeCompletedBtn.addActionListener(e -> {
			int confirm = JOptionPane.showConfirmDialog(mainFrame, 
					"This will permanently delete all completed tasks.\nContinue?", 
					"Confirm Cleanup", JOptionPane.YES_NO_OPTION);

			if (confirm == JOptionPane.YES_OPTION) {
				TasksTableManager.removeCompletedTasks();
			}
		});

		// Style buttons consistently
		for (JButton btn : new JButton[]{addTaskBtn, editTaskBtn, deleteTaskBtn, completeTaskBtn, removeCompletedBtn}) {
			btn.setFont(new Font("Tahoma", Font.BOLD, 14));
			btn.setFocusPainted(false);
			btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		}

		// Add buttons with spacing
		tasksActionPanel.add(addTaskBtn);
		tasksActionPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		tasksActionPanel.add(editTaskBtn);
		tasksActionPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		tasksActionPanel.add(deleteTaskBtn);
		tasksActionPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		tasksActionPanel.add(completeTaskBtn);
		tasksActionPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		tasksActionPanel.add(removeCompletedBtn);

		tasksPanel.add(tableListofTasks);
		tasksPanel.add(createBorderGap());
		tasksPanel.add(tasksActionPanel);

		/*===================== DASHBOARD =====================*/
		dashboardPanel.setLayout(new BoxLayout(dashboardPanel, BoxLayout.Y_AXIS));
		dashboardPanel.setBackground(Color.white);

		// Container for tables (side-by-side)
		JPanel tablesContainer = new JPanel();
		tablesContainer.setLayout(new GridLayout(1, 2, 8, 8)); // 1 row, 2 columns, 8px gaps
		tablesContainer.setBackground(Color.white);

		// Crop Table Panel
		JPanel cropTablePanel = new JPanel(new BorderLayout());
		TitledBorder cropBorder = BorderFactory.createTitledBorder("Crops");
		cropBorder.setTitleJustification(TitledBorder.CENTER); // Center align title
		cropTablePanel.setBorder(cropBorder);
		JTable dashboardCropTable = new JTable(CropTableManager.model);
		dashboardCropTable.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 14));
		dashboardCropTable.setRowHeight(25);
		JScrollPane cropScroll = new JScrollPane(dashboardCropTable);
		cropTablePanel.add(cropScroll);

		// Tasks Table Panel
		JPanel tasksTablePanel = new JPanel(new BorderLayout());
		TitledBorder taskBorder = BorderFactory.createTitledBorder("Tasks");
		taskBorder.setTitleJustification(TitledBorder.CENTER); // Center align title
		tasksTablePanel.setBorder(taskBorder);
		JTable dashboardTaskTable = new JTable(TasksTableManager.model);
		dashboardTaskTable.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 14));
		dashboardTaskTable.setRowHeight(25);
		JScrollPane taskScroll = new JScrollPane(dashboardTaskTable);
		tasksTablePanel.add(taskScroll);

		// Add tables to container
		tablesContainer.add(cropTablePanel);
		tablesContainer.add(tasksTablePanel);

		// Add container to dashboard
		dashboardPanel.add(tablesContainer);
		dashboardPanel.add(Box.createRigidArea(new Dimension(0, 10)));

		// Refresh Button
		JPanel refreshPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5));
		refreshPanel.setBackground(Color.white);

		JButton refreshBtn = new JButton("Refresh Tables");

		// Match exact styling from other panels
		refreshBtn.setFont(new Font("Tahoma", Font.BOLD, 14));
		refreshBtn.setFocusPainted(false);
		refreshBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));



		// Action listener
		refreshBtn.addActionListener(e -> {
			CropTableManager.refreshCropTable();
			TasksTableManager.refreshTaskTable();
		});

		refreshPanel.add(refreshBtn);

		// Add components to dashboard with minimal spacing
		dashboardPanel.add(Box.createRigidArea(new Dimension(0, 5))); // Small gap between tables and button
		dashboardPanel.add(refreshPanel);

		/*===================== INVENTORY =====================*/
		/*_____________________ PANEL #1 _____________________*/
		JPanel inventoryListTable = new JPanel(new BorderLayout());
		inventoryListTable.setPreferredSize(new Dimension(0, 400));
		InventoryTableManager.addInventoryTable(inventoryListTable);

		/*_____________________ PANEL #2 _____________________*/
		JPanel inventoryActionPanel = new JPanel();
		inventoryActionPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));

		/*_____________________ BUTTONS FOR PANEL #2 _____________________*/
		JButton addItemBtn = new JButton("Add Item");
		JButton editItemBtn = new JButton("Edit Item");
		JButton deleteItemBtn = new JButton("Delete Item");

		/*_____________________ STYLE FOR BUTTONS _____________________*/
		for (JButton btn : new JButton[]{addItemBtn, editItemBtn, deleteItemBtn}) {
			btn.setFont(new Font("Tahoma", Font.BOLD, 14));
			btn.setFocusPainted(false);
			btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}

		/*_____________________ LISTENER FOR BUTTONS _____________________*/
		addItemBtn.addActionListener(e -> showAddInventoryDialog());
		editItemBtn.addActionListener(e -> showEditInventoryDialog());
		deleteItemBtn.addActionListener(e -> deleteInventoryItem());

		/*_____________________ ADD BUTTONS TO PANEL #2 _____________________*/
		inventoryActionPanel.add(addItemBtn);
		inventoryActionPanel.add(editItemBtn);
		inventoryActionPanel.add(deleteItemBtn);

		/*_____________________ ADD PANEL #1 AND #2 TO MAIN INVENTORY PANEL _____________________*/
		InventoryPanel.add(inventoryListTable);
		InventoryPanel.add(createBorderGap());
		InventoryPanel.add(inventoryActionPanel);

		/*===================== REPORT =====================*/
		/*_____________________ PANEL #1 _____________________*/
		JPanel growthTrendPanel = new JPanel();
		growthTrendPanel.setPreferredSize(new Dimension(0, 300));
		growthTrendPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

		/*_____________________ PANEL #2 _____________________*/
		JPanel pestTrendPanel = new JPanel();
		pestTrendPanel.setPreferredSize(new Dimension(0, 300));
		pestTrendPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

		/*_____________________ ADD TABLE TO PANEL #1 _____________________*/
		// Adding the reports table to the Reports Management panel using ReportsTableManager
		ReportsTableManager.addReportsTable(reportPanel);

		/*_____________________ PANEL #3 _____________________*/
		JPanel exportActionPanel = new JPanel();
		exportActionPanel.setPreferredSize(new Dimension(0, 100));
		exportActionPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
		exportActionPanel.setLayout(new BorderLayout(0, 0));

		/*_____________________ EXPORT BUTTON _____________________*/
		JButton export = new JButton("EXPORT STATISTICS");
		export.setFont(new Font("Tahoma", Font.BOLD, 20));
		export.setSize(500, 500);
		export.setFocusPainted(false);
		exportActionPanel.add(export, BorderLayout.CENTER);

		reportPanel.add(growthTrendPanel);
		reportPanel.add(createBorderGap());
		reportPanel.add(pestTrendPanel);
		reportPanel.add(createBorderGap());
		reportPanel.add(exportActionPanel);

		/*===================== ADD TO CENTRAL PANEL =====================*/
		mainPanel.add(dashboardPanel, "dashboard");
		mainPanel.add(cropManagementPanel, "management");
		mainPanel.add(InventoryPanel, "inventory");
		mainPanel.add(tasksPanel, "tasks");
		mainPanel.add(reportPanel, "reports");

		/*===================== ACTION LISTENER FOR CARD LAYOUT =====================*/
		dashboardBtn.addActionListener(e -> {  CropTableManager.refreshCropTable(); TasksTableManager.refreshTaskTable(); cardLayout.show(mainPanel, "dashboard"); });
		manageBtn.addActionListener(e -> cardLayout.show(mainPanel, "management"));
		monitorBtn.addActionListener(e -> cardLayout.show(mainPanel, "inventory"));
		tasksBtn.addActionListener(e -> cardLayout.show(mainPanel, "tasks"));
		reportsBtn.addActionListener(e -> cardLayout.show(mainPanel, "reports"));
	}
}