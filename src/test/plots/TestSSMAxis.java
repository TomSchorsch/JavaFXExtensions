package test.plots;

import java.util.Random;

import javaFX.ext.controls.Instructions;
import javaFX.ext.utility.Logger;
import javaFX.plots.NumberPlotData;
import javaFX.plots.Plot;
import javaFX.plots.SSMPlot;
import javaFX.plots.overlay.SceneOverlayManager;
import javaFX.plots.overlay.SceneOverlayManager.SceneOption;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart.SortingPolicy;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import test.FXTester;


public class TestSSMAxis implements FXTester {

	Random random = new Random();
	@Override
	public void execute(Logger logger) {

		NumberPlotData plotData = new NumberPlotData();

		// Generate data
		for (double d = 1.0; d <= 32; d = d+1.0) {
			double factor = 1 + 1.0/d;
			double val = d;
			XYChart.Series<Number,Number> series = FXTester.getSeriesData("series"+String.format("%02.0f",d), 5, 1.0,  d,  
					(xx -> xx.doubleValue()+Math.random()*3600.0), 
					(yy -> val+random.nextGaussian()*factor));
			plotData.addAll(series);
		}

		// Create Plot
//		final Plot lineChart = new Plot(new NumberAxis(), new SSMAxis());              
		final Plot lineChart = new SSMPlot();              
		lineChart.getXAxis().setLabel("Time (SSM)");
		lineChart.getYAxis().setLabel("Y");
		lineChart.setTitle("Test SSM Axis Editor");
		lineChart.setAxisSortingPolicy(SortingPolicy.NONE);
		lineChart.addData(plotData.getJavaFXSeries());
	
		Scene scene = new Scene(lineChart,1200,600);
		
		SceneOverlayManager.addOverlays(scene, logger, SceneOption.All);	
		
		Stage stage = FXTester.displayResults(scene);
		
		Instructions txt = new Instructions(stage.getScene());
		txt.addCenter("Tests the Seconds Since Midnight (SSM) Axis");
		txt.add("Use the zooming capability to verify that the SSM Axis works at various levels of zooming");
		txt.display();
	}
}
