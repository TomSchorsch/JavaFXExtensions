package javaFX.plots.callouts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javaFX.ext.css.CSS;
import javaFX.ext.utility.FXUtil;
import javaFX.plots.Plot;
import javaFX.plots.PlotData;
import javaFX.plots.overlay.SceneOverlayManager;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class CallOut<XTYPE,YTYPE> {
	/*
	 * An example of a CallOut at angle 0 from a given point:    ---- my CallOut
	 * 
	 *                                                              my CallOut  
	 *                                                             /
	 * An example of an CallOut at angle 75 from a given point:   /
	 *  
	 * CallOut Design:
	 * 
	 * A CallOut is its own data series but the data points have been supplemented with CallOuts like the above
	 * -- The CallOut series will need to be removed from the legend of the chart it is attached to
	 * 
	 * CallOut Design Details:
	 * The CallOut series lines are not displayed and the points are set to transparent
	 * Instead, a CallOut is created and displayed in place of the data point that is set to transparent
	 * The data points need NOT line up to existing data points from other series 
	 * 	 *  
	 * The CallOut consists of a line and a text value and its initial positioning is set by the angle (as shown in the examples above)
	 * 
	 * An angle of 0 is to the right of the point, 90 is straight up, 180 is to the left, 270 is down, etc. around to 360
	 * 
	 * The CallOUt Text can be horizontal as depicted above.  The CallOut text can also be rotated to be aligned with the line
	 * 
	 * How the CallOut is positioned on the chart so that line starts at the correct point:
	 * 
	 * (1) The angle and line lengths determine the shape of the CallOut
	 * -- The line is drawn from 0,0 to a line of the given length, drawn at the given angle
	 * -- The Text is then "added" to the end of the line at appropriate positioning (appropriate distance from 0,0 at the end of the drawn line) 
	 * (2) The line and text are added to a group and the size (Height, Width) of the group as a combined entity is determined
	 * (3) The Group (consisting of the line and text) replaces the StackPane that normally represents the Datapoint
	 * Note - The Stack Pane (and now the Group) centers its node over the data point (this is not good as the line is not pointing to that center point)
	 * (4) Translate the group so that the line (0,0) is overlayed on the center of the StackPane (where the data point is)
	 * -- I.e. shift the group by 1/2 its width and/or height (positively or negatively depending on the angle) so the line end point is now in the center of the stack pane
	 * (5) Make the original data point transparent and mouse invisible so all that is seen is the CallOut
	 * Note - The end point of the CallOut is guaranteed to be displayed as it is over the (now invisible) data point, 
	 * 			but the rest of the line and the text may be off Screen
	 * (6) Create a second data point (also not visible / mouse visible and place its location on the text (opposite from where the line origination is)
	 * Note: The System will try to ensure both data points are visible (extend the x & Y axis until they are). 
	 * 			Between the first data point and second data point the entire CallOut should be visible on the display
	 * (7) perform a final shift to ensure the line origin point is directly over the data point the CallOut is commenting on
	 * Note: In many cases  the text (whether rotated or not) causes the line origin point to NOT be at a corner of the groups (line and text) layout bounds.
	 *       This means that the quadrant shift (performed above) will be off.  As a final check,look at the line origin point layout bonds and the   
	 *       group layout bounds and remove and delta between the two.
	 * 
	 * The CallOut can be dragged by selecting the line and dragging it
	 * 
	 * The CallOut can be rotated around the point with its line extended by dragging the text around the point
	 * 
	 * The CallOut settings can be modified by right clicking on the CallOut and making changes in the pop-up
	 * 
	 * CallOuts can be created on the fly if programmed so at the chart level) - NOT IMPLEMENTED
	 * 
	 * Settings for the CallOuts can be the default, modified default, or supplied via a template with every CallOut added 
	 * 
	 * A single chart can have multiple CallOut series.  each series can have a different set of settings associated with them
	 * 
	 */
	
	// All CallOuts will have the default CallOut settings applied to them.  
	// However, those defaults can be modified after creating the CallOut object and before adding CallOut data points
	public CallOutSettings defaultCallOutSettings = new CallOutSettings();

	static Map<Scene,List<CallOut<?,?>>> mapScene2CallOuts = new HashMap<Scene,List<CallOut<?,?>>>();
	static Map<LineChart<Number,Number>,List<CallOut<?,?>>> mapLineChart2CallOuts = new HashMap<LineChart<Number,Number>,List<CallOut<?,?>>>();
	public static Set<String> setCallOutSeries = new HashSet<String>();

	public static List<CallOut<?,?>> getCallOuts(Scene scene) {
		if (mapScene2CallOuts.containsKey(scene)) return mapScene2CallOuts.get(scene);
		else return new ArrayList<CallOut<?,?>>();
	}
	
	// Two maps are needed to keep track of items
	// mapData2CallOut maps the Data point to the CallOut (CallOut Settings actually) that will be annnotating that data point
	// mapText2Data enables you to get to the original data given that you have the text (because the user clicked on it)
	// With the two maps you can get from the text on the display to the Data and then to the CallOut Settings
	public Map<Data<Number,Number>,CallOutSettings> mapData2CallOutSettings = new HashMap<Data<Number,Number>,CallOutSettings>(); 
	Map<Text,Data<Number,Number>> mapText2Data = new HashMap<Text,Data<Number,Number>>(); 

	// THis is the chart being annotated
	// It is needed to get the X and Y Axis to transform mouse coordinates into chart coordinates
	@SuppressWarnings("rawtypes")
	LineChart lineChart = null;

	// CallOut Behavior Settings
	boolean moveCallOutByDragging = true;
	boolean rotateCallOutbyDragging = true;
	boolean editCallOutByRightClicking = true;
	
	PlotData<XTYPE,YTYPE> plotData;
	
	@SuppressWarnings("rawtypes")
	protected Series callOutSeries;
	protected String callOutName;

	// The default constructor
	public CallOut(String name, PlotData<XTYPE,YTYPE> plotData) { 
		if (plotData.getSeriesNames().contains(name)) {
			System.out.println("Invalid callout Name '"+name+"'. It already exists in PlotData");
			return;
		}
		setCallOutSeries.add(name);
		this.callOutName = name;
		this.plotData = plotData;
	}

	public String getName() {
		return callOutName;
	}
	
	public CallOutSettings copyDefaultSettings() {
		CallOutSettings cos = new CallOutSettings();
		CallOutSettings.duplicateSettingsFromTo(defaultCallOutSettings,cos);
		return cos;
	}

	@SuppressWarnings("unchecked")
	public List<Data<Number,Number>> getData() {
		List<Data<Number,Number>> list = new ArrayList<>();
		for (Object data : callOutSeries.getData()) {
			if (mapData2CallOutSettings.containsKey(data)) {
				list.add((Data<Number,Number>)data);
			}
		}
		return list;
	}
	// These routines enable the default CallOut behavior settings to be modified
	public boolean getMoveCallOutByDragging()			{return moveCallOutByDragging;}
	public void setMoveCallOutByDragging(boolean moveCallOutByDragging)					{this.moveCallOutByDragging = moveCallOutByDragging;}
	public boolean getRotateCallOutbyDragging() 		{return rotateCallOutbyDragging;}
	public void setRotateCallOutByDragging(boolean rotateCallOutbyDragging) 			{this.rotateCallOutbyDragging = rotateCallOutbyDragging;}
	public boolean getEditCallOutByRightClicking() 		{return editCallOutByRightClicking;}
	public void setEditCallOutByRightClicking(boolean editCallOutByRightClicking)		{this.editCallOutByRightClicking = editCallOutByRightClicking;}
	
	
	List<CallOutSettings> callOutList = new ArrayList<CallOutSettings>();


	// Uses Default CallOut Settings
	public void create(XTYPE x, YTYPE y, String text) {
		create(x,y,text, this.defaultCallOutSettings);
	}

	// Uses provided CallOut Settings
	public void create(XTYPE x, YTYPE y, String text, CallOutSettings cos) {
		CallOutSettings cosDup = new CallOutSettings();
		CallOutSettings.duplicateSettingsFromTo(cos, cosDup);
		plotData.add(callOutName, x, y);
		plotData.add(callOutName, x, y);
		cosDup.setText(text);
		callOutList.add(cosDup);
	}

//	@SuppressWarnings({"rawtypes" })
//	public void addToChart(LineChart chart) {
//		this.lineChart = chart;
////		chart.getData().add(callOutSeries);
//		if (!mapLineChart2CallOuts.containsKey(lineChart)) {
//			mapLineChart2CallOuts.put(chart, new ArrayList<CallOut>());
//		}
//		mapLineChart2CallOuts.get(chart).add(this);
//	}

	// creates and configures the CallOuts given the individual CallOut Settings
	public static void configure(Scene scene, CallOut<?,?>... callOuts) {
//		Scene scene = stage.getScene();
		Plot lineChart = SceneOverlayManager.getLineChart(scene);
		if (lineChart == null) {
			System.out.println("Must call 'callOut.addToChart(lineChart);' (or equivalent) to add the CallOuts (as a data series) to the Line Chart");
			return;
		}
		if (!scene.equals(lineChart.getScene())) {
			System.out.println("Must call 'CallOut.configureCallOuts' AFTER the LineChart is attached to a Scene which is attached to a Stage");
			return;
		}
		if (!mapScene2CallOuts.containsKey(scene)) {
			mapScene2CallOuts.put(scene, new ArrayList<CallOut<?,?>>());
		}
		
		for (CallOut<?,?> callOut : callOuts) {
			callOut.lineChart = lineChart;
			for (Series<Number,Number> series : lineChart.getData()) {
				if (series.getName().equals(callOut.callOutName)) {
					List<Data<Number, Number>> listData = series.getData();
					callOut.callOutSeries = series;
					for(int i = 0; i < callOut.callOutList.size(); i++) {
						CallOutSettings cos = (CallOutSettings) callOut.callOutList.get(i);
						Data<Number,Number> data = listData.get(i*2);
						Data<Number,Number> data2 = listData.get(i*2+1);
						cos.setData(data);
						cos.setData2(data2);		
						callOut.mapData2CallOutSettings.put(data,cos);
					}
					mapScene2CallOuts.get(scene).add(callOut);
					callOut.configure();
				}
			}
		}
		
	
		
		
//		for (CallOut2 callOut : mapLineChart2CallOuts.get(lineChart)) {		
		
	
	}
	
	private void configure() {
		

		@SuppressWarnings("unchecked")
		ObservableList<Data<Number,Number>> listData = callOutSeries.getData();

		CSS css = CSS.get(lineChart);
		css.setSymbolColor(callOutSeries, Color.TRANSPARENT);
		css.setSymbolFillColor(callOutSeries, Color.TRANSPARENT);
		css.setSymbolOutlineColor(callOutSeries, Color.TRANSPARENT);
		css.setLineColor(callOutSeries, Color.TRANSPARENT);
		replaceStackPaneDataNodeWithLineAndTextGroup(listData);
		for (Data<Number,Number> data : listData) {

			// Construct the CallOut line and text
			CallOutSettings cos = mapData2CallOutSettings.get(data);
			if (cos != null) {  // skip over the data2 elements

				// Create the text and set all the properties
				Text text = setCalloutTextProperties(new Text(),cos);
				mapText2Data.put(text, data);	

				// Create the line
				Line line = createLineAndSetLineProperties(cos);

				//				Group group = new Group();
				Group group = (Group) data.getNode();
				group.getChildren().addAll(line,text);

				// Set the CallOut location (translate it from point based on the CallOut size and the selected angle)
				setCallOutDataLocation(group, cos);
				setCallOutData2Location(group,cos);

				// Add the CallOut group to the data location
				//				Pane sp = (Pane)data.getNode();
				//				sp.getChildren().add(group);

				// Set mouse handlers to the text
				setMouseTextEventHandlers(data,text,cos);
			}
		}
	}

	private void replaceStackPaneDataNodeWithLineAndTextGroup(ObservableList<Data<Number,Number>> listData) {
		listData.forEach((Data<Number,Number> data) -> {replaceStackPaneWithGroup(data);});
	}
	
	private void replaceStackPaneWithGroup(Data<Number,Number> data) {
		StackPane sp = (StackPane)data.getNode();
		Group group = (Group)sp.getParent();
		group.getChildren().remove(sp);
		Group callOutGroup = new Group();
		data.setNode(callOutGroup);
		group.getChildren().add(callOutGroup);
	}
	
	
	// Sets all of the properties of the text of the annotation
	// Note Text is not created... just modified
	protected Text setCalloutTextProperties(Text text, CallOutSettings cos) {
		text.setText(cos.getText());
		text.setFont(new Font(cos.getFontSize()));
		text.setFill(cos.getColor());
		CSS.setFontStyle(text, cos.getFontStyle());
		CSS.setFontWeight(text, cos.getFontWeight());
//		CSS.setFontFamily(text, cos.getFontFamily());
		setCallOutTextLocation( text, cos);
		return text;
	}

	// return the length of the CallOut Line in the X and Y directions given its length and rotation angle
	private double lineLengthX(CallOutSettings cos) {
		return Math.cos(cos.getAngleRadians())*cos.getLineLength();}
	private double lineLengthY(CallOutSettings cos) {
		return -Math.sin(cos.getAngleRadians())*cos.getLineLength();}


	// The text must be positioned at the end of the line
	// The line can either be pointing to the Left or Right (X-wise) and up or down (Y-wise)
	// The text must be at the end in these 4 situations
	double gap = 3.0;
	private void setCallOutTextLocation(Text text, CallOutSettings cos) {
		// since line origin is 0,0
		// its length in the X and Y directions are the end points where the text must be
		double endX = lineLengthX(cos);
		double endY = lineLengthY(cos);
		
		text.getTransforms().clear();
		
		if (cos.getTextRotated()) {
			// The text width is needed if the angle is going to the right
			double textWidth = FXUtil.getWidth(text);
			double textHeight = FXUtil.getHeight(text);
			// This fudge is based on the text height being different from the font size
			// Sometimes the fudge is needed to overcome that delta
			double ang = cos.getAngle(); 
			if (ang <= 90.0) { 			// Upper Right
				text.getTransforms().add(new Rotate(-cos.getAngleDegrees()));
				text.getTransforms().add(new Translate(cos.getLineLength()+gap,(textHeight/3.0)*(ang/90.0)));
			}
			else if (ang <= 180.0) {	// Upper Left
				text.getTransforms().add(new Rotate(180-cos.getAngleDegrees(),0,0));
				text.getTransforms().add(new Translate(-(cos.getLineLength()+textWidth+gap),-(textHeight/3.0)*((ang-180)/90.0)));
			}
			else if (ang <= 270.0) {	// Lower Left
				text.getTransforms().add(new Rotate(180-cos.getAngleDegrees(),0,0));
				text.getTransforms().add(new Translate(-(cos.getLineLength()+textWidth+gap),(textHeight/3.0)*((ang-180)/90.0)));
			}
			else {													// Lower Right
				text.getTransforms().add(new Rotate(-cos.getAngleDegrees()));
				text.getTransforms().add(new Translate(cos.getLineLength()+gap,-(textHeight/3.0)*((ang-360)/90.0)));
			}

		}
		else {
			text.setRotate(0);
			// The text width is needed if the angle is going to the right
			double textWidth = FXUtil.getWidth(text);
			double textHeight = FXUtil.getHeight(text);
			// This fudge is based on the text height being different from the font size
			// Sometimes the fudge is needed to overcome that delta
			double fudge = textHeight-cos.getFontSize();

			double ang = cos.getAngle(); 
			if (ang <= 90.0) { 			// Upper Right
				text.getTransforms().add(new Translate(endX,endY-fudge));
			}
			else if (ang <= 180.0) {	// Upper Left
				text.getTransforms().add(new Translate(endX-textWidth,endY-fudge));
			}
			else if (ang <= 270.0) {	// Lower Left
				text.getTransforms().add(new Translate(endX-textWidth,endY+textHeight-fudge));
			}
			else {													// Lower Right
				text.getTransforms().add(new Translate(endX,endY+textHeight-fudge));
			}
		}
	}

	// Create the CallOut line
	// from 0, 0 to its length along its angle
	private Line createLineAndSetLineProperties (CallOutSettings cos) {
		double endX = lineLengthX(cos);
		double endY = lineLengthY(cos);
		Line line = new Line(0,0,endX, endY);
		line.setStrokeWidth(cos.getLineWidth());
		line.setStroke(cos.getColor());
		if (cos.getLineLength() < 20) {
			line.setPickOnBounds(true);  // makes short lines easier to select						
		}
		else {
			line.setPickOnBounds(true);			
		}
		// Mouse Handler set in here as new line is created any time any of the line properties are changed
		setMouseLineEventHandlers(cos.getData(),line);
		return line;
	}


	// Creates a new line and adds it to its existing group
	protected void setCallOutLineAndPositioningProperties(Text text, CallOutSettings cos) {
		Group group = (Group)text.getParent();
		setCallOutTextLocation(text, cos);
		Line line = createLineAndSetLineProperties(cos);
		Line lineToRemove = null;
		for (Object obj : group.getChildren()) {
			if (obj instanceof Line) lineToRemove = (Line)obj; 
		}
		if (lineToRemove != null) group.getChildren().remove(lineToRemove);
		group.getChildren().add(line);

	}


	// sets the CallOut offset based on the size of the CallOut group (line + text)creates line
	// By default the CallOut is "centered" on the data point
	// its needs to be shifted by 1/2 its width and width either positively or negatively based on the CallOut rotation angle
	public void setCallOutDataLocation(Group group, CallOutSettings cos) {
		final double width = FXUtil.getWidth(group);
		final double height = FXUtil.getHeight(group);

		double ang = cos.getAngle();

		group.getTransforms().clear();

		// This accomplishes the major shifting from the CallOut being centered over the spot to being in the correct quadrant
		if (ang >= 270.0 || ang <= 90.0) {
			double translateX = width/2.0;
			group.getTransforms().add(new Translate(translateX,0));		// Shift to Right				
//			group.setTranslateX(translateX);			// Shift to Right
		}
		else {
			double translateX = -width/2.0;
			group.getTransforms().add(new Translate(translateX,0));		// Shift to Left				
//			group.setTranslateX(translateX);			// Shift to Left
		}
		if (ang >= 0 && ang < 180) {
			double translateY = -height/2.0;
			group.setTranslateY(translateY);			// Shift Up
		}
		else {
			double translateY = height/2.0;
			group.setTranslateY(translateY);		// Shift Down
		}
		
		// The following does the fine tuning.  You want the line origin point (which is at 0,0) to be over the Center point
		// Unfortunately the position of the text may shift that origin in the quadrant shifts above
		// The following fine-tunes that shift by taking into account the line bounding box and the group bounding box and getting rid of the delta.
		Line line;
		if (group.getChildren().get(0) instanceof Line) {
			line = (Line)group.getChildren().get(0);
		}
		else {
			line = (Line)group.getChildren().get(1);
		}
		Bounds lBounds = line.getBoundsInParent();
		Bounds gBounds = group.getBoundsInLocal();

		if (ang <= 90) {
			double deltaX = gBounds.getMinX() - lBounds.getMinX();
			group.getTransforms().add(new Translate(deltaX,0));
			double deltaY = gBounds.getMaxY() - lBounds.getMaxY();
			group.getTransforms().add(new Translate(0,deltaY));
		}
		else if (ang < 180 ) {
			double deltaX = gBounds.getMaxX() - lBounds.getMaxX();
			group.getTransforms().add(new Translate(deltaX,0));
		}
		else if (ang < 270) {
			double deltaX = gBounds.getMaxX() - lBounds.getMaxX();
			group.getTransforms().add(new Translate(deltaX,0));
			double deltaY = gBounds.getMinY() - lBounds.getMinY();
			group.getTransforms().add(new Translate(0,deltaY));
		}
		else {
			double deltaX = gBounds.getMinX() - lBounds.getMinX();
			group.getTransforms().add(new Translate(deltaX,0));
			double deltaY = gBounds.getMinY() - lBounds.getMinY();
			group.getTransforms().add(new Translate(0,deltaY));
		}
	}
	
	public static Line getLine(Data<Number,Number> data) {
		return getLine((Group)data.getNode());
	}
	public static Line getLine(Group group) {
		Line line;
		if (group.getChildren().get(0) instanceof Line) {
			line = (Line)group.getChildren().get(0);
		}
		else {
			line = (Line)group.getChildren().get(1);
		}
		return line;
	}
	
	public static Text getText(Data<Number,Number> data) {
		return getText((Group)data.getNode());
	}
	public static Text getText(Group group) {
		Text text;
		if (group.getChildren().get(0) instanceof Text) {
			text = (Text)group.getChildren().get(0);
		}
		else {
			text = (Text)group.getChildren().get(1);
		}
		return text;
	}


	// fools the autoresize capability to include the CallOut in its calculations (by adding a 2nd datapoint for the callOut)
	// By default the CallOut is "centered" on the data point
	// When the callout is shifted so the line start point is on the data point, the Callout text may be off screen (no longer visible)
	// By setting a data2 location near the end of the callout text, this enables the auto-resize function of the chart to grow the chart (if needed) to include that 2nd datapoint
	static final double translationFactor = 0.5;  	// This factors in the translation (1/2 width or height)
	static final double widthSizeFactor = 0.75;			// This factors in the overall size (not quite the full height or width of the group holding the annotation
	static final double heightSizeFactor = 0.55;			// This factors in the overall size (not quite the full height or width of the group holding the annotation
	static double widthFactor = translationFactor + widthSizeFactor;
	static double heightFactor = translationFactor + heightSizeFactor;
	@SuppressWarnings("unchecked")
	private void setCallOutData2Location(Group group, CallOutSettings cos) {
		final double width = FXUtil.getWidth(group);
		final double height = FXUtil.getHeight(group);
		double ang = cos.getAngle();
		double xPixels = lineChart.getXAxis().getDisplayPosition(cos.getData().getXValue());
		double yPixels = lineChart.getYAxis().getDisplayPosition(cos.getData().getYValue());
		Double x2 = null;
		Double y2 = null;
		if (ang >= 270 || ang <= 90.0) {
			double x2Scene = xPixels + width*widthFactor;	// Shift to Right
			x2 = (Double)lineChart.getXAxis().getValueForDisplay(x2Scene);
		}
		else {
			double xS2cene = xPixels - width*widthFactor;	// Shift to Left
			x2 = (Double)lineChart.getXAxis().getValueForDisplay(xS2cene);
		}
		if (ang >= 0 && ang < 180) {
			double y2Scene = yPixels - height*heightFactor;  // Shift Up
			y2 = (Double)lineChart.getYAxis().getValueForDisplay(y2Scene);
		}
		else {
			double y2Scene = yPixels + height*heightFactor;  // Shift Down
			y2 = (Double)lineChart.getYAxis().getValueForDisplay(y2Scene);
		}

		// Set the location of data 2 to be the opposite side of the group from the translated settings
		// basically the data X plus the translation plus the width/height of the group
		var data2 = cos.getData2();
		if (x2 == Double.POSITIVE_INFINITY || y2 == Double.POSITIVE_INFINITY || x2 == Double.NEGATIVE_INFINITY || y2 == Double.NEGATIVE_INFINITY ) {
			// do nothing
		}
		else {
			data2.setXValue(x2);
			data2.setYValue(y2);
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Setting Mouse Event handlers
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// Helper class for dragging and dropping
	class Delta { double x, y; } 
	private final Delta dataOffset = new Delta();

	private void setMouseTextEventHandlers(Data<Number,Number> data, Text text, CallOutSettings cos) {

		if(editCallOutByRightClicking){
			text.setOnMouseClicked((MouseEvent mouseEvent) -> callOutEditClickEvent(mouseEvent, text));
		}
		if (rotateCallOutbyDragging) {	
			text.setOnMouseEntered(mouseEvent -> {((Text)mouseEvent.getSource()).setCursor(Cursor.HAND);});
			text.setOnMouseExited (mouseEvent -> {((Text)mouseEvent.getSource()).setCursor(Cursor.DEFAULT);});
			text.setOnMousePressed(mouseEvent -> mousePressed(mouseEvent, data));
			text.setOnMouseReleased(mouseEvent -> mouseReleased(mouseEvent, data));
			text.setOnDragDetected((MouseEvent mouseEvent) -> {mouseEvent.consume();});
			text.setOnMouseDragged((MouseEvent mouseEvent) -> draggingTextToRotate(mouseEvent, cos, data));
		}	

	}

	// Enables the line (entire CallOut) to be dragged)
	private void setMouseLineEventHandlers(Data<Number,Number> data, Line line) {
		if (moveCallOutByDragging) {
			line.setOnMouseEntered(mouseEvent -> {((Line)mouseEvent.getSource()).setCursor(Cursor.HAND);});
			line.setOnMouseExited (mouseEvent -> {((Line)mouseEvent.getSource()).setCursor(Cursor.DEFAULT);});
			
			line.setOnMousePressed(mouseEvent -> mousePressed(mouseEvent, data));
			line.setOnMouseReleased(mouseEvent -> mouseReleased(mouseEvent, data));
			
			line.setOnDragDetected((MouseEvent mouseEvent) -> {mouseEvent.consume();});
			line.setOnMouseDragged(mouseEvent -> moveCallOutLineByDragging(mouseEvent, data));
		}
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Mouse Event Handlers
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


	public void mousePressed(MouseEvent mouseEvent, Data<Number,Number> data) {
		//		System.out.println("Mouse Pressed");
		if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
			((Node)mouseEvent.getSource()).setCursor(Cursor.HAND);  

			// translate mouse coordinates to the scene
			Point2D mouseSceneCoords = new Point2D(mouseEvent.getSceneX(), mouseEvent.getSceneY());
			// translate mouse coordinates to the chart part of the scene
			double localPressedX = lineChart.getXAxis().sceneToLocal(mouseSceneCoords).getX();
			double localPressedY = lineChart.getYAxis().sceneToLocal(mouseSceneCoords).getY();
			// translate mouse coordinates into the data coordinates on the chart
			Number dataPressedX = (Number)lineChart.getXAxis().getValueForDisplay(localPressedX);
			Number dataPressedY = (Number)lineChart.getYAxis().getValueForDisplay(localPressedY);
			// create an offset between where the mouse was pressed and the data in terms of the data
			// The goal is to use "dataOffset" to maintain the same  "data distance" between the mouse location and the data origin
			dataOffset.x = ((Number)data.getXValue()).doubleValue() - dataPressedX.doubleValue();
			dataOffset.y = ((Number)data.getYValue()).doubleValue() - dataPressedY.doubleValue();
			mouseEvent.consume();
		}
	}

	private void mouseReleased(MouseEvent mouseEvent, Data<Number,Number> data ) {
		// When the mouse is pressed the cursor is changed to a HAND (symbol for dragging)
		// This code changes it back to the default when the mouse is released
		Cursor cursor = ((Node)mouseEvent.getSource()).getCursor();
		if (cursor != null && cursor.equals(Cursor.HAND)) {
			((Node)mouseEvent.getSource()).setCursor(Cursor.DEFAULT);
		}
		Object node = mouseEvent.getSource();
		Group group = null;
		if (node instanceof Line) {
			group = (Group)((Line)node).getParent();
		}
		if (node instanceof Text) {
			group = (Group)((Text)node).getParent();
			if (group != null) setCallOutDataLocation(group, mapData2CallOutSettings.get(data));
			group.getParent().getParent().getParent().requestLayout();   // code needed because of a flaw in JavaFX where request Layout has been disabled for som lower level chart nodes
		}
		mouseEvent.consume();
		setCallOutData2Location(group,mapData2CallOutSettings.get(data));
	}

	@SuppressWarnings("unchecked")
	private void draggingTextToRotate(MouseEvent mouseEvent, CallOutSettings cos, Data<Number,Number> data) {
		if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
			// Get mouse coordinates on scene
			Point2D pointInScene = new Point2D(mouseEvent.getSceneX(), mouseEvent.getSceneY());
			// get mouse coordinates on the chart part of the screen
			double localMouseX = lineChart.getXAxis().sceneToLocal(pointInScene).getX();
			double localMouseY = lineChart.getYAxis().sceneToLocal(pointInScene).getY();
			// translate data coordinates into chart coordinates
			double localDataX = lineChart.getXAxis().getDisplayPosition(data.getXValue());
			double localDataY = lineChart.getYAxis().getDisplayPosition(data.getYValue());

			// Chart and Data coordinates are in local chart coordinates in "pixels"
			// calculate the angle using delta pixels from origin
			Double angle = cos.getAngleType(localDataX, localDataY, localMouseX,  localMouseY);


			// calculate the line length in pixels
			double newLineLength = cos.getLineLengthType(localDataX, localDataY, localMouseX,  localMouseY);

			// if either the angle or line length changed then update the CallOut
			if (!angle.equals(cos.getAngle()) || newLineLength != cos.getLineLength()) {
				cos.setAngle(angle);
				cos.setLineLength(newLineLength);
				setCallOutLineAndPositioningProperties(((Text)mouseEvent.getSource()), cos);
			}
			mouseEvent.consume();
			mapData2CallOutSettings.get(data).setEditorData();
		}
		else if(mouseEvent.getButton().equals(MouseButton.SECONDARY)){
		}
	}


	private void callOutEditClickEvent(MouseEvent mouseEvent, Text text) {
		if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
			var cos = mapData2CallOutSettings.get(mapText2Data.get(text));
			cos.edit(this, mouseEvent.getScreenX(), mouseEvent.getScreenY(), text);
			mouseEvent.consume();
		}
	}

	private void moveCallOutLineByDragging(MouseEvent mouseEvent, Data<Number,Number> data) {
		Node node = (Node)mouseEvent.getSource();
		node.setOnMouseDragged(e -> {
			// Get mouse coordinates on scene
			Point2D pointInScene = new Point2D(e.getSceneX(), e.getSceneY());
			// get mouse coordinates on the chart part of the screen
			double xAxisLoc = lineChart.getXAxis().sceneToLocal(pointInScene).getX();
			double yAxisLoc = lineChart.getYAxis().sceneToLocal(pointInScene).getY();
			// turn mouse chart coordinates into data coordinates
			Number x = (Number)lineChart.getXAxis().getValueForDisplay(xAxisLoc);
			Number y = (Number)lineChart.getYAxis().getValueForDisplay(yAxisLoc);
			// update the data coordinates maintain the data offset established when you pressed down the button
			Number deltaX = x.doubleValue() + dataOffset.x;
			Number deltaY = y.doubleValue() + dataOffset.y;
			// actually change the data (change to appropriate type)
			data.setXValue(deltaX);
			data.setYValue(deltaY);
			mouseEvent.consume();
			mapData2CallOutSettings.get(data).setEditorData();
		});
	}

	public void debug(double... args) {
		for (int i = 0; i < args.length; i++) {
			double d = args[i];
			System.out.print(d);
			if (i+1 < args.length) System.out.print(", ");
		}
		System.out.println();
	}

}
