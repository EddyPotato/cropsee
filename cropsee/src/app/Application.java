package app;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
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

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Application {
	/*_____________________ CLASS-LEVEL _____________________*/
	private JFrame mainFrame;
	private CardLayout cardLayout = new CardLayout();
	@SuppressWarnings("unused")
	private Connection connection; // IT IS USED BALIW LANG ECLIPSE

	/*_____________________ REUSABLE METHODS _____________________*/

	private Component createBorderGap() {
		return Box.createRigidArea(new Dimension(0, 5));
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
		mainFrame.setSize(800, 545);
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

		/*===================== DASHBOARD =====================*/
		/*_____________________ PRIMARY CONTAINER _____________________*/
		JPanel container_SummmaryCard = new JPanel();
		container_SummmaryCard.setPreferredSize(new Dimension(Integer.MAX_VALUE, 1000)); // width - height
		container_SummmaryCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		container_SummmaryCard.setBackground(Color.red);

		JPanel container_ActionDashboard = new JPanel();
		container_ActionDashboard.setPreferredSize(new Dimension(Integer.MAX_VALUE, 200)); // width - height
		container_ActionDashboard.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		container_ActionDashboard.setBackground(Color.blue);

		/*_____________________ LAYOUT _____________________*/
		container_SummmaryCard.setLayout(new BoxLayout(container_SummmaryCard, BoxLayout.X_AXIS));
		container_ActionDashboard.setLayout(new GridLayout(2, 3, 5, 5));

		/*_____________________ SUMMARY CARDS _____________________*/
		JPanel summaryCard1 = new JPanel();
		summaryCard1.setFont(new Font("Tahoma", Font.BOLD, 18));
		summaryCard1.setCursor(new Cursor(Cursor.HAND_CURSOR));
		summaryCard1.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		summaryCard1.setLayout(new BoxLayout(summaryCard1, BoxLayout.Y_AXIS));

		JPanel summaryCard2 = new JPanel();
		summaryCard2.setFont(new Font("Tahoma", Font.BOLD, 18));
		summaryCard2.setCursor(new Cursor(Cursor.HAND_CURSOR));
		summaryCard2.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		summaryCard2.setLayout(new BoxLayout(summaryCard2, BoxLayout.Y_AXIS));

		/*_____________________ ADD CARDS _____________________*/
		container_SummmaryCard.add(summaryCard1);
		container_SummmaryCard.add(summaryCard2);

		JLabel SumCardDescription1 = new JLabel("CROPS");
		SumCardDescription1.setBounds(107, 10, 69, 25);
		SumCardDescription1.setFont(new Font("Tahoma", Font.BOLD, 20));

		JLabel SumCardDescription2 = new JLabel("TASKS");
		SumCardDescription2.setBounds(105, 10, 66, 25);
		SumCardDescription2.setFont(new Font("Tahoma", Font.BOLD, 20));

		JScrollPane scrollPane1 = new JScrollPane();
		scrollPane1.setBounds(10, 46, 264, 394);

		JScrollPane scrollPane2 = new JScrollPane();
		scrollPane2.setBounds(10, 46, 264, 394);

		summaryCard1.add(SumCardDescription1);
		summaryCard1.add(scrollPane1);

		summaryCard2.add(SumCardDescription2);
		summaryCard2.add(scrollPane2);

		dashboardPanel.add(container_SummmaryCard);
		dashboardPanel.add(createBorderGap());
		dashboardPanel.add(container_ActionDashboard);

	    /*===================== CROP MANAGEMENT =====================*/
	    JPanel tableListofCrops = new JPanel();
	    tableListofCrops.setPreferredSize(new Dimension(Integer.MAX_VALUE, 400));
	    tableListofCrops.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
	    
	    // Adding the table to the Crop Management panel
	    addCropManagementTable(tableListofCrops);

	    JPanel cropManagementActionPanel = new JPanel();
	    cropManagementActionPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 100));
	    cropManagementActionPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 500));

	    cropManagementPanel.add(tableListofCrops);
	    cropManagementPanel.add(createBorderGap());
	    cropManagementPanel.add(cropManagementActionPanel);

		/*===================== TASK =====================*/
		JPanel tasksListTable = new JPanel();
		tasksListTable.setPreferredSize(new Dimension(Integer.MAX_VALUE, 400));
		tasksListTable.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

		JPanel tasksActionPanel = new JPanel();
		tasksActionPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 100));
		tasksActionPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 500));

		tasksPanel.add(tasksListTable);
		tasksPanel.add(createBorderGap());
		tasksPanel.add(tasksActionPanel);

		/*===================== INVENTORY =====================*/
		JPanel inventoryListTable = new JPanel();
		inventoryListTable.setPreferredSize(new Dimension(0, 400));
		inventoryListTable.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

		JPanel inventoryActionPanel = new JPanel();
		inventoryActionPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 100));
		inventoryActionPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 500));

		InventoryPanel.add(inventoryListTable);
		InventoryPanel.add(createBorderGap());
		InventoryPanel.add(inventoryActionPanel);

		/*===================== REPORT =====================*/
		JPanel growthTrendPanel = new JPanel();
		growthTrendPanel.setPreferredSize(new Dimension(0, 300));
		growthTrendPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

		JPanel pestTrendPanel = new JPanel();
		pestTrendPanel.setPreferredSize(new Dimension(0, 300));
		pestTrendPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

		/*_____________________ EXPORT _____________________*/
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
		dashboardBtn.addActionListener(e -> cardLayout.show(mainPanel, "dashboard"));
		manageBtn.addActionListener(e -> cardLayout.show(mainPanel, "management"));
		monitorBtn.addActionListener(e -> cardLayout.show(mainPanel, "inventory"));
		tasksBtn.addActionListener(e -> cardLayout.show(mainPanel, "tasks"));
		reportsBtn.addActionListener(e -> cardLayout.show(mainPanel, "reports"));
	}
	
	private void addCropManagementTable(JPanel tableListofCrops) {
	    // Column names for the table
	    String[] columnNames = { "Crop ID", "Crop Name", "Planting Date", "Harvest Date", "Status" };

	    // Sample data for the crops table (replace with database data later)
	    Object[][] data = {
	        { "1", "Tomato", "2023-03-15", "2023-06-15", "Growing" },
	        { "2", "Corn", "2023-02-20", "2023-05-20", "Harvested" },
	        { "3", "Wheat", "2023-04-10", "2023-07-10", "Growing" },
	    };

	    // Creating a table model with the data
	    DefaultTableModel model = new DefaultTableModel(data, columnNames);
	    JTable cropTable = new JTable(model);

	    // Setting the table in a JScrollPane for scrolling
	    JScrollPane tableScrollPane = new JScrollPane(cropTable);
	    tableListofCrops.add(tableScrollPane);
	}
}