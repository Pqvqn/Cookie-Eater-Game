package levels;


import ce3.*;

import java.awt.Color;
import java.util.*;

public class Floor3 extends Level{
	
	private int[][] areas = {{0,board.X_RESOL/2,0,board.Y_RESOL/2}, //regions of board - one node per until all filled
			{0,board.X_RESOL/2,board.Y_RESOL/2,board.Y_RESOL},
			{board.X_RESOL/2,board.X_RESOL,0,board.Y_RESOL/2},
			{board.X_RESOL/2,board.X_RESOL,board.Y_RESOL/2,board.Y_RESOL}};
	
	public Floor3(Board frame) {
		this(frame,null);
	}
	public Floor3(Board frame, Level nextFloor) {
		super(frame, nextFloor);
		next = nextFloor;
		scale = .9;
		board = frame;
		minDecay = 90;
		maxDecay = 3000;
		shieldCost = 75;
		nodes = new ArrayList<int[]>();
		lines = new ArrayList<int[]>();
		bgColor = new Color(50,50,50);
		wallColor = new Color(20,10,30);
	}
	
	public void build() {
		super.build();
		genPaths(4, 100, 200, 100, 10, areas); //num nodes, radius around nodes, radius around lines, nodes per line, board regions to fill
		genWalls(200, 50, 600); //wall separation, wall min size, wall max size
		nodes = new ArrayList<int[]>();
		lines = new ArrayList<int[]>();
	}
	
	
	
	public void placeCookies() {
		super.placeCookies(5,(int)(100*scale));
	}

}
