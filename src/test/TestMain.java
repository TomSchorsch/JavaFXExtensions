package test;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javaFX.ext.controls.Config;
import javaFX.ext.controls.MappedRadioButtons;
import javaFX.ext.controls.TextWindow;
import javaFX.ext.utility.FXUtil;
import javaFX.ext.utility.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import test.callouts.TestAllCallOuts;
import test.callouts.TestCallOutSeriesEditor;
import test.callouts.TestEditCallOuts;
import test.callouts.TestFontProperties;
import test.callouts.TestLineProperties;
import test.callouts.TestMovingCallOuts;
import test.callouts.TestRotateCallOutsDrag;
import test.controls.FileChoiceBoxTest;
import test.controls.LabelerTest;
import test.controls.MappedRadioButtonsTest;
import test.plotEditMenu.TestSaveAsPngGiven;
import test.plotEditMenu.TestSaveAsPngTitle;
import test.plots.TestCDFPlot;
import test.plots.TestPlotAndSeriesEditor;
import test.plots.TestPlotEditor;
import test.plots.TestSSMAxis;
import test.plots.TestSeriesEditor;
import test.plots.TestSymbolsIndividualSizes;
import test.plots.TestXAndYAxisEditors;
import test.zoom.TestZoom;
import test.zoom.TestZoomWithMoveableCallOuts;

public class TestMain extends Application {

	Stage mainStage;
	BorderPane mainPane = new BorderPane();
	Scene mainScene = null;
	Logger logger = new Logger();
	
	MappedRadioButtons<FXTester> rbs;
	
	Button executeButton = new Button("Execute");
	Button closeButton = new Button("Close");
	@Override
	public void start(Stage primaryStage) {
		Stage mainStage = primaryStage;
		mainStage.setTitle("Test JavaFX Extended Infrastructure");
		executeButton.setOnAction(event -> execute());
		closeButton.setOnAction(event -> mainStage.close());
			
		initializeCenterDisplayArea();
		initializeBottomDisplayArea();

		restoreConfig();
		double width = FXUtil.getWidth(mainPane);
		double height = FXUtil.getHeight(mainPane);
		
		mainScene = new Scene(mainPane, width, height, Color.WHITE);
		logger = new Logger();
		mainStage.setScene(mainScene);
		mainStage.setOnCloseRequest(event -> {Platform.exit();System.exit(0);});
		mainStage.show();
	}
	
	@Override // Called before start
	public void init() {}
	@Override  // Called when ending
	public void stop() {}

	// Creates an instance of this class, calls the init method, 
	// then calls the start method (sending in the primaryStage)
	public static void main(String[] args) {
		Application.launch(args);
	}
	
	Map<FXTester,String> mapClass2Text = new HashMap<FXTester,String>();
	private void initializeCenterDisplayArea() {
		// Center controls go here
		
		// These are "do nothing" testers, They serve as place holder and enable me to establish new Ranks
		FXTester controls = (l) -> {;};  
		FXTester plots = (l) -> {;};		
		FXTester plotsExtra = (l) -> {;};
		FXTester plotsSpecific = (l) -> {;};
		FXTester callOuts = (l) -> {;};
		FXTester saves = (l) -> {;};
		FXTester zoom = (l) -> {;};
		// Note: The above are anonymous constructors of the Interface FX Tester
		// FXTestyer has a single Abstract Method "Execute" that takes a Logger as a parameter
		// (l) -> {;};  is a method constructor for an anonymous execute... method
		// it accepts a Logger "l" as a parameter -> and then does nothing {;}

		var mapClass2Text = new LinkedHashMap<FXTester,String>();
		mapClass2Text.put(controls,"Test Controls:");
		mapClass2Text.put(new LabelerTest(),"- Labeler");
		mapClass2Text.put(new FileChoiceBoxTest(),"- File Choice Box");
		mapClass2Text.put(new MappedRadioButtonsTest(),"- Mapped Radio Buttons");
		
		mapClass2Text.put(plots,"Test Plot and Series Editors:");
		mapClass2Text.put(new TestXAndYAxisEditors(),"- X and Y Axis Editors");
		mapClass2Text.put(new TestPlotEditor(),"- Test Plot Editor");
		mapClass2Text.put(new TestSeriesEditor(),"- Test Series Editor");
		mapClass2Text.put(new TestPlotAndSeriesEditor(),"- Test Plot and Series Editor");
		
		mapClass2Text.put(plotsExtra,"Test Additional Plot capabilities:");
		mapClass2Text.put(new TestSymbolsIndividualSizes(),"- Test Individual Symbol Sizes");
		
		mapClass2Text.put(plotsSpecific,"Test Plot variants:");
		mapClass2Text.put(new TestCDFPlot(),"- Test CDF");
		mapClass2Text.put(new TestSSMAxis(),"- Test Seconds Since Midnight Axis");
		
		mapClass2Text.put(callOuts, "Test CallOuts:");
		mapClass2Text.put(new TestFontProperties(),"- Font Properties");
		mapClass2Text.put(new TestLineProperties(),"- Line Properties");
		mapClass2Text.put(new TestMovingCallOuts(),"- Move Callout by Dragging Line");
		mapClass2Text.put(new TestRotateCallOutsDrag(),"- Rotate / Extend CallOut by Dragging Text");
		mapClass2Text.put(new TestEditCallOuts(),"- Edit CallOuts");
		mapClass2Text.put(new TestAllCallOuts(),"- Move, Rotate, Extend, Edit Test");
		mapClass2Text.put(new TestCallOutSeriesEditor(),"- Test Editing individual CallOut Series");
		
		mapClass2Text.put(saves, "Test Context Menu (right Click on Chart):");
		mapClass2Text.put(new TestSaveAsPngTitle(), "- Test 'Save' & 'Save as' (title)");
		mapClass2Text.put(new TestSaveAsPngGiven(), "- Test 'Save' & 'Save as' (given)");


		mapClass2Text.put(zoom, "Test Plot Zoom:");
		mapClass2Text.put(new TestZoom(), "- Zoom Controls");
		mapClass2Text.put(new TestZoomWithMoveableCallOuts(), "- Zoom With moveable annotations");

		rbs = new MappedRadioButtons<FXTester>(mapClass2Text, MappedRadioButtons.PAGE_AXIS);
		rbs.addNewRank(controls);
		rbs.addNewRank(plots);
		rbs.addNewRank(plotsSpecific);
		rbs.addNewRank(callOuts);
		rbs.addNewRank(saves);
		
		rbs.setFontSize(10.0);
		
		VBox centerBox = new VBox();
		centerBox.setAlignment(Pos.CENTER);
		centerBox.setSpacing(10);
		centerBox.setPadding(new Insets(10));
		centerBox.getChildren().add(rbs.getPane());
		centerBox.setMaxHeight(Control.USE_PREF_SIZE);
		centerBox.setMaxWidth(Control.USE_PREF_SIZE);
		mainPane.setCenter(centerBox);

	}
	
	private void initializeBottomDisplayArea() {
		
		// position execute and close buttons
		HBox buttonPane = new HBox(80.0, executeButton, closeButton);
		buttonPane.setAlignment(Pos.CENTER);

		// position bottom window text box beneath buttons
		TextWindow windowText = new TextWindow(10);
		VBox bottomPane= new VBox(10.0, buttonPane, windowText.getPane());
		VBox.setVgrow(windowText.getPane(),Priority.ALWAYS);
		
		// assign to bottom of Border Pane
		mainPane.setBottom(bottomPane);
		BorderPane.setMargin(bottomPane, new Insets(10));  // top, right, bottom, left
	}
	
	private void execute() {
		List<FXTester> selected = rbs.getSelected();
		for (FXTester test : selected) {
			test.execute(logger);
		}
		saveConfig();
	}
	
	public void restoreConfig() {
		Config config = new Config(this.getClass().getName(), logger);
		config.load();
		config.restoreConfigs("mappedRadioButtons", rbs);
	}

	public void saveConfig() {
		Config config = new Config(this.getClass().getName(), logger);
		config.saveConfigs("mappedRadioButtons", rbs);
		config.save();
	}

}

