package javaFX.ext.utility;

import javaFX.ext.css.CSS;
import javaFX.plots.Plot;
import javaFX.plots.overlay.SceneOverlayManager;
import javaFX.plots.zoommanager.ZoomManager;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;

public class FXUtil {

	public static double getHeight(Node node) {
		return getBounds(node).getHeight();
	}

	// Same comments as above
	public static double getWidth(Node node) {
		return getBounds(node).getWidth();
	}
	
	// A node that is part of a scene can provide its own size IF you applyCSS() and do a layout.
	// If it is not already part of a scene you must add it to a Dummy scene
	public static Bounds getBounds(Node node) {
		if (node.getScene() != null) {
			node.getParent().applyCss();
			node.getParent().layout();
			return node.getLayoutBounds();
		}
		// This add the node to a Dummy scene 
		// BUT a node can only be part of one scene at a time so you must be careful to remove it from the Dummy Scene
		// The code below is my solution to the problem... 
		// it may be overkill but I have gotten bitten if I add it to a dummy scene and then add it to its final scene too quickly
		Group parent = new Group(node);
		Scene scene = new Scene(parent);  // create a dummy scene with dummy Group node
		scene.getStylesheets().add(CSS.cssFile);
		parent.applyCss(); 
		parent.layout();
		Bounds bounds = node.getLayoutBounds();
		parent.getChildren().remove(node); // ensure that the node is NOT part of the dummy scene
		return bounds;
	}
	
	public static String removeCharsNotAllowedInAFileName(String fileName) {
		String newFileName = fileName.replace(" ", "_").replace(":", "-")
		.replace("<", "-").replace(">", "-")
		.replace("\\", "").replace("/", "")
		.replace("*", "").replace("?", "").replace("|", "").replace("\"", "");
		return newFileName;
	}
	
	public static void runSafe (Runnable runnable) {
		if (Platform.isFxApplicationThread()) {
			runnable.run();
		}
		else {
			Platform.runLater(runnable);
		}
	}
	
	public static void layoutChart(Scene scene) {
		Plot plot = SceneOverlayManager.getLineChart(scene);
		ZoomManager zoomManager = ZoomManager.get(scene);
		runSafe(()-> zoomManager.restoreChart());
	}

}
