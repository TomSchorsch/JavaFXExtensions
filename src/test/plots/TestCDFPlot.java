package test.plots;

import java.util.Random;

import javaFX.ext.controls.Instructions;
import javaFX.ext.utility.Logger;
import javaFX.plots.NumberPlotData;
import javaFX.plots.Plot;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.LineChart.SortingPolicy;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import test.FXTester;


public class TestCDFPlot implements FXTester {

	Random random = new Random();
	@Override
	public void execute(Logger logger) {

		XYChart.Series<Number,Number> series1 = FXTester.getSeriesData("series1", 20, 1.0,  0.0,  
				(xx -> xx.doubleValue()+Math.random()*4), 
				(yy -> -6+random.nextGaussian()*4));

		XYChart.Series<Number,Number> series2 = FXTester.getSeriesData("series2", 30, 0.0,  0.0,  
				(xx -> xx.doubleValue()+Math.random()*3.8), 
				(yy -> 6+random.nextGaussian()*6));

		NumberPlotData plotData = new NumberPlotData();
		plotData.addAll(series1,series2);
		

		{  // Tests CDF of X axis values
			final Plot lineChart = new Plot();   
			lineChart.getXAxis().setLabel("X");
			lineChart.getYAxis().setLabel("Y");
			lineChart.setTitle("Random data");
			lineChart.getData().addAll(plotData.getJavaFXSeries());


			final NumberAxis xAxis_cdf = new NumberAxis();
			final NumberAxis yAxis_cdf = new NumberAxis();
			xAxis_cdf.setLabel("X");
			yAxis_cdf.setLabel("CDF");
			final LineChart<Number,Number> lineChart_cdf = new LineChart<Number,Number>(xAxis_cdf,yAxis_cdf);              
			lineChart_cdf.setTitle("Random X data as CDF");
			lineChart_cdf.getData().addAll(plotData.getCDFofX().getJavaFXSeries());
			
			final NumberAxis xAxis_cdf_swap = new NumberAxis();
			final NumberAxis yAxis_cdf_swap  = new NumberAxis();
			xAxis_cdf_swap.setLabel("CDF");
			yAxis_cdf_swap.setLabel("X");
			final LineChart<Number,Number> lineChart_cdf_swap  = new LineChart<Number,Number>(xAxis_cdf_swap ,yAxis_cdf_swap );              
			lineChart_cdf_swap .setTitle("Random X data as CDF, Swapped Axis");
			lineChart_cdf_swap.getData().addAll(plotData.getCDFofX().swapXY().getJavaFXSeries());
			
			HBox hBox = new HBox();
			hBox.getChildren().addAll(lineChart, lineChart_cdf, lineChart_cdf_swap );
			hBox.setSpacing(10.0);
			FXTester.displayResults(hBox,1200,600);
		}


		{  // Tests CDF of Y axis values
			final NumberAxis xAxis = new NumberAxis();
			final NumberAxis yAxis = new NumberAxis();
			xAxis.setLabel("X");
			yAxis.setLabel("Y");
			final LineChart<Number,Number> lineChart = new LineChart<Number,Number>(xAxis,yAxis);              
			lineChart.setTitle("Random data");
			lineChart.setAxisSortingPolicy(SortingPolicy.NONE);
			lineChart.getData().addAll(plotData.getJavaFXSeries());

			final NumberAxis xAxis_cdf = new NumberAxis();
			final NumberAxis yAxis_cdf = new NumberAxis();
			xAxis_cdf.setLabel("Y");
			yAxis_cdf.setLabel("CDF");
			final LineChart<Number,Number> lineChart_cdf = new LineChart<Number,Number>(xAxis_cdf,yAxis_cdf);              
			lineChart_cdf.setTitle("Random Y data as CDF");
			lineChart_cdf.getData().addAll(plotData.getCDFofY().getJavaFXSeries());
			
			final NumberAxis xAxis_cdf_swap = new NumberAxis();
			final NumberAxis yAxis_cdf_swap  = new NumberAxis();
			xAxis_cdf_swap.setLabel("CDF");
			yAxis_cdf_swap.setLabel("Y");
			final LineChart<Number,Number> lineChart_cdf_swap  = new LineChart<Number,Number>(xAxis_cdf_swap ,yAxis_cdf_swap );              
			lineChart_cdf_swap .setTitle("Random Y data as CDF, Swapped Axis");
			lineChart_cdf_swap.getData().addAll(plotData.getCDFofY().swapXY().getJavaFXSeries());
			
			HBox hBox = new HBox();
			hBox.getChildren().addAll(lineChart, lineChart_cdf, lineChart_cdf_swap);
			hBox.setSpacing(10.0);
			
			
			
			Stage stage = FXTester.displayResults(hBox,1200,600);			
			
			Instructions txt = new Instructions(stage.getScene());
			txt.addCenter("Tests Cumulative Distribution Function plots");
			txt.add("The two windows contain random X, Y data plus that same data (either the X data or the Y data) coded as a CDF plot");

			
			txt.display();
		}



	}


}
