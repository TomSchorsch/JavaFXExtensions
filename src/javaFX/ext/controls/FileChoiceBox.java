package javaFX.ext.controls;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Control;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

/*
 * This class is based on the JavaFX choice box with several differences
 * 
 * -- File names are presented for user selection but the file itself is what is returned
 * 
 * -- Files (up to 10, but selectable if you want to change that) are stored in
 * a last used fashion.  If you select a previous file that file will now become 
 * first in the list
 * 
 * -- new files can be added to the list via a fileChooser
 * 
 * -- A file Chooser selected file is then put at the top of the Choicebox list
 * 
 * -- The history (the listed of previously selected files) can be saved and restored but
 * that ability is external to this class 
 * 
 */

public class FileChoiceBox  {
	Stage stage = null;
	FileType fileType;
	HBox panel = null;
	FileChooser fileChooser = new FileChooser();
	DirectoryChooser directoryChooser = new DirectoryChooser();
	Button fileChooserButton = new Button("\u23F7");
	ChoiceBox<String> choiceBox = new ChoiceBox<String>();
	List<File> fileList = new ArrayList<File>();
	static final int MAX = 10;
	int localMax = MAX;
	final String NULL_SELECTION;

	public enum FileType {FILE, DIRECTORY}
	
	
	public FileChoiceBox(Stage stage, FileType fileType) {
		this.stage = stage;
		this.fileType = fileType;
		NULL_SELECTION = "No "+fileType.toString()+" Selected";
		panel = new HBox();
		panel.setMaxWidth(Control.USE_PREF_SIZE);
		panel.setMaxHeight(Control.USE_PREF_SIZE);
		panel.setSpacing(4);
		setChoiceBoxToFileList();
		if (fileType.equals(FileType.FILE)) {
			fileChooserButton.setOnAction((event) ->{ addFile(fileChooser.showOpenDialog(stage)); });
		}
		else {
			fileChooserButton.setOnAction((event) ->{ addFile(directoryChooser.showDialog(stage)); });
		}
		enableChoiceBoxListener();
	}
	
	// example new ExtensionFilter("C2BMC AllMsg files","*AllMsgs.csv")
	public void setFilter(ExtensionFilter filter) {
		if (fileType.equals(FileType.FILE)) {
			fileChooser.getExtensionFilters().add(filter);
		}
		// Directory Choosers cannot be filtered
	}
	public HBox getPane() {
		return panel;
	}
	
	public File getSelected() {
		if (NULL_SELECTION.equals(choiceBox.getValue())) return null;
		int selected = choiceBox.getSelectionModel().getSelectedIndex();
		if (selected == -1) return null;
		return fileList.get((fileList.size()-1)-selected);  // files are selected in reverse order of the list
	}
	
	public void setSelected(File file) {
		addFile(file);
	}
	//  adds a file to the combobox (it is the top file)
	public void addFile(File file) {
		if (file != null) {
			if (fileList.contains(file)) fileList.remove(file);  // moves file to the top
			fileList.add(file);
			trimFiles();
			setChoiceBoxToFileList();
			initializeFileChooser();
		}
	}
	
	// change the default localMax files
	public void setMaxFiles(int max) {
		localMax = max;
		trimFiles();
		setChoiceBoxToFileList();
	}
	
	protected List<File> getFiles() {
		return fileList;
	}
	
	protected void setFiles(List<File> files) {
		this.fileList = files;
		setChoiceBoxToFileList();
		initializeFileChooser();
	}
	
	private void enableChoiceBoxListener() {
		choiceBox.getSelectionModel().selectedItemProperty()
	    .addListener((obs, oldValue, newValue) -> {setChoiceBoxSelection(newValue);});

	}
	
	private void setChoiceBoxSelection(String newValue) {
		File file = getSelected();
		fileList.remove(file);  // remove from list location
		fileList.add(file);		// add to file end (now first file in list)
		setChoiceBoxToFileList();	// update ChoiceBox
		initializeFileChooser();	// update file Chooser
	}
	// make sure the combobox only has at most localMax members
	private void trimFiles() {
		while (fileList.size() > localMax) {
			fileList.remove(0);
		}
	}
	

	private void setChoiceBoxToFileList() {
		removeChildren();
		choiceBox = new ChoiceBox<String>();
		if (fileList.size() == 0) {
			choiceBox.getItems().add(NULL_SELECTION);
	        choiceBox.setValue(NULL_SELECTION);
		}
		else {
			// add files in reverse order
			for (int i = fileList.size()-1; i >= 0; i--) {
				File next = fileList.get(i);
				choiceBox.getItems().add(next.getName());
			}
			// initialize some setting based on the current file list
			File top = getTopFile();
			choiceBox.setValue(top.getName());
		}
		enableChoiceBoxListener();
		addChildren();
	}
	
	private void initializeFileChooser() {
		File top = getTopFile();
		if (top != null ) {
			if (fileType.equals(FileType.FILE)) {
				fileChooser.setInitialDirectory(top.getParentFile());
				fileChooser.setInitialFileName(top.getName());
			}
			else {
				if (top.isDirectory()) {
					directoryChooser.setInitialDirectory(top);
				}
				else {
					directoryChooser.setInitialDirectory(top.getParentFile());
				}
			}
		}
	}
	
	private File getTopFile() {
		if (fileList.size() == 0) return null;
		return fileList.get(fileList.size()-1);
	}
	
	private void addChildren() {
		panel.getChildren().add(choiceBox);
		panel.getChildren().add(fileChooserButton);
	}
	private void removeChildren(){
		panel.getChildren().remove(choiceBox);
		panel.getChildren().remove(fileChooserButton);
	}
}
