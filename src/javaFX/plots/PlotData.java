package javaFX.plots;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javaFX.plots.axis.AxisTickFormatter;
import javaFX.plots.axis.DefaultAxisTickFormatter;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;

public class PlotData<XTYPE, YTYPE> {
	static int STARTING_SIZE = 100;
	final Map<String,Number> mapXString2Number = new LinkedHashMap<String,Number>();
	final Map<Number,String> mapXNumber2String = new LinkedHashMap<Number,String>();
	final Map<String,Number> mapYString2Number = new LinkedHashMap<String,Number>();
	final Map<Number,String> mapYNumber2String = new LinkedHashMap<Number,String>();

	protected Map<String,List<Pair<XTYPE,YTYPE>>> mapSeries2Data = new HashMap<String,List<Pair<XTYPE,YTYPE>>>();
	
	public static Comparator<String> normalSort = Comparator.naturalOrder();
	public static Comparator<String> reverseSort = normalSort.reversed();
	public static Comparator<String> noSort = (o1,o2) -> 0;
	private Comparator<String> defaultXComparator = normalSort;
	private Comparator<String> defaultYComparator = normalSort;

	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Public interface
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void add(String seriesName, XTYPE x, YTYPE y) {
		if (!mapSeries2Data.containsKey(seriesName)) {
			mapSeries2Data.put(seriesName, new ArrayList<Pair<XTYPE,YTYPE>>(STARTING_SIZE));
		}
		mapSeries2Data.get(seriesName).add(new Pair<XTYPE,YTYPE>(x,y));
	}
	
	@SafeVarargs
	public final void addAll(Series<XTYPE,YTYPE> ... series) {
		for (Series<XTYPE,YTYPE> s : series) {
			addAll(s.getName(),s.getData());		
		}
	}
	
	public List<String> getSeriesNames() {
		List<String> list = new ArrayList<String>(mapSeries2Data.size());
		list.addAll(mapSeries2Data.keySet());
		Collections.sort(list);
		return list;
	}
	
	public List<Pair<XTYPE,YTYPE>> getSeriesData(String series) {
		return mapSeries2Data.get(series);
	}
	
	public void setXAxisComparator(Comparator<String> comp) { defaultXComparator = comp;}
	public void setYAxisComparator(Comparator<String> comp) { defaultYComparator = comp;}
	
	public List<Series<Number,Number>> getJavaFXSeries() {		
		Pair<XTYPE,YTYPE> p = mapSeries2Data.get(getSeriesNames().get(0)).get(0);
		if (p.x instanceof String) {
			setMapXString2Number();
		}
		if (p.y instanceof String) {
			setMapYString2Number();
		}
		List<Series<Number,Number>> list = new ArrayList<Series<Number,Number>>();
		for (String seriesName : this.getSeriesNames()) {
			list.add(getJavaFXSeries(seriesName));
		}
		return list;
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
	
	public AxisTickFormatter getXAxisTickFormatter() {
		AxisTickFormatter atf = new DefaultAxisTickFormatter() {
			@Override
			public String format( Number value ) {
				if (value.doubleValue() == value.intValue() && mapXNumber2String.containsKey(value.intValue())) {
					return mapXNumber2String.get(value.intValue());
				}
				return "";
			}
			
			public List<String> getlabels(){
				List<String> list = new ArrayList<String>();
				for (String s : mapXString2Number.keySet()) {
					list.add(s);
				}
				return list;
			}
			
			public void setlabels(List<String> labels){
				if (labels.size() != mapXString2Number.keySet().size()) {
					System.out.println("AxisTickFormatter setLabels are of the wrong size");
				}
				else {
					int i = 1;
					for (String s : labels) {
						mapXNumber2String.put(i,s);
						mapXString2Number.put(s, i++);
						
					}
				}
			}
		};
		return atf;
	}
	
	public AxisTickFormatter getYAxisTickFormatter() {
		AxisTickFormatter atf = new DefaultAxisTickFormatter() {
			@Override
			public String format( Number value ) {
				if (value.doubleValue() == value.intValue() && mapYNumber2String.containsKey(value.intValue())) {
					return mapYNumber2String.get(value.intValue());
				}
				return "";
			}
			
			public List<String> getlabels(){
				List<String> list = new ArrayList<String>();
				for (String s : mapYString2Number.keySet()) {
					list.add(s);
				}
				return list;
			}
			
			public void setlabels(List<String> labels){
				if (labels.size() != mapYString2Number.keySet().size()) {
					System.out.println("AxisTickFormatter setLabels are of the wrong size");
				}
				else {
					int i = 1;
					for (String s : labels) {
						mapYNumber2String.put(i,s);
						mapYString2Number.put(s, i++);
						
					}
				}
			}

			
		};
		return atf;
	
	}
	
	public Number convertXType(XTYPE val) {
		if (val instanceof Number) return (Number)val;
		else if (val instanceof String) return mapXString2Number.get(val);
		else return 0;
	}
	public Number convertYType(YTYPE val) {
		if (val instanceof Number) return (Number)val;
		else if (val instanceof String) return mapYString2Number.get(val);
		else return 0;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// private interface
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


	private Series<Number,Number> getJavaFXSeries(String seriesName) {
		Series<Number,Number> seriesData = new Series<Number,Number>();
		seriesData.setName(seriesName);
		mapSeries2Data.get(seriesName)
		.forEach(p->seriesData.getData().add(new Data<Number, Number>(convertXType(p.x), convertYType(p.y))));
		return seriesData;
	}
	
	private void setMapXString2Number() {
		Set<String> set = new HashSet<String>();
		for (String seriesName : this.getSeriesNames()) {
			mapSeries2Data.get(seriesName).forEach(p -> set.add((String)p.x));
		}
		setMaps(set,mapXString2Number,mapXNumber2String,defaultXComparator);
	}

	private void setMapYString2Number() {
		Set<String> set = new HashSet<String>();
		for (String seriesName : this.getSeriesNames()) {
			mapSeries2Data.get(seriesName).forEach(p -> set.add((String)p.y));
		}
		setMaps(set,mapYString2Number,mapYNumber2String,defaultYComparator);
	}
	
	private void setMaps(Set<String> set, Map<String,Number> mapString2Number, Map<Number,String> mapNumber2String, Comparator<String> comp) {
		String[] array = new String[set.size()];
		int i = 0;
		for (String s : set) {
			array[i++] = s;
		}
		Arrays.sort(array, comp);
		mapString2Number.clear();
		mapNumber2String.clear();
		i = 1;
		for (String s : array) {
			mapNumber2String.put(i,s);
			mapString2Number.put(s, i++);
			
		}
	}	

}
