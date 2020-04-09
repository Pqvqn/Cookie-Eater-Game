package levels;


import ce3.*;

import java.awt.*;
import java.util.*;

public class Floor4 extends Level{
	
	private int[][] areas = {{0,board.X_RESOL/2,0,board.Y_RESOL/2}, //regions of board - one node per until all filled
			{0,board.X_RESOL/2,board.Y_RESOL/2,board.Y_RESOL},
			{board.X_RESOL/2,board.X_RESOL,0,board.Y_RESOL/2},
			{board.X_RESOL/2,board.X_RESOL,board.Y_RESOL/2,board.Y_RESOL}};
	
	public Floor4(Board frame) {
		this(frame,null);
	}
	public Floor4(Board frame, Level nextFloor) {
		super(frame, nextFloor);
		next = nextFloor;
		scale = .3;
		board = frame;
		minDecay = 60;
		maxDecay = 3600;
		nodes = new ArrayList<int[]>();
		lines = new ArrayList<int[]>();
		bgColor = new Color(30,0,30);
		wallColor = new Color(200,50,0);
	}
	
	public void build() {
		super.build();
		genPaths(10, 100, 50, 30, areas); //num nodes, radius around nodes, radius around lines, nodes per line, board regions to fill
		genWalls(50, 10, 600); //wall separation, wall min size, wall max size
		nodes = new ArrayList<int[]>();
		lines = new ArrayList<int[]>();
	}
	
	
	
	public void placeCookies() {
		super.placeCookies(1,(int)(100*scale));
	}

}
