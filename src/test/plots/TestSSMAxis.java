package test.plots;

import java.util.Random;

import org.gillius.jfxutils.chart.StableTicksAxis;

import javaFX.ext.controls.Instructions;
import javaFX.ext.css.CSS;
import javaFX.ext.css.CSS.SymbolStyle;
import javaFX.ext.utility.Logger;
import javaFX.plots.NumberPlotData;
import javaFX.plots.overlay.SceneOverlay;
import javaFX.plots.overlay.SceneOverlay.SceneOption;
import javaFX.plots.timessmaxis.StableTicksSSMAxis;
import javaFX.plots.timessmaxis.TimeSSMAxis;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.LineChart.SortingPolicy;
import javafx.scene.chart.NumberAxis;
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
		final StableTicksSSMAxis xAxis = new StableTicksSSMAxis();
		final StableTicksAxis yAxis = new StableTicksAxis();
		xAxis.setLabel("Time (SSM)");
		yAxis.setLabel("Y");
		final LineChart<Number,Number> lineChart = new LineChart<Number,Number>(xAxis,yAxis);              
		lineChart.setTitle("Test SSM Axis Editor");
		lineChart.setAxisSortingPolicy(SortingPolicy.NONE);
		lineChart.getData().addAll(plotData.getJavaFXSeries());
		
		new CSS(lineChart, SymbolStyle.whitefilled);
	
		Scene scene = new Scene(lineChart,1200,600);
		
		SceneOverlay.addOverlays(scene, logger, SceneOption.All);	
		
		Stage stage = FXTester.displayResults(scene);
		
		Instructions txt = new Instructions(stage.getScene());
		txt.addCenter("Tests the Seconds SInce Midnight (SSM) Axis");
		txt.add("Use the zooming capability to verify that the SSM Axis works at various levels of zooming");
		txt.display();
	}
}
