package com.novatec.instrumentit.scenecontroller;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.novatec.instrumentit.properties.StringProperties;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class StartSceneController {
	
	@FXML
	private ChoiceBox<String> systemCoiceBox;
	
	private ObservableList<String> systemElements;
	
	@FXML
	private Button projectButton;
	
	@FXML
	private Button continueButton;
	
	@FXML
	private TextField projectPathTextfield;
	
	private String projectFolder;
	
	public StartSceneController() {
		this.systemElements = FXCollections.observableArrayList();
		systemElements.add(StringProperties.IOS_SYSTEM);
		systemElements.add(StringProperties.ANDROID_SYSTEM);
	}
	
	@FXML
	public void projectButtonClicked() {
		FileChooser chooser = new FileChooser();
		if (this.systemCoiceBox.getSelectionModel().getSelectedItem().equals(StringProperties.IOS_SYSTEM)) {
			chooser.setTitle("Select iOS Project");
			chooser.getExtensionFilters().add(new ExtensionFilter("iOS Project (*.xcodeproj)", "*.xcodeproj"));
		} else if (this.systemCoiceBox.getSelectionModel().getSelectedItem().equals(StringProperties.ANDROID_SYSTEM)) {
			// TODO: Android Project Selection
		}
		File file = chooser.showOpenDialog(projectButton.getScene().getWindow());
	    if (file != null) {
	    	this.projectPathTextfield.setText(file.getPath());
        }
	}
	
	@FXML
	public void continueButtonClicked() {
		File file = new File(this.projectPathTextfield.getText());
		if (file.exists()){
			file = file.getParentFile();
			this.projectFolder = file.getPath();
			this.openInstrumentationScene();
		}
	}
	
	public void openInstrumentationScene() {
		try {
			FXMLLoader loader = new FXMLLoader(StartSceneController.class.getResource("InstrumentationScene.fxml"));
			Parent instrumentationScene = loader.load();
			InstrumentationSceneController controller = loader.<InstrumentationSceneController>getController();
			controller.setProjectDirectory(this.projectFolder);
			controller.setSelectedSystem(this.systemCoiceBox.getSelectionModel().getSelectedItem());
			controller.populateTreeView();
			Stage primaryStage = (Stage) projectButton.getScene().getWindow();
			primaryStage.setTitle("INSTRUMENTIT");
			primaryStage.setScene(new Scene(instrumentationScene));
		} catch (IOException e) {
			Logger.getLogger(StartSceneController.class.getName()).log(Level.SEVERE, null, e);
		}
	}
	
	public void populateChoiceBox() {
		if (this.systemCoiceBox != null) {
			this.systemCoiceBox.setItems(this.systemElements);
			this.systemCoiceBox.getSelectionModel().selectFirst();
		}
	}
	
}
