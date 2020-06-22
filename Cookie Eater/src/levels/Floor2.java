package levels;


import java.awt.Color;
import java.util.ArrayList;

import ce3.*;
import cookies.CookieItem;
import entities.*;

public class Floor2 extends Level{

	//public double scale;
	//private Level next;
	//private Board board;
	//public double startx;
	//public double starty;
	
	public Floor2(Board frame) {
		this(frame,null);
	}
	public Floor2(Board frame, Level nextFloor) {
		super(frame, nextFloor);
		name = "Dungeon Foyer";
		nameAbbrev = "dun";
		next = nextFloor;
		scale = .95;
		board = frame;
		minDecay = 90;
		maxDecay = 3000;
		bgColor = new Color(100,100,100);
		wallColor = new Color(50,40,30);
	}
	
	public void build() {
		super.build();
		genWalls(3);
		
	}
	//creates random walls
	public void genWalls(int num) {
		for(int i=0; i<num; i++) {
			int x=-1,y=-1,w=-1,h=-1; //regenerate wall until it doesn't overlap player start
			while(x<0 || collidesCircleAndRect((int)(startx+.5),(int)(starty+.5),board.player.getRadius()*3,(int)(x-.5*w+.5),(int)(y-.5*h+.5),w,h)) {
				x=(int)(Math.random()*board.X_RESOL);
				y=(int)(Math.random()*board.Y_RESOL);
				w=(int)(Math.random()*300)+200;
				h=(int)(Math.random()*300)+200;
			}
			board.walls.add(new Wall(board,(int)(x-.5*w+.5),(int)(y-.5*h+.5),w,h)); //adds wall to list of walls
		}
	}
	public void placeCookies() {
		super.placeCookies(50,(int)(100*scale));
	}
	public void spawnEnemies() { 
		ArrayList<String> possible = new ArrayList<String>();
		possible.add("Field");
		for(int i=0;i<Math.random()*2+1;i++) {
			Enemy e;
			spawnAtRandom(e = new EnemyBlob(board,0,0));
			e.giveCookie(new CookieItem(board,0,0,board.currFloor.generateItem(possible.get((int)(Math.random()*possible.size()))),0));
		}
		
		for(int i=0;i<1;i++) {
			Enemy e;
			spawnAtRandom(e = new EnemyGlob(board,0,0));
			e.giveCookie(new CookieItem(board,0,0,board.currFloor.generateItem(possible.get((int)(Math.random()*possible.size()))),0));
		}
	}

}
