package test.plots;

import java.util.Random;

import javaFX.ext.controls.Instructions;
import javaFX.ext.css.CSS;
import javaFX.ext.css.CSS.SymbolStyle;
import javaFX.ext.utility.Logger;
import javaFX.plots.PlotData;
import javaFX.plots.axis.StableTicksAxis;
import javaFX.plots.overlay.SceneOverlayManager;
import javaFX.plots.overlay.SceneOverlayManager.SceneOption;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.stage.Stage;
import test.FXTester;


public class TestTrackNumberAxisSort implements FXTester {

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
		final StableTicksAxis xAxis = new StableTicksAxis();
		final StableTicksAxis yAxis = new StableTicksAxis();
		yAxis.setMinorTickVisible(false);
		xAxis.setLabel("X");
		yAxis.setLabel("Y");
		final LineChart<Number,Number> lineChart = new LineChart<Number,Number>(xAxis,yAxis);              
		lineChart.setTitle("Test Y Axis with String values - Sorted");
		lineChart.getData().addAll(plotData.getJavaFXSeries());
		yAxis.setAxisTickFormatter(plotData.getYAxisTickFormatter());
			
		new CSS(lineChart, SymbolStyle.whitefilled);
	
		Scene scene = new Scene(lineChart,1200,600);
		
		
		SceneOverlayManager.addOverlays(scene, logger, SceneOption.EditMenu, SceneOption.Legend, SceneOption.ZoomManager);	
		
		Stage stage = FXTester.displayResults(scene);
		
		Instructions txt = new Instructions(stage.getScene());
		txt.addCenter("Tests String Axis- Sorted");
		txt.add("The Y Axis is a String, the ordering is based on the natural String order from low up to high");
		txt.display();
	}
	
	private String getXY(Data data) {
		String ans = "x:"+data.getXValue().toString()+
				"\ny:"+data.getYValue().toString();
		return ans;
	}
}
