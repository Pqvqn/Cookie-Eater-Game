package levels;


import ce3.*;
import cookies.*;
import entities.*;
import items.*;

import java.awt.*;
import java.util.*;

public class Floor3 extends Level{
	
	private int[][] areas = {{0,board.x_resol/2,0,board.y_resol/2}, //regions of board - one node per until all filled
			{0,board.x_resol/2,board.y_resol/2,board.y_resol},
			{board.x_resol/2,board.x_resol,0,board.y_resol/2},
			{board.x_resol/2,board.x_resol,board.y_resol/2,board.y_resol}};
	
	public Floor3(Game frame, Board gameboard, String id) {
		this(frame,gameboard,id,null);
	}
	public Floor3(Game frame, Board gameboard, String id, ArrayList<Level> nextFloor) {
		super(frame,gameboard,id,nextFloor);
		name = "Descending Labyrinths";
		nameAbbrev = "lab";
		scale = .9;
		minDecay = 90;
		maxDecay = 3000;
		bgColor = new Color(50,50,50);
		wallColor = new Color(20,10,30);
	}
	public Floor3(Game frame, Board gameboard, ArrayList<Level> prev, ArrayList<Level> next, SaveData sd) {
		super(frame, gameboard, prev, next, sd);
	}
	
	public void build() {
		super.build();
		genPaths(4, 200, 300, 100, 10, areas); //num nodes, min radius around nodes, max radius around nodes, radius around lines, nodes per line, board regions to fill
		genWalls(100, 50, 600, true); //wall separation, wall min size, wall max size, angled
		nodes = new ArrayList<int[]>();
		lines = new ArrayList<int[]>();
	}
	
	
	public void placeCookies() {
		super.placeCookies(15,(int)(100*scale));
	}

	public void spawnEnemies() { 
		int cycle = game.getCycle();
		ArrayList<String> possible = new ArrayList<String>();
		possible.add("Field");
		possible.add("Boost");
		possible.add("Ricochet");
		possible.add("Teleport");
		for(int i=0;i<Math.random()*3;i++) {
			Enemy e;
			spawnAtRandom(e = new EnemyBlob(game,board,cycle,0,0));
			if(Math.random()>.5)e.giveCookie(new CookieItem(game,board,0,0,Item.generateItem(game,board,possible.get((int)(Math.random()*possible.size()))),0));
		}
		
		for(int i=0;i<1;i++) {
			spawnAtRandom(new EnemyGlob(game,board,cycle,0,0));
		}
		
	}
	
}
