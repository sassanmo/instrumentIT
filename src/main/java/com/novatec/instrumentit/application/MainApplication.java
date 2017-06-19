package com.novatec.instrumentit.application;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.novatec.instrumentit.scenecontroller.StartSceneController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApplication extends Application {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		try {
			FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("../scenecontroller/StartScene.fxml"));
			Parent root = loader.load();
			StartSceneController controller = loader.<StartSceneController>getController();
			controller.populateChoiceBox();
			primaryStage.setTitle("INSTRUMENTIT");
			primaryStage.setScene(new Scene(root, 700, 320));
		} catch (IOException e) {
			Logger.getLogger(MainApplication.class.getName()).log(Level.SEVERE, null, e);
		}
		primaryStage.show();
	}

}
