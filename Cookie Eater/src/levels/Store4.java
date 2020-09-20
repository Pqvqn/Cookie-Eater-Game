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
		int[][][] vs = {{{board.X_RESOL/2-200,board.Y_RESOL-110} , {board.X_RESOL/2+200,board.Y_RESOL-110} , {board.X_RESOL/2,board.Y_RESOL-110} , {board.X_RESOL/2-200,board.Y_RESOL-260} , {board.X_RESOL/2+200,board.Y_RESOL-260} , {board.X_RESOL/2,board.Y_RESOL-260}} ,
				{{board.X_RESOL/2-200,110} , {board.X_RESOL/2+200,110} , {board.X_RESOL/2,110} , {board.X_RESOL/2-200,260} , {board.X_RESOL/2+200,260} , {board.X_RESOL/2,260}} };
		vendorSpaces = vs;
		int[][] ps = {{board.X_RESOL/2-300,400}};
		passerbySpaces = ps;
		configureCatalogue(defaultItemCost,catalogue,prices);
	}
	
}

