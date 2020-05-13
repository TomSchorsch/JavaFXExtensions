package javaFX.plots;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class NumberPlotData extends PlotData<Number,Number> {

	public NumberPlotData getCDFofY() {
		NumberPlotData seriesData = new NumberPlotData();
		for (String seriesName : mapSeries2Data.keySet() ) {
			List<Number> list = mapSeries2Data.get(seriesName).stream()
					.map(p->p.y)
					.collect(Collectors.toList());
			Collections.sort(list, NumberComparator);
			if (!seriesData.mapSeries2Data.containsKey(seriesName)) {
				seriesData.mapSeries2Data.put(seriesName, new ArrayList<Pair<Number,Number>>(list.size()));
			}
			List<Pair<Number,Number>> dataList = seriesData.mapSeries2Data.get(seriesName);
			if (list.size() == 0) {
				// do nothing
			}
			if (list.size() == 1) {
				dataList.add(new Pair<Number, Number>(0, list.get(0)));
				dataList.add(new Pair<Number, Number>(100, list.get(0)));
			}
			else { 
				double rangeOffset = 99.0/(list.size()-1.0);
				int i = 0;
				for (Number y : list) {
					dataList.add(new Pair<Number, Number>(y, rangeOffset*i++));
				}
			}
		}
		return seriesData;
	}

	public NumberPlotData getCDFofX() {
		NumberPlotData seriesData = new NumberPlotData();
		for (String seriesName : mapSeries2Data.keySet() ) {
			List<Number> list = mapSeries2Data.get(seriesName).stream()
					.map(p->p.x)
					.collect(Collectors.toList());
			Collections.sort(list, NumberComparator);
			double rangeOffset = 99.0/(list.size()-1);
			if (!seriesData.mapSeries2Data.containsKey(seriesName)) {
				seriesData.mapSeries2Data.put(seriesName, new ArrayList<Pair<Number,Number>>(list.size()));
			}
			List<Pair<Number,Number>> dataList = seriesData.mapSeries2Data.get(seriesName);		
			if (list.size() == 0) {
				// do nothing
			}
			if (list.size() == 1) {
				dataList.add(new Pair<Number, Number>(0, list.get(0)));
				dataList.add(new Pair<Number, Number>(100, list.get(0)));
			}
			else { 
				int i = 0;
				for (Number x : list) {
					dataList.add(new Pair<Number, Number>(x, rangeOffset*i++));
				}
			}
		}
		return seriesData;
	}

	// Stole this... apparently comparing "Number" is really hard if you have to account for every possible implementation of number by anyone
	// I know I could have written a simple one based on number.doubleValue() but since this was "free"
	Comparator<Number> NumberComparator = (n1,n2) -> ((Double)n1.doubleValue()).compareTo(n2.doubleValue());

}
