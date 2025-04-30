package app;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
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

public class Application {
	/*_____________________ CLASS-LEVEL _____________________*/
	private JFrame mainFrame;
	private CardLayout cardLayout = new CardLayout();

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

	private void initialize() {
		/*_____________________ FRAME _____________________*/
		mainFrame = new JFrame();
		mainFrame.setResizable(false);
		mainFrame.setAlwaysOnTop(true);
		mainFrame.getContentPane().setBackground(Color.WHITE);
		mainFrame.setTitle("Crop See");
		mainFrame.setSize(800, 545);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setLocationRelativeTo(null);

		/*_____________________ STAGE _____________________*/
		mainFrame.getContentPane().setLayout(new BorderLayout());

		/*===================== TOP PANEL =====================*/
		JPanel topPanel = new JPanel();
		topPanel.setBackground(Color.white);
		topPanel.setPreferredSize(new Dimension(0, 45));
		topPanel.setLayout(null);

		/*_____________________ TOP PANEL - LOGO _____________________*/
		ImageIcon originalImage = new ImageIcon(getClass().getResource("/resources/Logo.jpg"));
		Image scaledImage = originalImage.getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH);
		ImageIcon resizedImage = new ImageIcon(scaledImage);
		JLabel logo = new JLabel(resizedImage);
		logo.setBounds(10, 5, 35, 35);
		logo.setCursor(new Cursor(Cursor.HAND_CURSOR));
		topPanel.add(logo);

		/*_____________________ TOP PANEL - TITLE _____________________*/
		JLabel Title = new JLabel("<html><center>Crop See</center></html>");
		Title.setFont(new Font("Tahoma", Font.BOLD, 20));
		Title.setBounds(55, 10, 90, 24);
		Title.setForeground(new Color(30, 138, 56)); // DARK GREEN
		Title.setCursor(new Cursor(Cursor.HAND_CURSOR));
		topPanel.add(Title);

		/*_____________________ TOP PANEL - EVENT LISTERNERS _____________________*/
		logo.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				JOptionPane.showMessageDialog(null, "Logo clicked!");
			}
		});

		Title.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				JOptionPane.showMessageDialog(null, "Title clicked!");
			}
		});

		/*===================== SIDE PANEL =====================*/
		JPanel sidePanel = new JPanel();
		sidePanel.setBackground(Color.white);
		sidePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		sidePanel.setLayout(new GridLayout(5, 1, 0, 5));
		sidePanel.setPreferredSize(new Dimension(200, 0));
		sidePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

		/*_____________________ SIDE PANEL - BUTTON TEXT _____________________*/
		JButton dashboardBtn = new JButton("<html>Dashboard</html>");
		JButton manageBtn = new JButton("<html><center>Crop<br>Management</center></html>");
		JButton tasksBtn = new JButton("<html>Tasks</html>");
		JButton monitorBtn = new JButton("<html><center>Inventory Management</center></html>");
		JButton reportsBtn = new JButton("<html>Reports</html>");

		/*_____________________ SIDE PANEL - BUTTON CUSTOMIZATION _____________________*/
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

		/*===================== CENTRAL =====================*/
		JPanel mainPanel = new JPanel(cardLayout);
		mainPanel.setBackground(Color.WHITE); // BACKGROUND
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // PADDING

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

		/*_____________________ PANELS - INFORMATION _____________________*/
		

		cropManagementPanel.setBackground(Color.white);
		cropManagementPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		cropManagementPanel.setLayout(new BoxLayout(cropManagementPanel, BoxLayout.Y_AXIS));

		tasksPanel.setBackground(Color.white);
		tasksPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		tasksPanel.setLayout(new BoxLayout(tasksPanel, BoxLayout.Y_AXIS));

		InventoryPanel.setBackground(Color.white);
		InventoryPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		InventoryPanel.setLayout(new BoxLayout(InventoryPanel, BoxLayout.Y_AXIS));

		reportPanel.setBackground(Color.white);
		reportPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		reportPanel.setLayout(new BoxLayout(reportPanel, BoxLayout.Y_AXIS));

		/*===================== DASHBOARD =====================*/
		dashboardPanel.setBackground(Color.white);
		dashboardPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		dashboardPanel.setLayout(new BoxLayout(dashboardPanel, BoxLayout.Y_AXIS));
		
		
		
		JPanel summaryCard1 = new JPanel();
		summaryCard1.setFont(new Font("Tahoma", Font.BOLD, 18));
		summaryCard1.setCursor(new Cursor(Cursor.HAND_CURSOR));
		summaryCard1.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		summaryCard1.setLayout(null);

		JPanel summaryCard2 = new JPanel();
		summaryCard2.setFont(new Font("Tahoma", Font.BOLD, 18));
		summaryCard2.setCursor(new Cursor(Cursor.HAND_CURSOR));
		summaryCard2.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		summaryCard2.setLayout(null);

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

		dashboardPanel.add(summaryCard1);
		dashboardPanel.add(summaryCard2);

		/*===================== CROP MANAGEMENT =====================*/
		JPanel manageTable = new JPanel();
		manageTable.setPreferredSize(new Dimension(Integer.MAX_VALUE, 400)); // preferred height
		manageTable.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE)); // maximum height

		JPanel tableListofCrop = new JPanel();
		tableListofCrop.setPreferredSize(new Dimension(Integer.MAX_VALUE, 100)); // preferred height
		tableListofCrop.setMaximumSize(new Dimension(Integer.MAX_VALUE, 500)); // width, height

		cropManagementPanel.add(manageTable);
		cropManagementPanel.add(Box.createRigidArea(new Dimension(0, 5))); // ADDS EMPTY BOX FOR SPACING // 0PX WIDTH, 5PX HEIGHT
		cropManagementPanel.add(tableListofCrop);

		/*===================== TASK =====================*/
		JPanel tasksTable = new JPanel();
		tasksTable.setPreferredSize(new Dimension(Integer.MAX_VALUE, 400)); // preferred height
		tasksTable.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE)); // maximum height

		JPanel tasksActions = new JPanel();
		tasksActions.setPreferredSize(new Dimension(Integer.MAX_VALUE, 100)); // preferred height
		tasksActions.setMaximumSize(new Dimension(Integer.MAX_VALUE, 500)); // width, height

		tasksPanel.add(tasksTable);
		Component rigidArea_1 = Box.createRigidArea(new Dimension(0, 5));
		tasksPanel.add(rigidArea_1);
		tasksPanel.add(tasksActions);

		/*===================== INVENTORY =====================*/
		JPanel inventoryTable = new JPanel();
		inventoryTable.setPreferredSize(new Dimension(0, 400)); // width - height
		inventoryTable.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE)); // maximum height

		JPanel inventoryActions = new JPanel();
		inventoryActions.setPreferredSize(new Dimension(Integer.MAX_VALUE, 100)); // preferred height
		inventoryActions.setMaximumSize(new Dimension(Integer.MAX_VALUE, 500));

		InventoryPanel.add(inventoryTable);
		Component rigidArea = Box.createRigidArea(new Dimension(0, 5));
		InventoryPanel.add(rigidArea);
		InventoryPanel.add(inventoryActions);

		/*===================== REPORT =====================*/
		JPanel growthTrendPanel = new JPanel();
		growthTrendPanel.setPreferredSize(new Dimension(0, 300)); // preferred height
		growthTrendPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE)); // maximum height

		JPanel pestTrendPanel = new JPanel();
		pestTrendPanel.setPreferredSize(new Dimension(0, 300)); // preferred height
		pestTrendPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE)); // maximum height

		/*_____________________ EXPORT _____________________*/
		JPanel exportPanel = new JPanel();
		exportPanel.setPreferredSize(new Dimension(0, 100));
		exportPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100)); // FIRST TRY HAHAHA GUMANA SIYA, NABAWASAN STILL KEPT IT
		exportPanel.setLayout(new BorderLayout(0, 0));

		/*_____________________ EXPORT - BUTTON _____________________*/
		JButton export = new JButton("EXPORT STATISTICS");
		export.setFont(new Font("Tahoma", Font.BOLD, 20));
		export.setSize(500, 500);
		export.setFocusPainted(false);
		exportPanel.add(export, BorderLayout.CENTER);

		reportPanel.add(growthTrendPanel);
		reportPanel.add(Box.createRigidArea(new Dimension(0, 5))); // ANOTHER WAY TO ADD SPACING // FOR BOXLAYOUT
		reportPanel.add(pestTrendPanel);
		reportPanel.add(Box.createRigidArea(new Dimension(0, 5))); // ANOTHER WAY TO ADD SPACING // FOR BOXLAYOUT
		reportPanel.add(exportPanel);

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
}