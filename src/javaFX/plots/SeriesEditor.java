package javaFX.plots;

import java.util.HashMap;
import java.util.Map;

import javaFX.ext.controls.Editor;
import javaFX.ext.controls.SymbolPicker;
import javaFX.ext.css.CSS;
import javaFX.ext.css.CSS.Symbol;
import javaFX.ext.css.CSS.SymbolStyle;
import javaFX.ext.utility.FXUtil;
import javaFX.plots.legend.Legend;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class SeriesEditor {

	static Map<Series<?,?>,Editor> mapSeries2Editor = new HashMap<Series<?,?>,Editor>();
	static Map<Series<?,?>,ColorPicker> mapSeries2SeriesColorPicker = new HashMap<Series<?,?>,ColorPicker>();
	static Map<Series<?,?>,SymbolPicker> mapSeries2SymbolPicker = new HashMap<Series<?,?>,SymbolPicker>();
	static Map<Series<?,?>,ColorPicker> mapSeries2SymbolColorPicker = new HashMap<Series<?,?>,ColorPicker>();
	static Map<Series<?,?>,ComboBox<Double>> mapSeries2SymbolSizePicker = new HashMap<Series<?,?>,ComboBox<Double>>();
	static Map<Series<?,?>,ColorPicker> mapSeries2LineColorPicker = new HashMap<Series<?,?>,ColorPicker>();
	static Map<Series<?,?>,ComboBox<Double>> mapSeries2LineWidthPicker = new HashMap<Series<?,?>,ComboBox<Double>>();

	static Map<Series<?,?>,Boolean> mapSeries2SeriesRemoved = new HashMap<Series<?,?>,Boolean>();
	
	
	
	static void removeEditors(Series<?,?> series) {
		mapSeries2Editor.remove(series);
		mapSeries2SeriesColorPicker.remove(series);
		mapSeries2SymbolPicker.remove(series);
		mapSeries2SymbolColorPicker.remove(series);
		mapSeries2SymbolSizePicker.remove(series);
		mapSeries2LineColorPicker.remove(series);
		mapSeries2LineWidthPicker.remove(series);
		mapSeries2SeriesRemoved.remove(series);
	}	
	
	public static void setEditorsSymbol(Symbol symbol, CSS css) {
		for(Series<?,?> series : mapSeries2Editor.keySet()) {
			Editor.setSymbolPicker(mapSeries2SymbolPicker.get(series), symbol, css.getSymbolColor(series));
			css.setSymbolColor(series, css.getSymbolColor(series));
		}
	}
	public static void setEditorsSymbol(CSS css) {
		for(Series<?,?> series : mapSeries2Editor.keySet()) {
			Editor.setSymbolPicker(mapSeries2SymbolPicker.get(series), css.getSymbol(series), css.getSymbolColor(series));
			css.setSymbolColor(series, css.getSymbolColor(series));
		}
	}
	public static void setEditorsSymbolSize(Double size) {
		for(Series<?,?> series : mapSeries2Editor.keySet()) {
			Editor.setDoubleComboBox(mapSeries2SymbolSizePicker.get(series), size);
		}
	}
	public static void setEditorsSymbolColor(Color color, CSS css) {
		for(Series<?,?> series : mapSeries2Editor.keySet()) {
			Editor.setColorPicker(mapSeries2SeriesColorPicker.get(series), color);
			Editor.setColorPicker(mapSeries2SymbolColorPicker.get(series), color);
			Editor.setSymbolPicker(mapSeries2SymbolPicker.get(series), css.getSymbol(series), color);
		}
	}
	public static void setEditorsLineColor(Color color) {
		for(Series<?,?> series : mapSeries2Editor.keySet()) {
			Editor.setColorPicker(mapSeries2LineColorPicker.get(series), color);
		}
	}
	public static void setEditorsLineWidth(Double width) {
		for(Series<?,?> series : mapSeries2Editor.keySet()) {
			Editor.setDoubleComboBox(mapSeries2LineWidthPicker.get(series), width);
		}
	}

	
	// This routine sets up the Editable window
	public static void open(Series<?, ?> series, CSS css, Label label, double screenX, double screenY) {
		
		if (mapSeries2Editor.containsKey(series)) {
			System.out.println("Editor already open");
		}
		else {
			Editor editor = new Editor(screenX, screenY, label.getScene());
			mapSeries2Editor.put(series, editor);
			editor.show("Series Settings", getEditItems(series, css, label), () -> {removeEditors(series); return true;});
		}
	}
	
	static final double MAX_CHOICEBOX_SIZE = 120.0;  // set a universal max size for the Choice Boxes that are created below 
	
	public void setEditorSymbol(Symbol symbol) {
		
	}
	private static GridPane getEditItems(Series<?, ?> series, CSS css, final Label label) {
		
	// set up GridPane for Editor labels and entries, set up spacing between entries and between other elements in the editor
		GridPane gridPane = new GridPane();
		gridPane.setVgap(6);
		gridPane.setHgap(2);
		CSS.setBorderWidth(gridPane, 0,10,10,10);
		CSS.setBorderColor(gridPane, Color.TRANSPARENT);		// needed or the border will have no size despite setting it below
		int row = 1;

		String originalName =  series.getName();
		ColorPicker seriesColorPicker = Editor.getColorPicker(css.getSymbolColor(series));		
		SymbolPicker symbolPicker = Editor.getSymbolPicker(css.getSymbol(series), css.getSymbolColor(series));
		ColorPicker symbolColorPicker = Editor.getColorPicker(css.getSymbolColor(series));
		ComboBox<Double> symbolSizePicker = Editor.getDoubleComboBox(CSS.symbolSizeArray, css.getSymbolSize(series));
		ChoiceBox<SymbolStyle> symbolStyleChoiceBox = Editor.getEnumChoiceBox(css.getSymbolStyle(series));
		ColorPicker lineColorPicker = Editor.getColorPicker(css.getLineColor(series));
		ComboBox<Double> lineWidthPicker = Editor.getDoubleComboBox(CSS.lineWidthArray, css.getLineWidth(series));
		
		mapSeries2SeriesColorPicker.put(series,seriesColorPicker);
		mapSeries2SymbolPicker.put(series,symbolPicker);
		mapSeries2SymbolColorPicker.put(series,symbolColorPicker);
		mapSeries2SymbolSizePicker.put(series,symbolSizePicker);
		mapSeries2LineColorPicker.put(series,lineColorPicker);
		mapSeries2LineWidthPicker.put(series,lineWidthPicker);	
		if (!mapSeries2SeriesRemoved.containsKey(series)) {
			mapSeries2SeriesRemoved.put(series,false);
		};
		
		addSeparator(gridPane, row++);
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		// Series Name
		////////////////////////////////////////////////////////////////////////////////////////////////////
		{
			gridPane.add(new Text("Series Name"), 1, row); // col, row
			// Get and set the initial String value, Center the text and add it to the Grid Pane
			final TextField textField = new TextField(series.getName());
			textField.setMaxWidth(MAX_CHOICEBOX_SIZE*2);
			textField.setAlignment(Pos.CENTER);
			gridPane.add(textField,3,row++);
			textField.textProperty().addListener((observable, oldValue, newValue) -> {
				series.setName(newValue);
				Legend.createLegendItem(series, css, label);
			});
			textField.setOnKeyReleased(event -> {
				  if (event.getCode() == KeyCode.ESCAPE){
					  	series.setName(originalName);
					  	textField.setText(series.getName());
					  	Legend.createLegendItem(series, css, label);
				  }
				});
		}
		
		{
			gridPane.add(new Text("Series Color"), 1, row); // col, row
			seriesColorPicker.setMaxSize(MAX_CHOICEBOX_SIZE, Double.MAX_VALUE);
			seriesColorPicker.setOnAction(event -> {
				css.setColor(series, seriesColorPicker.getValue());
				Legend.createLegendItem(series, css, label);
				symbolColorPicker.setValue(css.getSymbolColor(series));
				lineColorPicker.setValue(css.getLineColor(series));
				symbolPicker.setButtonSymbolColor(css.getSymbolColor(series));
			});
			gridPane.add(seriesColorPicker,3,row++);
		}
		
		addSeparator(gridPane, row++);
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		// Symbols
		////////////////////////////////////////////////////////////////////////////////////////////////////
		{
			RadioButton symbolsVisibleButton = new RadioButton("Symbols Visible");
			symbolsVisibleButton.setSelected(css.getSymbolsVisible(series));

			symbolsVisibleButton.setMinSize(FXUtil.getWidth(symbolsVisibleButton)+30, FXUtil.getHeight(symbolsVisibleButton));
			gridPane.add(symbolsVisibleButton, 1, row++, 3, 1); // col, row
			symbolsVisibleButton.setOnAction((ActionEvent event) -> { 
				if (symbolsVisibleButton.isSelected()) {
					css.setSymbolsVisible(series, true); 
					css.setSymbolColor(series, css.getSymbolColor(series));
					Legend.createLegendItem(series, css, label);	
				}
				else {
					css.setSymbolsVisible(series, false); 
					Legend.createLegendItem(series, css, label);
				}
			});
		}

		
		{
			gridPane.add(new Text("Symbol :"), 1, row); // col, row
			symbolPicker.setOnSymbolSelection(event -> {
				css.clearStyleSettings(series);
				css.setSymbol(series, symbolPicker.getValue());
				css.setSymbolColor(series, css.getSymbolColor(series));
				Legend.createLegendItem(series, css, label);
				SymbolStyle symbolStyle = CSS.getSymbolStyle(symbolPicker.getValue());
				symbolStyleChoiceBox.setValue(symbolStyle);
			});
			gridPane.add(symbolPicker,3,row++);
		}	

		{
			gridPane.add(new Text("Symbol Color"), 1, row); // col, row
			symbolColorPicker.setMaxSize(MAX_CHOICEBOX_SIZE, Double.MAX_VALUE);
			symbolColorPicker.setOnAction(event -> {
				css.clearStyleSettings(series);
				css.setSymbolColor(series, symbolColorPicker.getValue());
				Legend.createLegendItem(series, css, label);
				symbolPicker.setButtonSymbolColor(css.getSymbolColor(series));
				seriesColorPicker.setValue(css.getSymbolColor(series));
			});
			gridPane.add(symbolColorPicker,3,row++);
		}
		
		{
			gridPane.add(new Text("Symbol Size"), 1, row); // col, row
			symbolSizePicker.setMaxSize(MAX_CHOICEBOX_SIZE, Double.MAX_VALUE);
			symbolSizePicker.setOnAction(event -> {
				css.setSymbolSize(series, symbolSizePicker.getValue());
			});
			gridPane.add(symbolSizePicker,3,row++);
		}
		
		{
			gridPane.add(new Text("Symbol Style"), 1, row); // col, row
			symbolStyleChoiceBox.setMaxSize(MAX_CHOICEBOX_SIZE, Double.MAX_VALUE);
			symbolStyleChoiceBox.setOnAction(event -> {
				Symbol symbol = css.getSymbol(series);
				SymbolStyle newSymbolStyle = symbolStyleChoiceBox.getValue();
				Symbol newSymbol = CSS.getChangedSymbol(symbol,newSymbolStyle);
				symbolPicker.setValue(newSymbol, css.getSymbolColor(series));
				css.setSymbol(series, newSymbol);
				Legend.createLegendItem(series, css, label);
			});
			gridPane.add(symbolStyleChoiceBox,3,row++);
		}
		
		addSeparator(gridPane, row++);

		////////////////////////////////////////////////////////////////////////////////////////////////////
		// Lines
		////////////////////////////////////////////////////////////////////////////////////////////////////
		{
			RadioButton linesVisibleButton = new RadioButton("Lines Visible");
			linesVisibleButton.setSelected(css.getLinesVisible(series));
			linesVisibleButton.setMinSize(FXUtil.getWidth(linesVisibleButton)+30, FXUtil.getHeight(linesVisibleButton));
			gridPane.add(linesVisibleButton, 1, row++, 3, 1); // col, row
			linesVisibleButton.setOnAction((ActionEvent event) -> { 
				if (linesVisibleButton.isSelected()) {
					css.setLinesVisible(series, true);
					Legend.createLegendItem(series, css, label);
				}
				else {				
					css.setLinesVisible(series, false);
					Legend.createLegendItem(series, css, label);
				}
			});
		}


		{
			gridPane.add(new Text("Line Color"), 1, row); // col, row
			lineColorPicker.setMaxSize(MAX_CHOICEBOX_SIZE, Double.MAX_VALUE);
			lineColorPicker.setOnAction(event -> { css.setLineColor(series, lineColorPicker.getValue());});
			gridPane.add(lineColorPicker,3,row++);
		}

		{
			gridPane.add(new Text("Line Width"), 1, row); // col, row
			lineWidthPicker.setMaxSize(MAX_CHOICEBOX_SIZE, Double.MAX_VALUE);
			lineWidthPicker.setOnAction(event -> { css.setLineWidth(series, lineWidthPicker.getValue());});
			gridPane.add(lineWidthPicker,3,row++);
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
