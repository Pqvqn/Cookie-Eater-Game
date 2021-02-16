package levels;

import ce3.*;

public class Store2 extends Store{
	
	public Store2(Game frame, Board gameboard) {
		super(frame,gameboard);
		name = "Dungeon Foyer";
		nameAbbrev = "dun";
		scale = .95;
		shieldCost = 30;
		defaultItemCost = 40;
		installCost = 2;
		shieldNum = 0;
		int[][][] vs = {{{board.x_resol/2-200,board.y_resol-110} , {board.x_resol/2+200,board.y_resol-110} , {board.x_resol/2,board.y_resol-110} , {board.x_resol/2-200,board.y_resol-260} , {board.x_resol/2+200,board.y_resol-260} , {board.x_resol/2,board.y_resol-260}} ,
				{{board.x_resol/2-200,110} , {board.x_resol/2+200,110} , {board.x_resol/2,110} , {board.x_resol/2-200,260} , {board.x_resol/2+200,260} , {board.x_resol/2,260}} };
		vendorSpaces = vs;
		int[][] ps = {{board.x_resol/2-300,400}};
		passerbySpaces = ps;
		int[][] ms = {{board.x_resol-200,board.y_resol/2+180},{board.x_resol-390,315},{board.x_resol-390,board.y_resol-315},{board.x_resol-185,board.y_resol/2}};
		mechanicSpaces = ms;
		configureCatalogue(defaultItemCost,catalogue,prices);
	}
	
}
