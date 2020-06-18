package javaFX.ext.controls;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javaFX.ext.utility.Logger;

/*
 * Config is used to to save and restore GUI configurations (such as last file used or selected and/or last set of radio buttons that were selected)
 * 
 * It works using a map.  The map key is the text used to save/retrieve the information from the map and the mapped object is an object... so it could be anything
 * 
 * The application puts items into the map
 * 
 */
public class Config {
	/*
	 * Enables GUI Control Settings and info to be saved and restored between call to an application
	 *  
	 */

	private String fileName;
	private Logger logger;
	private Map<String,Object> map = new HashMap<String,Object>();

	/* Constructor
	 * Creates a config object which is supplied the file name and a logger
	 * the map object will store all of the config information to be saved/restored
	 * the map key is used to save info in the map and later used to retrieve that information 
	 * (possibly after having been saved to a file and then loaded again)
	 * 
	 * Typical use in an application is to have a saveConfig method and a loadConfig config method (like below examples)
	 * which can  be called elsewhere in the program to save and load configs
	 * 
//	public void saveConfig() {
//		Config config = new Config(this.getClass().getName(), logger);
//		config.save("mappedRadioButtons", rbs);
//		config.saveToFile();
//	}
	 * 
//	public void restoreConfig() {
//		Config config = new Config(this.getClass().getName(), logger);
//		config.loadFromFile();
//		config.restore("mappedRadioButtons", rbs);
//	}
	 * 
	 * In the above code example of using the Config class, the persisted file name IS the application class name that is saving it
	 * rbs is of type MappedRadioButtons and saveConfig/restoreConfig knows how to save and restore from that control
	 *  
	 * Rather than have every control or application "know" details of how config works the design here is to have config know details of every control (that can be persisted/restored)
	 * Presumably Config only has access to publicly available methods of the control so it does not need to know internal details of any control
	 * Every "control" whose data is saved/restored should have defined methods for "saveConfig" and "restoreConfig"
	 */


	public Config(String fileName, Logger logger) {
		this.fileName = fileName;
		if (!this.fileName.endsWith(".config")) this.fileName+= ".config";
		this.logger = logger;
	}

	/*
	 * save writes the map to a file so the settings are persisted
	 */
	public void saveToFile() {
		try (ObjectOutputStream outStream = new ObjectOutputStream(new FileOutputStream(fileName))) {
			outStream.writeObject(map);
			outStream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * load reads the map from a file so the settings can be restored
	 */
	@SuppressWarnings("unchecked")
	public void loadFromFile() {
		try (ObjectInputStream inStream = new ObjectInputStream(new FileInputStream(fileName))) {
			Object obj = inStream.readObject();
			map = (Map<String,Object>)obj;
			inStream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			logger.println("File '"+fileName+"' not found, using default values");
			//			e.printStackTrace();
		}
		catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * This save/restore pair works for MappedRadioButtons
	 */
	public void save(String key, MappedRadioButtons<?> mappedRadioButtons) {
		Map<String,Boolean> saveMap = new HashMap<String,Boolean>();
		mappedRadioButtons.mapOptionKey2RadioButton.keySet().stream()
		.forEach(optionKey -> {
			saveMap.put(toString(optionKey),mappedRadioButtons.mapOptionKey2RadioButton.get(optionKey).isSelected());
		});
		map.put(key, saveMap);
	}

	public void restore(String key, MappedRadioButtons<?> mappedRadioButtons) {
		Object obj = get(key,new HashMap<String,Boolean>());
		if (obj != null  && obj instanceof Map<?,?>) {
			try {
				@SuppressWarnings("unchecked")
				Map<String,Boolean> restoreMap = (Map<String,Boolean>)obj;
				for (Object keyObj : mappedRadioButtons.mapOptionKey2RadioButton.keySet()) {
					String name = toString(keyObj);
					if (restoreMap.containsKey(name)) 
						mappedRadioButtons.mapOptionKey2RadioButton.get(keyObj).setSelected(restoreMap.get(name));
					else
						mappedRadioButtons.mapOptionKey2RadioButton.get(keyObj).setSelected(false);

				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	/*
	 * This save/restore pair works for FileChoiceBox
	 */
	public void save(String key, FileChoiceBox fileChoiceBox) {
		map.put(key+"-files",fileChoiceBox.getFiles());
		map.put(key+"-selected", fileChoiceBox.getSelected());
	}

	@SuppressWarnings("unchecked")
	public void restore(String key, FileChoiceBox fileChoiceBox) {
		Object objFiles = get(key+"-files",fileChoiceBox.getFiles());
		if (objFiles instanceof List<?>) {
			fileChoiceBox.setFiles((List<File>)objFiles);
		}
		Object objFile  = get(key+"-selected",null);
		if (objFile instanceof File) {
			fileChoiceBox.setSelected((File)objFile);
		}
	}
	
	/*
	 * Private methods
	 */

	// restores the object or a default object if it is not present
	private Object get(String key, Object def) {
		if (map.containsKey(key)) return map.get(key);
		return def;
	}

	// This is a normal "To String", except for objects
	// Objects are typically named full.class.name@lskhf;ouslje
	// This keeps the class name part and removes the @psejfoaishfa; part (object identifier part)
	private String toString(Object obj) {
		String name = obj.toString();
		int index = name.indexOf("@");
		if (index > 0) name = name.substring(0,index);
		return name;
	}
}
