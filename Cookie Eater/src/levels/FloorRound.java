package levels;


import ce3.*;
import cookies.*;
import entities.*;
import mechanisms.MovingWall;
import mechanisms.WallPath;

import java.awt.*;
import java.util.*;

public class FloorRound extends Level{
	
	private int[][] areas = {{0,board.X_RESOL/2,0,board.Y_RESOL/2}, //regions of board - one node per until all filled
			{0,board.X_RESOL/2,board.Y_RESOL/2,board.Y_RESOL},
			{board.X_RESOL/2,board.X_RESOL,0,board.Y_RESOL/2},
			{board.X_RESOL/2,board.X_RESOL,board.Y_RESOL/2,board.Y_RESOL}};
	
	public FloorRound(Game frame) {
		this(frame,null);
	}
	public FloorRound(Game frame, Level nextFloor) {
		super(frame, nextFloor);
		name = "Hostile Tunnels";
		nameAbbrev = "enm";
		next = nextFloor;
		scale = .9;
		minDecay = 90;
		maxDecay = 3000;
		nodes = new ArrayList<int[]>();
		lines = new ArrayList<int[]>();
		bgColor = new Color(50,50,50);
		wallColor = new Color(20,10,30);
	}
	
	public void build() {
		super.build();
		genPaths(4, 100, 300, 100, 10, areas); //num nodes, min radius around nodes, max radius around nodes, radius around lines, nodes per line, board regions to fill
		genRoundWalls(100, 50, 600); //wall separation, wall min size, wall max size
		nodes = new ArrayList<int[]>();
		lines = new ArrayList<int[]>();
		
		
		int len = 2;
		WallPath path = new WallPath(len);
		int x = (int)(.5+Math.random()*board.X_RESOL);
		int y = (int)(.5+Math.random()*board.Y_RESOL);
		path.setCheckpoint(0,x,y,WallPath.TIME,100,0,0,0);
		path.setCheckpoint(1,x,y,WallPath.TIME,100,200,0,0);
		board.mechanisms.add(new MovingWall(board,(int)(.5+path.position()[0]),(int)(.5+path.position()[1]),(int)(.5+path.size()[0]),path));
		
		
		
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
			spawnAtRandom(e = new EnemyBlob(game,cycle,0,0));
			if(Math.random()>.5)e.giveCookie(new CookieItem(game,0,0,Level.generateItem(game,possible.get((int)(Math.random()*possible.size()))),0));
		}
		for(int i=0;i<Math.random()*2;i++) {
			spawnAtRandom(new EnemyParasite(game,cycle,0,0));
		}
		for(int i=0;i<1;i++) {
			spawnAtRandom(new EnemyBlob(game,cycle,0,0));
		}
		for(int i=0;i<1;i++) {
			spawnAtRandom(new EnemyGlob(game,cycle,0,0));
		}
		
	}
	
}
