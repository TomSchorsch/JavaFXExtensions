package javaFX.plots;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;

public class PlotData<XTYPE, YTYPE> {
	static int STARTING_SIZE = 100;
	protected Map<String,List<Pair<XTYPE,YTYPE>>> mapSeries2Data = new HashMap<String,List<Pair<XTYPE,YTYPE>>>();

	public void add(String seriesName, XTYPE x, YTYPE y) {
		if (!mapSeries2Data.containsKey(seriesName)) {
			mapSeries2Data.put(seriesName, new ArrayList<Pair<XTYPE,YTYPE>>(STARTING_SIZE));
		}
		mapSeries2Data.get(seriesName).add(new Pair<XTYPE,YTYPE>(x,y));
	}
	
	public List<String> getSeriesNames() {
		List<String> list = new ArrayList<String>(mapSeries2Data.size());
		list.addAll(mapSeries2Data.keySet());
		Collections.sort(list);
		return list;
	}
	
	public List<Series<XTYPE,YTYPE>> getJavaFXSeries() {
		List<Series<XTYPE,YTYPE>> list = new ArrayList<Series<XTYPE,YTYPE>>();
		for (String seriesName : this.getSeriesNames()) {
			list.add(getJavaFXSeries(seriesName));
		}
		return list;
	}
	
	public Series<XTYPE,YTYPE> getJavaFXSeries(String seriesName) {
		Series<XTYPE,YTYPE> seriesData = new Series<XTYPE,YTYPE>();
		seriesData.setName(seriesName);
		mapSeries2Data.get(seriesName)
		.forEach(p->seriesData.getData().add(new Data<XTYPE, YTYPE>(p.x, p.y)));
		return seriesData;
	}
	
	
	
	

	@SafeVarargs
	public final void addAll(Series<XTYPE,YTYPE> ... series) {
		for (Series<XTYPE,YTYPE> s : series) {
			addAll(s.getName(),s.getData());		
		}
	}

	public void addAll(String seriesName, ObservableList<Data<XTYPE,YTYPE>> data) {
		if (!mapSeries2Data.containsKey(seriesName)) {
			mapSeries2Data.put(seriesName, new ArrayList<Pair<XTYPE,YTYPE>>(data.size()));
		}
		List<Pair<XTYPE,YTYPE>> list = mapSeries2Data.get(seriesName);
		for (Data<XTYPE,YTYPE> d : data) {
			list.add(new Pair<XTYPE,YTYPE>(d.getXValue(),d.getYValue()));
		}
	}
	
	public PlotData<YTYPE, XTYPE> swapXY() {
		PlotData<YTYPE, XTYPE> plotData = new PlotData<YTYPE, XTYPE>(); 
		for (String series : this.mapSeries2Data.keySet()) {
			List<Pair<XTYPE,YTYPE>> list = mapSeries2Data.get(series);
			for (Pair<XTYPE,YTYPE> p : list) {
				plotData.add(series, p.y, p.x);
			}
		}
		return plotData;
	}
	
}
