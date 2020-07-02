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
		int[][][] vs = {{{board.X_RESOL/2-200,board.Y_RESOL-150} , {board.X_RESOL/2+200,board.Y_RESOL-150} , {board.X_RESOL/2,board.Y_RESOL-150} , {board.X_RESOL/2-200,board.Y_RESOL-250} , {board.X_RESOL/2+200,board.Y_RESOL-250} , {board.X_RESOL/2,board.Y_RESOL-250}} ,
				{{board.X_RESOL/2-200,150} , {board.X_RESOL/2+200,150} , {board.X_RESOL/2,150} , {board.X_RESOL/2-200,250} , {board.X_RESOL/2+200,250} , {board.X_RESOL/2,250}} };
		vendorSpaces = vs;
		int[][] ps = {{board.X_RESOL/2-300,400}};
		passerbySpaces = ps;
		configureCatalogue(defaultItemCost,catalogue,prices);
	}
	
}
