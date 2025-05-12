package buttonFunctions;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import com.toedter.calendar.JDateChooser;

import dataManagers.CropDataManager;

import java.awt.*;
import java.sql.Date;

public class CropButtonActions {
	/*========================================== CLASS-LEVEL ==========================================*/
	private JFrame mainFrame;
	public CropButtonActions(JFrame referencedFrame) {
		this.mainFrame = referencedFrame;
	}

	/*========================================== ADD DIALOG ==========================================*/
	/*========================================== ADD DIALOG ==========================================*/
	/*========================================== ADD DIALOG ==========================================*/
	public void showAddCropDialog() {
		/*_______________________________ CREATE DIALOG _______________________________*/
		JDialog dialog = new JDialog(mainFrame, "Add New Crop", true);
		dialog.setPreferredSize(new Dimension(500, 400));

		/*_______________________________ CREATE CONTENT PANEL _______________________________*/
		JPanel contentPanel = new JPanel(new GridLayout(9, 2, 8, 8));
		contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

		/*_______________________________ TEXT FIELDS _______________________________*/
		JTextField nameField = createLimitedTextField(100); // LIMITED TO 100 CHARACTERS
		JDateChooser plantingDateField = new JDateChooser();
		JDateChooser harvestDateField = new JDateChooser();

		/*_______________________________ WATER _______________________________*/
		JSpinner waterAmountSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 365, 1));
		JComboBox<String> waterUnitCombo = new JComboBox<>(new String[]{"day(s)", "week(s)", "month(s)", "year(s)"});
		JPanel waterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
		waterPanel.add(waterAmountSpinner);
		waterPanel.add(waterUnitCombo);

		/*_______________________________ FERTILIZER _______________________________*/
		JSpinner fertAmountSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 365, 1));
		JComboBox<String> fertUnitCombo = new JComboBox<>(new String[]{"day(s)", "week(s)", "month(s)", "year(s)"});
		JPanel fertPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
		fertPanel.add(fertAmountSpinner);
		fertPanel.add(fertUnitCombo);

		/*_______________________________ GROWTH AND STATUS _______________________________*/
		JComboBox<String> growthStageCombo = new JComboBox<>(new String[]{"Seedling", "Vegetative", "Flowering", "Fruiting", "Mature"});
		JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Planted", "Growing", "Ready to Harvest", "Harvested"});

		/*_______________________________ VALIDATION _______________________________*/
		Border defaultBorder = nameField.getBorder();
		Border errorBorder = BorderFactory.createLineBorder(Color.RED);

		Runnable validateFields = () -> {
			validateField(nameField, !nameField.getText().trim().isEmpty(), defaultBorder, errorBorder);
			validateField(plantingDateField, plantingDateField.getDate() != null, defaultBorder, errorBorder);
			validateField(harvestDateField, harvestDateField.getDate() != null, defaultBorder, errorBorder);
			validateField(waterUnitCombo, waterUnitCombo.getSelectedIndex() != -1, defaultBorder, errorBorder);
			validateField(fertUnitCombo, fertUnitCombo.getSelectedIndex() != -1, defaultBorder, errorBorder);
			validateField(growthStageCombo, growthStageCombo.getSelectedIndex() != -1, defaultBorder, errorBorder);
			validateField(statusCombo, statusCombo.getSelectedIndex() != -1, defaultBorder, errorBorder);
		};

		attachFieldListeners(nameField, plantingDateField, harvestDateField, waterAmountSpinner, waterUnitCombo, fertAmountSpinner, fertUnitCombo, growthStageCombo, statusCombo, validateFields);

		/*_______________________________ INSERTION TO CONTENT PANEL _______________________________*/
		String[][] labels = {
				{"Crop Name:", "Enter crop name"},
				{"Planting Date:", "Select planting date (MMM dd, yyyy)"},
				{"Harvest Date:", "Select harvest date (MMM dd, yyyy)"},
				{"Water Schedule:", "Enter water schedule"},
				{"Fertilizer Schedule:", "Enter fertilizer schedule"},
				{"Growth Stage:", "Select growth stage"},
				{"Status:", "Select crop status"}
		};

		JComponent[] fields = {
				nameField, 
				plantingDateField, 
				harvestDateField, 
				waterPanel, 
				fertPanel, 
				growthStageCombo, 
				statusCombo
		};

		for (int i = 0; i < labels.length; i++) {
			contentPanel.add(new JLabel(labels[i][0]));
			fields[i].setToolTipText(labels[i][1]);
			contentPanel.add(fields[i]);

		}

		/*_______________________________ SUBMIT BUTTON _______________________________*/
		JButton submitButton = new JButton("ADD CROP");
		submitButton.setFocusPainted(false);

		/*_______________________________ ACTION _______________________________*/
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

			/*_______________________________ ADD CROP _______________________________*/
			addCrop(dialog, nameField, plantingDate, harvestDate, waterAmountSpinner, waterUnitCombo, fertAmountSpinner, fertUnitCombo, growthStageCombo, statusCombo);
		});

		contentPanel.add(new JPanel()); // spacer
		contentPanel.add(submitButton);

		dialog.setContentPane(contentPanel);
		dialog.pack();
		dialog.setLocationRelativeTo(mainFrame);
		dialog.setVisible(true);
	}

	/*========================================== HELPER METHODS ==========================================*/
	/*_______________________________ LIMITED TEXT FIELD _______________________________*/
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

	/*_______________________________ LISTENERS _______________________________*/
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

	/*_______________________________ VALIDATION _______________________________*/
	private boolean isValidForm(JTextField nameField, JDateChooser plantingDateField, JDateChooser harvestDateField, JComboBox<String> waterUnitCombo, JComboBox<String> fertUnitCombo, JComboBox<String> growthStageCombo, JComboBox<String> statusCombo) {
		return !nameField.getText().trim().isEmpty() && plantingDateField.getDate() != null && harvestDateField.getDate() != null &&
				waterUnitCombo.getSelectedIndex() != -1 && fertUnitCombo.getSelectedIndex() != -1 &&
				growthStageCombo.getSelectedIndex() != -1 && statusCombo.getSelectedIndex() != -1;
	}

	/*_______________________________ ADD CROP _______________________________*/
	private void addCrop(JDialog dialog, JTextField nameField, Date plantingDate, Date harvestDate, JSpinner waterAmountSpinner, JComboBox<String> waterUnitCombo, JSpinner fertAmountSpinner, JComboBox<String> fertUnitCombo, JComboBox<String> growthStageCombo, JComboBox<String> statusCombo) {
		String cropName = nameField.getText().trim();
		String waterSchedule = waterAmountSpinner.getValue() + " " + waterUnitCombo.getSelectedItem();
		String fertSchedule = fertAmountSpinner.getValue() + " " + fertUnitCombo.getSelectedItem();
		String growthStage = (String) growthStageCombo.getSelectedItem();
		String status = (String) statusCombo.getSelectedItem();

		try {
			CropDataManager.addCrop(cropName, plantingDate, harvestDate, waterSchedule, fertSchedule, growthStage, status);
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
	public void showEditCropDialog() {
		/*_______________________________ CHECK SELECTED ROW _______________________________*/
		int selectedRow = CropDataManager.cropTable.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(mainFrame, "Please select a crop to edit!");
			return;
		}

		/*_______________________________ GET SELECTED CROP _______________________________*/
		int cropId = (int) CropDataManager.model.getValueAt(selectedRow, 0);
		String name = (String) CropDataManager.model.getValueAt(selectedRow, 1);
		Date plantingDate = (Date) CropDataManager.model.getValueAt(selectedRow, 2);
		Date harvestDate = (Date) CropDataManager.model.getValueAt(selectedRow, 3);
		String waterSchedule = (String) CropDataManager.model.getValueAt(selectedRow, 4);
		String fertilizerSchedule = (String) CropDataManager.model.getValueAt(selectedRow, 5);
		String growthStage = (String) CropDataManager.model.getValueAt(selectedRow, 6);
		String status = (String) CropDataManager.model.getValueAt(selectedRow, 7);

		/*_______________________________ CREATE DIALOG _______________________________*/
		JDialog dialog = new JDialog(mainFrame, "Edit Crop", true);
		dialog.setPreferredSize(new Dimension(500, 400));

		/*_______________________________ CREATE CONTENT PANEL _______________________________*/
		JPanel contentPanel = new JPanel(new GridLayout(9, 2, 5, 5));
		contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

		/*____________ TEXTFIELD ____________*/
		/*____________ TEXTFIELD ____________*/
		/*____________ TEXTFIELD ____________*/
		JTextField nameField = new JTextField(name);

		/*____________ DATE PICKER ____________*/
		JDateChooser plantingDateField = new JDateChooser();
		plantingDateField.setDate(plantingDate);
		JDateChooser harvestDateField = new JDateChooser();
		harvestDateField.setDate(harvestDate);

		/*____________ WATER PARSING ____________*/
		int waterAmount = 1;
		String waterUnit = "day(s)";
		if (waterSchedule != null && !waterSchedule.trim().isEmpty() && waterSchedule.contains(" ")) {
			try {
				String[] waterParts = waterSchedule.trim().split(" ");
				waterAmount = Integer.parseInt(waterParts[0]);
				waterUnit = waterParts[1];
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/*____________ FERTILIZER PARSING ____________*/
		int fertAmount = 1;
		String fertUnit = "day(s)";
		if (fertilizerSchedule != null && !fertilizerSchedule.trim().isEmpty() && fertilizerSchedule.contains(" ")) {
			try {
				String[] fertParts = fertilizerSchedule.trim().split(" ");
				fertAmount = Integer.parseInt(fertParts[0]);
				fertUnit = fertParts[1];
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/*_______________________________ WATER _______________________________*/
		JSpinner waterAmountSpinner = new JSpinner(new SpinnerNumberModel(waterAmount, 1, 365, 1));
		JComboBox<String> waterUnitCombo = new JComboBox<>(new String[]{"day(s)", "week(s)", "month(s)", "year(s)"});
		waterUnitCombo.setSelectedItem(waterUnit);

		JPanel waterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
		waterPanel.add(waterAmountSpinner);
		waterPanel.add(waterUnitCombo);

		/*_______________________________ FERTILIZER _______________________________*/
		JSpinner fertAmountSpinner = new JSpinner(new SpinnerNumberModel(fertAmount, 1, 365, 1));
		JComboBox<String> fertUnitCombo = new JComboBox<>(new String[]{"day(s)", "week(s)", "month(s)", "year(s)"});
		fertUnitCombo.setSelectedItem(fertUnit);

		JPanel fertPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
		fertPanel.add(fertAmountSpinner);
		fertPanel.add(fertUnitCombo);

		/*_______________________________ GROWTH AND STATUS _______________________________*/
		JComboBox<String> growthStageCombo = new JComboBox<>(new String[]{"Seedling", "Vegetative", "Flowering", "Fruiting", "Mature"});
		growthStageCombo.setSelectedItem(growthStage);
		JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Planted", "Growing", "Ready to Harvest", "Harvested"});
		statusCombo.setSelectedItem(status);

		/*_______________________________ HELPER METHODS _______________________________*/
		Border defaultBorder = nameField.getBorder();
		Border errorBorder = BorderFactory.createLineBorder(Color.RED);

		Runnable liveValidate = () -> {
			nameField.setBorder(nameField.getText().trim().isEmpty() ? errorBorder : defaultBorder);
			plantingDateField.setBorder(plantingDateField.getDate() == null ? errorBorder : defaultBorder);
			harvestDateField.setBorder(harvestDateField.getDate() == null ? errorBorder : defaultBorder);
			waterAmountSpinner.setBorder(waterUnitCombo.getSelectedIndex() == -1 ? errorBorder : defaultBorder);
			waterUnitCombo.setBorder(waterUnitCombo.getSelectedIndex() == -1 ? errorBorder : defaultBorder);
			fertAmountSpinner.setBorder(fertUnitCombo.getSelectedIndex() == -1 ? errorBorder : defaultBorder);
			fertUnitCombo.setBorder(fertUnitCombo.getSelectedIndex() == -1 ? errorBorder : defaultBorder);
			growthStageCombo.setBorder(growthStageCombo.getSelectedIndex() == -1 ? errorBorder : defaultBorder);
			statusCombo.setBorder(statusCombo.getSelectedIndex() == -1 ? errorBorder : defaultBorder);
		};

		// REQUIRED-MAKER LISTENERS
		nameField.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) { liveValidate.run(); }
			public void removeUpdate(DocumentEvent e) { liveValidate.run(); }
			public void changedUpdate(DocumentEvent e) { liveValidate.run(); }
		});

		plantingDateField.getDateEditor().addPropertyChangeListener("date", e -> liveValidate.run());
		harvestDateField.getDateEditor().addPropertyChangeListener("date", e -> liveValidate.run());
		waterAmountSpinner.addChangeListener(e -> liveValidate.run());
		waterUnitCombo.addItemListener(e -> liveValidate.run());
		fertAmountSpinner.addChangeListener(e -> liveValidate.run());
		fertUnitCombo.addItemListener(e -> liveValidate.run());
		growthStageCombo.addItemListener(e -> liveValidate.run());
		statusCombo.addItemListener(e -> liveValidate.run());

		/*_______________________________ INSERTION TO CONTENT PANEL _______________________________*/
		String[][] labels = {
				{"Crop Name:", "Enter crop name"},
				{"Planting Date:", "Select planting date (MMM dd, yyyy)"},
				{"Harvest Date:", "Select harvest date (MMM dd, yyyy)"},
				{"Water Schedule:", "Enter water schedule"},
				{"Fertilizer Schedule:", "Enter fertilizer schedule"},
				{"Growth Stage:", "Select growth stage"},
				{"Status:", "Select crop status"}
		};

		JComponent[] fields = {
				nameField, 
				plantingDateField, 
				harvestDateField, 
				waterPanel, 
				fertPanel, 
				growthStageCombo, 
				statusCombo
		};

		for (int i = 0; i < labels.length; i++) {
			contentPanel.add(new JLabel(labels[i][0]));
			fields[i].setToolTipText(labels[i][1]);
			contentPanel.add(fields[i]);

		}

		/*_______________________________ SUBMIT BUTTON _______________________________*/
		JButton submitButton = new JButton("Update Crop");
		submitButton.setFocusPainted(false);

		submitButton.addActionListener(e -> {
			try {
				/*_______________________________ VALIDATION _______________________________*/
				boolean hasError = false;
				if (nameField.getText().trim().isEmpty()) {
					nameField.setBorder(errorBorder);
					hasError = true;
				}
				if (plantingDateField.getDate() == null) {
					plantingDateField.setBorder(errorBorder);
					hasError = true;
				}
				if (harvestDateField.getDate() == null) {
					harvestDateField.setBorder(errorBorder);
					hasError = true;
				}
				if (waterUnitCombo.getSelectedIndex() == -1) {
					waterUnitCombo.setBorder(errorBorder);
					hasError = true;
				}
				if (fertUnitCombo.getSelectedIndex() == -1) {
					fertUnitCombo.setBorder(errorBorder);
					hasError = true;
				}
				if (growthStageCombo.getSelectedIndex() == -1) {
					growthStageCombo.setBorder(errorBorder);
					hasError = true;
				}
				if (statusCombo.getSelectedIndex() == -1) {
					statusCombo.setBorder(errorBorder);
					hasError = true;

				}
				if (plantingDateField.getDate() != null && harvestDateField.getDate() != null) {
					if (harvestDateField.getDate().before(plantingDateField.getDate())) {
						JOptionPane.showMessageDialog(dialog, "Harvest date cannot be before planting date.");
						hasError = true;
					}
				}
				if (hasError) throw new IllegalArgumentException("Please fill in all required fields.");


				Date updatedPlantingDate = new Date(plantingDateField.getDate().getTime());
				Date updatedHarvestDate = harvestDateField.getDate() != null
						? new Date(harvestDateField.getDate().getTime())
								: null;

				String updatedWaterSchedule = waterAmountSpinner.getValue() + " " + waterUnitCombo.getSelectedItem();
				String updatedFertSchedule = fertAmountSpinner.getValue() + " " + fertUnitCombo.getSelectedItem();

				/*_______________________________ FUNCTON _______________________________*/
				/*_______________________________ FUNCTON _______________________________*/
				/*_______________________________ FUNCTON _______________________________*/
				CropDataManager.updateCrop(
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

	/*========================================== DELETE DIALOG ==========================================*/
	/*========================================== DELETE DIALOG ==========================================*/
	/*========================================== DELETE DIALOG ==========================================*/
	public void deleteSelectedCrop() {
		int selectedRow = CropDataManager.cropTable.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(mainFrame, "Please select a crop to delete!");
			return;
		}
		int confirm = JOptionPane.showConfirmDialog(mainFrame, 
				"Are you sure you want to delete this crop?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
		if (confirm == JOptionPane.YES_OPTION) {
			int cropId = (int) CropDataManager.model.getValueAt(selectedRow, 0);
			CropDataManager.deleteCrop(cropId);
		}
	}
}
