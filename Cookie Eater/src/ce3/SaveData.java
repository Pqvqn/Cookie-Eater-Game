package ce3;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class SaveData {
	
	private HashMap<String,ArrayList<Object>> dataStorage; //keeps tags and data
	
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
			ArrayList<Object> info2 = new ArrayList<Object>();
			
			//test for type of info before adding
			for(String s2:info) {
				if(s2.substring(0,1).equals("*")) { //savedata
					info2.add(new SaveData(s2.substring(1)));
				}else { //string
					info2.add(s2);
				}
			}
			
			//add to data storage
			dataStorage.put(parts[0],info2);
		}
	}

	public Object getData(String tag) {
		return dataStorage.get(tag);
	}
	
	
	public String toString() {
		String ret = "*";
		Iterator<String> it = dataStorage.keySet().iterator();
		while(it.hasNext()) {
			String tag = it.next();
			ret+="|"+tag+":";
			for(Object o : dataStorage.get(tag)) {
				ret+=o.toString()+";";
			}
		}
		return ret;
	}
	
}
