package javaFX.ext.controls;

import java.util.function.Supplier;

import javaFX.ext.css.CSS;
import javaFX.ext.css.CSS.Symbol;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.converter.DoubleStringConverter;

public class Editor {

	// default stage to show/edit the properties
	private Stage editorStage = null;

//	final CallOut<XTYPE,YTYPE> co;
//	final CallOutSettings<XTYPE,YTYPE> coSettings;
	final double screenX, screenY;
	final Scene sc;
	Color defaultColor = Color.WHITESMOKE;

//	// will contain the list of editable items that are assessed when editing the CallOut Properties
//	// The list will be populated with "consumers" that will update the individual fields of a CallOut with the values set in the CallOut Editor
//	private List<Consumer<CallOutSettings<XTYPE,YTYPE>>> updaterList = new ArrayList<Consumer<CallOutSettings<XTYPE,YTYPE>>>();


	public Editor(double screenX, double screenY, Scene sc) {
		this.screenX = screenX;
		this.screenY = screenY;
		this.sc = sc;
	}

	public void setBackGroundColor(Color color) {
		defaultColor = color;
	}
	//
	public Stage show(String title, Node node) {
		if (sc != null) sc.getRoot().requestFocus();
		return show(title, node,()->{return true;});
	}
	
	public Stage show(String title, Node node, Supplier<Boolean> endcode) {	
		editorStage = new Stage();
		editorStage.initStyle(StageStyle.TRANSPARENT);  		// no background or "title" decorations plus 
//		editorStage.initModality(Modality.APPLICATION_MODAL);	// must be dealt with prior to doing anything else with the application  
		
		// set title of the Editor
		Label editorTitle = new Label(title);
		CSS.setFontSize(editorTitle, 16.0);
		
		// Create the two buttons and add them to an HBox with appropriate Spacing
//		Button applyButton = new Button("Apply");
		Button exitButton = new Button("Exit");
		VBox exitPane = new VBox(exitButton);
		// JavaFX has a bug which does not correctly determine the height.  
		// The code below sets different paddings on the exit buttons to overcome that flaw
		if (title.equals("Series Settings")) {
			exitPane.setPadding(new Insets(0,0,20,0));			
		}
		else if (title.equals("Plot Settings")) {
			exitPane.setPadding(new Insets(0,0,30,0));	
		}
		else if (title.contains("Axis")) {
			exitPane.setPadding(new Insets(0,0,20,0));	
		}
		else if (title.contains("CallOut")) {
			exitPane.setPadding(new Insets(0,0,20,0));	
		}
		else {
			exitPane.setPadding(new Insets(10,0,10,0));	
		}
		exitPane.setAlignment(Pos.TOP_CENTER);
		
//		// Create a new VBOX and add in the Title, GridPane, and the Buttons
//		VBox vbox = new VBox(editorTitle, node, exitPane);
//		vbox.setAlignment(Pos.CENTER);

		// Create a rounded BorderPane 
		BorderPane borderPane = new BorderPane();
		CSS.setBorderWidth(borderPane, 2);
		CSS.setBorderColor(borderPane,Color.BLACK);
		CSS.setBorderRadius(borderPane,25);
		CSS.setBackgroundRadius(borderPane, 25);
		CSS.setBackgroundColor(borderPane, defaultColor);

		// Create a Border Pane as the top-level of the Edit Window and get the needed width and height of the Edit Window
		borderPane.setTop(editorTitle);
		borderPane.setCenter(node);
		borderPane.setBottom(exitPane);
		BorderPane.setAlignment(editorTitle,  Pos.CENTER);
		BorderPane.setAlignment(node,  Pos.CENTER);
		BorderPane.setAlignment(exitPane,  Pos.TOP_CENTER);
		Scene scene = new Scene(borderPane);	// attach the Border Pane to a scene
		borderPane.applyCss();					// Perform any needed layout of the entire Scene tree
		borderPane.layout();
		
		scene.setRoot(new Label(""));  // reset the old scene to point to something else, so you can create a new scene of the correct size
		scene = new Scene(borderPane,BorderPane.USE_PREF_SIZE,BorderPane.USE_PREF_SIZE,Color.TRANSPARENT);  // Ensure the Scene is transparent so the window will be round
		editorStage.setScene(scene);
		if (sc != null) editorStage.initOwner(sc.getWindow());
		editorStage.show();


		// This repositions the stage so it is relative to the clicked CallOut, to the CallOut Left or Right as appropriate, and NOT off the screen
		repositionStage();

		// Sets various Button actions 
		exitButton.setOnAction(event -> {editorStage.close(); endcode.get();});
		// The apply button causes any changes to be made visual on the underlying charat 
//		applyButton.setOnAction(applyHandler);

		// These two enable the Editor Window to be dragged to a more appropriate spot by the user
		borderPane.setOnMousePressed(mouseEvent -> mousePressed_SetEditorInitialLocation(mouseEvent));
		borderPane.setOnMouseDragged(mouseEvent -> mouseDragged_MoveEditor(mouseEvent));

		return editorStage;
	}

	// This sets the CallOut Editors position based on the mouse click (the selected CallOut to edit)
	// It is positioned to the Left or right of the CallOut depending on whether the CallOut is on the Left or right of the Screen
	// It also ensures that the CallOut Editor is not hanging off the display monitor
	private void repositionStage() {
		// get Screen bounds
		Rectangle2D sb = Screen.getScreens().get(0).getBounds();
		
		// Set X to the right of the mouse position
		double x = screenX+60;
		// if this is on the farther right hand side of the screen then move the x positioning over to the left
		if (screenX > sb.getWidth()/2+100) {
			x = screenX-60-editorStage.getWidth();
		}
		// set the height of the CallOut Editor to be about mid height to the Y mouse position
		double y = screenY-editorStage.getHeight()/2-30;
		
		// Ensure that the editor is within the screen bounds on the top and left
		x = Math.max(x, 40);
		y = Math.max(y, 40);

		// ensure that the editor is within the screen bounds on the bottom and right (account for the bottom ribbon
		x = Math.min(x, sb.getMaxX()-editorStage.getWidth()  -10);
		y = Math.min(y, sb.getMaxY()-editorStage.getHeight() -50);

		// Position the stage based on all the above
		editorStage.setX(x);
		editorStage.setY(y);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	// The following methods get the Choice boxes for enumerations, doubles, and colors
	////////////////////////////////////////////////////////////////////////////////////////////////////

	@SuppressWarnings("unchecked")
	public static <T extends Enum<T>> ChoiceBox<T> getEnumChoiceBox(T value) {
		// get list of enumerations
		ObservableList<T> list = FXCollections.observableArrayList();
		list.addAll((T[])value.getClass().getEnumConstants());
		// create a ChoiceBox with those values, set the default value, set the size, etc.
		var choiceBox = new ChoiceBox<T>(list);
		GridPane.setFillWidth(choiceBox, true);
		choiceBox.setValue(value);
		return choiceBox;
	}
	public static <T extends Enum<T>> ChoiceBox<T> getEnumChoiceBox(T[] listT, T value) {
		// get list of enumerations
		ObservableList<T> list = FXCollections.observableArrayList();
		list.addAll(listT);
		// create a ChoiceBox with those values, set the default value, set the size, etc.
		var choiceBox = new ChoiceBox<T>(list);
		GridPane.setFillWidth(choiceBox, true);
		choiceBox.setValue(value);
		return choiceBox;
	}
	public static <T extends Enum<T>> void setEnumChoiceBox(ChoiceBox<T> cb, T value) {
		EventHandler<ActionEvent> eh = cb.getOnAction();
		cb.setOnAction(null);
		cb.setValue(value);
		cb.setOnAction(eh);
	}
	public static ChoiceBox<Double> getDoubleChoiceBox(Double[] values, Double value) {
		// get list of enumerations
		ObservableList<Double> list = FXCollections.observableArrayList();
		list.addAll(values);
		// create a ChoiceBox with those values, set the default value, set the size, etc.
		var choiceBox = new ChoiceBox<Double>(list);
		GridPane.setFillWidth(choiceBox, true);
		choiceBox.setValue(value);
		return choiceBox;
	}
	public static void setDoubleChoiceBox(ChoiceBox<Double> cb, Double value) {
		EventHandler<ActionEvent> eh = cb.getOnAction();
		cb.setOnAction(null);
		cb.setValue(value);
		cb.setOnAction(eh);
	}
	
	public static ComboBox<Double> getDoubleComboBox(Double[] values, Double value) {
		// get list of enumerations
		ObservableList<Double> list = FXCollections.observableArrayList();
		list.addAll(values);
		// create a ChoiceBox with those values, set the default value, set the size, etc.
		var comboBox = new ComboBox<Double>(list);
		comboBox.setEditable(true);
		comboBox.setConverter(new DoubleStringConverter());
		GridPane.setFillWidth(comboBox, true);
		comboBox.setValue(value);
		return comboBox;
	}
	public static void setDoubleComboBox(ComboBox<Double> cb, Double value) {
		EventHandler<ActionEvent> eh = cb.getOnAction();
		cb.setOnAction(null);
		cb.setValue(value);
		cb.setOnAction(eh);
	}


	public static ColorPicker getColorPicker(Color color) {
		ColorPicker colorPicker = new ColorPicker();
		colorPicker.setValue(color);
		colorPicker.setCenterShape(true);
		GridPane.setFillWidth(colorPicker, true);
		GridPane.setFillHeight(colorPicker, true);
		colorPicker.setMinHeight(24);
		return colorPicker;
	}
	
	public static void setColorPicker(ColorPicker cp, Color color) {
		EventHandler<ActionEvent> eh = cp.getOnAction();
		cp.setOnAction(null);
		cp.setValue(color);
		cp.setOnAction(eh);
	}

	
	public static SymbolPicker getSymbolPicker(Symbol symbol, Color color) {
		SymbolPicker symbolPicker = new SymbolPicker();
		symbolPicker.setValue(symbol, color);
		GridPane.setFillWidth(symbolPicker, true);
		return symbolPicker;
	}
	
	public static void setSymbolPicker(SymbolPicker sp, Symbol symbol, Color color) {
		EventHandler<ActionEvent> eh = sp.getOnAction();
		sp.setOnAction(null);
		sp.setValue(symbol, color);
		sp.setOnAction(eh);
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////
	// These are helper routines for dragging (moving) the CallOut edit window to a different location
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	
	// Helper class for dragging and dropping
	class Delta { double x, y; } 
	private final Delta dragDelta = new Delta();

	public void mousePressed_SetEditorInitialLocation(MouseEvent event) {
		//		System.out.println("Mouse Pressed");
		dragDelta.x = editorStage.getX() - event.getScreenX();
		dragDelta.y = editorStage.getY() - event.getScreenY();
	}

	public void mouseDragged_MoveEditor(MouseEvent event) {
		//		System.out.println("Mouse Dragged");
		double x = event.getScreenX() + dragDelta.x;
		double y = event.getScreenY() + dragDelta.y;
		editorStage.setX(x);
		editorStage.setY(y);
	}

}
