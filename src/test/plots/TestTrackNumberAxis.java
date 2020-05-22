package test.plots;

import java.util.Random;

import org.gillius.jfxutils.chart.StableTicksAxis;

import javaFX.ext.controls.Instructions;
import javaFX.ext.css.CSS;
import javaFX.ext.css.CSS.SymbolStyle;
import javaFX.ext.utility.Logger;
import javaFX.plots.HoverLabel;
import javaFX.plots.NumberPlotData;
import javaFX.plots.PlotData;
import javaFX.plots.overlay.SceneOverlay;
import javaFX.plots.overlay.SceneOverlay.SceneOption;
import javaFX.plots.timessmaxis.StableTicksSSMAxis;
import javaFX.plots.timessmaxis.TimeSSMAxis;
import javafx.scene.Scene;
import javafx.scene.chart.Axis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.LineChart.SortingPolicy;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.stage.Stage;
import test.FXTester;


public class TestTrackNumberAxis implements FXTester {

	Random random = new Random();
	@Override
	public void execute(Logger logger) {

		PlotData<Number,String> plotData = new PlotData<Number,String>();

		String[] yData = {"MA003","120", "121", "122","BE001", "MA001", "BE002","MA002","123","124","BE003","MA004"};
		// Generate data
		for (String y : yData) {
			XYChart.Series<Number,String> series = new Series<>();
			series.setName(y);
			int yy = Integer.parseInt(y.substring(y.length()-1,y.length()));
			double time = (Math.random()*yy*40)+(Math.random()*10.0);
			for (int d = 1; d< 100; d++) {				
				time = time+4*Math.random()*2;
				Data<Number,String> data = new Data(time,y);
				series.getData().add(data);
			}
			plotData.addAll(series);
		}

		// Create Plot
		final StableTicksSSMAxis xAxis = new StableTicksSSMAxis();
		final CategoryAxis yAxis = new CategoryAxis();
		xAxis.setLabel("Time (SSM)");
		yAxis.setLabel("Y");
		final LineChart<Number,String> lineChart = new LineChart<Number,String>(xAxis,yAxis);              
		lineChart.setTitle("Test SSM Axis Editor");
		lineChart.getData().addAll(plotData.getJavaFXSeries());
			
		new CSS(lineChart, SymbolStyle.whitefilled);
		
		HoverLabel hl = new HoverLabel();
		for (Series series : lineChart.getData()) {
			for (Object d : series.getData()) {
				Data data = (Data)d; 
				hl.create(data, getXY(data));
			}
		}
	
		Scene scene = new Scene(lineChart,1200,600);
		
		
		SceneOverlay.addOverlays(scene, logger, SceneOption.EditMenu, SceneOption.Legend, SceneOption.ZoomManager);	
		
		Stage stage = FXTester.displayResults(scene);
		
		hl.addLabelsToChart();
		
		Instructions txt = new Instructions(stage.getScene());
		txt.addCenter("Tests Hover Labels");
		txt.add("Hover over any data point and a pop-up with the points X and Y value will appear");
		txt.add("-- Note, any data could be displayed for any point");
		txt.add("Use the zooming capability to verify that the SSM Axis works at various levels of zooming");
		txt.display();
	}
	
	private String getXY(Data data) {
		String ans = "x:"+data.getXValue().toString()+
				"\ny:"+data.getYValue().toString();
		return ans;
	}
}
