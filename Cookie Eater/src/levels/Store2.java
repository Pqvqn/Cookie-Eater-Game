package levels;

import ce3.*;

public class Store2 extends Store{
	
	public Store2(Board frame) {
		super(frame);
		name = "Dungeon Foyer";
		nameAbbrev = "dun";
		scale = .95;
		shieldCost = 30;
		defaultItemCost = 40;
		installCost = 2;
		shieldNum = 2;
		configureCatalogue(defaultItemCost,catalogue,prices);
	}
	
}
