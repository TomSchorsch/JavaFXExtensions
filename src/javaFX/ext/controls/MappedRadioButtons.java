package javaFX.ext.controls;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.geometry.Insets;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;

public class MappedRadioButtons<T> {

	private Map<T,String> optionMap;
	int axis;
	public static final int PAGE_AXIS = 0;
	public static final int LINE_AXIS = 1;


	protected Map<T,RadioButton> mapOptionKey2RadioButton = new HashMap<T,RadioButton>();
//	private EventHandler<ActionEvent> userEventHandler = null;
//	private boolean eventHandlingEnabled = true;
	private GridPane gridPane = null;
	private Double fontSize = null;
	private Set<T> setNewRankItems = new HashSet<T>();
	private static final int indent = 10;
	private Insets insets = new Insets(0,4,0,0);	// Top, Right, Bottom, Left
	private Insets indentedInsets = new Insets(0,4,0,0+indent);
	private Map<RadioButton,Set<RadioButton>> mapController2Controlled = new HashMap<RadioButton,Set<RadioButton>>();

	public MappedRadioButtons(Map<T,String> optionMap, int axis) {
		this.optionMap = optionMap;
		this.axis = axis;
		createRadioButtonsFromOptions();
		createControllersForSubButtons();
	}

	public Pane getPane() {
		if (gridPane == null) {
			createRadioButtonsPane();
		}
		return gridPane;
	}

	public boolean isSelected(String optionKey) {
		if (mapOptionKey2RadioButton.containsKey(optionKey)) return mapOptionKey2RadioButton.get(optionKey).isSelected();
		return false;
	}

	public void setSelected(String optionKey, boolean value) {
		if (mapOptionKey2RadioButton.containsKey(optionKey)) mapOptionKey2RadioButton.get(optionKey).setSelected(value);
	}

	public void setSelected(List<String> optionKeys) {
		optionKeys.parallelStream().forEach(optionKey -> setSelected(optionKey,true));
	}

	public List<T> getSelected() {
		return mapOptionKey2RadioButton.keySet().stream()
				.filter(optionKey -> mapOptionKey2RadioButton.get(optionKey).isSelected())
				.collect(Collectors.toList());
	}

	public void disableOption(String optionKey) {
		if (mapOptionKey2RadioButton.containsKey(optionKey)) mapOptionKey2RadioButton.get(optionKey).setDisable(true);
	}

	public void enableOption(String optionKey) {
		if (mapOptionKey2RadioButton.containsKey(optionKey)) mapOptionKey2RadioButton.get(optionKey).setDisable(false);
	}

	public void setEnabled(String optionKey, boolean value) {
		if (mapOptionKey2RadioButton.containsKey(optionKey)) mapOptionKey2RadioButton.get(optionKey).setDisable(!value);
	}

	public void addNewRank(T firstItemOfNewRank) {
		setNewRankItems.add(firstItemOfNewRank);
	}

//	public void setOnAction(EventHandler<ActionEvent> value) {
//		userEventHandler = value;
//	}
//
//	public void disableEventHandling() {eventHandlingEnabled = false;}
//	public void enableEventHandling() {eventHandlingEnabled = true;}

	public void setFontSize(double fontSize) {
		this.fontSize = fontSize;
	}

	public void setButtonInsets(int top, int right, int bottom, int left) {
		insets = new Insets(top,right,bottom,left);
		indentedInsets = new Insets(top,right,bottom,left+indent);
	}


	private void createRadioButtonsFromOptions() {
		for (T optionKey : optionMap.keySet()) {
			RadioButton rb = new RadioButton(optionMap.get(optionKey));
			rb.setSelected(false);
			mapOptionKey2RadioButton.put(optionKey, rb);
//			rb.setOnAction(internalEventHandler);
		}
	}

	private void createControllersForSubButtons() {
		RadioButton main = null;
		Set<RadioButton> subButtons = null;
		for (T optionKey : optionMap.keySet()) {
			RadioButton temp = mapOptionKey2RadioButton.get(optionKey);
			if (temp.getText().startsWith("-")) {
				if (subButtons == null) subButtons = new HashSet<RadioButton>();
				subButtons.add(temp);
			}
			else {
				if (subButtons != null) {
					mapController2Controlled.put(main,subButtons);
					final RadioButton self = main;
					main.setOnAction(action -> setSubButtons(self));
					subButtons = null;
				}
				main = temp;
			}
		}
	}

	protected void setSubButtons(RadioButton controller) {
		boolean selected = controller.isSelected();
		mapController2Controlled.get(controller).forEach(rb -> rb.setSelected(selected));
	}

//	private EventHandler<ActionEvent> internalEventHandler = 
//			(actionEvent -> {
//				if (eventHandlingEnabled && userEventHandler != null) {
//					userEventHandler.handle(actionEvent);
//				}
//			});


	private void createRadioButtonsPane() {
		gridPane = new GridPane();
		int i = 0;
		int j = 0;
		for (T optionKey : optionMap.keySet()) {
			if (setNewRankItems.contains(optionKey)) {
				i = 0;
				j = j+1;
			}
			addRadioButtonToPanel(i++,j,optionKey);
		}


	}

	private void addRadioButtonToPanel(int i, int j, T optionKey) {
		int row = i;
		int col = j;
		if (axis == LINE_AXIS) {
			col = i;
			row = j;
		}
		RadioButton rb = mapOptionKey2RadioButton.get(optionKey);
		if (fontSize != null) {
			Font font = rb.getFont();
			rb.setFont(new Font(font.getName(),fontSize));
		}
		if (rb.getText().startsWith("-")) { // sub-option
			rb.setPadding(indentedInsets);  // top right bottom left
		}
		else {
			rb.setPadding(insets);  // top right bottom left
		}
		GridPane.setConstraints(rb, col, row);
		gridPane.getChildren().add(rb);
	}

}
