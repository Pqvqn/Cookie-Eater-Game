package ce3;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class SaveData {
	
	private HashMap<String,Object> dataStorage; //keeps tags and data
	
	public SaveData(File f) throws IOException {
		String data = Files.readString(f.toPath()); //read full file into string
		interpretString(data);
	}
	
	public SaveData(String s) {
		interpretString(s);
	}
	
	private void interpretString(String s) {
		//split sections of data
		String[] sections = s.split("|");
		for(int i=0; i<sections.length; i++) {
			//split tag from information
			String[] parts = sections[i].split(":");
			//split parts of information
			String[] info = parts[1].split(";");
			//add to data storage
			dataStorage.put(parts[0],info);
		}
	}

	public Object getData(String tag) {
		return dataStorage.get(tag);
	}
	
}
