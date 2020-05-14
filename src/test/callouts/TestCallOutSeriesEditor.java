package test.callouts;

import java.util.Random;

import javaFX.ext.controls.Instructions;
import javaFX.ext.css.CSS;
import javaFX.ext.css.CSS.SymbolStyle;
import javaFX.ext.utility.ListIterator;
import javaFX.ext.utility.Logger;
import javaFX.ext.utility.MyColors;
import javaFX.plots.NumberPlotData;
import javaFX.plots.callouts.CallOut;
import javaFX.plots.callouts.CallOutSettings.Angle;
import javaFX.plots.overlay.SceneOverlay;
import javaFX.plots.overlay.SceneOverlay.SceneOption;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import test.FXTester;


public class TestCallOutSeriesEditor implements FXTester {

	Random random = new Random();
	@Override
	public void execute(Logger logger) {
		final int RANGE = Angle.values().length;
		
		ListIterator<Angle> angleList = new ListIterator<Angle>(Angle.values());
		ListIterator<Double> lengthList = new ListIterator<Double>(new Double[] {6.0, 8.0, 10.0, 14.0, 20.0, 30.0, 40.0, 60.0, 80.0, 100.0});
		ListIterator<Double> widthList = new ListIterator<Double>(new Double[] {0.5, 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0});
		ListIterator<Color> colorList = new ListIterator<Color>(MyColors.plotColors);
		
		
		XYChart.Series<Number,Number> sAngle0 = FXTester.getSeriesData("Angles 000 - 090", angleList.size()/4, 1.0,  7.0,  
				(xx -> xx.doubleValue()+RANGE/(angleList.size()/4)), 
				(yy -> 7.0));
		
		XYChart.Series<Number,Number> sAngle90 = FXTester.getSeriesData("Angles 090 - 180", angleList.size()/4, 1.0,  6.0,  
				(xx -> xx.doubleValue()+RANGE/(angleList.size()/4)), 
				(yy -> 6.0));
		
		XYChart.Series<Number,Number> sAngle180 = FXTester.getSeriesData("Angles 180 - 270", angleList.size()/4, 1.0,  5.0,  
				(xx -> xx.doubleValue()+RANGE/(angleList.size()/4)), 
				(yy -> 5.0));
		
		XYChart.Series<Number,Number> sAngle270 = FXTester.getSeriesData("Angles 270 - 360", angleList.size()/4, 1.0,  4.0,  
				(xx -> xx.doubleValue()+RANGE/(angleList.size()/4)), 
				(yy -> 4.0));


		NumberPlotData plotData = new NumberPlotData();
		plotData.addAll(sAngle0, sAngle90, sAngle180, sAngle270);
		
		XYChart.Series<Number,Number> sColor = FXTester.getSeriesData("Line Color", RANGE/5, 1.0,  3.0,  
				(xx -> xx.doubleValue()+5), 
				(yy -> 3.0));

		XYChart.Series<Number,Number> sLength = FXTester.getSeriesData("Line Length", lengthList.size(), 1.0,  2.0,  
				(xx -> xx.doubleValue()+RANGE/lengthList.size()), 
				(yy -> 2.0));
		
		XYChart.Series<Number,Number> sWidth = FXTester.getSeriesData("Line Width", widthList.size(), 1.0,  1.0,  
				(xx -> xx.doubleValue()+RANGE/widthList.size()), 
				(yy -> 1.0));


		plotData.addAll(sLength, sWidth, sColor);
		
		final NumberAxis xAxis = new NumberAxis();
		final NumberAxis yAxis = new NumberAxis();
		xAxis.setLabel("X");
		yAxis.setLabel("Y");
		final LineChart<Number,Number> lineChart = new LineChart<Number,Number>(xAxis,yAxis);              
		lineChart.setTitle("Test Setting Line Properties");
		lineChart.getData().addAll(plotData.getJavaFXSeries());


		{
			var callOutAngles = new CallOut("line angle");
			var cos = callOutAngles.copyDefaultSettings();
			for (var data : sAngle0.getData()) {
				cos.setAngle(angleList.getNext());
				callOutAngles.create(data.getXValue(), data.getYValue(),angleList.repeat().toString(),cos);			
			}
			for (var data : sAngle90.getData()) {
				cos.setAngle(angleList.getNext());	// continues from previous
				callOutAngles.create(data.getXValue(), data.getYValue(),angleList.repeat().toString(),cos);			
			}
			for (var data : sAngle180.getData()) {
				cos.setAngle(angleList.getNext());   // continues from previous
				callOutAngles.create(data.getXValue(), data.getYValue(),angleList.repeat().toString(),cos);			
			}
			for (var data : sAngle270.getData()) {
				cos.setAngle(angleList.getNext());   // continues from previous
				callOutAngles.create(data.getXValue(), data.getYValue(),angleList.repeat().toString(),cos);			
			}
			callOutAngles.addToChart(lineChart);
		}
		{
			var callOutColor = new CallOut("line color");
			callOutColor.defaultCallOutSettings.setAngle(Angle.a345);
			callOutColor.defaultCallOutSettings.setTextRotated(false);
			var cos = callOutColor.copyDefaultSettings();
			for (var data : sColor.getData()) {
				cos.setLineColor(colorList.getNext());
				cos.setFontColor(colorList.repeat());
				callOutColor.create(data.getXValue(), data.getYValue(),"\u25a0 colored",cos);			
			}
			callOutColor.addToChart(lineChart);
		}
		{
			var callOutLength = new CallOut("line length");
			callOutLength.defaultCallOutSettings.setAngle(Angle.a345);
			callOutLength.defaultCallOutSettings.setTextRotated(false);
			var cos = callOutLength.copyDefaultSettings();
			for (var data : sLength.getData()) {
				cos.setLineLength(lengthList.getNext());
				callOutLength.create(data.getXValue(), data.getYValue(),"length "+lengthList.repeat().toString(),cos);			
			}
			callOutLength.addToChart(lineChart);
		}
		
		{
			var callOutWidth = new CallOut("line width");
			callOutWidth.defaultCallOutSettings.setAngle(Angle.a345);
			callOutWidth.defaultCallOutSettings.setTextRotated(false);
			var cos = callOutWidth.copyDefaultSettings();
			for (var data : sWidth.getData()) {
				cos.setLineWidth(widthList.getNext());
				cos.setAngle(Angle.a345);
				cos.setTextRotated(false);
				callOutWidth.create(data.getXValue(), data.getYValue(),"width "+widthList.repeat().toString(),cos);			
			}
			callOutWidth.addToChart(lineChart);
		}

		
		
		CSS css = new CSS(lineChart,SymbolStyle.unfilled);
		
		Scene scene = new Scene(lineChart,1200,600);
		
		SceneOverlay.addOverlays(scene, logger, SceneOption.Legend, SceneOption.EditMenu);	

		Stage stage = FXTester.displayResults(scene);

		CallOut.configureCallOuts(stage);
		
		Instructions txt = new Instructions(stage.getScene());
		txt.addCenter("Tests CallOut Series Editor");
		txt.add("Use the Context Menu to select one or more Call Out Editors");
		txt.add("-- Rotation Angle, Color, Length, and Width");
		txt.add("Only the CallOuts associated with the named editor you select will be changed");
		txt.display();
	}
}
