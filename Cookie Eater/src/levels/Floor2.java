package levels;


import java.awt.*;
import java.util.*;

import ce3.*;
import entities.*;
import mechanisms.*;

public class Floor2 extends Level{

	//public double scale;
	//private Level next;
	//private Board board;
	//public double startx;
	//public double starty;
	
	public Floor2(Game frame, Board gameboard) {
		this(frame,gameboard,null);
	}
	public Floor2(Game frame, Board gameboard, ArrayList<Level> nextFloor) {
		super(frame,gameboard, nextFloor);
		name = "Dungeon Foyer";
		nameAbbrev = "dun";
		scale = .95;
		minDecay = 90;
		maxDecay = 3000;
		bgColor = new Color(100,100,100);
		wallColor = new Color(50,40,30);
	}
	public Floor2(Game frame, Board gameboard, ArrayList<Level> nextFloor, SaveData sd) {
		super(frame, gameboard, nextFloor, sd);
	}
	
	public void build() {
		super.build();
		genWalls(3);
		
	}
	//creates random walls
	public void genWalls(int num) {
		for(int i=0; i<num; i++) {
			int x=-1,y=-1,w=-1,h=-1; //regenerate wall until it doesn't overlap player start
			while(x<0 || collidesCircleAndRect((int)(startx+.5),(int)(starty+.5),board.player().getRadius()*3,(int)(x-.5*w+.5),(int)(y-.5*h+.5),w,h)) {
				x=(int)(Math.random()*board.x_resol);
				y=(int)(Math.random()*board.y_resol);
				w=(int)(Math.random()*300)+200;
				h=(int)(Math.random()*300)+200;
			}
			board.walls.add(new Wall(game,board,(int)(x-.5*w+.5),(int)(y-.5*h+.5),w,h)); //adds wall to list of walls
		}
		//board.walls.add(new Wall(board,(int)(Math.random()*(board.x_resol-100))+50,(int)(Math.random()*(board.y_resol-100))+50,(int)(Math.random()*200)+100,(int)(Math.random()*200)+100,Math.random()*Math.PI*2));
		//board.walls.add(new Wall(board,(int)(Math.random()*(board.x_resol-100))+50,(int)(Math.random()*(board.y_resol-100))+50,(int)(Math.random()*400)+100));
		/*int len = (int)(Math.random()*3+2);
		Path path = new Path(len);
		for(int i=0; i<len; i++) {
			int x = (int)(Math.random()*(board.x_resol-100))+50;
			int y = (int)(Math.random()*(board.y_resol-100))+50;
			int mode = Path.SPEED;
			double rate = Math.random()*10;
			int sizea = (int)(Math.random()*200)+100;
			int sizeb = (int)(Math.random()*200)+100;
			double a = Math.random()*Math.PI*4-Math.PI*2;
			path.setCheckpoint(i,x,y,mode,rate,sizea,sizeb,a);
		}
		board.mechanisms.add(new WallMove(game,board,(int)(.5+path.position()[0]),(int)(.5+path.position()[1]),(int)(.5+path.size()[0]),(int)(.5+path.size()[1]),path.angle(),path));
		*/
		/*WallPath path2 = new WallPath(2);
		path2.setCheckpoint(0,400,400,WallPath.TIME,2000,400,50,Math.PI*10);
		path2.setCheckpoint(1,400,400,WallPath.TIME,2000,400,50,Math.PI*-10);
		board.mechanisms.add(new MovingWall(game,board,(int)(.5+path2.position()[0]),(int)(.5+path2.position()[1]),(int)(.5+path2.size()[0]),(int)(.5+path2.size()[1]),path2.angle(),path2));
		*/
		board.mechanisms.add(new WallDoor(game,board,400,600,200,100,23));
	}
	public void placeCookies() {
		super.placeCookies(50,(int)(100*scale));
	}
	public void spawnEnemies() { 
		int cycle = game.getCycle();
		//ArrayList<String> possible = new ArrayList<String>();
		//possible.add("Field");
		for(int i=0;i<Math.random()*3-1;i++) {
			//Enemy e;
			spawnAtRandom(new EnemyBlob(game,board,cycle,0,0));
			//e.giveCookie(new CookieItem(board,0,0,Level.generateItem(board,possible.get((int)(Math.random()*possible.size()))),0));
		}
		for(int i=0;i<Math.random()*2-1;i++) {
			//Enemy e;
			spawnAtRandom(new EnemyBloc(game,board,cycle,0,0));
			//e.giveCookie(new CookieItem(board,0,0,Level.generateItem(board,possible.get((int)(Math.random()*possible.size()))),0));
		}
		
		for(int i=0;i<Math.random()*3-1;i++) {
			//Enemy e;
			spawnAtRandom(new EnemyParasite(game,board,cycle,0,0));}
		
		/*for(int i=0;i<1;i++) {
			Enemy e;
			spawnAtRandom(e = new EnemyGlob(board,0,0));
			e.giveCookie(new CookieItem(board,0,0,Level.generateItem(board,possible.get((int)(Math.random()*possible.size()))),0));
		}*/
	}

}
