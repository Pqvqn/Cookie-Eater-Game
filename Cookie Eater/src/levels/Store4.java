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
		int[][][] vs = {{{board.X_RESOL/2-200,board.Y_RESOL-150} , {board.X_RESOL/2+200,board.Y_RESOL-150} , {board.X_RESOL/2,board.Y_RESOL-150} , {board.X_RESOL/2-200,board.Y_RESOL-250} , {board.X_RESOL/2+200,board.Y_RESOL-250} , {board.X_RESOL/2,board.Y_RESOL-250}} ,
				{{board.X_RESOL/2-200,150} , {board.X_RESOL/2+200,150} , {board.X_RESOL/2,150} , {board.X_RESOL/2-200,250} , {board.X_RESOL/2+200,250} , {board.X_RESOL/2,250}} };
		vendorSpaces = vs;
		int[][] ps = {{board.X_RESOL/2-300,400}};
		passerbySpaces = ps;
		configureCatalogue(defaultItemCost,catalogue,prices);
	}
	
}

