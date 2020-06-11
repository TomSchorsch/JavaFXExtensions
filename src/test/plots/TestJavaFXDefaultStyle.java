package test.plots;

import java.util.Random;

import javaFX.ext.controls.Instructions;
import javaFX.ext.utility.Logger;
import javaFX.plots.NumberPlotData;
import javaFX.plots.Plot;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import test.FXTester;


public class TestJavaFXDefaultStyle implements FXTester {

	Random random = new Random();
	@Override
	public void execute(Logger logger) {

		NumberPlotData plotData = new NumberPlotData();

		// Generate data
		for (double d = 1.0; d <=32.0; d = d+1.0) {
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
		lineChart.setTitle("JavaFX Default Symbols and Colors");
		lineChart.getData().addAll(plotData.getJavaFXSeries());

		Scene scene = new Scene(lineChart,1200,600);
		Stage stage = FXTester.displayResults(scene);
		
		Instructions txt = new Instructions(stage.getScene());
		txt.addCenter("Tests the JavaFX Default Symbols and Colors");
		txt.add("By default JavaFX only uses 8 colors and the same symbol (whitefilled circle) for all line plots");
		txt.add("The only way to get more colors or more symbols is to rewrite that portion of JavaFX");
		txt.add("This test justs shows what the default colors and symbol (singular) is for a Java FX Line Chart");
		txt.display();
	}
}
