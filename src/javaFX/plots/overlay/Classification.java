package javaFX.plots.overlay;

import javaFX.ext.css.CSS;
import javaFX.ext.css.CSS.FontWeight;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class Classification {

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//Classification Markings routines
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// Adds A classification Markings Border Pane (removes one first if it is present)
	public static void add(Scene scene) {
		remove(scene);
		StackPane sp = SceneOverlayManager.getStackPaneOverlay(scene);
		sp.getChildren().add(createClassificationMarkings());		
	}	

	// Removes a Classification Markings Border Pane if it is present
	public static void remove(Scene scene) {
		BorderPane bp = getClassificationMarkingsBorderPane(scene);
		if (bp != null) {
			StackPane sp = SceneOverlayManager.getStackPaneOverlay(scene);
			sp.getChildren().remove(bp);		
		}
	}	

	// Returns true/false whether or not a Classification Border Pane is present (or not)
	public static boolean isPresent(Scene scene) {
		return getClassificationMarkingsBorderPane(scene) != null;
	}

	// returns the Classification Markings Border Pane (if it exists) or null if it does not
	// The Classification Markings Border Pane is (1) a Border Pane and (2) has a Text object "Bottom" and (3) has a Text object "top"
	private static  BorderPane getClassificationMarkingsBorderPane(Scene scene) {
		StackPane sp = SceneOverlayManager.getStackPaneOverlay(scene);
		for (Node node : sp.getChildren()) {
			if (node instanceof BorderPane) {
				BorderPane bp = (BorderPane) node;
				// upper left and lower right set and center is empty
				if (bp.getTop() != null && bp.getBottom() != null && bp.getCenter() == null) return bp;
			}
		}
		return null;
	}	

	// Create BorderPane with Classification Markings at Top Left and at Bottom Right
	private static BorderPane createClassificationMarkings() {
		BorderPane bp = new BorderPane();
		bp.setMouseTransparent(true);
		Text topLeft = createClassificationLabel(" SECRET");
		bp.setTop(topLeft);
		BorderPane.setAlignment(topLeft, Pos.TOP_LEFT);
		Text bottomRight = createClassificationLabel("SECRET ");
		bp.setBottom(bottomRight);
		BorderPane.setAlignment(bottomRight, Pos.BOTTOM_RIGHT);
		return bp;
	}

	// Create an Individual Classification Mark Label
	private static Text createClassificationLabel(String marking) {
		Text text = new Text(marking);
		text.setFill(Color.RED);
		CSS.setFontWeight(text, FontWeight.bold);
		CSS.setFontSize(text, 14.0);
		return text;
	}

}
