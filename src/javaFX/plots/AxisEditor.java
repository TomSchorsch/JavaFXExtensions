package javaFX.plots;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javaFX.ext.controls.Editor;
import javaFX.ext.css.CSS;
import javaFX.ext.utility.FXUtil;
import javaFX.plots.overlay.SceneOverlayManager;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.ValueAxis;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
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

	static double defaultFontSize = 14.0;
	static Double[] FontSize = new Double[] {8.0,9.0,10.0,10.5,11.0,12.0,14.0,16.0,18.0,20.0,22.0,24.0,26.0,28.0,30.0};


	static Map<Axis<?>,Editor> mapAxis2Editor = new HashMap<Axis<?> ,Editor>();
	static Map<Axis<?>,TextField> mapAxis2LowerRange = new HashMap<Axis<?> ,TextField>();
	static Map<Axis<?>,TextField> mapAxis2UpperRange = new HashMap<Axis<?> ,TextField>();
	static Map<Axis<?>,RadioButton> mapAxis2RadioButton = new HashMap<Axis<?> ,RadioButton>();

	// This routine sets up the Editable window
	public static void open(ValueAxis<?> axis, CSS css, double screenX, double screenY) {

		if (mapAxis2Editor.containsKey(axis)) {
			System.out.println("Editor already open");
		}
		else {
			Editor editor = new Editor(screenX, screenY, axis.getScene());
			mapAxis2Editor.put(axis, editor);

			String label = "X Axis Settings";
			Scene scene = axis.getScene();
			LineChart<?,?> lineChart = SceneOverlayManager.getLineChart(scene);
			if (lineChart.getYAxis().equals(axis)) label = "Y Axis Settings";
			editor.show(label, getEditItems(axis, css), () -> {mapAxis2Editor.remove(axis); mapAxis2LowerRange.remove(axis); mapAxis2UpperRange.remove(axis); mapAxis2RadioButton.remove(axis); return true;});
		}
	}

	static final double MAX_CHOICEBOX_SIZE = 140.0;  // set a universal max size for the Choice Boxes that are created below 

	public static void setBounds (Axis<?> axis) {
		TextField lowerRangeTextField = mapAxis2LowerRange.get(axis);
		TextField upperRangeTextField = mapAxis2UpperRange.get(axis);
		RadioButton autoRangeButton = mapAxis2RadioButton.get(axis);
		if (lowerRangeTextField!= null) lowerRangeTextField.setText(Double.valueOf(((ValueAxis<?>)axis).getLowerBound()).toString());
		if (upperRangeTextField!= null) upperRangeTextField.setText(Double.valueOf(((ValueAxis<?>)axis).getUpperBound()).toString());
		if (autoRangeButton!= null) autoRangeButton.setSelected(((ValueAxis<?>)axis).isAutoRanging());
	}

	private static GridPane getEditItems(ValueAxis<?> axis, CSS css) {

		// set up GridPane for Editor labels and entries, set up spacing between entries and between other elements in the editor
		GridPane gridPane = new GridPane();
		gridPane.setVgap(6);
		gridPane.setHgap(2);
		CSS.setBorderWidth(gridPane, 0,10,10,10);
		CSS.setBorderColor(gridPane, Color.TRANSPARENT);		// needed or the border will have no size despite setting it below
		int row = 1;

		String originalLabel = axis.getLabel();
		Double lowerBound = ((ValueAxis<?>)axis).getLowerBound();			
		Double upperBound = ((ValueAxis<?>)axis).getUpperBound();	
		TextField lowerRangeTextField = new TextField();
		TextField upperRangeTextField = new TextField();
		mapAxis2LowerRange.put(axis, lowerRangeTextField);
		mapAxis2UpperRange.put(axis, upperRangeTextField);

		RadioButton axisAutoRangeButton = new RadioButton("Axis AutoRange");
		mapAxis2RadioButton.put(axis, axisAutoRangeButton);	


		addSeparator(gridPane, row++);


		////////////////////////////////////////////////////////////////////////////////////////////////////
		// Axis Label Settings
		////////////////////////////////////////////////////////////////////////////////////////////////////



		{
			final TextField titleTextField = new TextField(originalLabel);
			titleTextField.end();
			gridPane.add(new Text("Label"), 1, row); // col, row
			ComboBox<Double> comboBox = Editor.getDoubleComboBox(FontSize, AxisEditor.getAxisFontSize(axis));
			comboBox.setMaxWidth(60);
			gridPane.add(comboBox, 2, row); // col, row
			comboBox.getSelectionModel().selectedItemProperty().addListener(
					(observable, oldValue, newValue) -> {
						AxisEditor.setAxisFontSize(axis,newValue);
					});
			GridPane.setHalignment(comboBox, HPos.RIGHT);
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

		{
			if (axis instanceof ValueAxis) {
				axisAutoRangeButton.setSelected(axis.isAutoRanging());
				axisAutoRangeButton.setMinSize(FXUtil.getWidth(axisAutoRangeButton)+30, FXUtil.getHeight(axisAutoRangeButton));
				gridPane.add(axisAutoRangeButton, 1, row++, 3, 1); // col, row
				axisAutoRangeButton.setOnAction((ActionEvent event) -> { 
					axis.setAutoRanging(axisAutoRangeButton.isSelected());
					if (axis instanceof ValueAxis) {
						if (axisAutoRangeButton.isSelected()) {
								// do nothing
							}
						else {
							Double lower = getNumber(lowerRangeTextField.getText(),lowerBound.doubleValue());
							((ValueAxis<?>)axis).setLowerBound(lower);	
							Double upper = getNumber(upperRangeTextField.getText(),upperBound.doubleValue());
							((ValueAxis<?>)axis).setUpperBound(upper);
						}
					}
				});
			}
		}


		// Get the current X and Y values, label them as such and add the two fields to the GridPane 
		{
			if (axis instanceof ValueAxis) {		

				gridPane.add(new Text("Range"), 1, row,2,1); // col, row
				lowerRangeTextField.setText(lowerBound.toString());
				lowerRangeTextField.setMaxWidth(MAX_CHOICEBOX_SIZE*0.8);
				lowerRangeTextField.setAlignment(Pos.CENTER);
				upperRangeTextField.setText(upperBound.toString());
				upperRangeTextField.setMaxWidth(MAX_CHOICEBOX_SIZE*0.8);
				upperRangeTextField.setAlignment(Pos.CENTER);
				HBox hbox = new HBox(lowerRangeTextField,new Text(" - "),upperRangeTextField);
				hbox.setAlignment(Pos.BASELINE_CENTER);
				lowerRangeTextField.focusedProperty().addListener(
						(obs, oldVal, focused) -> {
							if (!focused)
								setLowerBound(axis, axisAutoRangeButton, lowerRangeTextField, lowerBound.doubleValue());}
						);
				lowerRangeTextField.setOnKeyReleased(event -> {
					if (event.getCode() == KeyCode.ENTER){
						setLowerBound(axis, axisAutoRangeButton, lowerRangeTextField, lowerBound.doubleValue());
//						axisAutoRangeButton.setSelected(false);
//						axis.setAutoRanging(false);
//						Double lower = getNumber(lowerRangeTextField.getText(),lowerBound.doubleValue());
//						lowerRangeTextField.setText(lower.toString());
//						((ValueAxis<?>)axis).setLowerBound(lower);	
						upperRangeTextField.requestFocus();
					}
					else if (event.getCode() == KeyCode.ESCAPE){
						lowerRangeTextField.setText(lowerBound.toString());
						lowerRangeTextField.end();
					}
				});
				upperRangeTextField.focusedProperty().addListener(
						(obs, oldVal, focused) -> {
							if (!focused)
								setUpperBound(axis, axisAutoRangeButton, upperRangeTextField, upperBound.doubleValue());}
						);
				upperRangeTextField.setOnKeyReleased(event -> {
					if (event.getCode() == KeyCode.ENTER){
						setUpperBound(axis, axisAutoRangeButton, upperRangeTextField, upperBound.doubleValue());
//						axisAutoRangeButton.setSelected(false);
//						axis.setAutoRanging(false);
//						Double upper = getNumber(upperRangeTextField.getText(),upperBound.doubleValue());
//						upperRangeTextField.setText(upper.toString());
//						((ValueAxis<?>)axis).setUpperBound(upper);
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

		ComboBox<Double> ticFontSizeComboBox = Editor.getDoubleComboBox(CSS.FontSizeArray, axis.getTickLabelFont().getSize());
		{
			gridPane.add(new Text("Tick Label Size : "),1, row,2,1);
			ticFontSizeComboBox.setMaxSize(MAX_CHOICEBOX_SIZE, Double.MAX_VALUE);
			ticFontSizeComboBox.setOnAction(event -> { 
				axis.setTickLabelFont(new Font(ticFontSizeComboBox.getValue()));
			});
			gridPane.add(ticFontSizeComboBox,3,row++);
		}

		{
			gridPane.add(new Text("Tick Label rotation : "),1, row,2,1);
			ComboBox<Double> comboBox = Editor.getDoubleComboBox(CSS.tickLabelRotationArray, axis.getTickLabelRotation());
			comboBox.setMaxSize(MAX_CHOICEBOX_SIZE, Double.MAX_VALUE);
			comboBox.setOnAction(event -> { 
				axis.setTickLabelRotation(-comboBox.getValue());
				axis.setTickLabelFont(new Font(ticFontSizeComboBox.getValue()));  // This was needed to force an update when auto ranging was off
			});
			gridPane.add(comboBox,3,row++);
		}
		{
			RadioButton minorTickMarksVisible = new RadioButton("Minor Tick Marks Visible");
			minorTickMarksVisible.setSelected(axis.isMinorTickVisible());
			minorTickMarksVisible.setMinSize(FXUtil.getWidth(minorTickMarksVisible)+30, FXUtil.getHeight(minorTickMarksVisible));
			gridPane.add(minorTickMarksVisible, 1, row++, 3, 1); // col, row
			minorTickMarksVisible.setOnAction((ActionEvent event) -> { 
				axis.setMinorTickVisible(minorTickMarksVisible.isSelected());
			});
		}

		return gridPane;		

	}
	
	private static void setLowerBound (Axis axis, RadioButton axisAutoRangeButton, TextField lowerRangeTextField, double lowBound) {
		axisAutoRangeButton.setSelected(false);
		axis.setAutoRanging(false);
		Double lower = getNumber(lowerRangeTextField.getText(),lowBound);
		lowerRangeTextField.setText(lower.toString());
		((ValueAxis<?>)axis).setLowerBound(lower);	
	}

	private static void setUpperBound (Axis axis, RadioButton axisAutoRangeButton, TextField upperRangeTextField, double upperBound) {
		axisAutoRangeButton.setSelected(false);
		axis.setAutoRanging(false);
		Double upper = getNumber(upperRangeTextField.getText(),upperBound);
		upperRangeTextField.setText(upper.toString());
		((ValueAxis<?>)axis).setUpperBound(upper);
	}

	private static Double getNumber(String s, Double def) {
		try {
			Double ans = Double.parseDouble(s);
			return ans;
		}
		catch (Exception e) {
			return def;
		}
	}

	private static void addSeparator(GridPane gridPane, int row) {
		Separator separator = new Separator(Orientation.HORIZONTAL);
		gridPane.add(separator, 1, row++, GridPane.REMAINING, 1);
		GridPane.setValignment(separator, VPos.CENTER);
	}
	
	public static Label getAxisLabel(Axis<?> axis) {
		List<Node> list =axis.getChildrenUnmodifiable();
		for (Node n : list) {
			if (n instanceof Label) return (Label)n;
		}
		return null;
	}
	
	public static void setAxisFontSize(Axis<?> axis, Double fontSize) {
		Label label = getAxisLabel(axis);
		if (label != null) CSS.setFontSize(label, fontSize);
		axis.setUserData(fontSize);
	}
	
	public static Double getAxisFontSize(Axis<?> axis) {
		Double size  = (Double)axis.getUserData();
		if (size == null) {
			axis.setUserData(defaultFontSize);
			size = defaultFontSize;
		}
		return size;
	}
}
