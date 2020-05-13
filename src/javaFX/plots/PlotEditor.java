package javaFX.plots;

import java.util.HashMap;
import java.util.Map;

import javaFX.ext.controls.Editor;
import javaFX.ext.controls.SymbolPicker;
import javaFX.ext.css.CSS;
import javaFX.ext.css.CSS.FontWeight;
import javaFX.ext.css.CSS.SymbolStyle;
import javaFX.ext.utility.FXUtil;
import javaFX.plots.legend.Legend;
import javaFX.plots.overlay.SceneOverlay;
import javaFX.plots.title.Title;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class PlotEditor {
	
	
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
		LineChart<?,?> lineChart = SceneOverlay.getLineChart(scene);
		
		String originalTitle = Title.getTitle(scene);
		String originalSubTitle = Title.getSubTitle(scene);

		addSeparator(gridPane, row++);

		////////////////////////////////////////////////////////////////////////////////////////////////////
		// Classification Markings
		////////////////////////////////////////////////////////////////////////////////////////////////////
		{
			RadioButton classificationsVisibleButton = new RadioButton("Classification Visible");
			classificationsVisibleButton.setSelected(SceneOverlay.isClassificationMarkingsPresent(scene));
			classificationsVisibleButton.setMinSize(FXUtil.getWidth(classificationsVisibleButton)+30, FXUtil.getHeight(classificationsVisibleButton));
			gridPane.add(classificationsVisibleButton, 1, row++, 3, 1); // col, row
			classificationsVisibleButton.setOnAction((ActionEvent event) -> { 
				if (classificationsVisibleButton.isSelected()) {
					SceneOverlay.addClassification(scene);
				}
				else {
					SceneOverlay.removeClassification(scene);		
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
			gridPane.add(new Text("Legend Position : "),1, row);
			ChoiceBox<Side> choiceBox = Editor.getEnumEntry(new Side[] {Side.BOTTOM, Side.LEFT, Side.RIGHT}, lineChart.getLegendSide());
			choiceBox.setMaxSize(MAX_CHOICEBOX_SIZE, Double.MAX_VALUE);
			choiceBox.setOnAction(event -> { Legend.repositionLegend(scene, choiceBox.getValue());});
			gridPane.add(choiceBox,3,row++);

		}
		
		addSeparator(gridPane, row++);
		
		{
			gridPane.add(new Text("Edit Plot Info"),1, row);
			String originalText = SceneOverlay.getPlotInfoText(scene);
			TextField textField = new TextField(originalText);
			textField.end();

			textField.setMaxSize(MAX_CHOICEBOX_SIZE, Double.MAX_VALUE);
			gridPane.add(textField,3,row++);
			textField.textProperty().addListener((observable, oldValue, newValue) -> {
				SceneOverlay.setPlotInfoText(scene, newValue);
			});
			textField.setOnKeyReleased(event -> {
				  if (event.getCode() == KeyCode.ESCAPE){
					  SceneOverlay.setPlotInfoText(scene, originalText);
					  textField.setText(originalText);
					  textField.end();
				  }
				});

		}
		
		addSeparator(gridPane, row++);
		addSeparator(gridPane, row++);


		{
			Text text = new Text("Changes below effect ALL series, ALL data");
			CSS.setFontWeight(text, FontWeight.bold);
			gridPane.add(text, 1, row++, 3, 1); // col, row
		}
		
		addSeparator(gridPane, row++);


		{
			RadioButton symbolsVisibleButton = new RadioButton("Symbols Visible");
			symbolsVisibleButton.setSelected(css.getSymbolsVisible());
			symbolsVisibleButton.setMinSize(FXUtil.getWidth(symbolsVisibleButton)+30, FXUtil.getHeight(symbolsVisibleButton));
			gridPane.add(symbolsVisibleButton, 1, row++, 3, 1); // col, row
			symbolsVisibleButton.setOnAction((ActionEvent event) -> { 
				css.setSymbolsVisible(symbolsVisibleButton.isSelected());
				if (Legend.isLegendVisible(lineChart.getScene())) {
					Legend.addLegend(lineChart.getScene());;
				}
			});
		}
		
		SymbolPicker symbolPicker = Editor.getSymbolPicker(css.getSymbol(), css.getSymbolColor());
		{
			symbolPicker.setAvailableSymbols(css.defaultSymbols);
			gridPane.add(new Text("Symbol :"), 1, row); // col, row
			symbolPicker.setOnSymbolSelection(event -> {
				css.setSymbol(symbolPicker.getValue());
				if (Legend.isLegendVisible(scene)) {
					Legend.addLegend(scene);
				}
				SeriesEditor.setEditorsSymbol(symbolPicker.getValue(),css);
			});
			gridPane.add(symbolPicker,3,row++);

		}
		
		{
			gridPane.add(new Text("Symbol Color"), 1, row); // col, row
			ColorPicker colorPicker = Editor.getColorPicker(css.getSymbolColor());
			colorPicker.setMaxSize(MAX_CHOICEBOX_SIZE, Double.MAX_VALUE);
			colorPicker.setOnAction(event -> {
				css.setSymbolColor(colorPicker.getValue());
				SeriesEditor.setEditorsSymbolColor(colorPicker.getValue(), css);
				Editor.setSymbolPicker(symbolPicker, css.getSymbol(), colorPicker.getValue());
				
			});
			gridPane.add(colorPicker,3,row++);
		}

		
		{
			gridPane.add(new Text("Symbol Size"), 1, row); // col, row
			ChoiceBox<Double> choiceBox = Editor.getDoubleEntry(CSS.symbolSizeArray, css.getSymbolSize());
			choiceBox.setMaxSize(MAX_CHOICEBOX_SIZE, Double.MAX_VALUE);
			choiceBox.setOnAction(event -> {
				css.setSymbolSize(choiceBox.getValue());
				SeriesEditor.setEditorsSymbolSize(choiceBox.getValue());
			});
			gridPane.add(choiceBox,3,row++);
		}
		
		{
			gridPane.add(new Text("Symbol Style"), 1, row); // col, row
			ChoiceBox<SymbolStyle> choiceBox = Editor.getEnumEntry(css.getSymbolStyle());
			choiceBox.setMaxSize(MAX_CHOICEBOX_SIZE, Double.MAX_VALUE);
			choiceBox.setOnAction(event -> {
				css.changeSymbolStyle(choiceBox.getValue());
				SeriesEditor.setEditorsSymbol(css);
				symbolPicker.setValue(css.getSymbol(), css.getSymbolColor());
				symbolPicker.setAvailableSymbols(css.defaultSymbols);
				if (Legend.isLegendVisible(lineChart.getScene())) {
					Legend.addLegend(lineChart.getScene());;
				}
			});
			gridPane.add(choiceBox,3,row++);
		}

		addSeparator(gridPane, row++);

		{
			RadioButton linesVisibleButton = new RadioButton("Lines Visible");
			linesVisibleButton.setSelected(css.getLinesVisible());
			linesVisibleButton.setMinSize(FXUtil.getWidth(linesVisibleButton)+30, FXUtil.getHeight(linesVisibleButton));
			gridPane.add(linesVisibleButton, 1, row++, 3, 1); // col, row
			linesVisibleButton.setOnAction((ActionEvent event) -> { 
				css.setLinesVisible(linesVisibleButton.isSelected());
				if (Legend.isLegendVisible(lineChart.getScene())) {
					Legend.addLegend(lineChart.getScene());;
				}
			});
		}
		
		{
			gridPane.add(new Text("Line Color"), 1, row); // col, row
			ColorPicker colorPicker = Editor.getColorPicker(css.getSymbolColor());
			colorPicker.setMaxSize(MAX_CHOICEBOX_SIZE, Double.MAX_VALUE);
			colorPicker.setOnAction(event -> {
				css.setLineColor(colorPicker.getValue());
				SeriesEditor.setEditorsLineColor(colorPicker.getValue());
			});
			gridPane.add(colorPicker,3,row++);
		}

		{
			gridPane.add(new Text("Line Width"), 1, row); // col, row
			ChoiceBox<Double> choiceBox = Editor.getDoubleEntry(CSS.lineWidthArray, css.getLineWidth());
			choiceBox.setMaxSize(MAX_CHOICEBOX_SIZE, Double.MAX_VALUE);
			choiceBox.setOnAction(event -> { 
				css.setLineWidth(choiceBox.getValue());
				SeriesEditor.setEditorsLineWidth(choiceBox.getValue());
				});
			gridPane.add(choiceBox,3,row++);
		}
		
		addSeparator(gridPane, row++);
		
		return gridPane;		
		
	}
	
	private static void addSeparator(GridPane gridPane, int row) {
		Separator separator = new Separator(Orientation.HORIZONTAL);
		gridPane.add(separator, 1, row++, GridPane.REMAINING, 1);
		GridPane.setValignment(separator, VPos.CENTER);
	}

}
