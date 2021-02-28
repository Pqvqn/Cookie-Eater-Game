package ce3;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class SaveData {
	
	private HashMap<String,ArrayList<Object>> dataStorage; //keeps tags and data
	private final String sectionSep = "]", tagSep = ":", infoSep = ";"; //strings for separating data file information
	private final String savedataOpen = "{", savedataClose = "}"; //strings for marking certain data types
	
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
		if(dataStorage.get(tag)==null)dataStorage.put(tag,new ArrayList<Object>());
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
				for(int j=0; j<info.length; j++) {
					String s2 = info[j];
					if(s2.contains(savedataOpen)) { //savedata
						String s3 = "";
						int markCount = 0;
						if(sections[i].contains(savedataOpen)) {
							markCount++;
						}if(sections[i].contains(savedataClose)) {
							markCount--;
						}
						for(i+=1; markCount!=0 && i<sections.length; i++) { //continue until all sub-datas are closed
							s3+=sectionSep+sections[i];
							if(sections[i].contains(savedataOpen)) {
								markCount++;
							}if(sections[i].contains(savedataClose)) {
								markCount--;
							}
						}
						i--;
						info2.add(new SaveData(s3));
					}else { //string
						info2.add(s2);
					}
				}
				//add to data storage
				dataStorage.put(parts[0],info2);
			}
		}
	}
	
	//get info list by its tag
	public ArrayList<Object> getData(String tag) {
		return dataStorage.get(tag);
	}
	//get info by its tag
	public Object getData(String tag, int index) {
		return dataStorage.get(tag).get(index);
	}
	//get info in string form
	public String getString(String tag, int index) {
		return getData(tag,index).toString();
	}
	//get info in double form
	public Double getDouble(String tag, int index) {
		return Double.parseDouble(getString(tag,index));
	}
	//get info in integer form
	public Integer getInteger(String tag, int index) {
		return Integer.parseInt(getString(tag,index));
	}
	//get info in boolean form
	public Boolean getBoolean(String tag, int index) {
		return Boolean.parseBoolean(getString(tag,index));
	}
	//get info in list of SaveData instances form
	public ArrayList<SaveData> getSaveDataList(String tag) {
		ArrayList<SaveData> ret = new ArrayList<SaveData>();
		ArrayList<Object> get = getData(tag);
		for(int i=0; i<get.size(); i++) {
			Object o = get.get(i);
			if(o instanceof SaveData) {
				ret.add((SaveData)o);
			}else {
				ret.add(new SaveData(o.toString()));
			}
		}
		return ret;
	}
	
	//number of tags registered
	public int numTags() {
		return dataStorage.size();
	}
	
	//convert storage into string form
	public String toString() {
		String ret = savedataOpen;
		Iterator<String> it = dataStorage.keySet().iterator();
		while(it.hasNext()) {
			String tag = it.next();
			ret+=sectionSep+tag+tagSep;
			boolean firstEntry = true;
			for(Object o : dataStorage.get(tag)) {
				if(firstEntry) {
					firstEntry = false;
				}else {
					ret+=infoSep;
				}
				if(o!=null) {
					ret+=o.toString();
				}
			}
		}
		ret+=sectionSep+savedataClose;
		return ret;
	}
	
	//saves storage string to file
	public void saveToFile(File f) throws FileNotFoundException {
		try (PrintWriter out = new PrintWriter(f)) {
		    out.println(this);
		}
	}
	
}
