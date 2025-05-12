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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
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
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.JComboBox;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.function.Supplier;

import java.io.BufferedWriter;
import java.io.FileWriter;
import javax.swing.JFileChooser;
import java.io.File;

import com.toedter.calendar.JDateChooser;

import dialogs_managers.*;
import ui_managers.*;

@SuppressWarnings("unused")
public class Application {
	/*_____________________ CLASS-LEVEL _____________________*/
	private JFrame mainFrame;
	private CardLayout cardLayout = new CardLayout();
	private Connection connection; // IT IS USED BALIW LANG ECLIPSE
	private JTabbedPane reportTabs;
	List<JButton> sidebarBtns;

	Color normalColor = Color.decode("#27AE60");
	Color hoverColor = Color.decode("#2ECC71");
	Color activeColor = Color.decode("#16A085"); // A color to indicate the active button

	/*_____________________ REUSABLE _____________________*/
	private Component createBorderGap() {
		return Box.createRigidArea(new Dimension(0, 5));
	}

	private Component createActionButtonGap() {
		return Box.createRigidArea(new Dimension(10, 0));
	}

	/*_____________________ CLASS REFERENCES _____________________*/
	CropManagementDialog cropDialog = new CropManagementDialog(mainFrame); // Class = new Object(Parameter)
	TaskDialog taskDialog = new TaskDialog(mainFrame);
	InventoryDialog inventoryDialog = new InventoryDialog(mainFrame);
	ChartReportDialog chartReportDialog = new ChartReportDialog(mainFrame);
	
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
		mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH); // FULLSCREEN ON START

		/*_____________________ STAGE _____________________*/
		mainFrame.getContentPane().setLayout(new BorderLayout());
		mainFrame.getContentPane().setBackground(Color.white);

		/*===================== MASTER PANELS =====================*/
		int logo_size = 50;
		int title_size = 30;

		/*--------------------- PANEL #1: TOP ---------------------*/
		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.setBackground(Color.decode("#2C3E50"));
		topPanel.setPreferredSize(new Dimension(0, 70)); // width - height
		topPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		topPanel.setBorder(null);

		/*_____________________ CONTENTS _____________________*/
		ImageIcon originalImage = new ImageIcon(getClass().getResource("/resources/Logo.png"));
		Image scaledImage = originalImage.getImage().getScaledInstance(logo_size, logo_size, Image.SCALE_SMOOTH);
		ImageIcon resizedImage = new ImageIcon(scaledImage);
		JLabel logo = new JLabel(resizedImage);
		logo.setPreferredSize(new Dimension(logo_size, logo_size));
		logo.setCursor(new Cursor(Cursor.HAND_CURSOR));

		JLabel title = new JLabel("<html><center>Crop See</center></html>");
		title.setFont(new Font("Roboto", Font.BOLD, title_size));
		title.setBounds(55, 10, 90, 24);
		title.setForeground(new Color(30, 138, 56)); // DARK GREEN
		title.setCursor(new Cursor(Cursor.HAND_CURSOR));

		/*_____________________ CONTAINER _____________________*/
		JPanel topContainer = new JPanel(new GridBagLayout());
		topContainer.setOpaque(false);
		topContainer.add(Box.createRigidArea(new Dimension(20, 0)));
		topContainer.add(logo);
		topContainer.add(Box.createRigidArea(new Dimension(20, 0)));
		topContainer.add(title);

		// PUTS IN LEFT SIDE
		topPanel.add(topContainer, BorderLayout.WEST); 

		// EVENT LISTENER
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

		/*--------------------- PANEL #2: SIDE ---------------------*/
		JPanel sidePanel = new JPanel(new GridLayout(5, 1, 0, 5)); // row, column, gaps-between-rows, gaps-between-columns
		sidePanel.setBackground(Color.decode("#34495E"));
		sidePanel.setPreferredSize(new Dimension(200, 0));
		sidePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		sidePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // top, left, bottom, right

		// Inside your main GUI class or the relevant method

		// Buttons for the sidebar
		JButton dashboardBtn = new JButton("<html>DASHBOARD</html>");
		JButton manageBtn = new JButton("<html><center>CROP<br>MANAGEMENT</center></html>");
		JButton tasksBtn = new JButton("<html>TASKS</html>");
		JButton monitorBtn = new JButton("<html><center>INVENTORY MANAGEMENT</center></html>");
		JButton reportsBtn = new JButton("<html>REPORTS</html>");

		// List of buttons
		sidebarBtns = Arrays.asList(dashboardBtn, tasksBtn, manageBtn, monitorBtn, reportsBtn);

		// Array of buttons for customization
		JButton[] sideButtons = {dashboardBtn, manageBtn, tasksBtn, monitorBtn, reportsBtn};


		// Set up button customization
		for (JButton btn : sideButtons) {
			btn.setFont(new Font("Roboto", Font.BOLD, 18));
			btn.setFocusPainted(false);
			btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
			btn.setBackground(normalColor); // Default background color
			btn.setForeground(Color.decode("#FFFFFF")); // Text color

			// Hover effect using MouseListener
			btn.addMouseListener(new java.awt.event.MouseAdapter() {
				@Override
				public void mouseEntered(java.awt.event.MouseEvent evt) {
					if (!btn.getBackground().equals(activeColor)) {
						btn.setBackground(hoverColor); // Change background when hovered (unless it's the active button)
					}
				}

				@Override
				public void mouseExited(java.awt.event.MouseEvent evt) {
					if (!btn.getBackground().equals(activeColor)) {
						btn.setBackground(normalColor); // Revert to normal color when not hovered
					}
				}
			});

			// Add action listener to set the active button
			btn.addActionListener(e -> setActiveButton(btn));

			sidePanel.add(btn); // Add button to the side panel
		}

		/*--------------------- PANEL #3: CENTER ---------------------*/
		JPanel mainPanel = new JPanel(cardLayout);
		mainPanel.setBackground(Color.decode("#34495E"));
		mainPanel.setPreferredSize(null);
		mainPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		/*------------------------------------------------------------*/
		mainFrame.getContentPane().add(topPanel, BorderLayout.NORTH);
		mainFrame.getContentPane().add(sidePanel, BorderLayout.WEST);
		mainFrame.getContentPane().add(mainPanel, BorderLayout.CENTER);

		/*===================== CONTENT PANELS=====================*/
		/*--------------------- CONTENT #2: CROP MANAGEMENT ---------------------*/
		JPanel cropManagementPanel = new JPanel();
		cropManagementPanel.setLayout(new BoxLayout(cropManagementPanel, BoxLayout.Y_AXIS));
		cropManagementPanel.setBackground(Color.decode("#34495E"));
		cropManagementPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); 

		/*_____________________ CONTAINER _____________________*/
		JPanel tableListofCrops = new JPanel(new BorderLayout()); // Use BorderLayout
		tableListofCrops.setPreferredSize(new Dimension(Integer.MAX_VALUE, 400));
		tableListofCrops.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

		JPanel cropManagementActionPanel = new JPanel(new GridBagLayout());
		cropManagementActionPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 100));
		cropManagementActionPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

		/*_____________________ CONTENT _____________________*/
		// TABLE (ON ANOTHER CLASS)
		Crop_Manager.addCropManagementTable(tableListofCrops);

		// ACTION BUTTONS
		JButton addCropBtn = new JButton("Add New Crop");
		addCropBtn.setBackground(Color.decode("#27AE60"));
		addCropBtn.setForeground(Color.decode("#FFFFFF"));

		JButton editCropBtn = new JButton("Edit Crop");
		editCropBtn.setBackground(Color.decode("#F9A825"));
		editCropBtn.setForeground(Color.decode("#FFFFFF"));

		JButton deleteCropBtn = new JButton("Delete Crop");
		deleteCropBtn.setBackground(Color.decode("#E74C3C"));
		deleteCropBtn.setForeground(Color.decode("#FFFFFF"));

		addCropBtn.addActionListener(e -> cropDialog.showAddCropDialog());
		editCropBtn.addActionListener(e -> cropDialog.showEditCropDialog());
		deleteCropBtn.addActionListener(e -> cropDialog.deleteSelectedCrop());

		// BUTTON DESIGN
		for (JButton btn : new JButton[]{addCropBtn, editCropBtn, deleteCropBtn}) {
			btn.setFont(new Font("Roboto", Font.BOLD, 20));
			btn.setFocusPainted(false);
			btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
			btn.setMargin(new Insets(10, 10, 10, 10));
		}

		JPanel cropButtonContainer = new JPanel(); // BUTTONS CONTAINER
		cropButtonContainer.add(addCropBtn);
		cropButtonContainer.add(createActionButtonGap());
		cropButtonContainer.add(editCropBtn);
		cropButtonContainer.add(createActionButtonGap());
		cropButtonContainer.add(deleteCropBtn);
		cropManagementActionPanel.add(cropButtonContainer); // ADDING TO MAIN CONTAINER

		cropManagementPanel.add(tableListofCrops);
		cropManagementPanel.add(createBorderGap());
		cropManagementPanel.add(cropManagementActionPanel); // HERE

		/*--------------------- CONTENT #3: TASK ---------------------*/
		JPanel tasksPanel = new JPanel();
		tasksPanel.setLayout(new BoxLayout(tasksPanel, BoxLayout.Y_AXIS));
		tasksPanel.setBackground(Color.decode("#34495E"));
		tasksPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); 

		/*_____________________ CONTAINER _____________________*/
		JPanel tableListofTasks = new JPanel(new BorderLayout());
		tableListofTasks.setPreferredSize(new Dimension(10000, 400));
		tableListofTasks.setMaximumSize(new Dimension(10000, Integer.MAX_VALUE));


		JPanel tasksActionPanel = new JPanel(new GridBagLayout());
		tasksActionPanel.setPreferredSize(new Dimension(10000, 100));
		tasksActionPanel.setMaximumSize(new Dimension(10000, 100));

		/*_____________________ CONTENT _____________________*/
		Task_Manager.addTasksTable(tableListofTasks); // ADDS TABLE TO CONTAINER

		// CUSTOMIZATION
		JButton addTaskBtn = new JButton("Add New Task");
		addTaskBtn.setBackground(Color.decode("#27AE60"));
		addTaskBtn.setForeground(Color.decode("#FFFFFF"));

		JButton editTaskBtn = new JButton("Edit Task");
		editTaskBtn.setBackground(Color.decode("#F9A825"));
		editTaskBtn.setForeground(Color.decode("#FFFFFF"));

		JButton deleteTaskBtn = new JButton("Delete Task");
		deleteTaskBtn.setBackground(Color.decode("#E74C3C"));
		deleteTaskBtn.setForeground(Color.decode("#FFFFFF"));

		JButton completeTaskBtn = new JButton("Mark Complete");
		completeTaskBtn.setBackground(Color.decode("#5DADE2"));
		completeTaskBtn.setForeground(Color.decode("#FFFFFF"));

		JButton removeCompletedBtn = new JButton("Remove Completed");
		removeCompletedBtn.setBackground(Color.decode("#AF7AC5"));
		removeCompletedBtn.setForeground(Color.decode("#FFFFFF"));

		// ACTION LISTENER
		addTaskBtn.addActionListener(e -> taskDialog.showAddTaskDialog());
		editTaskBtn.addActionListener(e -> taskDialog.showEditTaskDialog());
		deleteTaskBtn.addActionListener(e -> taskDialog.deleteSelectedTask());
		completeTaskBtn.addActionListener(e -> taskDialog.markTaskComplete());
		removeCompletedBtn.addActionListener(e -> {
			int confirm = JOptionPane.showConfirmDialog(mainFrame, 
					"This will permanently delete all completed tasks.\nContinue?", 
					"Confirm Cleanup", JOptionPane.YES_NO_OPTION);

			if (confirm == JOptionPane.YES_OPTION) {
				Task_Manager.removeCompletedTasks();
			}
		});

		// BUTTON DESIGN
		for (JButton btn : new JButton[]{addTaskBtn, editTaskBtn, deleteTaskBtn, completeTaskBtn, removeCompletedBtn}) {
			btn.setFont(new Font("Roboto", Font.BOLD, 20));
			btn.setFocusPainted(false);
			btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
			btn.setMargin(new Insets(10, 10, 10, 10));
		}

		JPanel taskButtonsContainer = new JPanel(); // BUTTONS CONTAINER
		taskButtonsContainer.add(addTaskBtn);
		taskButtonsContainer.add(createActionButtonGap());
		taskButtonsContainer.add(editTaskBtn);
		taskButtonsContainer.add(createActionButtonGap());
		taskButtonsContainer.add(deleteTaskBtn);
		taskButtonsContainer.add(createActionButtonGap());
		taskButtonsContainer.add(completeTaskBtn);
		taskButtonsContainer.add(createActionButtonGap());
		taskButtonsContainer.add(removeCompletedBtn);
		tasksActionPanel.add(taskButtonsContainer); // ADDING TO MAIN CONTAINER

		tasksPanel.add(tableListofTasks);
		tasksPanel.add(createBorderGap());
		tasksPanel.add(tasksActionPanel); // HERE

		// THIS IS BEFORE BECAUSE ITS TABLES CONTENT ARE THE FIRST TWO PANELS (REFERENCE)
		/*--------------------- CONTENT #1: DASHBOARD ---------------------*/
		JPanel dashboardPanel = new JPanel();
		dashboardPanel.setLayout(new BoxLayout(dashboardPanel, BoxLayout.Y_AXIS));
		dashboardPanel.setBackground(Color.decode("#34495E"));
		dashboardPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); 

		/*_____________________ MAIN _____________________*/
		JPanel tablesContainer = new JPanel();
		tablesContainer.setLayout(new GridLayout(1, 2, 0, 0)); // 1 row, 2 columns, 8px gaps
		tablesContainer.setPreferredSize(new Dimension(10000, 400));
		tablesContainer.setMaximumSize(new Dimension(10000, Integer.MAX_VALUE));
		tablesContainer.setBackground(Color.decode("#F2F2F2"));

		/*_____________________ CONTAINER (FOR BORDERING) _____________________*/
		JPanel cropContainer = new JPanel(new BorderLayout());
		TitledBorder cropBorder = new TitledBorder(BorderFactory.createEmptyBorder(), "CROPS");
		cropBorder.setTitleFont(new Font("Roboto", Font.BOLD, 20));
		cropBorder.setTitleJustification(TitledBorder.CENTER); // ALIGNMENT OF NAME
		cropContainer.setBorder(cropBorder);

		JPanel taskContainer = new JPanel(new BorderLayout());
		TitledBorder taskBorder = new TitledBorder(BorderFactory.createEmptyBorder(), "TASKS");
		taskBorder.setTitleFont(new Font("Roboto", Font.BOLD, 20));
		taskBorder.setTitleJustification(TitledBorder.CENTER); // ALIGNMENT OF NAME
		taskContainer.setBorder(taskBorder);

		/*_____________________ TABLE _____________________*/
		JTable initialCropTable = new JTable(Crop_Manager.model);
		chartReportDialog.styleTable(initialCropTable);
		JScrollPane scrollableCropTable = new JScrollPane(initialCropTable);
		cropContainer.add(scrollableCropTable);

		/*_____________________ TABLE _____________________*/
		JTable initialTaskTable = new JTable(Task_Manager.model);
		chartReportDialog.styleTable(initialTaskTable);
		JScrollPane scrollableTaskTable = new JScrollPane(initialTaskTable);
		taskContainer.add(scrollableTaskTable);

		/*_____________________ ADD _____________________*/
		tablesContainer.add(cropContainer);
		tablesContainer.add(taskContainer);

		/*_____________________ REFRESH CONTAINER _____________________*/
		JPanel refreshPanel = new JPanel(new GridBagLayout());
		refreshPanel.setPreferredSize(new Dimension(10000, 100));
		refreshPanel.setMaximumSize(new Dimension(10000, 100));
		refreshPanel.setBackground(Color.decode("#E8F5E9"));

		/*_____________________ BUTTON _____________________*/
		JButton refreshBtn = new JButton("REFRESH TABLES");
		refreshBtn.setFont(new Font("Roboto", Font.BOLD, 20));
		refreshBtn.setBackground(Color.decode("#F9A825"));
		refreshBtn.setForeground(Color.decode("#FFFFFF"));
		refreshBtn.setFocusPainted(false);
		refreshBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		refreshBtn.setMargin(new Insets(10, 10, 10, 10));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.anchor = GridBagConstraints.CENTER;

		refreshPanel.add(refreshBtn, gbc);
		refreshBtn.addActionListener(e -> {
			Crop_Manager.refreshCropTable();
			Task_Manager.refreshTaskTable();
		});

		/*_____________________ ADD _____________________*/
		dashboardPanel.add(tablesContainer);
		dashboardPanel.add(createBorderGap());
		dashboardPanel.add(refreshPanel);

		/*--------------------- CONTENT #4: INVENTORY MANAGEMENT ---------------------*/
		JPanel InventoryPanel = new JPanel();
		InventoryPanel.setLayout(new BoxLayout(InventoryPanel, BoxLayout.Y_AXIS));
		InventoryPanel.setBackground(Color.decode("#34495E"));
		InventoryPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); 

		/*_____________________ PANEL #1 _____________________*/
		JPanel inventoryListTable = new JPanel(new BorderLayout());
		inventoryListTable.setPreferredSize(new Dimension(10000, 400));
		inventoryListTable.setMaximumSize(new Dimension(10000, Integer.MAX_VALUE));

		Inventory_Manager.addInventoryTable(inventoryListTable);

		/*_____________________ PANEL #2 _____________________*/
		JPanel inventoryActionPanel = new JPanel(new GridBagLayout());
		inventoryActionPanel.setPreferredSize(new Dimension(10000, 100));
		inventoryActionPanel.setMaximumSize(new Dimension(10000, 100));


		/*_____________________ BUTTONS FOR PANEL #2 _____________________*/
		JButton addItemBtn = new JButton("Add Item");
		addItemBtn.setBackground(Color.decode("#27AE60"));
		addItemBtn.setForeground(Color.decode("#FFFFFF"));

		JButton editItemBtn = new JButton("Edit Item");
		editItemBtn.setBackground(Color.decode("#F9A825"));
		editItemBtn.setForeground(Color.decode("#FFFFFF"));

		JButton deleteItemBtn = new JButton("Delete Item");
		deleteItemBtn.setBackground(Color.decode("#E74C3C"));
		deleteItemBtn.setForeground(Color.decode("#FFFFFF"));

		/*_____________________ STYLE FOR BUTTONS _____________________*/
		for (JButton btn : new JButton[]{addItemBtn, editItemBtn, deleteItemBtn}) {
			btn.setFont(new Font("Roboto", Font.BOLD, 20));
			btn.setFocusPainted(false);
			btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
			btn.setMargin(new Insets(10, 10, 10, 10));
		}

		/*_____________________ LISTENER FOR BUTTONS _____________________*/
		addItemBtn.addActionListener(e -> inventoryDialog.showAddInventoryDialog());
		editItemBtn.addActionListener(e -> inventoryDialog.showEditInventoryDialog());
		deleteItemBtn.addActionListener(e -> inventoryDialog.deleteInventoryItem());

		/*_____________________ ADD BUTTONS TO PANEL #2 _____________________*/
		JPanel buttoncontainer = new JPanel();

		buttoncontainer.add(addItemBtn);
		buttoncontainer.add(createActionButtonGap());
		buttoncontainer.add(editItemBtn);
		buttoncontainer.add(createActionButtonGap());
		buttoncontainer.add(deleteItemBtn);
		inventoryActionPanel.add(buttoncontainer);

		/*_____________________ ADD PANEL #1 AND #2 TO MAIN INVENTORY PANEL _____________________*/
		InventoryPanel.add(inventoryListTable);
		InventoryPanel.add(createBorderGap());
		InventoryPanel.add(inventoryActionPanel);

		/*--------------------- CONTENT #5: REPORTS ---------------------*/
		JPanel reportPanel = new JPanel();
		reportPanel.setLayout(new BoxLayout(reportPanel, BoxLayout.Y_AXIS));
		reportPanel.setBackground(Color.decode("#34495E"));
		reportPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); 

		reportTabs = new JTabbedPane();
		reportTabs.setPreferredSize(new Dimension(0, 400));
		reportTabs.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

		/*_____________________ #1 CROP TAB _____________________*/
		JPanel cropReportTab = chartReportDialog.createCropReportTab();
		reportTabs.addTab("CROPS", cropReportTab);

		/*_____________________ #2 TASK TAB _____________________*/
		JPanel taskReportTab = chartReportDialog.createTaskReportTab();
		reportTabs.addTab("TASKS", taskReportTab);

		/*_____________________ #3 INVENTORY TAB _____________________*/
		JPanel inventoryReportTab = chartReportDialog.createInventoryReportTab();
		reportTabs.addTab("INVENTORY", inventoryReportTab);

		/*_____________________ EXPORT _____________________*/
		JPanel exportPanel = new JPanel(new GridBagLayout());
		exportPanel.setPreferredSize(new Dimension(0, 100));
		exportPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

		JPanel exportButtonContainer = new JPanel();

		JButton exportBtn = new JButton("EXPORT CURRENT REPORT");
		exportBtn.setFont(new Font("Roboto", Font.BOLD, 20));
		exportBtn.setFocusPainted(false);
		exportBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		exportBtn.setMargin(new Insets(10, 10, 10, 10));
		exportBtn.setBackground(Color.decode("#27AE60"));
		exportBtn.setForeground(Color.decode("#FFFFFF"));

		exportBtn.addActionListener(e -> {
			int selectedTab = reportTabs.getSelectedIndex();
			switch(selectedTab) {
			case 0: 
				chartReportDialog.exportCropData();
				break;
			case 1: 
				chartReportDialog.exportTaskData();
				break;
			case 2: 
				chartReportDialog.exportInventoryData();
				break;
			}
		});

		exportButtonContainer.add(exportBtn);
		exportPanel.add(exportButtonContainer);

		/*_____________________ ADD _____________________*/
		reportPanel.add(reportTabs);
		reportPanel.add(createBorderGap());
		reportPanel.add(exportPanel);

		/*===================== ADD TO CENTRAL PANEL =====================*/
		mainPanel.add(dashboardPanel, "dashboard");
		mainPanel.add(cropManagementPanel, "crop management");
		mainPanel.add(InventoryPanel, "inventory");
		mainPanel.add(tasksPanel, "tasks");
		mainPanel.add(reportPanel, "reports");

		/*===================== ACTION LISTENER FOR CARD LAYOUT =====================*/
		dashboardBtn.addActionListener(e -> {  Crop_Manager.refreshCropTable(); Task_Manager.refreshTaskTable(); cardLayout.show(mainPanel, "dashboard"); });
		manageBtn.addActionListener(e -> { Crop_Manager.refreshCropTable(); cardLayout.show(mainPanel, "crop management"); });
		monitorBtn.addActionListener(e -> { Inventory_Manager.refreshInventoryTable(); cardLayout.show(mainPanel, "inventory"); });
		tasksBtn.addActionListener(e -> { Task_Manager.refreshTaskTable(); cardLayout.show(mainPanel, "tasks"); });
		reportsBtn.addActionListener(e -> cardLayout.show(mainPanel, "reports"));
	}

	// Method to set the active button
	private void setActiveButton(JButton activeButton) {
		for (JButton btn : sidebarBtns) {
			if (btn == activeButton) {
				btn.setBackground(activeColor); // Highlight active button
				btn.setFont(new Font("Roboto", Font.BOLD, 18));
			} else {
				btn.setBackground(normalColor); // Revert others to normal color
				btn.setFont(new Font("Roboto", Font.BOLD, 18));
			}
		}
	}

}