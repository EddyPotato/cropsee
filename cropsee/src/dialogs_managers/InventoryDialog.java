package dialogs_managers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import ui_managers.Inventory_Manager;

public class InventoryDialog {
	private JFrame mainFrame;

	public InventoryDialog(JFrame referencedFrame) {
		this.mainFrame = referencedFrame;
	}

	/*--------------------- ADD INVENTORY ---------------------*/
	@SuppressWarnings({ "serial", "unused" })
	public void showAddInventoryDialog() {
		JDialog dialog = new JDialog(mainFrame, "Add Inventory Item", true);
		dialog.setPreferredSize(new Dimension(400, 300));
		JPanel contentPanel = new JPanel(new GridLayout(4, 2, 5, 5)); // (3 FIELDS + 1 SUBMIT) x 2
		contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20)); 

		/*___________________ TEXTFIELDS ___________________*/
		JTextField itemNameField = new JTextField();
		itemNameField.setDocument(new PlainDocument() {
			@Override
			public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
				if (str != null && (getLength() + str.length()) <= 100) {
					super.insertString(offs, str, a);
				}
			}
		});

		JSpinner quantityField = new JSpinner(new SpinnerNumberModel(1, 1, 999999, 1));
		JComboBox<String> conditionField = new JComboBox<>(new String[]{"Good", "Bad"});

		/*___________________ HELPER METHOD (REQUIRED MAKER) ___________________*/
		Border defaultBorder = itemNameField.getBorder();
		Border errorBorder = BorderFactory.createLineBorder(Color.RED);

		Runnable liveValidate = () -> {
			// Validate item name (JTextField)
			itemNameField.setBorder(itemNameField.getText().trim().isEmpty() ? errorBorder : defaultBorder);

			// Validate quantity (JSpinner - must be >= 1)
			int quantity = (int) quantityField.getValue();
			quantityField.setBorder(quantity >= 1 ? defaultBorder : errorBorder);

			// Validate condition (JComboBox - must not be null or empty)
			String selectedCondition = (String) conditionField.getSelectedItem();
			boolean isConditionValid = selectedCondition != null && !selectedCondition.trim().isEmpty();
			conditionField.setBorder(isConditionValid ? defaultBorder : errorBorder); // CONDITION ? TRUE : FALSE
		};

		/*___________________ REQUIRED-MAKER LISTENERS ___________________*/
		itemNameField.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) { liveValidate.run(); }
			public void removeUpdate(DocumentEvent e) { liveValidate.run(); }
			public void changedUpdate(DocumentEvent e) { liveValidate.run(); }
		});
		quantityField.addChangeListener(e -> liveValidate.run());
		conditionField.addItemListener(e -> liveValidate.run());

		/*___________________ ADD ___________________*/
		String[][] labels = {
				{"Item Name: ", "Enter the item's name"}, // text label --- tooltip
				{"Quantity: ", "Enter the quantity of the item"},
				{"Condition: ", "Enter the condition of the item"}
		}; 

		JComponent[] fields = {itemNameField, quantityField, conditionField};

		for(int i = 0; i < labels.length; i++) {
			contentPanel.add(new JLabel(labels[i][0])); // FIXED 1ST COL
			fields[i].setToolTipText(labels[i][1]); // FIXED 2ND COL
			contentPanel.add(fields[i]);
		}

		/*___________________ SUBMIT BUTTTON ___________________*/
		JButton submitButton = new JButton("ADD ITEM");
		submitButton.setFocusPainted(false);

		/*___________________ LISTENER ___________________*/
		submitButton.addActionListener(e -> {
			try {
				/*____________ VALIDATION ____________*/
				boolean hasError = false;

				if (itemNameField.getText().trim().isEmpty()) {
					itemNameField.setBorder(errorBorder);
					hasError = true;
				}

				// Validate quantity (JSpinner - must be >= 1)
				int quantity = (int) quantityField.getValue();
				if (quantity < 1) {
					quantityField.setBorder(errorBorder);
					hasError = true;
				} else {
					quantityField.setBorder(defaultBorder);
				}

				// Validate condition (JComboBox - must not be null or empty)
				Object selectedCondition = conditionField.getSelectedItem();
				if (selectedCondition == null || selectedCondition.toString().trim().isEmpty()) {
					conditionField.setBorder(errorBorder);
					hasError = true;
				} else {
					conditionField.setBorder(defaultBorder);
				}

				if (hasError) {
					JOptionPane.showMessageDialog(dialog, "Please fill in all required fields.", "Input Error", JOptionPane.WARNING_MESSAGE);
				    return;
				};

				Inventory_Manager.addItem(
						itemNameField.getText(),
						quantity,
						(String) conditionField.getSelectedItem()
						);
				dialog.dispose();
			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(dialog, "Invalid format!");
			}
		});

		contentPanel.add(new JLabel()); // SPACE
		contentPanel.add(submitButton);
		dialog.setContentPane(contentPanel);
		dialog.pack();
		dialog.setLocationRelativeTo(mainFrame);
		dialog.setVisible(true);
	}

	/*--------------------- EDIT INVENTORY ---------------------*/
	@SuppressWarnings("unused")
	public void showEditInventoryDialog() {
		int selectedRow = Inventory_Manager.inventoryTable.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(mainFrame, "Please select an item to edit!");
			return;
		}

		// GET VARIABLE FOR REFERENCE
		int itemId = (int) Inventory_Manager.model.getValueAt(selectedRow, 0);
		String name = (String) Inventory_Manager.model.getValueAt(selectedRow, 1);
		int quantity = (int) Inventory_Manager.model.getValueAt(selectedRow, 2);
		String condition = (String) Inventory_Manager.model.getValueAt(selectedRow, 3); // condition is not being used

		/*____________ DIALOG ____________*/
		JDialog dialog = new JDialog(mainFrame, "Edit Inventory Item", true);
		dialog.setPreferredSize(new Dimension(400, 300));
		JPanel contentPanel = new JPanel(new GridLayout(4, 2, 5, 5));
		contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

		// ADD ALREADY MADE VALUES (REFERENCE TO EDIT)
		JTextField itemNameField = new JTextField(name);
		JSpinner quantityField = new JSpinner(new SpinnerNumberModel(quantity, 1, 999999, 1));
		JComboBox<String> conditionField = new JComboBox<>(new String[]{"Good", "Bad"});
		conditionField.setSelectedItem(condition); // Pre-select the current condition

		/*____________ HELPER METHOD ____________*/
		Border defaultBorder = itemNameField.getBorder();
		Border errorBorder = BorderFactory.createLineBorder(Color.RED);

		Runnable liveValidate = () -> {
			// Validate item name
			itemNameField.setBorder(itemNameField.getText().trim().isEmpty() ? errorBorder : defaultBorder);

			// Validate quantity (JSpinner)
			int quantityValue = (int) quantityField.getValue();
			quantityField.setBorder(quantityValue < 1 ? errorBorder : defaultBorder);

			// Validate condition (JComboBox)
			String selectedCondition = (String) conditionField.getSelectedItem();
			conditionField.setBorder(selectedCondition == null || selectedCondition.trim().isEmpty() ? errorBorder : defaultBorder);
		};

		// REQUIRED-MAKER LISTENERS
		itemNameField.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) { liveValidate.run(); }
			public void removeUpdate(DocumentEvent e) { liveValidate.run(); }
			public void changedUpdate(DocumentEvent e) { liveValidate.run(); }
		});

		quantityField.addChangeListener(e -> liveValidate.run());
		conditionField.addItemListener(e -> liveValidate.run());

		/*____________ ADD FIELDS TO DIALOG ____________*/
		contentPanel.add(new JLabel("Item Name:"));
		contentPanel.add(itemNameField);
		contentPanel.add(new JLabel("Quantity:"));
		contentPanel.add(quantityField);
		contentPanel.add(new JLabel("Condition:"));
		contentPanel.add(conditionField);

		/*____________ SUBMIT BUTTON ____________*/
		JButton submitButton = new JButton("Update Item");
		submitButton.setFocusPainted(false);
		contentPanel.add(new JLabel()); // Empty space for alignment
		contentPanel.add(submitButton);

		submitButton.addActionListener(e -> {
			// Validate fields before updating
			liveValidate.run();

			// If validation fails, show error
			if (itemNameField.getBorder().equals(errorBorder) || 
					quantityField.getBorder().equals(errorBorder) || 
					conditionField.getBorder().equals(errorBorder)) {
				JOptionPane.showMessageDialog(dialog, "Please fill in all required fields.", "Input Error", JOptionPane.WARNING_MESSAGE);
				return;
			}

			// Perform update action
			try {
				int newQuantity = (int) quantityField.getValue();
				Inventory_Manager.updateItem(
						itemId,
						itemNameField.getText(),
						newQuantity,
						(String) conditionField.getSelectedItem()
						);
				dialog.dispose();
			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(dialog, "Invalid format!");
			}
		});

		dialog.setContentPane(contentPanel);
		dialog.pack();
		dialog.setLocationRelativeTo(mainFrame);
		dialog.setVisible(true);
	}


	/*--------------------- DELETE INVENTORY ---------------------*/
	public void deleteInventoryItem() {
		int selectedRow = Inventory_Manager.inventoryTable.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(mainFrame, "Please select an item to delete!");
			return;
		}

		int confirm = JOptionPane.showConfirmDialog(mainFrame, 
				"Are you sure you want to delete this item?", "Confirm Delete", JOptionPane.YES_NO_OPTION);

		if (confirm == JOptionPane.YES_OPTION) {
			int itemId = (int) Inventory_Manager.model.getValueAt(selectedRow, 0);
			Inventory_Manager.deleteItem(itemId);
		}
	}
}