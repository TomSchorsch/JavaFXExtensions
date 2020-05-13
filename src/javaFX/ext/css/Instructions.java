package javaFX.ext.css;

import javaFX.ext.controls.Editor;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Instructions {

	Stage instructionStage = new Stage();
	WebView textArea = new WebView();
	StringBuilder sb = new StringBuilder();
	final Editor editor;
	final Scene parentScene;
	int numLines = 0;
	public Instructions(Scene scene) {
		parentScene = scene;
		editor = new Editor(2000,2000,scene);  // positioning logic will put it back on the screen at lower right
		}
	public void add(String text) {
		sb.append("<p "+
				"style = \""+
				"text-indent: -1.5em;"+
				"margin-left:  2.0em;"+
				"margin-top: 0;"+
				"margin-bottom: 0;"+
				"\">"+text+"</p>");
		numLines = numLines + 1 + text.length()/80;
	}
	public void addCenter(String text) {
		sb.append("<p><center>"+text+"</center></p>");
		numLines = numLines + 2 + text.length()/80;
	}
	public void display() {
		textArea.getEngine().loadContent(sb.toString(),"text/html");
		editor.setBackGroundColor(Color.LIGHTCYAN);
		textArea.setMinWidth(600);
		textArea.setMaxWidth(600);
		int minHeight = Math.min(200, 12+numLines*18);  // use 18 pixels per line of text + 12 buffer
		textArea.setMinHeight(minHeight);
		textArea.setMaxHeight(minHeight);
		Stage stage = editor.show("Instructions",textArea);
		if (parentScene != null) {
			EventHandler<WindowEvent> handler = parentScene.getWindow().getOnCloseRequest(); 
			if (handler == null) {
				parentScene.getWindow().setOnCloseRequest((event) -> {stage.close();});
			}
			else {
				parentScene.getWindow().setOnCloseRequest((event) -> {handler.handle(event);stage.close();});				
			}
		}
	}
	
	


	// These are helper routines for dragging (moving) the CallOut edit window to a different location
	///////////////////////////////////////////////////////////////////////////////////////////////////////

	// Helper class for dragging and dropping
	class Delta { double x, y; } 
	private final Delta dragDelta = new Delta();

	public void mousePressed_SetEditorInitialLocation(MouseEvent event) {
		//		System.out.println("Mouse Pressed");
		dragDelta.x = instructionStage.getX() - event.getScreenX();
		dragDelta.y = instructionStage.getY() - event.getScreenY();
	}

	public void mouseDragged_MoveEditor(MouseEvent event) {
		//		System.out.println("Mouse Dragged");
		double x = event.getScreenX() + dragDelta.x;
		double y = event.getScreenY() + dragDelta.y;
		instructionStage.setX(x);
		instructionStage.setY(y);
	}
}
