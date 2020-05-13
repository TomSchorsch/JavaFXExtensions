package javaFX.plots.callouts;

import java.util.HashMap;
import java.util.Map;

import javaFX.ext.controls.Editor;
import javaFX.ext.css.CSS;
import javaFX.ext.css.CSS.FontFamily;
import javaFX.ext.css.CSS.FontStyle;
import javaFX.ext.css.CSS.FontWeight;
import javaFX.ext.utility.FXUtil;
import javaFX.plots.callouts.CallOutSettings.Angle;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class CallOutSettingsSeriesEditor  {

		static Map<CallOut,Editor> map2Editor = new HashMap<CallOut,Editor>();
		// This routine sets up the Editable window
		public static void open(Scene scene, CallOut callOut, CSS css, double screenX, double screenY) {
			Editor editor = map2Editor.get(callOut);
			if (editor != null) {
				System.out.println("Editor already open");
			}
			else {
				editor = new Editor(screenX, screenY, scene);
				editor.show("CallOut Settings", getGridPaneForEditor(scene, callOut), () -> {map2Editor.remove(callOut); return true;});
			}
		}
		
		// Editor for the settings

		ChoiceBox<Double> lineLengthChoiceBox = null;
		ChangeListener<? super Double> lineLengthListener = null; 
		ChoiceBox<Angle> angleChoiceBox = null;
		ChangeListener<? super Angle> angleListener = null; 
		
		
		static final double MAX_CHOICEBOX_SIZE = 100.0;  // set a universal max size for the Choice Boxes that are created below 

		private static GridPane getGridPaneForEditor(Scene scene, CallOut callOut) {

			// set up GridPane for Editor labels and entries, set up spacing between entries and between other elements in the editor
			GridPane gridPane = new GridPane();
			gridPane.setVgap(6);
			gridPane.setHgap(2);
			CSS.setBorderWidth(gridPane, 0,10,10,10);
			CSS.setBorderColor(gridPane, Color.TRANSPARENT);		// needed or the border will have nno size despite setting it below
						
			
			int row = 1;

			////////////////////////////////////////////////////////////////////////////////////////////////////
			// Text rotated 
			////////////////////////////////////////////////////////////////////////////////////////////////////

			{
				Text text = new Text("Changes effect '"+callOut.getName()+"' CallOuts");
				CSS.setFontWeight(text, FontWeight.bold);
				gridPane.add(text, 1, row++, 3, 1); // col, row
			}
			
			addSeparator(gridPane, row++);
			
			{
				RadioButton textRotatedButton = new RadioButton("Text Rotated");
				textRotatedButton.setSelected(callOut.defaultCallOutSettings.getTextRotated());
				textRotatedButton.setMaxWidth(MAX_CHOICEBOX_SIZE*2);
				textRotatedButton.setMinSize(FXUtil.getWidth(textRotatedButton)+30, FXUtil.getHeight(textRotatedButton));
				gridPane.add(textRotatedButton, 1, row++, 3, 1); // col, row
				textRotatedButton.setOnAction((ActionEvent event) -> { 
					callOut.defaultCallOutSettings.setTextRotated(textRotatedButton.isSelected());
					callOut.getData().stream().forEach(data -> callOut.mapData2CallOutSettings.get(data).setTextRotated(textRotatedButton.isSelected()));
					resetLineAndText(callOut);
					resetCallOutLocation(callOut);
				});
			}

			addSeparator(gridPane, row++);

			////////////////////////////////////////////////////////////////////////////////////////////////////
			// angle
			////////////////////////////////////////////////////////////////////////////////////////////////////
			{
				gridPane.add(new Text("Angle"), 1, row); // col, row
				ChoiceBox<Angle> angleChoiceBox = Editor.getEnumEntry(callOut.defaultCallOutSettings.getAngle());
				angleChoiceBox.setMaxSize(MAX_CHOICEBOX_SIZE, Double.MAX_VALUE);

				ChangeListener<? super Angle> angleListener = (observable, oldValue, newValue) -> {
					callOut.defaultCallOutSettings.setAngle((Angle)newValue);
					callOut.getData().stream().forEach(data -> callOut.mapData2CallOutSettings.get(data).setAngle((Angle)newValue));
					resetText(callOut);
					resetLineAndText(callOut);
					resetCallOutLocation(callOut);
				}; 
				angleChoiceBox.getSelectionModel().selectedItemProperty().addListener(angleListener);
				gridPane.add(angleChoiceBox,3,row++);
			}

			////////////////////////////////////////////////////////////////////////////////////////////////////
			// lineLength
			////////////////////////////////////////////////////////////////////////////////////////////////////
			{
				gridPane.add(new Text("Line Length"), 1, row); // col, row
				ChoiceBox<Double>lineLengthChoiceBox = Editor.getDoubleEntry(CallOutSettings.LineLength, callOut.defaultCallOutSettings.getLineLength());
				lineLengthChoiceBox.setMaxSize(MAX_CHOICEBOX_SIZE, Double.MAX_VALUE);
				ChangeListener<? super Double> lineLengthListener = (observable, oldValue, newValue) -> {
					callOut.defaultCallOutSettings.setLineLength((Double)newValue);
					callOut.getData().stream().forEach(data -> callOut.mapData2CallOutSettings.get(data).setLineLength((Double)newValue));
					resetLineAndText(callOut);
					resetCallOutLocation(callOut);
				};
				lineLengthChoiceBox.getSelectionModel().selectedItemProperty().addListener(lineLengthListener);
				gridPane.add(lineLengthChoiceBox,3,row++);
			}	

			////////////////////////////////////////////////////////////////////////////////////////////////////
			// lineWidth
			////////////////////////////////////////////////////////////////////////////////////////////////////
			{
				gridPane.add(new Text("Line Width"), 1, row); // col, row
				ChoiceBox<Double> choiceBox = Editor.getDoubleEntry(CallOutSettings.LineWidth, callOut.defaultCallOutSettings.getLineWidth());
				choiceBox.setMaxSize(MAX_CHOICEBOX_SIZE, Double.MAX_VALUE);
				choiceBox.getSelectionModel().selectedItemProperty().addListener(
						(observable, oldValue, newValue) -> {
							callOut.defaultCallOutSettings.setLineWidth((Double)newValue);
							callOut.getData().stream().forEach(data -> callOut.mapData2CallOutSettings.get(data).setLineWidth((Double)newValue));
							resetLineAndText(callOut);
						});
				gridPane.add(choiceBox,3,row++);
			}	

			////////////////////////////////////////////////////////////////////////////////////////////////////
			// lineColor
			////////////////////////////////////////////////////////////////////////////////////////////////////
			{
				gridPane.add(new Text("Line Color"), 1, row); // col, row
				ColorPicker colorPicker = Editor.getColorPicker(callOut.defaultCallOutSettings.getLineColor());
				colorPicker.setMaxSize(MAX_CHOICEBOX_SIZE, Double.MAX_VALUE);
				colorPicker.setOnAction(event -> {
					callOut.defaultCallOutSettings.setLineColor(colorPicker.getValue());
					callOut.getData().stream().forEach(data -> callOut.mapData2CallOutSettings.get(data).setLineColor(colorPicker.getValue()));
					resetLineAndText(callOut);
				});
				gridPane.add(colorPicker,3,row++);
			}
			
			addSeparator(gridPane, row++);

			////////////////////////////////////////////////////////////////////////////////////////////////////
			// fontSize
			////////////////////////////////////////////////////////////////////////////////////////////////////
			{
				gridPane.add(new Text("Font Size"), 1, row); // col, row
				ChoiceBox<Double> choiceBox = Editor.getDoubleEntry(CallOutSettings.FontSize, callOut.defaultCallOutSettings.getFontSize());
				choiceBox.setMaxSize(MAX_CHOICEBOX_SIZE, Double.MAX_VALUE);
				choiceBox.getSelectionModel().selectedItemProperty().addListener(
						(observable, oldValue, newValue) -> {
							callOut.defaultCallOutSettings.setFontSize((Double)newValue);
							callOut.getData().stream().forEach(data -> callOut.mapData2CallOutSettings.get(data).setFontSize((Double)newValue));
							resetText(callOut);
							resetCallOutLocation(callOut);
						});
				gridPane.add(choiceBox,3,row++);
			}			

			////////////////////////////////////////////////////////////////////////////////////////////////////
			// FontColor
			////////////////////////////////////////////////////////////////////////////////////////////////////
			{
				gridPane.add(new Text("Font Color"), 1, row); // col, row
				ColorPicker colorPicker = Editor.getColorPicker(callOut.defaultCallOutSettings.getFontColor());
				colorPicker.setMaxSize(MAX_CHOICEBOX_SIZE, Double.MAX_VALUE);
				colorPicker.setOnAction(event -> {
					callOut.defaultCallOutSettings.setFontColor(colorPicker.getValue());
					callOut.getData().stream().forEach(data -> callOut.mapData2CallOutSettings.get(data).setFontColor(colorPicker.getValue()));
					resetText(callOut);
				});
				gridPane.add(colorPicker,3,row++);
			}

			////////////////////////////////////////////////////////////////////////////////////////////////////
			// fontStyle
			////////////////////////////////////////////////////////////////////////////////////////////////////
			{
				gridPane.add(new Text("Font Style"), 1, row); // col, row
				ChoiceBox<FontStyle> choiceBox = Editor.getEnumEntry(callOut.defaultCallOutSettings.getFontStyle());
				choiceBox.setMaxSize(MAX_CHOICEBOX_SIZE, Double.MAX_VALUE);
				choiceBox.getSelectionModel().selectedItemProperty().addListener(
						(observable, oldValue, newValue) -> {
							callOut.defaultCallOutSettings.setFontStyle((FontStyle)newValue);
							callOut.getData().stream().forEach(data -> callOut.mapData2CallOutSettings.get(data).setFontStyle((FontStyle)newValue));
							resetText(callOut);
						});
				gridPane.add(choiceBox,3,row++);
			}

			////////////////////////////////////////////////////////////////////////////////////////////////////
			// fontWeight
			////////////////////////////////////////////////////////////////////////////////////////////////////
			{
				gridPane.add(new Text("Font Weight"), 1, row); // col, row
				ChoiceBox<FontWeight> choiceBox = Editor.getEnumEntry(callOut.defaultCallOutSettings.getFontWeight());
				choiceBox.setMaxSize(MAX_CHOICEBOX_SIZE, Double.MAX_VALUE);
				choiceBox.getSelectionModel().selectedItemProperty().addListener(
						(observable, oldValue, newValue) -> {
							callOut.defaultCallOutSettings.setFontWeight((FontWeight)newValue);
							callOut.getData().stream().forEach(data -> callOut.mapData2CallOutSettings.get(data).setFontWeight((FontWeight)newValue));
							resetText(callOut);
						});
				gridPane.add(choiceBox,3,row++);
			}

			////////////////////////////////////////////////////////////////////////////////////////////////////
			// fontFamily
			////////////////////////////////////////////////////////////////////////////////////////////////////
			{
				gridPane.add(new Text("Font Family"), 1, row); // col, row
				ChoiceBox<FontFamily> choiceBox = Editor.getEnumEntry(callOut.defaultCallOutSettings.getFontFamily());
				choiceBox.setMaxSize(MAX_CHOICEBOX_SIZE, Double.MAX_VALUE);
				choiceBox.getSelectionModel().selectedItemProperty().addListener(
						(observable, oldValue, newValue) -> {
							callOut.defaultCallOutSettings.setFontFamily((FontFamily)newValue);
							callOut.getData().stream().forEach(data -> callOut.mapData2CallOutSettings.get(data).setFontFamily((FontFamily)newValue));
							resetText(callOut);
						});
				gridPane.add(choiceBox,3,row++);
			}
			
			addSeparator(gridPane, row++);

			return gridPane;
		}
		
		private static void resetText(CallOut callOut) {
			callOut.getData().stream().forEach(data -> callOut.setCalloutTextProperties(CallOut.getText(data),callOut.mapData2CallOutSettings.get(data)));
		}
		
		private static void resetLineAndText(CallOut callOut) {
			callOut.getData().stream().forEach(data -> callOut.setCallOutLineAndPositioningProperties(CallOut.getText(data),callOut.mapData2CallOutSettings.get(data)));				
		}

		private static void resetCallOutLocation(CallOut callOut) {
			callOut.getData().stream().forEach(data -> callOut.setCallOutDataLocation((Group)data.getNode(),callOut.mapData2CallOutSettings.get(data)));
			callOut.callOutSeries.getNode().getParent().getParent().getParent().requestLayout();   // needed because it won't relayout otherwise
		}
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		// The following method sets the editor values when the CallOut has been moved by the Mouse
		////////////////////////////////////////////////////////////////////////////////////////////////////

		public void setEditorData() {
//			if (editor != null) {
//				xTextField.setText(getData().getXValue().toString());
//				yTextField.setText(getData().getYValue().toString());
//
//				lineLengthChoiceBox.getSelectionModel().selectedItemProperty().removeListener(lineLengthListener);
//				lineLengthChoiceBox.setValue(getLineLength());
//				lineLengthChoiceBox.getSelectionModel().selectedItemProperty().addListener(lineLengthListener);
//
//				angleChoiceBox.getSelectionModel().selectedItemProperty().removeListener(angleListener);
//				angleChoiceBox.setValue(getAngle());
//				angleChoiceBox.getSelectionModel().selectedItemProperty().addListener(angleListener);
//
//			}
		}
		
		private static void addSeparator(GridPane gridPane, int row) {
			Separator separator = new Separator(Orientation.HORIZONTAL);
			gridPane.add(separator, 1, row++, GridPane.REMAINING, 1);
			GridPane.setValignment(separator, VPos.CENTER);
		}
}
