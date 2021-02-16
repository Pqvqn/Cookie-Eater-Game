package levels;

import ce3.*;

public class Store4 extends Store{
	
	public Store4(Game frame, Board gameboard) {
		super(frame,gameboard);
		name = "Frozen Chambers";
		nameAbbrev = "ice";
		scale = .85;
		shieldCost = 75;
		defaultItemCost = 90;
		installCost = 10;
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

