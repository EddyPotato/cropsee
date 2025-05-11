package dialogs_managers;

import javax.swing.*;
import com.toedter.calendar.JDateChooser;

import ui_managers.Crop_Manager;

import java.awt.*;
import java.sql.Date;

public class CropManagementDialog {
	private JFrame mainFrame;

	public CropManagementDialog(JFrame mainFrame) {
		this.mainFrame = mainFrame;
	}

	/*________________________ ADD CROP DIALOG ________________________*/
	public void showAddCropDialog() {
		JDialog dialog = new JDialog(mainFrame, "Add New Crop", true);
		dialog.setPreferredSize(new Dimension(500, 500));

		JPanel contentPanel = new JPanel(new GridLayout(9, 2, 5, 5));
		contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

		JTextField nameField = new JTextField();

		JDateChooser plantingDateField = new JDateChooser();
		JDateChooser harvestDateField = new JDateChooser();

		// Water schedule
		JSpinner waterAmountSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 365, 1));
		JComboBox<String> waterUnitCombo = new JComboBox<>(new String[]{"day(s)", "week(s)", "month(s)", "year(s)"});

		// Fertilizer schedule
		JSpinner fertAmountSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 365, 1));
		JComboBox<String> fertUnitCombo = new JComboBox<>(new String[]{"day(s)", "week(s)", "month(s)", "year(s)"});

		// Growth stage and status
		JComboBox<String> growthStageCombo = new JComboBox<>(new String[]{
				"Seedling", "Vegetative", "Flowering", "Fruiting", "Mature"
		});

		JComboBox<String> statusCombo = new JComboBox<>(new String[]{
				"Planted", "Growing", "Ready to Harvest", "Harvested"
		});

		JTextArea notesArea = new JTextArea(3, 20);
		JScrollPane notesScrollPane = new JScrollPane(notesArea);

		// Add components to panel
		contentPanel.add(new JLabel("Crop Name:"));
		contentPanel.add(nameField);

		contentPanel.add(new JLabel("Planting Date:"));
		contentPanel.add(plantingDateField);

		contentPanel.add(new JLabel("Harvest Date:"));
		contentPanel.add(harvestDateField);

		contentPanel.add(new JLabel("Water Schedule:"));
		JPanel waterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
		waterPanel.add(waterAmountSpinner);
		waterPanel.add(waterUnitCombo);
		contentPanel.add(waterPanel);

		contentPanel.add(new JLabel("Fertilizer Schedule:"));
		JPanel fertPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
		fertPanel.add(fertAmountSpinner);
		fertPanel.add(fertUnitCombo);
		contentPanel.add(fertPanel);

		contentPanel.add(new JLabel("Growth Stage:"));
		contentPanel.add(growthStageCombo);

		contentPanel.add(new JLabel("Status:"));
		contentPanel.add(statusCombo);

		contentPanel.add(new JLabel("Notes:"));
		contentPanel.add(notesScrollPane);

		// Submit button
		JButton submitButton = new JButton("Add Crop");
		submitButton.setFocusPainted(false);
		submitButton.addActionListener(e -> {
			try {
				String cropName = nameField.getText().trim();
				Date plantingDate = new java.sql.Date(plantingDateField.getDate().getTime());
				Date harvestDate = new java.sql.Date(harvestDateField.getDate().getTime());

				String waterSchedule = waterAmountSpinner.getValue() + " " + waterUnitCombo.getSelectedItem();
				String fertSchedule = fertAmountSpinner.getValue() + " " + fertUnitCombo.getSelectedItem();

				String growthStage = (String) growthStageCombo.getSelectedItem();
				String status = (String) statusCombo.getSelectedItem();
				String notes = notesArea.getText().trim();

				Crop_Manager.addCrop(
						cropName,
						plantingDate,
						harvestDate,
						waterSchedule,
						fertSchedule,
						growthStage,
						status,
						notes
						);

				dialog.dispose();
			} catch (NullPointerException ex) {
				JOptionPane.showMessageDialog(dialog, "Please fill in all required fields correctly.");
			}
		});

		// Bottom button area
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(submitButton);

		dialog.setLayout(new BorderLayout());
		dialog.add(contentPanel, BorderLayout.CENTER);
		dialog.add(buttonPanel, BorderLayout.SOUTH);

		dialog.pack();
		dialog.setLocationRelativeTo(mainFrame);
		dialog.setVisible(true);
	}

	/*________________________ EDIT CROP DIALOG ________________________*/
	public void showEditCropDialog() {
		int selectedRow = Crop_Manager.cropTable.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(mainFrame, "Please select a crop to edit!");
			return;
		}

		// Get the current crop details from the selected row
		int cropId = (int) Crop_Manager.model.getValueAt(selectedRow, 0);
		String name = (String) Crop_Manager.model.getValueAt(selectedRow, 1);
		Date plantingDate = (Date) Crop_Manager.model.getValueAt(selectedRow, 2);
		Date harvestDate = (Date) Crop_Manager.model.getValueAt(selectedRow, 3);
		String waterSchedule = (String) Crop_Manager.model.getValueAt(selectedRow, 4);
		String fertilizerSchedule = (String) Crop_Manager.model.getValueAt(selectedRow, 5);
		String growthStage = (String) Crop_Manager.model.getValueAt(selectedRow, 6);
		String status = (String) Crop_Manager.model.getValueAt(selectedRow, 7);
		String note = (String) Crop_Manager.model.getValueAt(selectedRow, 8);

		// Create dialog for editing crop details
		JDialog dialog = new JDialog(mainFrame, "Edit Crop", true);
		dialog.setPreferredSize(new Dimension(500, 400));

		JPanel contentPanel = new JPanel(new GridLayout(9, 2, 5, 5));
		contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

		// Fields to edit crop details
		JTextField nameField = new JTextField(name);
		JDateChooser plantingDateField = new JDateChooser();
		plantingDateField.setDate(plantingDate);

		JDateChooser harvestDateField = new JDateChooser();
		harvestDateField.setDate(harvestDate);

		JTextField waterScheduleField = new JTextField(waterSchedule);
		JTextField fertilizerScheduleField = new JTextField(fertilizerSchedule);
		JTextField growthStageField = new JTextField(growthStage);
		JTextField noteField = new JTextField(note);

		JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Planning", "Growing", "Harvested"});
		statusCombo.setSelectedItem(status);

		// Adding components to content panel
		contentPanel.add(new JLabel("Crop Name:"));
		contentPanel.add(nameField);
		contentPanel.add(new JLabel("Planting Date:"));
		contentPanel.add(plantingDateField);
		contentPanel.add(new JLabel("Harvest Date:"));
		contentPanel.add(harvestDateField);
		contentPanel.add(new JLabel("Water Schedule:"));
		contentPanel.add(waterScheduleField);
		contentPanel.add(new JLabel("Fertilizer Schedule:"));
		contentPanel.add(fertilizerScheduleField);
		contentPanel.add(new JLabel("Growth Stage:"));
		contentPanel.add(growthStageField);
		contentPanel.add(new JLabel("Status:"));
		contentPanel.add(statusCombo);
		contentPanel.add(new JLabel("Notes:"));
		contentPanel.add(noteField);

		// Submit button to update crop details
		JButton submitButton = new JButton("Update Crop");
		submitButton.setFocusPainted(false);

		submitButton.addActionListener(e -> {
			try {
				// Convert selected dates to sql.Date format
				java.sql.Date updatedPlantingDate = new java.sql.Date(plantingDateField.getDate().getTime());
				java.sql.Date updatedHarvestDate = harvestDateField.getDate() != null
						? new java.sql.Date(harvestDateField.getDate().getTime())
								: null;

				// Call the update method from Crop_Manager
				Crop_Manager.updateCrop(
						cropId,
						nameField.getText(),
						updatedPlantingDate,
						updatedHarvestDate,
						waterScheduleField.getText(),
						fertilizerScheduleField.getText(),
						growthStageField.getText(),
						(String) statusCombo.getSelectedItem(),
						noteField.getText()
						);

				dialog.dispose(); // Close the dialog after updating
			} catch (NullPointerException ex) {
				JOptionPane.showMessageDialog(dialog, "Please fill in all required fields correctly.");
			}
		});

		contentPanel.add(new JPanel()); // spacer
		contentPanel.add(submitButton);

		dialog.setContentPane(contentPanel);
		dialog.pack();
		dialog.setLocationRelativeTo(mainFrame);
		dialog.setVisible(true);
	}


	/*________________________ DELETE CROP DIALOG ________________________*/
	public void showDeleteCropDialog() {
		int selectedRow = Crop_Manager.cropTable.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(mainFrame, "Please select a crop to delete!");
			return;
		}

		// Get crop details for confirmation
		int cropId = (int) Crop_Manager.model.getValueAt(selectedRow, 0);
		String cropName = (String) Crop_Manager.model.getValueAt(selectedRow, 1);

		// Create confirmation dialog for deleting the selected crop
		JDialog dialog = new JDialog(mainFrame, "Delete Crop", true);
		dialog.setPreferredSize(new Dimension(400, 200));

		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BorderLayout(10, 10));
		contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		JLabel messageLabel = new JLabel("<html><h3>Are you sure you want to delete the crop: <i>" + cropName + "</i>?</h3></html>");
		messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
		contentPanel.add(messageLabel, BorderLayout.CENTER);

		// Buttons for confirming or canceling deletion
		JPanel buttonPanel = new JPanel();
		JButton confirmButton = new JButton("Delete");
		JButton cancelButton = new JButton("Cancel");

		confirmButton.addActionListener(e -> {
			// Proceed with the deletion
			Crop_Manager.deleteCrop(cropId);
			dialog.dispose();
		});

		cancelButton.addActionListener(e -> {
			// Close the dialog without deletion
			dialog.dispose();
		});

		buttonPanel.add(confirmButton);
		buttonPanel.add(cancelButton);
		contentPanel.add(buttonPanel, BorderLayout.SOUTH);

		dialog.setContentPane(contentPanel);
		dialog.pack();
		dialog.setLocationRelativeTo(mainFrame);
		dialog.setVisible(true);
	}
}