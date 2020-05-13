package test.controls;
import java.util.LinkedHashMap;
import java.util.Map;

import javaFX.ext.controls.Config;
import javaFX.ext.controls.MappedRadioButtons;
import javaFX.ext.css.Instructions;
import javaFX.ext.utility.Logger;
import javafx.stage.Stage;
import test.FXTester;

public class MappedRadioButtonsTest implements FXTester {

	MappedRadioButtons<String> rbs;
	Logger logger;
	
	@Override
	public void execute(Logger logger) {
		this.logger = logger;
		Map<String,String> map = new LinkedHashMap<String,String>();
		map.put("first","FIRST");
		map.put("second","SECOND");
		map.put("3rd","3RD");
		map.put("3rdsub","-3RD_SUB");
		map.put("anotherSub","- One more sub");
		map.put("next","NEXT");
		map.put("4th","4TH");
		map.put("5th","5TH");
		map.put("5thsub","-5TH_SUB");
		map.put("6thsub","- 6TH-SUB");
		map.put("last","LAST");
		rbs = new MappedRadioButtons<String>(map, MappedRadioButtons.PAGE_AXIS);
		rbs.addNewRank("next");
		rbs.setFontSize(10.0);

		restoreConfig();
		
		Stage stage = FXTester.displayResults(rbs.getPane());
		stage.setOnCloseRequest(value -> saveConfig());
		
		Instructions txt = new Instructions(stage.getScene());
		txt.addCenter("Tests the 'MappedRadioButtons' control");
		txt.add("This controls allows for radio buttons with sub-buttons");
		txt.add("Radio buttons set/unset all sub-buttons");
		txt.add("Sub-buttons can be set/unset independently");
		txt.add("This control will remember previous selections for when you run this test the next time");
		txt.display();
	}
		
	public void restoreConfig() {
		Config config = new Config(this.getClass().getName(), logger);
		config.load();
		config.restoreConfigs("radioButtons", rbs);
	}

	public void saveConfig() {
		Config config = new Config(this.getClass().getName(), logger);
		config.saveConfigs("radioButtons", rbs);
		config.save();
	}

}

