package test.plots;

import java.util.Random;

import javaFX.ext.controls.Instructions;
import javaFX.ext.css.CSS;
import javaFX.ext.utility.ListIterator;
import javaFX.ext.utility.Logger;
import javaFX.plots.NumberPlotData;
import javaFX.plots.Plot;
import javaFX.plots.overlay.SceneOverlayManager;
import javaFX.plots.overlay.SceneOverlayManager.SceneOption;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart.SortingPolicy;
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
		final Plot lineChart = new Plot();              
		lineChart.getXAxis().setLabel("X");
		lineChart.getYAxis().setLabel("Y");                
		lineChart.setTitle("Test Symbols of different sizes on the same series");
		lineChart.setAxisSortingPolicy(SortingPolicy.NONE); 
		lineChart.addData(plotData.getJavaFXSeries());

		ListIterator<Double> listSizes = new ListIterator<Double>(CSS.symbolSizeArray);
		CSS css = CSS.get(lineChart);
		for (Series<Number, Number> series : css.getSeriesFromChart()) {
			css.setSymbol(series, css.defaultSymbols.getNext());
			listSizes.reset();
			for (Data<Number, Number> data : series.getData()) {
				css.setSymbolSize(data, listSizes.getNext());
			}
		}
		
		Scene scene = new Scene(lineChart,1200,600);
		
		SceneOverlayManager.addOverlays(scene, logger, SceneOption.All);	
		
		Stage stage = FXTester.displayResults(scene);
		
		Instructions txt = new Instructions(stage.getScene());
		txt.addCenter("Tests Individual Symbol Sizes");
		txt.add("Programmatically, the individual data values of a datga series were given different sizes");
		txt.add("These sizes can be set by the Plot and Series Editors as a whole but cannot be individually changed / edited");
		txt.add("<b>I.e. there is no data point editor at this time</b>");
		txt.display();
	}
}
