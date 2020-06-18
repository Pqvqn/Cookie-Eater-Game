package levels;

import ce3.*;

public class Store4 extends Store{
	
	public Store4(Board frame) {
		super(frame);
		name = "Frozen Chambers";
		nameAbbrev = "ice";
		scale = .85;
		shieldCost = 75;
		defaultItemCost = 90;
		installCost = 10;
		configureCatalogue(defaultItemCost,catalogue,prices);
	}
	
}

