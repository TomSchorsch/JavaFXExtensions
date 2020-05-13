package test.controls;
import javaFX.ext.controls.Config;
import javaFX.ext.controls.FileChoiceBox;
import javaFX.ext.css.Instructions;
import javaFX.ext.utility.Logger;
import javafx.scene.control.Control;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import test.FXTester;

public class FileChoiceBoxTest implements FXTester {

	FileChoiceBox fileChoiceBox;
	FileChoiceBox dirChoiceBox;
	Logger logger;
	
	@Override
	public void execute(Logger logger) {
		this.logger = logger;

		dirChoiceBox = new FileChoiceBox(new Stage(), FileChoiceBox.FileType.DIRECTORY);
		fileChoiceBox = new FileChoiceBox(new Stage(), FileChoiceBox.FileType.FILE);
		VBox centerBox = new VBox();
		centerBox.setSpacing(10);
		centerBox.setMaxHeight(Control.USE_PREF_SIZE);
		centerBox.setMaxWidth(Control.USE_PREF_SIZE);
		centerBox.getChildren().addAll(dirChoiceBox.getPane(), fileChoiceBox.getPane());	
		BorderPane borderPane = new BorderPane();
		borderPane.setCenter(centerBox);
		
		restoreConfig();
		
		Stage stage = FXTester.displayResults(borderPane);
		stage.setOnCloseRequest(value -> saveConfig());
		
		Instructions txt = new Instructions(stage.getScene());
		txt.addCenter("Tests the 'FileChoiceBox' control for files and directories");
		txt.add("The top control only allows you top select directories");
		txt.add("The bottom control only allows you top select files");
		txt.add("In each control...");
		txt.add("-- The right button allows you to select a new file/directory");
		txt.add("-- The left button allows you to select from amongst the previously selected files/directories using a pulldown");
		txt.add("---- When you select a previously chosen file/directory that choice becomes the top item in the pulldown");
		txt.add("Both controls will remember previous file / directory choices for when you run this test the next time");
		txt.display();
	}
		
	public void restoreConfig() {
		Config config = new Config(this.getClass().getName(), logger);
		config.load();
		config.restoreConfigs("fileChoiceBox", fileChoiceBox);
		config.restoreConfigs("dirChoiceBox", dirChoiceBox);
	}

	public void saveConfig() {
		Config config = new Config(this.getClass().getName(), logger);
		config.saveConfigs("fileChoiceBox", fileChoiceBox);
		config.saveConfigs("dirChoiceBox", dirChoiceBox);
		config.save();
	}

}

