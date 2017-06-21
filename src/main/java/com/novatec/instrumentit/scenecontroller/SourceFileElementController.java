package com.novatec.instrumentit.scenecontroller;

import com.novatec.instrumentit.parser.Method;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import lombok.Getter;
import lombok.Setter;

public class SourceFileElementController {
	
	@Getter
	@Setter
	@FXML
	private ChoiceBox<String> strategyChoiceBox;
	
	@Getter
	@Setter
	@FXML
	private Label functionLabel;
	
	@Getter
	@Setter
	@FXML
	private CheckBox instrumentCheckbox;
	
	@Getter
	@Setter
	private Method method;
	
	public void setProperties(Method method) {
		this.method = method;
		this.functionLabel.setText(method.getSignature());
	}
}
