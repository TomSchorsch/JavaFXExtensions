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
import javaFX.plots.overlay.SceneOverlayManager;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class AllSeriesEditor {
	
	
	static Map<LineChart<?,?>,Editor> mapLineChart2Editor = new HashMap<LineChart<?,?>,Editor>();
	
	// This routine sets up the Editable window
	public static void open(LineChart<?, ?> lineChart, CSS css, double screenX, double screenY) {
		
		if (mapLineChart2Editor.containsKey(lineChart)) {
			System.out.println("Editor already open");
		}
		else {
			Editor editor = new Editor(screenX, screenY, lineChart.getScene());
			mapLineChart2Editor.put(lineChart, editor);
			editor.show("All Series Settings", getEditItems(lineChart.getScene(), css), () -> {mapLineChart2Editor.remove(lineChart); return true;});
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
		ChoiceBox<SymbolStyle> symbolStyleChoiceBox = Editor.getEnumChoiceBox(css.getSymbolStyle());

		{
//			symbolPicker.setAvailableSymbols(css.defaultSymbols);
			gridPane.add(new Text("Symbol :"), 1, row); // col, row
			symbolPicker.setOnSymbolSelection(event -> {
				css.setSymbol(symbolPicker.getValue());
				if (Legend.isLegendVisible(scene)) {
					Legend.addLegend(scene);
				}
				SymbolStyle ss = CSS.getSymbolStyle(symbolPicker.getValue());
				symbolStyleChoiceBox.setValue(ss);
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
			ComboBox<Double> comboBox = Editor.getDoubleComboBox(CSS.symbolSizeArray, css.getSymbolSize());
			comboBox.setMaxSize(MAX_CHOICEBOX_SIZE, Double.MAX_VALUE);
			comboBox.setOnAction(event -> {
				css.setSymbolSize(comboBox.getValue());
				SeriesEditor.setEditorsSymbolSize(comboBox.getValue());
			});
			gridPane.add(comboBox,3,row++);
		}
		
		{
			gridPane.add(new Text("Symbol Style"), 1, row); // col, row
			symbolStyleChoiceBox.setMaxSize(MAX_CHOICEBOX_SIZE, Double.MAX_VALUE);
			symbolStyleChoiceBox.setOnAction(event -> {
				css.changeSymbolStyle(symbolStyleChoiceBox.getValue());
				SeriesEditor.setEditorsSymbol(css);
				symbolPicker.setValue(css.getSymbol(), css.getSymbolColor());
				if (Legend.isLegendVisible(lineChart.getScene())) {
					Legend.addLegend(lineChart.getScene());;
				}
			});
			gridPane.add(symbolStyleChoiceBox,3,row++);
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
			ComboBox<Double> comboBox = Editor.getDoubleComboBox(CSS.lineWidthArray, css.getLineWidth());
			comboBox.setMaxSize(MAX_CHOICEBOX_SIZE, Double.MAX_VALUE);
			comboBox.setOnAction(event -> { 
				css.setLineWidth(comboBox.getValue());
				SeriesEditor.setEditorsLineWidth(comboBox.getValue());
				});
			gridPane.add(comboBox,3,row++);
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
