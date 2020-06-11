package javaFX.plots;

import java.util.HashMap;
import java.util.Map;

import javaFX.ext.controls.Editor;
import javaFX.ext.css.CSS;
import javaFX.ext.utility.FXUtil;
import javaFX.plots.legend.Legend;
import javaFX.plots.overlay.Classification;
import javaFX.plots.overlay.PlotInfo;
import javaFX.plots.overlay.SceneOverlayManager;
import javaFX.plots.title.Title;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.converter.DoubleStringConverter;

public class PlotEditor {
	
	static Double[] FontSize = new Double[] {8.0,9.0,10.0,10.5,11.0,12.0,14.0,16.0,18.0,20.0,22.0,24.0,26.0,28.0,30.0};
	
	static Map<LineChart<?,?>,Editor> mapLineChart2Editor = new HashMap<LineChart<?,?>,Editor>();
	
	// This routine sets up the Editable window
	public static void open(LineChart<?, ?> lineChart, CSS css, double screenX, double screenY) {
		
		if (mapLineChart2Editor.containsKey(lineChart)) {
			System.out.println("Editor already open");
		}
		else {
			Editor editor = new Editor(screenX, screenY, lineChart.getScene());
			mapLineChart2Editor.put(lineChart, editor);
			editor.show("Plot Settings", getEditItems(lineChart.getScene(), css), () -> {mapLineChart2Editor.remove(lineChart); return true;});
		}
	}

	static final double MAX_CHOICEBOX_SIZE = 140.0;  // set a universal max size for the Choice Boxes that are created below 

	private static GridPane getEditItems(Scene scene, CSS css) {
		
	// set up GridPane for Editor labels and entries, set up spacing between entries and between other elements in the editor
		GridPane gridPane = new GridPane();
		gridPane.setVgap(6);
		gridPane.setHgap(2);
		CSS.setBorderWidth(gridPane, 0,10,10,10);
		CSS.setBorderColor(gridPane, Color.TRANSPARENT);		// needed or the border will have no size despite setting it below
		int row = 1;
		LineChart<?,?> lineChart = SceneOverlayManager.getLineChart(scene);
		
		String originalTitle = Title.getTitle(scene);
		String originalSubTitle = Title.getSubTitle(scene);

		addSeparator(gridPane, row++);

		////////////////////////////////////////////////////////////////////////////////////////////////////
		// Classification Markings
		////////////////////////////////////////////////////////////////////////////////////////////////////
		{
			RadioButton classificationsVisibleButton = new RadioButton("Classification Visible");
			classificationsVisibleButton.setSelected(Classification.isPresent(scene));
			classificationsVisibleButton.setMinSize(FXUtil.getWidth(classificationsVisibleButton)+30, FXUtil.getHeight(classificationsVisibleButton));
			gridPane.add(classificationsVisibleButton, 1, row++, 3, 1); // col, row
			classificationsVisibleButton.setOnAction((ActionEvent event) -> { 
				if (classificationsVisibleButton.isSelected()) {
					Classification.add(scene);
				}
				else {
					Classification.remove(scene);		
				}
			});

		}
		
		addSeparator(gridPane, row++);
		////////////////////////////////////////////////////////////////////////////////////////////////////
		// Title Settings
		////////////////////////////////////////////////////////////////////////////////////////////////////
		{
			RadioButton titleVisibleButton = new RadioButton("Title Visible");
			titleVisibleButton.setSelected(Title.isTitleVisible(scene));
			titleVisibleButton.setMinSize(FXUtil.getWidth(titleVisibleButton)+30, FXUtil.getHeight(titleVisibleButton));
			gridPane.add(titleVisibleButton, 1, row++, 3, 1); // col, row
			titleVisibleButton.setOnAction((ActionEvent event) -> { 
				if (titleVisibleButton.isSelected()) {
					Title.addTitle(scene);
				}
				else {
					Title.removeTitle(scene);		
				}
			});
		}
		
		final TextField titleTextField = new TextField(Title.getTitle(scene));
		final TextField subTitleTextField = new TextField(Title.getSubTitle(scene));
		{
			titleTextField.end();
			gridPane.add(new Text("Title"), 1, row); // col, row
			ComboBox<Double> comboBox = Editor.getDoubleComboBox(FontSize, Title.getTitleSize(scene));
			comboBox.setMaxWidth(60);
			gridPane.add(comboBox, 2, row); // col, row
			comboBox.getSelectionModel().selectedItemProperty().addListener(
					(observable, oldValue, newValue) -> {
						Title.setTitleSize(scene,newValue);
					});
			// Get and set the initial String value, Center the text and add it to the Grid Pane
			titleTextField.setMaxWidth(MAX_CHOICEBOX_SIZE*2);
			titleTextField.setAlignment(Pos.CENTER);
			gridPane.add(titleTextField,3,row++);
			titleTextField.textProperty().addListener((observable, oldValue, newValue) -> {
				Title.setTitle(scene,newValue);
			});
			titleTextField.setOnKeyReleased(event -> {
				  if (event.getCode() == KeyCode.ESCAPE){
					  	Title.setTitle(scene,originalTitle);
					  	titleTextField.setText(originalTitle);
					  	titleTextField.end();
				  }
				  else if (event.getCode() == KeyCode.ENTER){
					  subTitleTextField.requestFocus();
					  subTitleTextField.end();
				  }
				});
		}
		

		{
			subTitleTextField.end();
			gridPane.add(new Text("SubTitle"), 1, row); // col, row
			ComboBox<Double> comboBox = Editor.getDoubleComboBox(FontSize, Title.getSubTitleSize(scene));
			comboBox.setMaxWidth(60);
			gridPane.add(comboBox, 2, row); // col, row
			comboBox.getSelectionModel().selectedItemProperty().addListener(
					(observable, oldValue, newValue) -> {
						Title.setSubTitleSize(scene,newValue);
					});

			// Get and set the initial String value, Center the text and add it to the Grid Pane
			subTitleTextField.setMaxWidth(MAX_CHOICEBOX_SIZE*2);
			subTitleTextField.setAlignment(Pos.CENTER);
			gridPane.add(subTitleTextField,3,row++);
			subTitleTextField.textProperty().addListener((observable, oldValue, newValue) -> {
				Title.setSubTitle(scene,newValue);
			});
			subTitleTextField.setOnKeyReleased(event -> {
				  if (event.getCode() == KeyCode.ESCAPE){
					  	Title.setSubTitle(scene,originalSubTitle);
					  	subTitleTextField.setText(originalSubTitle);
					  	subTitleTextField.end();
				  }
				  else if (event.getCode() == KeyCode.ENTER){
					  titleTextField.requestFocus();
					  titleTextField.end();				  }
				});
		}
		
		addSeparator(gridPane, row++);
		////////////////////////////////////////////////////////////////////////////////////////////////////
		// Legend Settings
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		{
			RadioButton legendVisibleButton = new RadioButton("Legend Visible");
			legendVisibleButton.setSelected(Legend.isLegendVisible(scene));
			legendVisibleButton.setMinSize(FXUtil.getWidth(legendVisibleButton)+30, FXUtil.getHeight(legendVisibleButton));
			gridPane.add(legendVisibleButton, 1, row++, 3, 1); // col, row
			legendVisibleButton.setOnAction((ActionEvent event) -> { 
				if (legendVisibleButton.isSelected()) {
					Legend.addLegend(scene);
				}
				else {
					Legend.removeLegend(scene);		
				}
			});
		}
		{
			gridPane.add(new Text("Legend Position : "),1, row,3,1);
			ChoiceBox<Side> choiceBox = Editor.getEnumChoiceBox(new Side[] {Side.BOTTOM, Side.LEFT, Side.RIGHT}, lineChart.getLegendSide());
			choiceBox.setMaxSize(MAX_CHOICEBOX_SIZE, Double.MAX_VALUE);
			choiceBox.setOnAction(event -> { Legend.repositionLegend(scene, choiceBox.getValue());});
			gridPane.add(choiceBox,3,row++);

		}
		
		addSeparator(gridPane, row++);
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		// Plot InfoSettings
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		{
			gridPane.add(new Text("Edit Plot Info"),1, row,3,1);
			String originalText = PlotInfo.getText(scene);
			TextField textField = new TextField(originalText);
			textField.end();

			textField.setMaxSize(MAX_CHOICEBOX_SIZE, Double.MAX_VALUE);
			gridPane.add(textField,3,row++);
			textField.textProperty().addListener((observable, oldValue, newValue) -> {
				PlotInfo.setText(scene, newValue);
			});
			textField.setOnKeyReleased(event -> {
				  if (event.getCode() == KeyCode.ESCAPE){
					  PlotInfo.setText(scene, originalText);
					  textField.setText(originalText);
					  textField.end();
				  }
				});

		}

		addSeparator(gridPane, row++);
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		// Grid Lines and tick marks Settings
		////////////////////////////////////////////////////////////////////////////////////////////////////
		{
			RadioButton verticalGridLinesVisible = new RadioButton("Vertical GridLines Visible");
			verticalGridLinesVisible.setSelected(lineChart.getVerticalGridLinesVisible());
			verticalGridLinesVisible.setMinSize(FXUtil.getWidth(verticalGridLinesVisible)+30, FXUtil.getHeight(verticalGridLinesVisible));
			gridPane.add(verticalGridLinesVisible, 1, row++, 3, 1); // col, row
			verticalGridLinesVisible.setOnAction((ActionEvent event) -> { 
				lineChart.setVerticalGridLinesVisible(verticalGridLinesVisible.isSelected());
			});
		}
		{
			RadioButton horizontalGridLinesVisible = new RadioButton("Horizontal GridLines Visible");
			horizontalGridLinesVisible.setSelected(lineChart.isHorizontalGridLinesVisible());
			horizontalGridLinesVisible.setMinSize(FXUtil.getWidth(horizontalGridLinesVisible)+30, FXUtil.getHeight(horizontalGridLinesVisible));
			gridPane.add(horizontalGridLinesVisible, 1, row++, 3, 1); // col, row
			horizontalGridLinesVisible.setOnAction((ActionEvent event) -> { 
				lineChart.setHorizontalGridLinesVisible(horizontalGridLinesVisible.isSelected());
			});
		}

		return gridPane;		
		
	}
	
	private static void addSeparator(GridPane gridPane, int row) {
		Separator separator = new Separator(Orientation.HORIZONTAL);
		gridPane.add(separator, 1, row++, GridPane.REMAINING, 1);
		GridPane.setValignment(separator, VPos.CENTER);
	}

//	public static ComboBox<Double> getDoubleComboBox(Double[] values, Double value) {
//		// get list of enumerations
//		ObservableList<Double> list = FXCollections.observableArrayList();
//		list.addAll(values);
//		// create a ChoiceBox with those values, set the default value, set the size, etc.
//		var comboBox = new ComboBox<Double>(list);
//		comboBox.setEditable(true);
//		comboBox.setConverter(new DoubleStringConverter());
////		GridPane.setFillWidth(comboBox, true);
//		comboBox.setValue(value);
//		return comboBox;
//	}
}
