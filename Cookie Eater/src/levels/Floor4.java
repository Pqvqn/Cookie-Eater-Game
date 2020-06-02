package levels;


import ce3.*;
import cookies.Cookie;
import enemies.*;

import java.awt.*;
import java.util.*;

public class Floor4 extends Level{
	
	private int[][] areas = {{0,board.X_RESOL/3,0,board.Y_RESOL/2}, //regions of board - one node per until all filled
			{0,board.X_RESOL/3,board.Y_RESOL/2,board.Y_RESOL},
			{board.X_RESOL/3,2*board.X_RESOL/3,0,board.Y_RESOL/2},
			{board.X_RESOL/3,2*board.X_RESOL/3,board.Y_RESOL/2,board.Y_RESOL},
			{2*board.X_RESOL/3,board.X_RESOL,0,board.Y_RESOL/2},
			{2*board.X_RESOL/3,board.X_RESOL,board.Y_RESOL/2,board.Y_RESOL}};
	
	public Floor4(Board frame) {
		this(frame,null);
	}
	public Floor4(Board frame, Level nextFloor) {
		super(frame, nextFloor);
		name = "Frozen Chambers";
		nameAbbrev = "ice";
		next = nextFloor;
		scale = .85;
		board = frame;
		minDecay = 90;
		maxDecay = 3000;
		nodes = new ArrayList<int[]>();
		lines = new ArrayList<int[]>();
		bgColor = new Color(50,60,60);
		wallColor = new Color(200,210,210);
	}
	
	public void build() {
		super.build();
		genPaths(9, 100, 150, 80, 10, areas); //num nodes, min radius around nodes, max radius around nodes, radius around lines, nodes per line, board regions to fill
		genWalls(100, 40, 600); //wall separation, wall min size, wall max size
		nodes = new ArrayList<int[]>();
		lines = new ArrayList<int[]>();
	}
	
	
	
	public void placeCookies() {
		super.placeCookies(5,(int)(100*scale));
	}
	
	public void spawnEnemies() { 
		ArrayList<Cookie> spaces = board.cookies;
		for(int i=0;i<Math.random()*2+2;i++) {
			Cookie c = spaces.remove((int)(Math.random()*spaces.size()));
			board.enemies.add(new EnemyBlob(board,c.getX(),c.getY()));}
		for(int i=0;i<Math.random()*1;i++) {
			Cookie c = spaces.remove((int)(Math.random()*spaces.size()));
			board.enemies.add(new EnemyGlob(board,c.getX(),c.getY()));}
	}

}
