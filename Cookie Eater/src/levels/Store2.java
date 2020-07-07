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
		shieldNum = 0;
		int[][][] vs = {{{board.X_RESOL/2-200,board.Y_RESOL-150} , {board.X_RESOL/2+200,board.Y_RESOL-150} , {board.X_RESOL/2,board.Y_RESOL-150} , {board.X_RESOL/2-200,board.Y_RESOL-250} , {board.X_RESOL/2+200,board.Y_RESOL-250} , {board.X_RESOL/2,board.Y_RESOL-250}} ,
				{{board.X_RESOL/2-200,150} , {board.X_RESOL/2+200,150} , {board.X_RESOL/2,150} , {board.X_RESOL/2-200,250} , {board.X_RESOL/2+200,250} , {board.X_RESOL/2,250}} };
		vendorSpaces = vs;
		int[][] ps = {{board.X_RESOL/2-300,400}};
		passerbySpaces = ps;
		configureCatalogue(defaultItemCost,catalogue,prices);
	}
	
}
