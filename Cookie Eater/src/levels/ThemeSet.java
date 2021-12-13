package levels;

import java.util.*;

public class ThemeSet {

	private HashMap<String, Double> tw;
	
	public ThemeSet() {
		tw = new HashMap<String, Double>();
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
	
}
