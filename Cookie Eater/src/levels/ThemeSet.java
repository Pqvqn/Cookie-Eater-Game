package levels;

import java.util.*;

import ce3.*;

public class ThemeSet {

	private HashMap<String, Double> tw;
	
	public ThemeSet() {
		tw = new HashMap<String, Double>();
	}
	
	public ThemeSet(String[] themes, double[] weights) {
		this();
		for(int i=0; i<themes.length; i++) {
			tw.put(themes[i], weights[i]);
		}
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
	
	//calculate theme affinity by summing products of theme weights
	public double affinityWith(ThemeSet other) {
		double total = 0;
		Iterator<String> it = tw.keySet().iterator();
		while(it.hasNext()) {
			String theme = it.next();
			total += weigh(theme) * other.weigh(theme);
		}
		return total;
	}
	//whether another theme surpasses each corresponding weight value
	public boolean metBy(ThemeSet other) {
		Iterator<String> it = tw.keySet().iterator();
		while(it.hasNext()) {
			String theme = it.next();
			if(weigh(theme) > other.weigh(theme)) {
				return false;
			}
		}
		return true;
	}
	//weight given to theme
	public double weigh(String theme) {
		if(tw.keySet().contains(theme)) {
			return tw.get(theme);
		}else {
			return 0;
		}
	}
	//number of themes used
	public int size() {return tw.keySet().size();}
	
	public Iterator<String> themeIterator() {return tw.keySet().iterator();}
	
	public String text(int roundScale) {
		String ret = "";
		Iterator<String> it = tw.keySet().iterator();
		while(it.hasNext()) {
			String theme = it.next();
			double weight = ((int)(weigh(theme)*roundScale))/((double)roundScale);
			ret += theme +": "+weight+"`";
		}
		return ret;
	}
	
}
