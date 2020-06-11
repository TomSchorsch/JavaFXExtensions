package test.plots;

import java.util.Random;

import javaFX.ext.controls.Instructions;
import javaFX.ext.utility.Logger;
import javaFX.plots.NumberPlotData;
import javaFX.plots.Plot;
import javaFX.plots.overlay.SceneOverlayManager;
import javaFX.plots.overlay.SceneOverlayManager.SceneOption;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart.SortingPolicy;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import test.FXTester;


public class TestPlotEditor implements FXTester {

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
		final Plot lineChart = new Plot();              
		lineChart.getXAxis().setLabel("X");
		lineChart.getYAxis().setLabel("Y");           
		lineChart.setTitle("Test Plot Settings Editor");
		lineChart.setAxisSortingPolicy(SortingPolicy.NONE);
		lineChart.addData(plotData.getJavaFXSeries());

		Scene scene = new Scene(lineChart,1200,600);
		
		SceneOverlayManager.addOverlays(scene, logger, SceneOption.Legend, SceneOption.EditMenu, SceneOption.ZoomManager);	
		
		Stage stage = FXTester.displayResults(scene);
		
		Instructions txt = new Instructions(stage.getScene());
		txt.addCenter("Tests the Plot Settings Editor");
		txt.add("Right-Click on the plot to open the context Menu, click on the Plot Settings Editor");
		txt.add("Test setting the Classification Visibility");
		txt.add("Test setting the Title and subTitle Text and Visibility");
		txt.add("Test setting the Legend Position and Visibility");
		txt.add("Test changing the Plot info (lower left corner text)");
		txt.add("Experiment changing the Plot Settings (changes all Series Symbol and Line Properties)");
		txt.add("<b>The Series on the Plot, the Legend, and the Plot Settings Editor itself should change to reflect the user interactions</b>");		
		txt.add("-- Change Symbol Visibility, Symbol (only Symbols of the chosen symbol style are avialable), Symbol Color, Symbol Size, Symbol Style");
		txt.add("-- Change Line Visibility, Line Color, Line Width");
		txt.display();
	}
}
