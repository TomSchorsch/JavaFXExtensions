package javaFX.plots;

import javaFX.plots.axis.NumberAxis;
import javaFX.plots.axis.SSMAxis;
import javafx.collections.FXCollections;

public class SSMPlot extends Plot {

	/**
	 * Construct a new LineChart with the X default axis an SSM Axis.
	 *
	 */
	public SSMPlot() {
		super(new SSMAxis(), new NumberAxis(), FXCollections.<Series<Number, Number>>observableArrayList());
		setAxisSortingPolicy(SortingPolicy.NONE);
	}

}
