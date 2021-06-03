package levels;


import ce3.*;
import cookies.*;
import entities.*;
import items.*;

import java.awt.*;
import java.util.*;

public class Floor4 extends Level{
	
	private int[][] areas = {{0,board.x_resol/3,0,board.y_resol/2}, //regions of board - one node per until all filled
			{0,board.x_resol/3,board.y_resol/2,board.y_resol},
			{board.x_resol/3,2*board.x_resol/3,0,board.y_resol/2},
			{board.x_resol/3,2*board.x_resol/3,board.y_resol/2,board.y_resol},
			{2*board.x_resol/3,board.x_resol,0,board.y_resol/2},
			{2*board.x_resol/3,board.x_resol,board.y_resol/2,board.y_resol}};
	
	public Floor4(Game frame, Board gameboard) {
		this(frame,gameboard,null);
	}
	public Floor4(Game frame, Board gameboard, ArrayList<Level> nextFloor) {
		super(frame,gameboard, nextFloor);
		name = "Frozen Chambers";
		nameAbbrev = "ice";
		scale = .85;
		minDecay = 90;
		maxDecay = 3000;
		bgColor = new Color(50,60,60);
		wallColor = new Color(200,210,210);
	}
	public Floor4(Game frame, Board gameboard, ArrayList<Level> nextFloor, SaveData sd) {
		super(frame, gameboard, nextFloor, sd);
	}
	
	public void build() {
		super.build();
		genPaths(9, 100, 150, 80, 10, areas); //num nodes, min radius around nodes, max radius around nodes, radius around lines, nodes per line, board regions to fill
		//genWalls(50, 40, 600); //wall separation, wall min size, wall max size
		genWalls(20, 40, 300);
		nodes = new ArrayList<int[]>();
		lines = new ArrayList<int[]>();
	}
	
	
	
	public void placeCookies() {
		super.placeCookies(5,(int)(100*scale));
	}
	
	public void spawnEnemies() { 
		int cycle = game.getCycle();
		ArrayList<String> possible = new ArrayList<String>();
		possible.add("Field");
		possible.add("Boost");
		possible.add("Boost");
		possible.add("Ricochet");
		possible.add("Ricochet");
		possible.add("Teleport");
		possible.add("Shield");
		possible.add("Ghost");
		possible.add("Return");
		possible.add("Circle");
		possible.add("Circle");
		possible.add("Shrink");
		for(int i=0;i<Math.random()*3;i++) {
			Enemy e;
			spawnAtRandom(e = new EnemyBlob(game,board,cycle,0,0));
			if(Math.random()>.3)e.giveCookie(new CookieItem(game,board,0,0,Item.generateItem(game,board,possible.get((int)(Math.random()*possible.size()))),0));
		}
		
		for(int i=0;i<(int)(Math.random()*2);i++) {
			spawnAtRandom(new EnemyGlob(game,board,cycle,0,0));
		}
		for(int i=0;i<(int)(Math.random()*2);i++) {
			spawnAtRandom(new EnemySlob(game,board,cycle,0,0));
		}
		
	}

}
