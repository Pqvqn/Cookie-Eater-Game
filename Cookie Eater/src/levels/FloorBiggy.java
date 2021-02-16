package levels;


import ce3.*;

import java.awt.*;
import java.util.*;

public class FloorBiggy extends Level{
	
	private int[][] areas = {{0,board.x_resol/2,0,board.y_resol/2}, //regions of board - one node per until all filled
			{0,board.x_resol/2,board.y_resol/2,board.y_resol},
			{board.x_resol/2,board.x_resol,0,board.y_resol/2},
			{board.x_resol/2,board.x_resol,board.y_resol/2,board.y_resol}};
	
	public FloorBiggy(Game frame, Board gameboard) {
		this(frame,gameboard,null);
	}
	public FloorBiggy(Game frame, Board gameboard, Level nextFloor) {
		super(frame,gameboard, nextFloor);
		name = "Big Boi";
		next = nextFloor;
		scale = .3;
		minDecay = 3600;
		maxDecay = 18000;
		//shieldCost = 250;
		nodes = new ArrayList<int[]>();
		lines = new ArrayList<int[]>();
		bgColor = new Color(30,0,30);
		wallColor = new Color(200,50,0);
	}
	
	public void build() {
		super.build();
		genPaths(15, 75, 200, 50, 20, areas); //num nodes, min radius around nodes, max radius around nodes, radius around lines, nodes per line, board regions to fill
		genWalls(50, 10, 600); //wall separation, wall min size, wall max size
		nodes = new ArrayList<int[]>();
		lines = new ArrayList<int[]>();
	}
	
	
	
	public void placeCookies() {
		super.placeCookies(1,(int)(100*scale));
	}

}
