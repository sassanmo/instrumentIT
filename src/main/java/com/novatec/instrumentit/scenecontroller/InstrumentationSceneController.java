package com.novatec.instrumentit.scenecontroller;

import java.io.File;

import com.novatec.instrumentit.properties.StringProperties;
import com.novatec.instrumentit.util.FileUtil;

import javafx.fxml.FXML;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
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

	public InstrumentationSceneController() {
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
					root.getChildren().add(new TreeItem<File>(f));
				}
			}
		}
		return root;
	}

	public void performTreeViewSettings() {
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

}
