package levels;


import ce3.*;

import java.awt.*;
import java.util.*;

public class FloorBiggy extends Level{
	
	private int[][] areas = {{0,board.X_RESOL/2,0,board.Y_RESOL/2}, //regions of board - one node per until all filled
			{0,board.X_RESOL/2,board.Y_RESOL/2,board.Y_RESOL},
			{board.X_RESOL/2,board.X_RESOL,0,board.Y_RESOL/2},
			{board.X_RESOL/2,board.X_RESOL,board.Y_RESOL/2,board.Y_RESOL}};
	
	public FloorBiggy(Board frame) {
		this(frame,null);
	}
	public FloorBiggy(Board frame, Level nextFloor) {
		super(frame, nextFloor);
		name = "Big Boi";
		next = nextFloor;
		scale = .3;
		board = frame;
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
