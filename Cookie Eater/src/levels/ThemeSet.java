package levels;

import java.util.*;

import ce3.*;

public class ThemeSet {

	private HashMap<String, Double> tw;
	
	public ThemeSet() {
		tw = new HashMap<String, Double>();
	}
	
	public ThemeSet(SaveData sd) {
		tw = new HashMap<String, Double>();
		for(int i=0; i<sd.getData("map").size(); i+=2) {
			tw.put(sd.getString("map",i), sd.getDouble("map",i+1));
		}
	}
	
	public SaveData getSaveData() {
		SaveData data = new SaveData();
		Iterator<String> it = tw.keySet().iterator();
		int i = 0;
		while(it.hasNext()) {
			String theme = it.next();
			double weight = tw.get(theme);
			data.addData("map",theme,i);
			data.addData("map",weight,i+1);
			i+=2;
		}
		return data;
	}
	
	public void addTheme(String theme, Double weight) {tw.put(theme, weight);}
	
	public double affinityWith(ThemeSet other) {
		double total = 0;
		Iterator<String> it = tw.keySet().iterator();
		while(it.hasNext()) {
			String theme = it.next();
			total += weigh(theme) * other.weigh(theme);
		}
		return total;
	}
	
	public double weigh(String theme) {return tw.get(theme);}
	public int size() {return tw.keySet().size();}
	
}
