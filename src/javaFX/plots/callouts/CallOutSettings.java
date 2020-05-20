package javaFX.plots.callouts;

import javaFX.ext.controls.Editor;
import javaFX.ext.css.CSS;
import javaFX.ext.css.CSS.FontFamily;
import javaFX.ext.css.CSS.FontStyle;
import javaFX.ext.css.CSS.FontWeight;
import javaFX.ext.utility.FXUtil;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class CallOutSettings {
	
	// These set up some reasonable values for Angle, Line Length, Line Width, and Font Size
	// Programmatically these can be set to any value but the editor offers these choices only
	public static Double[] Angle = new Double[] {0.0, 5.0, 10.0, 15.0, 20.0, 25.0, 30.0, 35.0, 40.0, 45.0, 50.0, 55.0, 60.0, 65.0, 70.0, 75.0, 80.0, 85.0,
			90.0, 95.0, 100.0, 105.0, 110.0, 115.0, 120.0, 125.0, 130.0, 135.0, 140.0, 145.0, 150.0, 155.0, 160.0, 165.0, 170.0, 175.0,
			180.0, 185.0, 190.0, 195.0, 200.0, 205.0, 210.0, 215.0, 220.0, 225.0, 230.0, 235.0, 240.0, 245.0, 250.0, 255.0, 260.0, 265.0, 
			270.0, 275.0, 280.0, 285.0, 290.0, 295.0, 300.0, 305.0, 310.0, 315.0, 320.0, 325.0, 330.0, 335.0, 340.0, 345.0, 350.0, 355.0, 360.0};

	static Double[] LineLength = new Double[] {6.0,8.0,10.0,12.0,14.0,16.0,18.0,20.0,22.0,24.0,26.0,28.0,30.0,35.0,40.0,45.0,
			50.0,55.0,60.0,70.0,80.0,90.0,100.0,110.0,120.0,130.0,140.0,150.0,160.0,170.0,180.0,190.0,200.0};
	static Double[] LineWidth = new Double[] {0.6,0.8,1.0,1.2,1.4,1.5,1.6,1.8,2.0,2.2,2.4,2.6,2.8,3.0,3.2,3.4,3.6,3.8,4.0};
	static Double[] FontSize = new Double[] {8.0,9.0,10.0,10.5,11.0,12.0,14.0,16.0,18.0,20.0,22.0,24.0,26.0,28.0,30.0};

	// Standard Setters and getters
	public Data<Object,Object> getData() {return data;}
	public void      		 setData(Data<Object,Object> data) {this.data = data;}
	protected Data<Object,Object>	getData2() {return data2;}
	protected void      		 setData2(Data<Object,Object> data2) {this.data2 = data2;}
	public String getText() {return text;}
	public void   setText(String text) {this.text = text;}
	public Boolean getTextRotated() {return textRotated;}
	public void   setTextRotated(Boolean rotated) {this.textRotated = rotated;}
	public Double getAngle() {return angle;}
	public void  setAngle(Double angle) {this.angle = angle;}
	public double getLineLength() {return lineLength;}
	public void   setLineLength(double lineLength) {this.lineLength = lineLength;}
	public double getLineWidth() {return lineWidth;}
	public void   setLineWidth(double lineWidth) {this.lineWidth = lineWidth;}
	public Color getLineColor() {return lineColor;}
	public void  setLineColor(Color lineColor) {this.lineColor = lineColor;}
	public double getFontSize() {return fontSize;}
	public void   setFontSize(double fontSize) {this.fontSize = fontSize;}
	public Color getFontColor() {return fontColor;}
	public void  setFontColor(Color fontColor) {this.fontColor = fontColor;}
	public FontStyle getFontStyle() {return fontStyle;}
	public void      setFontStyle(FontStyle fontStyle) {this.fontStyle = fontStyle;}
	public FontWeight getFontWeight() {return fontWeight;}
	public void       setFontWeight(FontWeight fontWeight) {this.fontWeight = fontWeight;}
	public FontFamily getFontFamily() {return fontFamily;}
	public void       setFontFamily(FontFamily fontFamily) {this.fontFamily = fontFamily;}

	// returns the angle in radians
	public double getAngleRadians() {
		return Math.toRadians(getAngle());
	}

	// returns the angle in degrees
	public double getAngleDegrees() {
		return getAngle();
	}

	//		// returns the Double angle value given an enumeration of the form a[angle in degrees] with an optional L,R,T,B at the end
	//		public Double angle2degrees(Angle angle) {
	//			return Double.parseDouble(angle.toString().substring(1).replace("L", "").replace("R", "").replace("T", "").replace("B", ""));
	//		}

	// Editor for the settings
	Editor editor = null;
	TextField xTextField = null;
	TextField yTextField = null;
	ComboBox<Double> lineLengthComboBox = null;
	ChangeListener<? super Double> lineLengthListener = null; 
	ComboBox<Double> angleComboBox = null;
	ChangeListener<? super Double> angleListener = null; 

	// default values of all the CallOut properties
	private Data<Object,Object> data = null;
	private Data<Object,Object> data2 = null;
	private String text = "";
	private boolean textRotated = true;
	private Double angle = 30.0;
	private double lineLength = 20;
	private double lineWidth = 1.5;
	private Color lineColor = Color.BLACK;
	private double fontSize = 12;
	private Color fontColor = Color.BLACK;
	private FontStyle fontStyle = FontStyle.normal;
	private FontWeight fontWeight = FontWeight.bold;
	private FontFamily fontFamily = FontFamily.arial;

	// gives you a CallOut settings w/out the text and data point
	// this can be modified and then supplied as a template when creating the CallOuts themselves
	protected CallOutSettings() { 
	}

	// CallOut Constructor (can change default settings based on a provided template)
	protected CallOutSettings(CallOutSettings cos, String text, Data<Object,Object> data, Data<Object,Object> data2) {
		if (cos != null) duplicateSettingsFromTo(cos,this);
		this.setText(text);
		this.setData(data);
		this.setData2(data2);
	}

	public static  CallOutSettings duplicateSettingsFromTo(CallOutSettings from, CallOutSettings to) {
		to.data			= from.data;
		to.data2		= from.data2;
		to.text 		= from.text;	
		to.textRotated	= from.textRotated;
		to.angle 		= from.angle;
		to.lineLength 	= from.lineLength;
		to.lineWidth	= from.lineWidth;
		to.lineColor	= from.lineColor;
		to.fontSize		= from.fontSize;
		to.fontColor	= from.fontColor;
		to.fontStyle	= from.fontStyle;
		to.fontWeight	= from.fontWeight;
		to.fontFamily	= from.fontFamily;
		return to;
	}

	// This routine sets up the Editable window
	public void edit(CallOut co, double screenX, double screenY, Text text) {
		if (this.editor != null) {
			System.out.println("Editor already open");
		}
		else {
			this.editor = new Editor(screenX, screenY, text.getScene());
			this.editor.show("CallOut Settings", getGridPaneForEditor(co, text), () -> {this.editor = null; return true;});
		}
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////
	// These are helper routines for dragging a mouse to update the CallOut angle
	// The are used by a Mouse Event routine in the CallOut class
	///////////////////////////////////////////////////////////////////////////////////////////////////////

	// given a point (mouse point)
	// return the angle the point is relative to the data point
	// so that the angle of the CallOut can be updated
	public Double getAngleType(double x1, double y1, double x2, double y2) {
		// get the data point values
		// compute the angle in degrees
		var deltaX = x2 - x1;
		var deltaY = y2 - y1;
		var rad = Math.atan2(deltaY, deltaX);
		var deg = -Math.toDegrees(rad);
		if (deg < 0) {
			deg = 360.0+deg;
		}
		//		System.out.println("deg "+deg);
		// find the closest angle (of 15 degree increments) to the "mouse" angle
		Double angle = findClosestAngle(deg);
		//		System.out.println(deg+" = "+angle.toString());
		return angle;
	}

	// Given an angle 0-360, find the closest 15 degree increment to that angle
	private double findClosestAngle(Double deg) {
		// Optionally you could change this to only allow (say) 5-degree increments (or 15 degree increments, etc.)
		// As it is I just get rid ovf the fractional part so you only have whole number degree settings.
		return deg.intValue();
	}

	// solves for the closest default Line Length
	public double getLineLengthType(double x1, double y1, double x2, double y2) {
		double length = Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));
		length = length - 10;  // subtract a 10%-ish fudge so mouse cursor's position is longer than line from origin
		length = findClosestLineLength(length);
		return length;
	}

	private double findClosestLineLength(Double length) {
		// I only allow whole number line lengths.  You can change this to allow more precision or say only allow increments of 5 pixels
		return length.intValue();
	}

	static final double MAX_CHOICEBOX_SIZE = 100.0;  // set a universal max size for the Choice Boxes that are created below 

	private GridPane getGridPaneForEditor(final CallOut co, final Text text) {

		// set up GridPane for Editor labels and entries, set up spacing between entries and between other elements in the editor
		GridPane gridPane = new GridPane();
		gridPane.setVgap(6);
		gridPane.setHgap(2);
		CSS.setBorderWidth(gridPane, 0,10,10,10);
		CSS.setBorderColor(gridPane, Color.TRANSPARENT);		// needed or the border will have nno size despite setting it below

		String originalText = this.getText();
		Double originalX = ((Number)this.getData().getXValue()).doubleValue();
		Double originalY = ((Number)this.getData().getYValue()).doubleValue();

		int row = 1;
		////////////////////////////////////////////////////////////////////////////////////////////////////
		// Data
		////////////////////////////////////////////////////////////////////////////////////////////////////
		// Get the current X and Y values, label them as such and add the two fields to the GridPane 
		{
			gridPane.add(new Text("Data"), 1, row); // col, row
			xTextField = new TextField(this.data.getXValue().toString());
			xTextField.setMaxWidth(MAX_CHOICEBOX_SIZE*0.8);
			xTextField.setAlignment(Pos.CENTER);
			yTextField = new TextField(this.data.getYValue().toString());
			yTextField.setMaxWidth(MAX_CHOICEBOX_SIZE*0.8);
			yTextField.setAlignment(Pos.CENTER);
			HBox hbox = new HBox(new Text("X: "),xTextField,new Text("   Y: "),yTextField);
			hbox.setAlignment(Pos.BASELINE_CENTER);

			xTextField.setOnKeyReleased(event -> {
				if (event.getCode() == KeyCode.ENTER){
					Object x = getNumber(xTextField.getText(),originalX);
					xTextField.setText(x.toString());
					data.setXValue(x);
					co.setCalloutTextProperties(text,this);
					co.setCallOutLineAndPositioningProperties(text, this);
					yTextField.requestFocus();
				}
				else if (event.getCode() == KeyCode.ESCAPE){
					data.setXValue(originalX);
					xTextField.setText(originalX.toString());
					xTextField.positionCaret(originalX.toString().length());

					co.setCalloutTextProperties(text,this);
					co.setCallOutLineAndPositioningProperties(text, this);
				}
			});
			yTextField.setOnKeyReleased(event -> {
				if (event.getCode() == KeyCode.ENTER){
					Number y = getNumber(yTextField.getText(),originalY);
					yTextField.setText(y.toString());
					data.setYValue(y);
					co.setCalloutTextProperties(text,this);
					co.setCallOutLineAndPositioningProperties(text, this);
					xTextField.requestFocus();
				}
				else if (event.getCode() == KeyCode.ESCAPE){
					data.setYValue(originalY);
					yTextField.setText(originalY.toString());
					yTextField.positionCaret(originalY.toString().length());
					co.setCalloutTextProperties(text,this);
					co.setCallOutLineAndPositioningProperties(text, this);
				}
			});
			gridPane.add(hbox,3,row++);
		}


		////////////////////////////////////////////////////////////////////////////////////////////////////
		// Text
		////////////////////////////////////////////////////////////////////////////////////////////////////
		{
			gridPane.add(new Text("Text"), 1, row); // col, row
			// Get and set the initial String value, Center the text and add it to the Grid Pane
			final TextField textField = new TextField(this.text);
			textField.setMaxWidth(MAX_CHOICEBOX_SIZE*2);
			textField.setAlignment(Pos.CENTER);
			gridPane.add(textField,3,row++);
			textField.textProperty().addListener((observable, oldValue, newValue) -> {
				this.text = newValue;
				co.setCalloutTextProperties(text,this);
				co.setCallOutLineAndPositioningProperties(text, this);
			});
			textField.setOnKeyReleased(event -> {
				if (event.getCode() == KeyCode.ESCAPE){
					this.text = originalText;
					textField.setText(originalText);
					textField.positionCaret(originalText.length());
					co.setCalloutTextProperties(text,this);
					co.setCallOutLineAndPositioningProperties(text, this);
				}
			});
		}

		////////////////////////////////////////////////////////////////////////////////////////////////////
		// Text rotated 
		////////////////////////////////////////////////////////////////////////////////////////////////////


		{
			RadioButton textRotatedButton = new RadioButton("Text Rotated");
			textRotatedButton.setSelected(this.textRotated);
			textRotatedButton.setMaxWidth(MAX_CHOICEBOX_SIZE*2);
			textRotatedButton.setMinSize(FXUtil.getWidth(textRotatedButton)+30, FXUtil.getHeight(textRotatedButton));
			gridPane.add(textRotatedButton, 1, row++, 3, 1); // col, row
			textRotatedButton.setOnAction((ActionEvent event) -> { 
				this.textRotated = textRotatedButton.isSelected();
				co.setCallOutLineAndPositioningProperties(text, this);
				co.setCallOutDataLocation((Group)data.getNode(), this);
				co.callOutSeries.getNode().getParent().getParent().getParent().requestLayout();
			});
		}

		addSeparator(gridPane, row++);

		////////////////////////////////////////////////////////////////////////////////////////////////////
		// angle
		////////////////////////////////////////////////////////////////////////////////////////////////////
		{
			gridPane.add(new Text("Angle"), 1, row); // col, row
			angleComboBox = Editor.getDoubleComboBox(Angle, this.angle);
			angleComboBox.setMaxSize(MAX_CHOICEBOX_SIZE, Double.MAX_VALUE);

			angleListener = (observable, oldValue, newValue) -> {
				this.angle = newValue;
				co.setCalloutTextProperties(text,this);
				co.setCallOutLineAndPositioningProperties(text, this);
			}; 
			angleComboBox.getSelectionModel().selectedItemProperty().addListener(angleListener);
			gridPane.add(angleComboBox,3,row++);
		}

		////////////////////////////////////////////////////////////////////////////////////////////////////
		// lineLength
		////////////////////////////////////////////////////////////////////////////////////////////////////
		{
			gridPane.add(new Text("Line Length"), 1, row); // col, row
			lineLengthComboBox = Editor.getDoubleComboBox(LineLength, this.lineLength);
			lineLengthComboBox.setMaxSize(MAX_CHOICEBOX_SIZE, Double.MAX_VALUE);
			lineLengthListener = (observable, oldValue, newValue) -> {
				this.lineLength = newValue;
				co.setCalloutTextProperties(text,this);
				co.setCallOutLineAndPositioningProperties(text, this);
			};
			lineLengthComboBox.getSelectionModel().selectedItemProperty().addListener(lineLengthListener);
			gridPane.add(lineLengthComboBox,3,row++);
		}	

		////////////////////////////////////////////////////////////////////////////////////////////////////
		// lineWidth
		////////////////////////////////////////////////////////////////////////////////////////////////////
		{
			gridPane.add(new Text("Line Width"), 1, row); // col, row
			ComboBox<Double> comboBox = Editor.getDoubleComboBox(LineWidth, this.lineWidth);
			comboBox.setMaxSize(MAX_CHOICEBOX_SIZE, Double.MAX_VALUE);
			comboBox.getSelectionModel().selectedItemProperty().addListener(
					(observable, oldValue, newValue) -> {
						this.lineWidth = newValue;
						co.setCalloutTextProperties(text,this);
						co.setCallOutLineAndPositioningProperties(text, this);
					});
			gridPane.add(comboBox,3,row++);
		}	

		////////////////////////////////////////////////////////////////////////////////////////////////////
		// lineColor
		////////////////////////////////////////////////////////////////////////////////////////////////////
		{
			gridPane.add(new Text("Line Color"), 1, row); // col, row
			ColorPicker colorPicker = Editor.getColorPicker(this.lineColor);
			colorPicker.setMaxSize(MAX_CHOICEBOX_SIZE, Double.MAX_VALUE);
			colorPicker.setOnAction(event -> {
				this.lineColor = colorPicker.getValue();
				co.setCalloutTextProperties(text,this);
				co.setCallOutLineAndPositioningProperties(text, this);
			});
			gridPane.add(colorPicker,3,row++);
		}

		addSeparator(gridPane, row++);

		////////////////////////////////////////////////////////////////////////////////////////////////////
		// fontSize
		////////////////////////////////////////////////////////////////////////////////////////////////////
		{
			gridPane.add(new Text("Font Size"), 1, row); // col, row
			ComboBox<Double> comboBox = Editor.getDoubleComboBox(FontSize, this.fontSize);
			comboBox.setMaxSize(MAX_CHOICEBOX_SIZE, Double.MAX_VALUE);
			comboBox.getSelectionModel().selectedItemProperty().addListener(
					(observable, oldValue, newValue) -> {
						this.fontSize = newValue;
						co.setCalloutTextProperties(text,this);
						co.setCallOutLineAndPositioningProperties(text, this);
					});
			gridPane.add(comboBox,3,row++);
		}			

		////////////////////////////////////////////////////////////////////////////////////////////////////
		// FontColor
		////////////////////////////////////////////////////////////////////////////////////////////////////
		{
			gridPane.add(new Text("Font Color"), 1, row); // col, row
			ColorPicker colorPicker = Editor.getColorPicker(this.fontColor);
			colorPicker.setMaxSize(MAX_CHOICEBOX_SIZE, Double.MAX_VALUE);
			colorPicker.setOnAction(event -> {
				this.fontColor = colorPicker.getValue();
				co.setCalloutTextProperties(text,this);
				co.setCallOutLineAndPositioningProperties(text, this);
			});
			gridPane.add(colorPicker,3,row++);
		}

		////////////////////////////////////////////////////////////////////////////////////////////////////
		// fontStyle
		////////////////////////////////////////////////////////////////////////////////////////////////////
		{
			gridPane.add(new Text("Font Style"), 1, row); // col, row
			ChoiceBox<FontStyle> choiceBox = Editor.getEnumChoiceBox(this.fontStyle);
			choiceBox.setMaxSize(MAX_CHOICEBOX_SIZE, Double.MAX_VALUE);
			choiceBox.getSelectionModel().selectedItemProperty().addListener(
					(observable, oldValue, newValue) -> {
						this.fontStyle = newValue;
						co.setCalloutTextProperties(text,this);
						co.setCallOutLineAndPositioningProperties(text, this);
					});
			gridPane.add(choiceBox,3,row++);
		}

		////////////////////////////////////////////////////////////////////////////////////////////////////
		// fontWeight
		////////////////////////////////////////////////////////////////////////////////////////////////////
		{
			gridPane.add(new Text("Font Weight"), 1, row); // col, row
			ChoiceBox<FontWeight> choiceBox = Editor.getEnumChoiceBox(this.fontWeight);
			choiceBox.setMaxSize(MAX_CHOICEBOX_SIZE, Double.MAX_VALUE);
			choiceBox.getSelectionModel().selectedItemProperty().addListener(
					(observable, oldValue, newValue) -> {
						this.fontWeight = newValue;
						co.setCalloutTextProperties(text,this);
						co.setCallOutLineAndPositioningProperties(text, this);
					});
			gridPane.add(choiceBox,3,row++);
		}

		////////////////////////////////////////////////////////////////////////////////////////////////////
		// fontFamily
		////////////////////////////////////////////////////////////////////////////////////////////////////
		{
			gridPane.add(new Text("Font Family"), 1, row); // col, row
			ChoiceBox<FontFamily> choiceBox = Editor.getEnumChoiceBox(this.fontFamily);
			choiceBox.setMaxSize(MAX_CHOICEBOX_SIZE, Double.MAX_VALUE);
			choiceBox.getSelectionModel().selectedItemProperty().addListener(
					(observable, oldValue, newValue) -> {
						this.fontFamily = newValue;
						co.setCalloutTextProperties(text,this);
						co.setCallOutLineAndPositioningProperties(text, this);
					});
			gridPane.add(choiceBox,3,row++);
		}

		addSeparator(gridPane, row++);

		return gridPane;
	}


	private static Double getNumber(String s, Double def) {
		try {
			Double ans = Double.parseDouble(s);
			return ans;
		}
		catch (Exception e) {
			return def;
		}
	}
	////////////////////////////////////////////////////////////////////////////////////////////////////
	// The following method sets the editor values when the CallOut has been moved by the Mouse
	////////////////////////////////////////////////////////////////////////////////////////////////////

	public void setEditorData() {
		if (editor != null) {
			xTextField.setText(data.getXValue().toString());
			yTextField.setText(data.getYValue().toString());

			lineLengthComboBox.getSelectionModel().selectedItemProperty().removeListener(lineLengthListener);
			lineLengthComboBox.setValue(getLineLength());
			lineLengthComboBox.getSelectionModel().selectedItemProperty().addListener(lineLengthListener);

			angleComboBox.getSelectionModel().selectedItemProperty().removeListener(angleListener);
			angleComboBox.setValue(getAngle());
			angleComboBox.getSelectionModel().selectedItemProperty().addListener(angleListener);

		}
	}

	private static void addSeparator(GridPane gridPane, int row) {
		Separator separator = new Separator(Orientation.HORIZONTAL);
		gridPane.add(separator, 1, row++, GridPane.REMAINING, 1);
		GridPane.setValignment(separator, VPos.CENTER);
	}
}
