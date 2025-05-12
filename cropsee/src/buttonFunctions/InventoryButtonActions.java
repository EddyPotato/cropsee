package buttonFunctions;

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

import dataManagers.InventoryDataManager;

public class InventoryButtonActions {
	/*========================================== CLASS-LEVEL ==========================================*/
	private JFrame mainFrame;
	public InventoryButtonActions(JFrame referencedFrame) {
		this.mainFrame = referencedFrame;
	}

	/*========================================== CLASS-LEVEL ==========================================*/
	public void showAddinventoryButtonActions() {
		/*_______________________________ CREATE DIALOG _______________________________*/
		JDialog dialog = new JDialog(mainFrame, "Add Inventory Item", true);
		dialog.setPreferredSize(new Dimension(400, 300));
		
		/*_______________________________ CREATE CONTENT PANEL _______________________________*/
		JPanel contentPanel = new JPanel(new GridLayout(4, 2, 5, 5)); // (3 FIELDS + 1 SUBMIT) x 2
		contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20)); 

		/*_______________________________ TEXT FIELD _______________________________*/
		/*_______________________________ ITEM NAME _______________________________*/
		JTextField itemNameField = new JTextField();
		itemNameField.setDocument(new PlainDocument() {
			@Override
			public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
				if (str != null && (getLength() + str.length()) <= 100) {
					super.insertString(offs, str, a);
				}
			}
		});

		/*_______________________________ QUANTITY _______________________________*/
		JSpinner quantityField = new JSpinner(new SpinnerNumberModel(1, 1, 999999, 1));
		
		/*_______________________________ CONDITION _______________________________*/
		JComboBox<String> conditionField = new JComboBox<>(new String[]{"Good", "Bad"});

		/*_______________________________ HELPER METHOD _______________________________*/
		Border defaultBorder = itemNameField.getBorder();
		Border errorBorder = BorderFactory.createLineBorder(Color.RED);

		Runnable liveValidate = () -> {
			itemNameField.setBorder(itemNameField.getText().trim().isEmpty() ? errorBorder : defaultBorder);
			int quantity = (int) quantityField.getValue();
			quantityField.setBorder(quantity >= 1 ? defaultBorder : errorBorder);
			String selectedCondition = (String) conditionField.getSelectedItem();
			boolean isConditionValid = selectedCondition != null && !selectedCondition.trim().isEmpty();
			conditionField.setBorder(isConditionValid ? defaultBorder : errorBorder); // CONDITION ? TRUE : FALSE
		};

		/*_______________________________ LISTENERS _______________________________*/
		itemNameField.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) { liveValidate.run(); }
			public void removeUpdate(DocumentEvent e) { liveValidate.run(); }
			public void changedUpdate(DocumentEvent e) { liveValidate.run(); }
		});
		quantityField.addChangeListener(e -> liveValidate.run());
		conditionField.addItemListener(e -> liveValidate.run());

		/*_______________________________ INSERT TO CONTENT PANEL _______________________________*/
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

		/*_______________________________ SUBMIT BUTTON _______________________________*/
		JButton submitButton = new JButton("ADD ITEM");
		submitButton.setFocusPainted(false);
		submitButton.addActionListener(e -> {
			try {
				/*_______________________________ VALIDATION _______________________________*/
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
				
				/*_______________________________ FUNCTION _______________________________*/
				/*_______________________________ FUNCTION _______________________________*/
				/*_______________________________ FUNCTION _______________________________*/
				InventoryDataManager.addItem(
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

	/*========================================== EDIT DIALOG ==========================================*/
	/*========================================== EDIT DIALOG ==========================================*/
	/*========================================== EDIT DIALOG ==========================================*/
	public void showEditinventoryButtonActions() {
		/*_______________________________ CHECK SELECTED ITEM _______________________________*/
		int selectedRow = InventoryDataManager.inventoryTable.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(mainFrame, "Please select an item to edit!");
			return;
		}

		/*_______________________________ GET SELECTED ITEM _______________________________*/
		int itemId = (int) InventoryDataManager.model.getValueAt(selectedRow, 0);
		String name = (String) InventoryDataManager.model.getValueAt(selectedRow, 1);
		int quantity = (int) InventoryDataManager.model.getValueAt(selectedRow, 2);
		String condition = (String) InventoryDataManager.model.getValueAt(selectedRow, 3); // condition is not being used

		/*_______________________________ CREATE DIALOG _______________________________*/
		JDialog dialog = new JDialog(mainFrame, "Edit Inventory Item", true);
		dialog.setPreferredSize(new Dimension(400, 300));
		
		/*_______________________________ CREATE CONTENT PANEL _______________________________*/
		JPanel contentPanel = new JPanel(new GridLayout(4, 2, 5, 5));
		contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

		/*_______________________________ TEXT FIELD _______________________________*/
		JTextField itemNameField = new JTextField(name);
		JSpinner quantityField = new JSpinner(new SpinnerNumberModel(quantity, 1, 999999, 1));
		JComboBox<String> conditionField = new JComboBox<>(new String[]{"Good", "Bad"});
		conditionField.setSelectedItem(condition);

		/*_______________________________ HELPER METHOD _______________________________*/
		Border defaultBorder = itemNameField.getBorder();
		Border errorBorder = BorderFactory.createLineBorder(Color.RED);

		Runnable liveValidate = () -> {
			itemNameField.setBorder(itemNameField.getText().trim().isEmpty() ? errorBorder : defaultBorder);
			int quantityValue = (int) quantityField.getValue();
			quantityField.setBorder(quantityValue < 1 ? errorBorder : defaultBorder);
			String selectedCondition = (String) conditionField.getSelectedItem();
			conditionField.setBorder(selectedCondition == null || selectedCondition.trim().isEmpty() ? errorBorder : defaultBorder);
		};

		/*_______________________________ LISTENERS _______________________________*/
		itemNameField.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) { liveValidate.run(); }
			public void removeUpdate(DocumentEvent e) { liveValidate.run(); }
			public void changedUpdate(DocumentEvent e) { liveValidate.run(); }
		});
		quantityField.addChangeListener(e -> liveValidate.run());
		conditionField.addItemListener(e -> liveValidate.run());

		/*_______________________________ INSERT TO CONTENT PANEL _______________________________*/
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

		/*____________ SUBMIT BUTTON ____________*/
		JButton submitButton = new JButton("UPDATE ITEM");
		submitButton.setFocusPainted(false);
		contentPanel.add(new JLabel()); // Empty space for alignment
		contentPanel.add(submitButton);

		submitButton.addActionListener(e -> {
			/*_______________________________ VALIDATION _______________________________*/
			liveValidate.run();
			if (itemNameField.getBorder().equals(errorBorder) || 
					quantityField.getBorder().equals(errorBorder) || 
					conditionField.getBorder().equals(errorBorder)) {
				JOptionPane.showMessageDialog(dialog, "Please fill in all required fields.", "Input Error", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			/*_______________________________ FUNCTION _______________________________*/
			/*_______________________________ FUNCTION _______________________________*/
			/*_______________________________ FUNCTION _______________________________*/
			try {
				int newQuantity = (int) quantityField.getValue();
				InventoryDataManager.updateItem(
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


	/*========================================== DELETE DIALOG ==========================================*/
	/*========================================== DELETE DIALOG ==========================================*/
	/*========================================== DELETE DIALOG ==========================================*/
	public void deleteInventoryItem() {
		/*_______________________________ CHECK SELECTED ITEM _______________________________*/
		int selectedRow = InventoryDataManager.inventoryTable.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(mainFrame, "Please select an item to delete!");
			return;
		}

		/*_______________________________ CREATE DIALOG _______________________________*/
		int confirm = JOptionPane.showConfirmDialog(mainFrame, 
				"Are you sure you want to delete this item?", "Confirm Delete", JOptionPane.YES_NO_OPTION);

		/*_______________________________ CHECK CONFIRMATION _______________________________*/
		if (confirm == JOptionPane.YES_OPTION) {
			int itemId = (int) InventoryDataManager.model.getValueAt(selectedRow, 0);
			
			/*_______________________________ FUNCTION _______________________________*/
			/*_______________________________ FUNCTION _______________________________*/
			/*_______________________________ FUNCTION _______________________________*/
			InventoryDataManager.deleteItem(itemId);
		}
	}
}