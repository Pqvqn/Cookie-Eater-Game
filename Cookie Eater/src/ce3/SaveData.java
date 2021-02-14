package ce3;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class SaveData {
	
	private HashMap<String,ArrayList<Object>> dataStorage; //keeps tags and data
	private final String sectionSep = "]", tagSep = ":", infoSep = ";"; //strings for separating data file information
	private final String savedataMark = "*"; //strings for marking certain data types
	
	public SaveData(File f) throws IOException {
		this();
		String data = Files.readString(f.toPath()); //read full file into string
		interpretString(data);
	}
	
	public SaveData(String s) {
		this();
		interpretString(s);
	}
	
	public SaveData() {
		dataStorage = new HashMap<String,ArrayList<Object>>();
	}
	
	//add data point (replace current list with list of size 1)
	public void addData(String tag, Object data) {
		dataStorage.put(tag,new ArrayList<Object>());
		dataStorage.get(tag).add(data);
	}
	
	//add data point (add point to current list at index)
	public void addData(String tag, Object data, int index) {
		dataStorage.get(tag).add(index,data);
	}
	
	//add data point (replace list with new list)
	public void addData(String tag, ArrayList<Object> data) {
		dataStorage.put(tag,data);
	}
	
	//add data points in string form to storage
	private void interpretString(String s) {
		//split sections of data
		String[] sections = s.split(sectionSep);
		for(int i=0; i<sections.length; i++) {
			
			//split tag from information
			String[] parts = sections[i].split(tagSep);
			if(parts.length==2) { //valid data must have tag and info
				
				//split parts of information
				String[] info = parts[1].split(infoSep);
				ArrayList<Object> info2 = new ArrayList<Object>();
				
				//test for type of info before adding
				for(String s2:info) {
					if(s2.substring(0,1).equals(savedataMark)) { //savedata
						info2.add(new SaveData(s2.substring(1)));
					}else { //string
						info2.add(s2);
					}
				}
				
				//add to data storage
				dataStorage.put(parts[0],info2);
			}
		}
	}
	
	//get info by its tag
	public ArrayList<Object> getData(String tag) {
		return dataStorage.get(tag);
	}
	
	//convert storage into string form
	public String toString() {
		String ret = savedataMark;
		Iterator<String> it = dataStorage.keySet().iterator();
		while(it.hasNext()) {
			String tag = it.next();
			ret+=sectionSep+tag+tagSep;
			for(Object o : dataStorage.get(tag)) {
				if(o!=null)ret+=o.toString()+infoSep;
			}
		}
		return ret;
	}
	
	//saves storage string to file
	public void saveToFile(File f) throws FileNotFoundException {
		try (PrintWriter out = new PrintWriter(f)) {
		    out.println(this);
		}
	}
	
}
