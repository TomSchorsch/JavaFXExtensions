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

public class Config {

	private String fileName;
	private Logger logger;
	private Map<String,Object> map = new HashMap<String,Object>();

	public Config(String fileName, Logger logger) {
		this.fileName = fileName;
		if (!this.fileName.endsWith(".config")) this.fileName+= ".config";
		this.logger = logger;
	}

	public void save() {
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

	@SuppressWarnings("unchecked")
	public void load() {
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


	public void saveConfigs(String key, MappedRadioButtons<?> mappedRadioButtons) {
		Map<String,Boolean> saveMap = new HashMap<String,Boolean>();
		mappedRadioButtons.mapOptionKey2RadioButton.keySet().stream()
		.forEach(optionKey -> {
			saveMap.put(toString(optionKey),mappedRadioButtons.mapOptionKey2RadioButton.get(optionKey).isSelected());
		});
		map.put(key, saveMap);
	}

	public void restoreConfigs(String key, MappedRadioButtons<?> mappedRadioButtons) {
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


	public void saveConfigs(String key, FileChoiceBox fileChoiceBox) {
		map.put(key+"-files",fileChoiceBox.getFiles());
		map.put(key+"-selected", fileChoiceBox.getSelected());
	}

	@SuppressWarnings("unchecked")
	public void restoreConfigs(String key, FileChoiceBox fileChoiceBox) {
		Object objFiles = get(key+"-files",fileChoiceBox.getFiles());
		if (objFiles instanceof List<?>) {
			fileChoiceBox.setFiles((List<File>)objFiles);
		}
		Object objFile  = get(key+"-selected",null);
		if (objFile instanceof File) {
			fileChoiceBox.setSelected((File)objFile);
		}
	}

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
