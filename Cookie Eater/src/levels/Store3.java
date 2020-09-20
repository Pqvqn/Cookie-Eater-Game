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
		configureCatalogue(defaultItemCost,catalogue,prices);
	}
	
}
