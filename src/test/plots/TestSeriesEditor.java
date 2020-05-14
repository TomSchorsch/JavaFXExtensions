package test.plots;

import java.util.Random;

import javaFX.ext.controls.Instructions;
import javaFX.ext.css.CSS;
import javaFX.ext.css.CSS.SymbolStyle;
import javaFX.ext.utility.Logger;
import javaFX.plots.NumberPlotData;
import javaFX.plots.overlay.SceneOverlay;
import javaFX.plots.overlay.SceneOverlay.SceneOption;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.LineChart.SortingPolicy;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import test.FXTester;


public class TestSeriesEditor implements FXTester {

	Random random = new Random();
	@Override
	public void execute(Logger logger) {

		NumberPlotData plotData = new NumberPlotData();

		// Generate data
		for (double d = 1.0; d <= 32; d = d+1.0) {
			double factor = 1 + 1.0/d;
			double val = d;
			XYChart.Series<Number,Number> series = FXTester.getSeriesData("series"+String.format("%02.0f",d), 5, 1.0,  d,  
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
		lineChart.setTitle("Test Series Settings Editor");
		lineChart.setAxisSortingPolicy(SortingPolicy.NONE);
		lineChart.getData().addAll(plotData.getJavaFXSeries());
		
		new CSS(lineChart, SymbolStyle.whitefilled);
	
		Scene scene = new Scene(lineChart,1200,600);
		
		SceneOverlay.addOverlays(scene, logger, SceneOption.Legend, SceneOption.EditMenu, SceneOption.ZoomManager);	
		
		Stage stage = FXTester.displayResults(scene);
		
		Instructions txt = new Instructions(stage.getScene());
		txt.addCenter("Tests the Series Settings Editor");
		txt.add("Right-Click on a Series in the Legend to open the Series Settings Editor");
		txt.add("Experiment changing the Series Name, Color, Symbol and Line Properties");
		txt.add("<b>The Series on the Plot, the Legend, and the Series Settings Editor itself should change to reflect the user interactions</b>");		
		txt.add("-- Change the Series Name.");
		txt.add("-- Change the Series Color (affects the Symbol and Line Colors)");		
		txt.add("-- Change the Symbols Visibility, Symbol (all symbols available), Symbol Color (should affect Series color as well), Symbol Size");
		txt.add("-- Change the Line Visibility, Line Color, Line Width");
		txt.display();
	}
}
