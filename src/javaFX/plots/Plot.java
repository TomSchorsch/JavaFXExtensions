package javaFX.plots;

import java.util.List;

import javaFX.ext.css.CSS;
import javaFX.ext.css.CSS.SymbolStyle;
import javaFX.plots.axis.NumberAxis;
import javafx.beans.NamedArg;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.ValueAxis;

public class Plot extends LineChart<Number, Number> {
    // -------------- CONSTRUCTORS ----------------------------------------------

	   /**
     * Construct a new LineChart with the default axis.
     *
     * @param xAxis The x axis to use
     * @param yAxis The y axis to use
     */
    public Plot() {
        super(new NumberAxis(), new NumberAxis(), FXCollections.<Series<Number, Number>>observableArrayList());
		setAxisSortingPolicy(SortingPolicy.NONE);
    }

    /**
     * Construct a new LineChart with the given axis.
     *
     * @param xAxis The x axis to use
     * @param yAxis The y axis to use
     */
    public Plot(@NamedArg("xAxis") ValueAxis<Number> xAxis, @NamedArg("yAxis") ValueAxis<Number> yAxis) {
        super(xAxis, yAxis, FXCollections.<Series<Number, Number>>observableArrayList());
    }

    /**
     * Construct a new LineChart with the given axis and data.
     *
     * @param xAxis The x axis to use
     * @param yAxis The y axis to use
     * @param data The data to use, this is the actual list used so any changes to it will be reflected in the chart
     */
    public Plot(@NamedArg("xAxis") ValueAxis<Number> xAxis, @NamedArg("yAxis") ValueAxis<Number> yAxis, @NamedArg("data") ObservableList<Series<Number,Number>> data) {
        super(xAxis,yAxis);
        setData(data);
    }
    
    /**
     * Add data to chart
     *
     * @param data The data to use, this is the actual list used so any changes to it will be reflected in the chart
     */
    public void addData(@NamedArg("data") List<Series<Number,Number>> data) {
    	this.getData().addAll(data);
    	CSS.get(this,SymbolStyle.unfilled);
    }
    @SafeVarargs
	final public void addData(Series<Number,Number>... series) {
    	for (Series<Number,Number> s :series) {
        	this.getData().add(s);    		
    	}
    	CSS.get(this,SymbolStyle.unfilled);
    }

	@Override
	public void layoutPlotChildren()
	{
		super.layoutPlotChildren();
	}
}