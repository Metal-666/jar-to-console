package com.metal666.jtc.config.view;

import com.metal666.jtc.Main;
import com.metal666.jtc.MainWindowController;
import com.metal666.jtc.config.ConfigItem;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ConfigController {

	@FXML
	GridPane root;

	@FXML
	TextField nameTextField, paramsTextField;

	@FXML
	Button changeButton, doneButton, cancelButton;

	@FXML
	RadioButton radioButton;

	private ConfigItem item;

	private MainWindowController mainWindowController;

	public void init(@NotNull ConfigItem item, MainWindowController mainWindowController) {

		this.item = item;
		this.mainWindowController = mainWindowController;

		nameTextField.setText(item.name);
		paramsTextField.setText(item.params);

		doneButton.setVisible(false);
		doneButton.setManaged(false);

		cancelButton.setVisible(false);
		cancelButton.setManaged(false);

	}

	@FXML
	private void onChange(ActionEvent event) {

		toggleButtons();
		toggleFields();

	}

	@FXML
	private void onDoneChanging(ActionEvent event) {

		item.name = nameTextField.getText();
		item.params = paramsTextField.getText();

		mainWindowController.save();

		toggleButtons();
		toggleFields();

	}

	@FXML
	private void onCancelChanging(ActionEvent event) {

		nameTextField.setText(item.name);
		paramsTextField.setText(item.params);

		toggleButtons();
		toggleFields();

	}

	@FXML
	private void onDelete(ActionEvent event) {

		mainWindowController.deleteConfig(item);

		((VBox) root.getParent()).getChildren().remove(root);

	}

	@FXML
	private void onSelect(ActionEvent event) {

		mainWindowController.selectConfig(radioButton, item);

	}

	public void setSelected(boolean selected) {

		radioButton.selectedProperty().set(selected);

	}

	private void toggleButtons() {

		changeButton.setVisible(!changeButton.isVisible());
		changeButton.setManaged(!changeButton.isManaged());

		doneButton.setVisible(!doneButton.isVisible());
		doneButton.setManaged(!doneButton.isManaged());

		cancelButton.setVisible(!cancelButton.isVisible());
		cancelButton.setManaged(!cancelButton.isManaged());

	}

	private void toggleFields() {

		nameTextField.setEditable(!nameTextField.isEditable());
		paramsTextField.setEditable(!paramsTextField.isEditable());

	}

}
