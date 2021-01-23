package levels;

import ce3.*;

public class Store4 extends Store{
	
	public Store4(Game frame) {
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
		int[][] ms = {{board.X_RESOL-200,board.Y_RESOL/2+180},{board.X_RESOL-390,315},{board.X_RESOL-390,board.Y_RESOL-315},{board.X_RESOL-185,board.Y_RESOL/2}};
		mechanicSpaces = ms;
		configureCatalogue(defaultItemCost,catalogue,prices);
	}
	
}

