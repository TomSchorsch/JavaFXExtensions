package test.callouts;

import java.util.Random;

import javaFX.ext.controls.Instructions;
import javaFX.ext.css.CSS;
import javaFX.ext.css.CSS.FontStyle;
import javaFX.ext.css.CSS.FontWeight;
import javaFX.ext.css.CSS.SymbolStyle;
import javaFX.ext.utility.ListIterator;
import javaFX.ext.utility.Logger;
import javaFX.ext.utility.MyColors;
import javaFX.plots.PlotData;
import javaFX.plots.axis.StableTicksAxis;
import javaFX.plots.callouts.CallOut;
import javaFX.plots.overlay.SceneOverlayManager;
import javaFX.plots.overlay.SceneOverlayManager.SceneOption;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import test.FXTester;


public class TestFontProperties implements FXTester {

	Random random = new Random();
	@Override
	public void execute(Logger logger) {
		final int RANGE = 14;
		final int FONTSIZE= 14;
		
		ListIterator<Color> colorList = new ListIterator<Color>(MyColors.plotColors);
		ListIterator<Double> sizeList = new ListIterator<Double>(new Double[] {6.0, 8.0, 9.0, 10.0, 10.5, 11.0, 12.0, 14.0, 16.0, 18.0, 20.0});
		ListIterator<FontStyle> styleList = new ListIterator<FontStyle>(FontStyle.values());
		ListIterator<FontWeight> weightList = new ListIterator<FontWeight>(FontWeight.values());
//		ListIterator<FontFamily> familyList = new ListIterator<FontFamily>(FontFamily.values());
		
		PlotData<Number,String> plotData = new PlotData<Number,String>();
		
		FXTester.setPlotData(plotData,"Font Color", RANGE, 1.0,  "Font Color",  
				(xx -> xx.doubleValue()+1), 
				(yy -> "Font Color"));
		FXTester.setPlotData(plotData,"Font Size", sizeList.size(), 1.0,  "Font Size",  
				(xx -> xx.doubleValue()+RANGE/sizeList.size()), 
				(yy -> "Font Size"));
		FXTester.setPlotData(plotData,"Font Style", styleList.size(), 1.0,  "Font Style",  
				(xx -> xx.doubleValue()+RANGE/styleList.size()), 
				(yy -> "Font Style"));
		FXTester.setPlotData(plotData,"Font Weight", weightList.size(), 1.0,  "Font Weight",  
				(xx -> xx.doubleValue()+RANGE/weightList.size()), 
				(yy -> "Font Weight"));

//		XYChart.Series<Number,Number> sSize = FXTester.getSeriesData("Font size", sizeList.size(), 1.0,  3.0,  
//				(xx -> xx.doubleValue()+RANGE/sizeList.size()), 
//				(yy -> 3.0));
//		XYChart.Series<Number,Number> sStyle = FXTester.getSeriesData("Font style", styleList.size(), 1.0,  2.0,  
//				(xx -> xx.doubleValue()+RANGE/styleList.size()), 
//				(yy -> 2.0));
//		XYChart.Series<Number,Number> sWeight = FXTester.getSeriesData("Font weight", weightList.size(), 1.0,  1.0,  
//				(xx -> xx.doubleValue()+RANGE/weightList.size()), 
//				(yy -> 1.0));


//		NumberPlotData plotData = new NumberPlotData();
//		plotData.addAll(sSize, sColor, sStyle, sWeight);


		var callOutFontSize = new CallOut<Number,String>("CallOutFontSize", plotData);
		var cos = callOutFontSize.copyDefaultSettings();

		for (var data : plotData.getSeriesData("Font Size")) {
			cos.setFontSize(sizeList.getNext());
			callOutFontSize.create(data.x, data.y, sizeList.repeat().toString(),cos);			
		}

		var callOutFontColor = new CallOut<Number,String>("CallOutFontColor", plotData);
		cos = callOutFontColor.copyDefaultSettings();
		colorList.set(Color.BLACK);
		for (var data : plotData.getSeriesData("Font Color")) {
			cos.setColor(colorList.getNext());
			callOutFontColor.create(data.x, data.y,"\u25a0 colored",cos);			
		}

		var callOutFontStyle = new CallOut<Number,String>("CallOutFontStyle", plotData);
		cos = callOutFontStyle.copyDefaultSettings();
		for (var data : plotData.getSeriesData("Font Style")) {
			cos.setFontStyle(styleList.getNext());
			cos.setFontSize(FONTSIZE);
			callOutFontStyle.create(data.x, data.y,styleList.repeat().toString(),cos);			
		}

		var callOutFontWeight = new CallOut<Number,String>("CallOutFontWeight", plotData);
		cos = callOutFontWeight.copyDefaultSettings();
		for (var data : plotData.getSeriesData("Font Weight")) {
			cos.setFontWeight(weightList.getNext());
			cos.setFontSize(FONTSIZE);
			callOutFontWeight.create(data.x, data.y,weightList.repeat().toString(),cos);			
		}

		final StableTicksAxis xAxis = new StableTicksAxis();
		final StableTicksAxis yAxis = new StableTicksAxis();
		xAxis.setLabel("X");
		yAxis.setLabel("Y");
		final LineChart<Number,Number> lineChart = new LineChart<Number,Number>(xAxis,yAxis);              
		lineChart.getData().addAll(plotData.getJavaFXSeries());
		
		yAxis.setAxisTickFormatter(plotData.getYAxisTickFormatter());
		lineChart.setTitle("CallOut Font Properties");
		
		CSS css = new CSS(lineChart,SymbolStyle.unfilled);
		
		Scene scene = new Scene(lineChart,1200,600);

		SceneOverlayManager.addOverlays(scene, logger, SceneOption.Legend, SceneOption.EditMenu);	

		CallOut.configure(scene, callOutFontSize, callOutFontColor, callOutFontStyle, callOutFontWeight);		

		Stage stage = FXTester.displayResults(scene);
		
		Instructions txt = new Instructions(stage.getScene());
		txt.addCenter("Tests CallOut Text (Font) properties");
		txt.add("The CallOut text can have varied");
		txt.add("-- Color, Font Family, Font Size, Font Style, and Font Weight");
		txt.add("This test displays some of the possible variations");
		txt.display();
	}



}
