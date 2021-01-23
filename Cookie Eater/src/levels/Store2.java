package levels;

import ce3.*;

public class Store2 extends Store{
	
	public Store2(Game frame) {
		super(frame);
		name = "Dungeon Foyer";
		nameAbbrev = "dun";
		scale = .95;
		shieldCost = 30;
		defaultItemCost = 40;
		installCost = 2;
		shieldNum = 0;
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
