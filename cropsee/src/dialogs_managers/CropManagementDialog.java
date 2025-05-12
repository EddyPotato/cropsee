package dialogs_managers;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import com.toedter.calendar.JDateChooser;

import ui_managers.Crop_Manager;

import java.awt.*;
import java.sql.Date;

public class CropManagementDialog {
	private JFrame mainFrame;

	public CropManagementDialog(JFrame referencedFrame) {
		this.mainFrame = referencedFrame;
	}

	/*----------- ADD CROP -----------*/
	@SuppressWarnings("unused")
	public void showAddCropDialog() {
		JDialog dialog = new JDialog(mainFrame, "Add New Crop", true);
		dialog.setPreferredSize(new Dimension(500, 400));
		JPanel contentPanel = new JPanel(new GridLayout(9, 2, 8, 8));
		contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

		// INPUT FIELDS
		JTextField nameField = createLimitedTextField(100);
		JDateChooser plantingDateField = new JDateChooser();
		JDateChooser harvestDateField = new JDateChooser();

		// Water schedule input components
		JSpinner waterAmountSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 365, 1));
		JComboBox<String> waterUnitCombo = new JComboBox<>(new String[]{"day(s)", "week(s)", "month(s)", "year(s)"});
		JPanel waterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
		waterPanel.add(waterAmountSpinner);
		waterPanel.add(waterUnitCombo);

		// Fertilizer schedule input components
		JSpinner fertAmountSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 365, 1));
		JComboBox<String> fertUnitCombo = new JComboBox<>(new String[]{"day(s)", "week(s)", "month(s)", "year(s)"});
		JPanel fertPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
		fertPanel.add(fertAmountSpinner);
		fertPanel.add(fertUnitCombo);

		// Growth stage and status
		JComboBox<String> growthStageCombo = new JComboBox<>(new String[]{"Seedling", "Vegetative", "Flowering", "Fruiting", "Mature"});
		JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Planted", "Growing", "Ready to Harvest", "Harvested"});

		// Validation Borders
		Border defaultBorder = nameField.getBorder();
		Border errorBorder = BorderFactory.createLineBorder(Color.RED);

		// Helper method for validation
		Runnable validateFields = () -> {
			validateField(nameField, !nameField.getText().trim().isEmpty(), defaultBorder, errorBorder);
			validateField(plantingDateField, plantingDateField.getDate() != null, defaultBorder, errorBorder);
			validateField(harvestDateField, harvestDateField.getDate() != null, defaultBorder, errorBorder);
			validateField(waterUnitCombo, waterUnitCombo.getSelectedIndex() != -1, defaultBorder, errorBorder);
			validateField(fertUnitCombo, fertUnitCombo.getSelectedIndex() != -1, defaultBorder, errorBorder);
			validateField(growthStageCombo, growthStageCombo.getSelectedIndex() != -1, defaultBorder, errorBorder);
			validateField(statusCombo, statusCombo.getSelectedIndex() != -1, defaultBorder, errorBorder);
		};

		// Attach listeners to fields
		attachFieldListeners(nameField, plantingDateField, harvestDateField, waterAmountSpinner, waterUnitCombo, fertAmountSpinner, fertUnitCombo, growthStageCombo, statusCombo, validateFields);

		// Layout
		contentPanel.add(new JLabel("Crop Name:*"));
		contentPanel.add(nameField);
		contentPanel.add(new JLabel("Planting Date:*"));
		contentPanel.add(plantingDateField);
		contentPanel.add(new JLabel("Harvest Date:*"));
		contentPanel.add(harvestDateField);
		contentPanel.add(new JLabel("Water Schedule:*"));
		contentPanel.add(waterPanel);
		contentPanel.add(new JLabel("Fertilizer Schedule:*"));
		contentPanel.add(fertPanel);
		contentPanel.add(new JLabel("Growth Stage:*"));
		contentPanel.add(growthStageCombo);
		contentPanel.add(new JLabel("Status:*"));
		contentPanel.add(statusCombo);

		// Submit Button
		JButton submitButton = new JButton("ADD CROP");
		submitButton.setFocusPainted(false);
		submitButton.addActionListener(e -> {
			validateFields.run();
			if (!isValidForm(nameField, plantingDateField, harvestDateField, waterUnitCombo, fertUnitCombo, growthStageCombo, statusCombo)) {
				JOptionPane.showMessageDialog(dialog, "Please fill in all required fields.");
				return;
			}

			Date plantingDate = new java.sql.Date(plantingDateField.getDate().getTime());
			Date harvestDate = new java.sql.Date(harvestDateField.getDate().getTime());

			if (harvestDate.before(plantingDate)) {
				JOptionPane.showMessageDialog(dialog, "Harvest date cannot be before planting date.");
				return;
			}

			// Collect crop data and add to system
			addCrop(dialog, nameField, plantingDate, harvestDate, waterAmountSpinner, waterUnitCombo, fertAmountSpinner, fertUnitCombo, growthStageCombo, statusCombo);
		});

		contentPanel.add(new JPanel()); // spacer
		contentPanel.add(submitButton);

		dialog.setContentPane(contentPanel);
		dialog.pack();
		dialog.setLocationRelativeTo(mainFrame);
		dialog.setVisible(true);
	}

	/* ---------------- Helper Methods ---------------- */

	// Create a limited text field
	@SuppressWarnings("serial")
	private JTextField createLimitedTextField(int maxLength) {
		JTextField field = new JTextField();
		field.setDocument(new PlainDocument() {
			@Override
			public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
				if (str != null && (getLength() + str.length()) <= maxLength) {
					super.insertString(offs, str, a);
				}
			}
		});
		return field;
	}

	// Attach listeners to all fields
	@SuppressWarnings("unused")
	private void attachFieldListeners(JTextField nameField, JDateChooser plantingDateField, JDateChooser harvestDateField, JSpinner waterAmountSpinner, JComboBox<String> waterUnitCombo, JSpinner fertAmountSpinner, JComboBox<String> fertUnitCombo, JComboBox<String> growthStageCombo, JComboBox<String> statusCombo, Runnable validateFields) {
		nameField.getDocument().addDocumentListener(simpleDocListener(validateFields));
		plantingDateField.getDateEditor().addPropertyChangeListener("date", e -> validateFields.run());
		harvestDateField.getDateEditor().addPropertyChangeListener("date", e -> validateFields.run());
		waterAmountSpinner.addChangeListener(e -> validateFields.run());
		waterUnitCombo.addItemListener(e -> validateFields.run());
		fertAmountSpinner.addChangeListener(e -> validateFields.run());
		fertUnitCombo.addItemListener(e -> validateFields.run());
		growthStageCombo.addItemListener(e -> validateFields.run());
		statusCombo.addItemListener(e -> validateFields.run());
	}

	// Validate form fields
	private boolean isValidForm(JTextField nameField, JDateChooser plantingDateField, JDateChooser harvestDateField, JComboBox<String> waterUnitCombo, JComboBox<String> fertUnitCombo, JComboBox<String> growthStageCombo, JComboBox<String> statusCombo) {
		return !nameField.getText().trim().isEmpty() && plantingDateField.getDate() != null && harvestDateField.getDate() != null &&
				waterUnitCombo.getSelectedIndex() != -1 && fertUnitCombo.getSelectedIndex() != -1 &&
				growthStageCombo.getSelectedIndex() != -1 && statusCombo.getSelectedIndex() != -1;
	}

	// Add crop to the system
	private void addCrop(JDialog dialog, JTextField nameField, Date plantingDate, Date harvestDate, JSpinner waterAmountSpinner, JComboBox<String> waterUnitCombo, JSpinner fertAmountSpinner, JComboBox<String> fertUnitCombo, JComboBox<String> growthStageCombo, JComboBox<String> statusCombo) {
		String cropName = nameField.getText().trim();
		String waterSchedule = waterAmountSpinner.getValue() + " " + waterUnitCombo.getSelectedItem();
		String fertSchedule = fertAmountSpinner.getValue() + " " + fertUnitCombo.getSelectedItem();
		String growthStage = (String) growthStageCombo.getSelectedItem();
		String status = (String) statusCombo.getSelectedItem();

		try {
			Crop_Manager.addCrop(cropName, plantingDate, harvestDate, waterSchedule, fertSchedule, growthStage, status);
			dialog.dispose();
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(dialog, "Error adding crop, please try again.");
		}
	}

	// Simple document listener for text fields
	private DocumentListener simpleDocListener(Runnable onChange) {
		return new DocumentListener() {
			public void insertUpdate(DocumentEvent e) { onChange.run(); }
			public void removeUpdate(DocumentEvent e) { onChange.run(); }
			public void changedUpdate(DocumentEvent e) { onChange.run(); }
		};
	}

	// Validate field
	private void validateField(JComponent comp, boolean isValid, Border defaultB, Border errorB) {
		comp.setBorder(isValid ? defaultB : errorB);
	}

	/*--------------------- EDIT ---------------------*/
	@SuppressWarnings("unused")
	public void showEditCropDialog() {
		int selectedRow = Crop_Manager.cropTable.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(mainFrame, "Please select a crop to edit!");
			return;
		}

		int cropId = (int) Crop_Manager.model.getValueAt(selectedRow, 0);
		String name = (String) Crop_Manager.model.getValueAt(selectedRow, 1);
		Date plantingDate = (Date) Crop_Manager.model.getValueAt(selectedRow, 2);
		Date harvestDate = (Date) Crop_Manager.model.getValueAt(selectedRow, 3);
		String waterSchedule = (String) Crop_Manager.model.getValueAt(selectedRow, 4);
		String fertilizerSchedule = (String) Crop_Manager.model.getValueAt(selectedRow, 5);
		String growthStage = (String) Crop_Manager.model.getValueAt(selectedRow, 6);
		String status = (String) Crop_Manager.model.getValueAt(selectedRow, 7);

		JDialog dialog = new JDialog(mainFrame, "Edit Crop", true);
		dialog.setPreferredSize(new Dimension(500, 400));

		JPanel contentPanel = new JPanel(new GridLayout(9, 2, 5, 5));
		contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

		// Fields
		JTextField nameField = new JTextField(name);
		JDateChooser plantingDateField = new JDateChooser();
		plantingDateField.setDate(plantingDate);

		JDateChooser harvestDateField = new JDateChooser();
		harvestDateField.setDate(harvestDate);

		// Parse water schedule safely
		int waterAmount = 1;
		String waterUnit = "day(s)";
		if (waterSchedule != null && !waterSchedule.trim().isEmpty() && waterSchedule.contains(" ")) {
			try {
				String[] waterParts = waterSchedule.trim().split(" ");
				waterAmount = Integer.parseInt(waterParts[0]);
				waterUnit = waterParts[1];
			} catch (Exception e) {
				e.printStackTrace(); // optional: show dialog
			}
		}

		// Parse fertilizer schedule safely
		int fertAmount = 1;
		String fertUnit = "day(s)";
		if (fertilizerSchedule != null && !fertilizerSchedule.trim().isEmpty() && fertilizerSchedule.contains(" ")) {
			try {
				String[] fertParts = fertilizerSchedule.trim().split(" ");
				fertAmount = Integer.parseInt(fertParts[0]);
				fertUnit = fertParts[1];
			} catch (Exception e) {
				e.printStackTrace(); // optional: show dialog
			}
		}

		// Spinners and combos
		JSpinner waterAmountSpinner = new JSpinner(new SpinnerNumberModel(waterAmount, 1, 365, 1));
		JComboBox<String> waterUnitCombo = new JComboBox<>(new String[]{"day(s)", "week(s)", "month(s)", "year(s)"});
		waterUnitCombo.setSelectedItem(waterUnit);

		JSpinner fertAmountSpinner = new JSpinner(new SpinnerNumberModel(fertAmount, 1, 365, 1));
		JComboBox<String> fertUnitCombo = new JComboBox<>(new String[]{"day(s)", "week(s)", "month(s)", "year(s)"});
		fertUnitCombo.setSelectedItem(fertUnit);

		JPanel waterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
		waterPanel.add(waterAmountSpinner);
		waterPanel.add(waterUnitCombo);

		JPanel fertPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
		fertPanel.add(fertAmountSpinner);
		fertPanel.add(fertUnitCombo);

		JComboBox<String> growthStageCombo = new JComboBox<>(new String[]{"Seedling", "Vegetative", "Flowering", "Fruiting", "Mature"});
		growthStageCombo.setSelectedItem(growthStage);

		JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Planted", "Growing", "Ready to Harvest", "Harvested"});
		statusCombo.setSelectedItem(status);

		// Add to panel
		contentPanel.add(new JLabel("Crop Name:"));
		contentPanel.add(nameField);
		contentPanel.add(new JLabel("Planting Date:"));
		contentPanel.add(plantingDateField);
		contentPanel.add(new JLabel("Harvest Date:"));
		contentPanel.add(harvestDateField);
		contentPanel.add(new JLabel("Water Schedule:"));
		contentPanel.add(waterPanel);
		contentPanel.add(new JLabel("Fertilizer Schedule:"));
		contentPanel.add(fertPanel);
		contentPanel.add(new JLabel("Growth Stage:"));
		contentPanel.add(growthStageCombo);
		contentPanel.add(new JLabel("Status:"));
		contentPanel.add(statusCombo);

		// Submit
		JButton submitButton = new JButton("Update Crop");
		submitButton.setFocusPainted(false);

		submitButton.addActionListener(e -> {
			try {
				// Validation
				if (nameField.getText().trim().isEmpty()) {
					JOptionPane.showMessageDialog(dialog, "Crop name must not be empty.");
					return;
				}

				if (plantingDateField.getDate() == null) {
					JOptionPane.showMessageDialog(dialog, "Please select a valid planting date.");
					return;
				}

				if (harvestDateField.getDate() != null && harvestDateField.getDate().before(plantingDateField.getDate())) {
					JOptionPane.showMessageDialog(dialog, "Harvest date cannot be before planting date.");
					return;
				}

				Date updatedPlantingDate = new Date(plantingDateField.getDate().getTime());
				Date updatedHarvestDate = harvestDateField.getDate() != null
						? new Date(harvestDateField.getDate().getTime())
								: null;

				String updatedWaterSchedule = waterAmountSpinner.getValue() + " " + waterUnitCombo.getSelectedItem();
				String updatedFertSchedule = fertAmountSpinner.getValue() + " " + fertUnitCombo.getSelectedItem();

				Crop_Manager.updateCrop(
						cropId,
						nameField.getText().trim(),
						updatedPlantingDate,
						updatedHarvestDate,
						updatedWaterSchedule,
						updatedFertSchedule,
						(String) growthStageCombo.getSelectedItem(),
						(String) statusCombo.getSelectedItem()
						);

				dialog.dispose();
			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(dialog, "Please fill in all required fields correctly.");
			}
		});

		contentPanel.add(new JPanel());
		contentPanel.add(submitButton);

		dialog.setContentPane(contentPanel);
		dialog.pack();
		dialog.setLocationRelativeTo(mainFrame);
		dialog.setVisible(true);
	}

	/*--------------------- DELETE ---------------------*/
	public void deleteSelectedCrop() {
		int selectedRow = Crop_Manager.cropTable.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(mainFrame, "Please select a crop to delete!");
			return;
		}
		int confirm = JOptionPane.showConfirmDialog(mainFrame, 
				"Are you sure you want to delete this crop?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
		if (confirm == JOptionPane.YES_OPTION) {
			int cropId = (int) Crop_Manager.model.getValueAt(selectedRow, 0);
			Crop_Manager.deleteCrop(cropId);
		}
	}
}
