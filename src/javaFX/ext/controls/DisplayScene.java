package javaFX.ext.controls;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class DisplayScene {

	public static void display(Scene scene) {
		Stage plotStage = new Stage();
		plotStage.setScene(scene);
		plotStage.show();
	}

}
