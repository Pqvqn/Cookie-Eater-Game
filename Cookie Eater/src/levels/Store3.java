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
