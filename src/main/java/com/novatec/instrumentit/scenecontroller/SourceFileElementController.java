package com.novatec.instrumentit.scenecontroller;

import com.novatec.instrumentit.parser.Method;
import com.novatec.instrumentit.parser.ios.SwiftKeywords;
import com.novatec.instrumentit.properties.StringProperties;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
	private ObservableList<String> strategies;
	
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
	
	public SourceFileElementController() {
		this.strategies = FXCollections.observableArrayList();
	}
	
	public void setProperties(Method method) {
		this.method = method;
		this.functionLabel.setText(method.getSignature());
		this.strategies = FXCollections.observableArrayList();
		if (this.method.getLanguage().equals(SwiftKeywords.LANGUAGE)) {
			for (String strategy : StringProperties.IOS_COLLECTION_STRATEGIES) {
				this.strategies.add(strategy);
			}
		}
		this.strategyChoiceBox.setItems(this.strategies);
		this.strategyChoiceBox.getSelectionModel().selectFirst();
	}
}
