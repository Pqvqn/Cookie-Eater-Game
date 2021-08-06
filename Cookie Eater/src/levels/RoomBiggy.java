package levels;


import ce3.*;

import java.awt.*;
import java.util.*;

public class RoomBiggy extends Level{
	
	private int[][] areas = {{0,board.x_resol/2,0,board.y_resol/2}, //regions of board - one node per until all filled
			{0,board.x_resol/2,board.y_resol/2,board.y_resol},
			{board.x_resol/2,board.x_resol,0,board.y_resol/2},
			{board.x_resol/2,board.x_resol,board.y_resol/2,board.y_resol}};
	
	public RoomBiggy(Game frame, Board gameboard, String id) {
		this(frame,gameboard,id,null);
	}
	public RoomBiggy(Game frame, Board gameboard, String id, ArrayList<Level> nextFloor) {
		super(frame,gameboard,id,nextFloor);
		name = "Big Boi";
		scale = .3;
		minDecay = 3600;
		maxDecay = 18000;
		//shieldCost = 250;
		exitProportion = .5;
		bgColor = new Color(30,0,30);
		wallColor = new Color(200,50,0);
	}
	public RoomBiggy(Game frame, Board gameboard, ArrayList<Level> prev, ArrayList<Level> next, SaveData sd) {
		super(frame, gameboard, prev, next, sd);
	}
	
	public void build() {
		super.build();
		genPaths(15, 75, 200, 50, 20, areas); //num nodes, min radius around nodes, max radius around nodes, radius around lines, nodes per line, board regions to fill
		genWalls(50, 10, 600, true); //wall separation, wall min size, wall max size, angled
		nodes = new ArrayList<int[]>();
		lines = new ArrayList<int[]>();
	}
	
	
	
	public void placeCookies() {
		super.placeCookies(1,(int)(100*scale));
	}

}
