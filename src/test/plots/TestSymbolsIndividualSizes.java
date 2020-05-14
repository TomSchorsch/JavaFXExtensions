package test.plots;

import java.util.Random;

import javaFX.ext.controls.Instructions;
import javaFX.ext.css.CSS;
import javaFX.ext.css.CSS.SymbolStyle;
import javaFX.ext.utility.ListIterator;
import javaFX.ext.utility.Logger;
import javaFX.plots.NumberPlotData;
import javaFX.plots.overlay.SceneOverlay;
import javaFX.plots.overlay.SceneOverlay.SceneOption;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.LineChart.SortingPolicy;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.stage.Stage;
import test.FXTester;


public class TestSymbolsIndividualSizes implements FXTester {

	Random random = new Random();
	@Override
	public void execute(Logger logger) {

		NumberPlotData plotData = new NumberPlotData();

		// Generate data
		for (double d = 1.0; d <=14.0; d = d+1.0) {
			double factor = 1 + 1.0/d;
			double val = d;
			XYChart.Series<Number,Number> series = FXTester.getSeriesData("series"+String.format("%02.0f",d), 12, 1.0,  d,  
					(xx -> xx.doubleValue()+Math.random()*factor), 
					(yy -> val+random.nextGaussian()*factor));
			plotData.addAll(series);
		}

		// Create Plot
		final NumberAxis xAxis = new NumberAxis();
		final NumberAxis yAxis = new NumberAxis();
		xAxis.setLabel("X");
		yAxis.setLabel("Y");
		final LineChart<Number,Number> lineChart = new LineChart<Number,Number>(xAxis,yAxis);              
		lineChart.setTitle("Test Symbols of different sizes on the same series");
		lineChart.setAxisSortingPolicy(SortingPolicy.NONE); 
		lineChart.getData().addAll(plotData.getJavaFXSeries());

		// change plot style
		CSS css = new CSS(lineChart, SymbolStyle.filled);
		
		ListIterator<Double> listSizes = new ListIterator<Double>(CSS.symbolSizeArray);
		for (Series<Number, Number> series : lineChart.getData()) {
			css.setSymbol(series, css.defaultSymbols.getNext());
			listSizes.reset();
			for (Data<Number, Number> data : series.getData()) {
				css.setSymbolSize(data, listSizes.getNext());
			}
		}
		
		Scene scene = new Scene(lineChart,1200,600);
		
		SceneOverlay.addOverlays(scene, logger, SceneOption.Legend, SceneOption.EditMenu);	
		
		Stage stage = FXTester.displayResults(scene);
		
		Instructions txt = new Instructions(stage.getScene());
		txt.addCenter("Tests Individual Symbol Sizes");
		txt.add("Programmatically, the individual data values of a datga series were given different sizes");
		txt.add("These sizes can be set by the Plot and Series Editors as a whole but cannot be individually changed / edited");
		txt.add("<b>I.e. there is no data point editor at this time</b>");
		txt.display();
	}
}
