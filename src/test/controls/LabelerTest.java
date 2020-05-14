
package test.controls;


import java.util.ArrayList;
import java.util.List;

import javaFX.ext.controls.Instructions;
import javaFX.ext.controls.Labeler;
import javaFX.ext.controls.Labeler.HPos;
import javaFX.ext.controls.Labeler.VPos;
import javaFX.ext.css.CSS;
import javaFX.ext.utility.Logger;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import test.FXTester;

public class LabelerTest implements FXTester {

	Logger logger;

	@Override
	public void execute(Logger logger) {
		this.logger = logger;

		for (VPos pos : reverse(VPos.values())) {
			Label label;
			Region region;


			label = new Label(pos.toString()+" position");
			CSS.setFontSize(label, 8.0);
			region = Labeler.rightLabel(label,	getBaseItem("Test \n Labeler.rightLabel "),pos);
			FXTester.displayResults(region);
		}
		
		for (VPos pos : reverse(VPos.values())) {
			Label label;
			Region region;

			label = new Label(pos.toString()+" position");
			CSS.setFontSize(label, 8.0);
			region = Labeler.leftLabel(label,	getBaseItem("Test \n Labeler.leftLabel "),pos);
			FXTester.displayResults(region);
		}
		
		for (HPos pos : reverse(HPos.values())) {
			Label label;
			Region region;

			label = new Label(pos.toString()+" position");
			CSS.setFontSize(label, 8.0);
			region = Labeler.bottomLabel(label,	getBaseItem("Test \n Labeler.bottomLabel "),pos);
			FXTester.displayResults(region);
		}

		for (HPos pos : reverse(HPos.values())) {
			Label label;
			Region region;
			
			label = new Label(pos.toString()+" position");
			CSS.setFontSize(label, 8.0);
			region = Labeler.topLabel(label,	getBaseItem("Test \n Labeler.topLabel "),pos);
			FXTester.displayResults(region);
		}
		
		Instructions txt = new Instructions(null);
		txt.addCenter("Labeler Tests");
		txt.add("The Labeler control enables you to set labels next to other controls");
		txt.add("The methods are...");
		txt.add("-- leftLabel and rightLabel, with positions TOP, BASELINE, CENTER, and BOTTOM");
		txt.add("-- topLabel and bottomLabel withh positions LEFT, CENTER, RIGHT");
		txt.add("This test just displays the various options");
		txt.display();
	}
	
	public StackPane getBaseItem(String labelText) {
		StackPane stackPane = new StackPane();
		CSS.setBackgroundColor(stackPane, Color.CYAN);
		Text text = new Text(labelText);
		text.setTextAlignment(TextAlignment.CENTER);
		CSS.setFontColor(text, Color.BLUE);
		CSS.setFontSize(text, 14.0);
		stackPane.getChildren().add(text);
		CSS.setBackgroundColor(stackPane, Color.LIGHTGOLDENRODYELLOW);
		stackPane.setMaxSize(100, 100);
		return stackPane;
	}
	
	private <T> List<T> reverse(T[] list) {
		List<T> rList = new ArrayList<T>(list.length);
		for (int i = list.length-1; i >=0 ; i--) {
			rList.add(list[i]);
		}
		return rList;
}
}

