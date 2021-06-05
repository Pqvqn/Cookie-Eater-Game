package levels;

import java.util.*;

import ce3.*;
import cookies.*;
import mechanisms.*;

public class Store1 extends Store{
	
	public Store1(Game frame, Board gameboard) {
		super(frame,gameboard);
		name = "Forest Entrance";
		nameAbbrev = "for";
		scale = 1;
	}
	public Store1(Game frame, Board gameboard, ArrayList<Level> nextFloor, SaveData sd) {
		super(frame, gameboard, nextFloor, sd);
	}
	
	public void placeCookies(){
	
		//stats
		board.cookies.add(new CookieStat(game,board,board.x_resol/2+200,board.y_resol/2));
		board.cookies.add(new CookieStat(game,board,board.x_resol/2-200,board.y_resol/2));
		board.cookies.add(new CookieStat(game,board,board.x_resol/2,board.y_resol/2+200));
		board.cookies.add(new CookieStat(game,board,board.x_resol/2,board.y_resol/2-200));
		
		
		board.player().setScoreToWin(2);
	}
	public void build() {
		Wall rig,rig2;
		board.walls.add(new Wall(game,board,0,0,board.x_resol,BORDER_THICKNESS)); //add border walls
		board.walls.add(new Wall(game,board,0,0,BORDER_THICKNESS,board.y_resol));
		board.walls.add(new Wall(game,board,0,board.y_resol-BORDER_THICKNESS,board.x_resol,BORDER_THICKNESS));
		board.walls.add(rig = new Wall(game,board,board.x_resol-BORDER_THICKNESS,0,BORDER_THICKNESS,board.y_resol));
		
		board.walls.add(new Wall(game,board,0,0,710,260));
		board.walls.add(new Wall(game,board,board.x_resol-710,0,board.x_resol-710,260));
		board.walls.add(new Wall(game,board,0,board.y_resol-260,710,board.y_resol-260));
		board.walls.add(new Wall(game,board,board.x_resol-710,board.y_resol-260,board.x_resol-710,board.y_resol-260));
		
		board.walls.add(new Wall(game,board,0,0,510,board.y_resol));
		board.walls.add(new Wall(game,board,0,0,board.x_resol,60));
		board.walls.add(rig2 = new Wall(game,board,board.x_resol-510,0,510,board.y_resol));
		board.walls.add(new Wall(game,board,0,board.y_resol-60,board.x_resol,60));
		
		board.walls.add(breakWall(rig,false,board.y_resol/2-100,board.y_resol+100));
		board.walls.add(breakWall(rig2,false,board.y_resol/2-100,board.y_resol+100));
		
	}
	
}
