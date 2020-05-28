package levels;


import ce3.*;
import cookies.*;
import enemies.*;

import java.awt.*;
import java.util.*;

public class Floor5 extends Level{
	
	private int[][] areas = {{0,board.X_RESOL/2,0,board.Y_RESOL/2}, //regions of board - one node per until all filled
			{0,board.X_RESOL/2,board.Y_RESOL/2,board.Y_RESOL},
			{board.X_RESOL/2,board.X_RESOL,0,board.Y_RESOL/2},
			{board.X_RESOL/2,board.X_RESOL,board.Y_RESOL/2,board.Y_RESOL}};
	
	public Floor5(Board frame) {
		this(frame,null);
	}
	public Floor5(Board frame, Level nextFloor) {
		super(frame, nextFloor);
		name = "Hostile Tunnels";
		nameAbbrev = "enm";
		next = nextFloor;
		scale = .6;
		board = frame;
		minDecay = 90;
		maxDecay = 3000;
		nodes = new ArrayList<int[]>();
		lines = new ArrayList<int[]>();
		bgColor = new Color(150,90,40);
		wallColor = new Color(50,20,10);
	}
	
	public void build() {
		super.build();
		genPaths(4, 200, 300, 100, 10, areas); //num nodes, min radius around nodes, max radius around nodes, radius around lines, nodes per line, board regions to fill
		genWalls(200, 50, 600); //wall separation, wall min size, wall max size
		nodes = new ArrayList<int[]>();
		lines = new ArrayList<int[]>();
	}
	
	public void placeCookies() {
		super.placeCookies(15,(int)(100*scale));
	}
	public void spawnEnemies() {
		for(int i=0;i<Math.random()*5+2;i++) {
			Cookie c = board.cookies.get((int)(Math.random()*board.cookies.size()));
			board.enemies.add(new EnemyBlob(board,c.getX(),c.getY()));}
		for(int i=0;i<Math.random()*2;i++) {
			Cookie c = board.cookies.get((int)(Math.random()*board.cookies.size()));
			board.enemies.add(new EnemyGlob(board,c.getX(),c.getY()));}
		for(int i=0;i<Math.random()*2;i++) {
			Cookie c = board.cookies.get((int)(Math.random()*board.cookies.size()));
			board.enemies.add(new EnemySlob(board,c.getX(),c.getY()));}
		Cookie c = board.cookies.get((int)(Math.random()*board.cookies.size()));
		board.enemies.add(new EnemySpawner(board,c.getX(),c.getY()));
	}

}
