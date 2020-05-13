package javaFX.ext.controls;
import java.util.List;

import javafx.scene.control.Control;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;

public class TextWindow {
	
	private ScrollPane scrollPane; 
	private TextArea textArea;	
	private String newLine = "\n";
	
	public TextWindow(int rowCount) {
		textArea = new TextArea();
		textArea.setWrapText(true);			// lines of text that are too big wrap
		textArea.setPrefRowCount(rowCount);	// sets default lines of text in window

		scrollPane = new ScrollPane(); 
		scrollPane.setContent(textArea);
		scrollPane.setFitToWidth(true);		// enables the width to grow to the full size of the app window
	}

	// returns the outer Pane (Scroll Pane)
	public Control getPane() {return scrollPane;}
	
	// displays a blank line
	public void println() {textArea.appendText(newLine);}
	
	// displays a single line
	public void println(String s) {textArea.appendText(s+newLine);}
	
	// displays multiple lines from a list
	public void println(List<String> list) {list.forEach(s -> textArea.appendText(s+newLine));}

}
