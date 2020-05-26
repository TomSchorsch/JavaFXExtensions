package javaFX.plots.overlay;

import javaFX.ext.css.CSS;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class PlotInfo {
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Plot Info routines
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// sets plot information in a BorderPane Overlay with the Plot Info text at the bottom on the left
	public static void setText(Scene scene, String plotInfo) {
		StackPane sp = SceneOverlayManager.getStackPaneOverlay(scene);		// create a new Stack Pane over the scene if needed
		BorderPane bp = getPlotInfoBorderPane(scene);	// get a Plot Info Border Pane if one is already present (null otherwise)
		bp = setBorderPanePlotInfo(bp,plotInfo);		// set (or replace) the Plot info text (on the bottom left of the border pane)
		if (!sp.getChildren().contains(bp)) sp.getChildren().add(bp);	// Add the Border Pane to the overlay Stack Pane (assuming it is not already there)
	}

	public static String getText(Scene scene) {
		BorderPane plotInfo = getPlotInfoBorderPane(scene);
		if (plotInfo == null) return "";
		Text textLabel = (Text)plotInfo.getBottom();
		String text = textLabel.getText();
		return text;
		
	}
	// returns the existing Plot info text or ("") if no such info is present
	public static TextField editText(Scene scene) {
		BorderPane plotInfo = getPlotInfoBorderPane(scene);
		if (plotInfo == null) {
			setText(scene,"");
			plotInfo = getPlotInfoBorderPane(scene);
		}
		final BorderPane plotInfoFinal = plotInfo; 
		Text text = (Text)plotInfoFinal.getBottom();
		final String originalText = text.getText();
		TextField textField = new TextField(text.getText());
		textField.setStyle("-fx-text-fill: gray; -fx-font-size: 10px;");
		CSS.setFontSize(textField, 10.0);
		plotInfoFinal.setBottom(textField);
		BorderPane.setAlignment(textField, Pos.BOTTOM_LEFT);
		textField.textProperty().addListener((observable, oldValue, newValue) -> {
			text.setText(" "+newValue);
		});
		textField.setOnKeyReleased(event -> {
			if (event.getCode() == KeyCode.ENTER){
				plotInfoFinal.setBottom(text);
			}
			else if (event.getCode() == KeyCode.ESCAPE){
				textField.setText(originalText);
				textField.positionCaret(originalText.length());
			}
		});
		textField.setMaxWidth(scene.getWindow().getWidth()*0.4);
		textField.end();
		textField.requestFocus();
		return textField;
	}	

	// returns the Plot info Border Pane (if it exists) or null if it does not
	// The Plot info Border Pane is (1) a Border Pane and (2) has a Text object "Bottom" and (3) null it its "top"
	private static  BorderPane getPlotInfoBorderPane(Scene scene) {
		StackPane sp = SceneOverlayManager.getStackPaneOverlay(scene);
		for (Node node : sp.getChildren()) {
			if (node instanceof BorderPane) {
				BorderPane bp = (BorderPane) node;
				if (bp.getTop() == null && bp.getCenter() == null &&bp.getBottom() != null) return bp;
			}
		}
		return null;
	}	

	// internal routine that sets (or resets) the plot info border text (bottom left)
	private static BorderPane setBorderPanePlotInfo(BorderPane bp, String plotInfo) {
		if (bp == null) bp = new BorderPane();
		bp.setMouseTransparent(true);
		Text bottomLeftText = new Text(" "+plotInfo);
		bottomLeftText.setFill(Color.GRAY);
		CSS.setFontSize(bottomLeftText, 10.0);
		bp.setBottom(bottomLeftText);
		BorderPane.setAlignment(bottomLeftText, Pos.BOTTOM_LEFT);
		return bp;
	}


}
