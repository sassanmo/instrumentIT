package com.novatec.instrumentit.scenecontroller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.google.common.base.Optional;
import com.novatec.instrumentit.application.MainApplication;
import com.novatec.instrumentit.codeinjector.ios.MethodSwizzling;
import com.novatec.instrumentit.parser.Method;
import com.novatec.instrumentit.parser.MethodController;
import com.novatec.instrumentit.parser.ios.SwiftKeywords;
import com.novatec.instrumentit.parser.ios.SwiftParser;
import com.novatec.instrumentit.properties.StringProperties;
import com.novatec.instrumentit.util.FileUtil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import lombok.Getter;
import lombok.Setter;

public class InstrumentationSceneController {

	@Getter
	@Setter
	private String projectDirectory;

	@Getter
	@Setter
	private String selectedSystem;

	@FXML
	private TreeView<File> sourceTreeView;
	
	@Getter
	@Setter
	private VBox instrumentatationPane;
	
	@Getter
	@Setter
	@FXML
	private ScrollPane scrollPane;
	
	@Getter
	@Setter
	private MethodController methodController;
	
	@Getter
	@Setter
	private boolean agentFound;
	
	@Getter
	@Setter
	@FXML
	private ChoiceBox<String> instrumentAllChoiceBox;
	
	@Getter
	@Setter
	private ObservableList<String> strategies;
	
	@Getter
	@Setter
	@FXML
	private Button instrumentAllButton;
	
	public InstrumentationSceneController() {
		this.methodController = new MethodController();
		this.instrumentatationPane = new VBox();
	}
	
	public void populateTreeView() {
		this.performTreeViewSettings();
		File directory = new File(this.projectDirectory); 
		sourceTreeView.getRoot().getChildren().add(this.getTreeItems(directory));
	}

	public TreeItem<File> getTreeItems(File directory) {
		TreeItem<File> root = new TreeItem<File>(directory);
		root.setExpanded(true);
		for (File f : directory.listFiles()) {
			System.out.println("Loading " + f.getName());
			if (f.isDirectory() && FileUtil.equalFileExtension(f, StringProperties.FOLDER_EXTENSION)) {
				root.getChildren().add(getTreeItems(f));
			} else {
				if (this.validateSourceFile(f)) {
					SwiftParser swiftParser = new SwiftParser();
					List<Method> parsedMethods = swiftParser.parse(f);
					methodController.mapMethods(f, parsedMethods);
					methodController.mapMethodsToClass(parsedMethods);
					System.out.println( "Path: " + getClass().getResource("/").toExternalForm());
					System.out.println( "Path: " + getClass().getResource("../res/swiftico2.png").toExternalForm());
					Image nodeImage = new Image(getClass().getResource("../res/swiftico2.png").toExternalForm());
					TreeItem<File> treeItem = new TreeItem<File>(f, new ImageView(nodeImage));
					root.getChildren().add(treeItem);
				}
			}
		}
		return root;
	}

	public void performTreeViewSettings() {
		this.strategies = FXCollections.observableArrayList();
		if (this.selectedSystem.equals(StringProperties.IOS_SYSTEM)) {
			for (String strategy : StringProperties.IOS_COLLECTION_STRATEGIES) {
				this.strategies.add(strategy);
			}
		}
		this.instrumentAllChoiceBox.setItems(this.strategies);
		this.instrumentAllChoiceBox.getSelectionModel().selectFirst();
		this.scrollPane.setContent(instrumentatationPane);
		this.scrollPane.setPannable(true);
		this.scrollPane.setFitToHeight(true);
		this.scrollPane.setFitToWidth(true);
		this.sourceTreeView.setCellFactory(new Callback<TreeView<File>, TreeCell<File>>() {
			public TreeCell<File> call(TreeView<File> tv) {
				return new TreeCell<File>() {

					@Override
					protected void updateItem(File item, boolean empty) {
						super.updateItem(item, empty);
						setText((empty || item == null) ? "" : item.getName());
					}

				};
			}
		});
		
		this.sourceTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
				// TODO Auto-generated method stub
				File newFile = newValue.getValue();
				if (validateSourceFile(newFile)) {
					this.instrumentatationPane.getChildren().clear();
					SwiftParser swiftParser = new SwiftParser();
					List<Method> parsedMethods = swiftParser.parse(newFile);
					//methodController.mapMethods(newFile, parsedMethods);
					for (Method method : parsedMethods) {
						this.addMethodElement(method);
					}
				} else {
					this.instrumentatationPane.getChildren().clear();
				}
	      });
		File projectFolder = new File(this.projectDirectory).getParentFile();
		TreeItem<File> root = new TreeItem<File>(projectFolder);
		sourceTreeView.setRoot(root);
		root.setExpanded(true);
		this.sourceTreeView.setShowRoot(false);
	}

	public boolean validateSourceFile(File f) {
		if (selectedSystem.equals(StringProperties.IOS_SYSTEM)) {
			return (FileUtil.equalFileExtension(f, StringProperties.SWIFT_FILE_EXTENSION)
					|| FileUtil.equalFileExtension(f, StringProperties.OBJECTIVEC_FILE_EXTENSION));
		} else {
			// TODO: Android
		}
		return false;
	}
	
	public void addMethodElement(Method method) {
		try {
			FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("../scenecontroller/SourceFileElementPane.fxml"));
			Parent methodView = loader.load();
			SourceFileElementController controller = loader.<SourceFileElementController>getController();
			controller.setProperties(method);
			this.instrumentatationPane.getChildren().add(methodView);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@FXML
	public void instrumentAllButtonClicked() {
		System.out.println("instrumentAllButtonClicked");
		if (this.selectedSystem.equals(StringProperties.IOS_SYSTEM)) {
			if (this.instrumentAllChoiceBox.getSelectionModel().getSelectedItem().equals(StringProperties.IOS_METHOD_SWIZZLING)) {
				MethodSwizzling methodSwizzling = new MethodSwizzling(this.methodController);
				try {
					methodSwizzling.performSwizzling();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}


}
