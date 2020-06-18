package levels;

import ce3.*;

public class Store3 extends Store{
	
	public Store3(Board frame) {
		super(frame);
		name = "Descending Labyrinths";
		nameAbbrev = "lab";
		scale = .9;
		shieldCost = 50;
		defaultItemCost = 70;
		installCost = 5;
		configureCatalogue(defaultItemCost,catalogue,prices);
	}
	
}
