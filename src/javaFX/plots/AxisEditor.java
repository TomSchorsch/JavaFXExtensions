package javaFX.plots;

import java.util.HashMap;
import java.util.Map;

import javaFX.ext.controls.Editor;
import javaFX.ext.css.CSS;
import javaFX.ext.utility.FXUtil;
import javaFX.plots.overlay.SceneOverlay;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.ValueAxis;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class AxisEditor {
	
	
	static Map<Axis,Editor> mapAxis2Editor = new HashMap<Axis,Editor>();
	
	// This routine sets up the Editable window
	public static void open(Axis<?> axis, CSS css, double screenX, double screenY) {
		
		if (mapAxis2Editor.containsKey(axis)) {
			System.out.println("Editor already open");
		}
		else {
			Editor editor = new Editor(screenX, screenY, axis.getScene());
			mapAxis2Editor.put(axis, editor);
			String label = "X Axis Settings";
			Scene scene = axis.getScene();
			LineChart<?,?> lineChart = SceneOverlay.getLineChart(scene);
			if (lineChart.getYAxis().equals(axis)) label = "Y Axis Settings";
			editor.show(label, getEditItems(axis, css), () -> {mapAxis2Editor.remove(axis); return true;});
		}
	}

	static final double MAX_CHOICEBOX_SIZE = 140.0;  // set a universal max size for the Choice Boxes that are created below 

	private static GridPane getEditItems(Axis<?> axis, CSS css) {

	// set up GridPane for Editor labels and entries, set up spacing between entries and between other elements in the editor
		GridPane gridPane = new GridPane();
		gridPane.setVgap(6);
		gridPane.setHgap(2);
		CSS.setBorderWidth(gridPane, 0,10,10,10);
		CSS.setBorderColor(gridPane, Color.TRANSPARENT);		// needed or the border will have no size despite setting it below
		int row = 1;
		
		String originalLabel = axis.getLabel();

		addSeparator(gridPane, row++);


		////////////////////////////////////////////////////////////////////////////////////////////////////
		// Title Settings
		////////////////////////////////////////////////////////////////////////////////////////////////////

		

		{
			final TextField titleTextField = new TextField(originalLabel);
			titleTextField.end();
			gridPane.add(new Text("Axis Label"), 1, row); // col, row
			// Get and set the initial String value, Center the text and add it to the Grid Pane
			titleTextField.setMaxWidth(MAX_CHOICEBOX_SIZE*2);
			titleTextField.setAlignment(Pos.CENTER);
			gridPane.add(titleTextField,3,row++);
			titleTextField.textProperty().addListener((observable, oldValue, newValue) -> {
				axis.setLabel(newValue);
			});
			titleTextField.setOnKeyReleased(event -> {
				if (event.getCode() == KeyCode.ESCAPE){
					axis.setLabel(originalLabel);
					titleTextField.setText(originalLabel);
					titleTextField.end();
				}
			});
		}

		////////////////////////////////////////////////////////////////////////////////////////////////////
		// Axis Range
		////////////////////////////////////////////////////////////////////////////////////////////////////
		TextField lowerRangeTextField = new TextField();
		TextField upperRangeTextField = new TextField();
		RadioButton axisAutoRangeButton = new RadioButton("Axis AutoRange");
		{
			axisAutoRangeButton.setSelected(axis.isAutoRanging());
			axisAutoRangeButton.setMinSize(FXUtil.getWidth(axisAutoRangeButton)+30, FXUtil.getHeight(axisAutoRangeButton));
			gridPane.add(axisAutoRangeButton, 1, row++, 3, 1); // col, row
			axisAutoRangeButton.setOnAction((ActionEvent event) -> { 
				axis.setAutoRanging(axisAutoRangeButton.isSelected());
				if (!axisAutoRangeButton.isSelected()) {
					if (axis instanceof ValueAxis) {
						Double lower = Double.parseDouble(lowerRangeTextField.getText());
						((ValueAxis<?>)axis).setLowerBound(lower);	
						Double upper = Double.parseDouble(upperRangeTextField.getText());
						((ValueAxis<?>)axis).setUpperBound(upper);
					}
				}
				
			});
		}

		
		// Get the current X and Y values, label them as such and add the two fields to the GridPane 
		{
			if (axis instanceof ValueAxis) {
				Double lowerBound = ((ValueAxis<?>)axis).getLowerBound();			
				Double upperBound = ((ValueAxis<?>)axis).getUpperBound();			

				gridPane.add(new Text("Range"), 1, row); // col, row
				lowerRangeTextField.setText(lowerBound.toString());
				lowerRangeTextField.setMaxWidth(MAX_CHOICEBOX_SIZE*0.8);
				lowerRangeTextField.setAlignment(Pos.CENTER);
				upperRangeTextField.setText(upperBound.toString());
				upperRangeTextField.setMaxWidth(MAX_CHOICEBOX_SIZE*0.8);
				upperRangeTextField.setAlignment(Pos.CENTER);
				HBox hbox = new HBox(lowerRangeTextField,new Text(" - "),upperRangeTextField);
				hbox.setAlignment(Pos.BASELINE_CENTER);

				lowerRangeTextField.setOnKeyReleased(event -> {
					if (event.getCode() == KeyCode.ENTER){
						axisAutoRangeButton.setSelected(false);
						axis.setAutoRanging(false);
						Double lower = Double.parseDouble(lowerRangeTextField.getText());
						((ValueAxis<?>)axis).setLowerBound(lower);	
						upperRangeTextField.requestFocus();
					}
					else if (event.getCode() == KeyCode.ESCAPE){
						lowerRangeTextField.setText(lowerBound.toString());
						lowerRangeTextField.end();
					}
				});
				upperRangeTextField.setOnKeyReleased(event -> {
					if (event.getCode() == KeyCode.ENTER){
						axisAutoRangeButton.setSelected(false);
						axis.setAutoRanging(false);
						Double upper = Double.parseDouble(upperRangeTextField.getText());
						((ValueAxis<?>)axis).setUpperBound(upper);
						lowerRangeTextField.requestFocus();
					}
					else if (event.getCode() == KeyCode.ESCAPE){
						upperRangeTextField.setText(upperBound.toString());
						upperRangeTextField.end();
					}
				});
				gridPane.add(hbox,3,row++);
			}

			addSeparator(gridPane, row++);
		}
		////////////////////////////////////////////////////////////////////////////////////////////////////
		// Tick Label
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		ChoiceBox<Double> fontSizeChoiceBox = Editor.getDoubleChoiceBox(CSS.FontSizeArray, axis.getTickLabelFont().getSize());
		{
			gridPane.add(new Text("Tick Label Font : "),1, row);
			fontSizeChoiceBox.setMaxSize(MAX_CHOICEBOX_SIZE, Double.MAX_VALUE);
			fontSizeChoiceBox.setOnAction(event -> { 
				axis.setTickLabelFont(new Font(fontSizeChoiceBox.getValue()));
			});
			gridPane.add(fontSizeChoiceBox,3,row++);
		}
		
		{
			
			gridPane.add(new Text("Tick Label rotation : "),1, row);
			Double d = -axis.getTickLabelRotation();
			ChoiceBox<Double> choiceBox = Editor.getDoubleChoiceBox(CSS.tickLabelRotationArray, axis.getTickLabelRotation());
			choiceBox.setMaxSize(MAX_CHOICEBOX_SIZE, Double.MAX_VALUE);
			choiceBox.setOnAction(event -> { 
				axis.setTickLabelRotation(-choiceBox.getValue());
				axis.setTickLabelFont(new Font(fontSizeChoiceBox.getValue()));  // This was needed to force an update when auto ranging was off
			});
			gridPane.add(choiceBox,3,row++);
		}
		
	
		return gridPane;		
		
	}
	
	private static void addSeparator(GridPane gridPane, int row) {
		Separator separator = new Separator(Orientation.HORIZONTAL);
		gridPane.add(separator, 1, row++, GridPane.REMAINING, 1);
		GridPane.setValignment(separator, VPos.CENTER);
	}

}
