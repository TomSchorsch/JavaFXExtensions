package javaFX.ext.controls;

import javaFX.ext.utility.FXUtil;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DisplayScene {


	public static Stage display(final Scene scene) {
		final Stage stage = new Stage();
		FXUtil.runSafe(() -> displayLater(stage, scene));
		return stage;
	}
	
	private static Stage displayLater(Stage stage, Scene scene) {
		stage.setScene(scene);
		stage.show();
		return stage;
	}

}
