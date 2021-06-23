package levels;


import ce3.*;
import cookies.*;
import entities.*;
import items.*;
import mechanisms.*;

import java.awt.*;
import java.util.*;

public class FloorRound extends Level{
	
	private int[][] areas = {{0,board.x_resol/2,0,board.y_resol/2}, //regions of board - one node per until all filled
			{0,board.x_resol/2,board.y_resol/2,board.y_resol},
			{board.x_resol/2,board.x_resol,0,board.y_resol/2},
			{board.x_resol/2,board.x_resol,board.y_resol/2,board.y_resol}};
	
	public FloorRound(Game frame, Board gameboard, String id) {
		this(frame,gameboard,id,null);
	}
	public FloorRound(Game frame, Board gameboard, String id, ArrayList<Level> nextFloor) {
		super(frame,gameboard,id,nextFloor);
		name = "Hostile Tunnels";
		nameAbbrev = "enm";
		scale = .9;
		minDecay = 90;
		maxDecay = 3000;
		bgColor = new Color(50,50,50);
		wallColor = new Color(20,10,30);
	}
	public FloorRound(Game frame, Board gameboard, ArrayList<Level> nextFloor, SaveData sd) {
		super(frame, gameboard, nextFloor, sd);
	}
	
	public void build() {
		super.build();
		genPaths(4, 100, 300, 100, 10, areas); //num nodes, min radius around nodes, max radius around nodes, radius around lines, nodes per line, board regions to fill
		genRoundWalls(100, 50, 600); //wall separation, wall min size, wall max size
		nodes = new ArrayList<int[]>();
		lines = new ArrayList<int[]>();
		
		
		int len = 2;
		Path path = new Path(len);
		int x = (int)(.5+Math.random()*board.x_resol);
		int y = (int)(.5+Math.random()*board.y_resol);
		path.setCheckpoint(0,x,y,Path.TIME,100,0,0,0);
		path.setCheckpoint(1,x,y,Path.TIME,100,200,0,0);
		board.mechanisms.add(new WallMove(game,board,(int)(.5+path.position()[0]),(int)(.5+path.position()[1]),(int)(.5+path.size()[0]),path));
		
		
		
	}
	
	
	public void placeCookies() {
		super.placeCookies(15,(int)(100*scale));
	}

	public void spawnEnemies() { 
		ArrayList<String> possible = new ArrayList<String>();
		possible.add("Field");
		possible.add("Boost");
		possible.add("Ricochet");
		possible.add("Teleport");
		int cycle = game.getCycle();
		for(int i=0;i<Math.random()*3;i++) {
			Enemy e;
			spawnAtRandom(e = new EnemyBlob(game,board,cycle,0,0));
			if(Math.random()>.5)e.giveCookie(new CookieItem(game,board,0,0,Item.generateItem(game,board,possible.get((int)(Math.random()*possible.size()))),0));
		}
		for(int i=0;i<Math.random()*2;i++) {
			spawnAtRandom(new EnemyParasite(game,board,cycle,0,0));
		}
		for(int i=0;i<1;i++) {
			spawnAtRandom(new EnemyBlob(game,board,cycle,0,0));
		}
		for(int i=0;i<1;i++) {
			spawnAtRandom(new EnemyGlob(game,board,cycle,0,0));
		}
		
	}
	
}
