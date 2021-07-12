package levels;

import java.util.*;

import ce3.*;
import cookies.*;
import mechanisms.*;

public class Store1 extends Store{
	
	public Store1(Game frame, Board gameboard, String id) {
		super(frame,gameboard,id);
		name = "Forest Entrance";
		nameAbbrev = "for";
		scale = 1;
		int[][] ms = {{600,board.y_resol/2+180},{board.x_resol/2+200,board.y_resol/2},{board.x_resol/2-200,board.y_resol/2},
				{board.x_resol/2,board.y_resol/2+200},{board.x_resol/2,board.y_resol/2-200}};
		mechanicSpaces = ms;
		passerbySpaces = new int[0][2];
		vendorSpaces = new int[0][1][2];
		
	}
	public Store1(Game frame, Board gameboard, ArrayList<Level> prev, ArrayList<Level> next, SaveData sd) {
		super(frame, gameboard, prev, next, sd);
	}
	
	public void placeCookies(){
	
		/*//stats
		board.cookies.add(new CookieStat(game,board,board.x_resol/2+200,board.y_resol/2));
		board.cookies.add(new CookieStat(game,board,board.x_resol/2-200,board.y_resol/2));
		board.cookies.add(new CookieStat(game,board,board.x_resol/2,board.y_resol/2+200));
		board.cookies.add(new CookieStat(game,board,board.x_resol/2,board.y_resol/2-200));*/
		
		
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
		
		board.walls.add(breakWall(rig,false,board.y_resol/2-100,board.y_resol/2+100));
		board.walls.add(breakWall(rig2,false,board.y_resol/2-100,board.y_resol/2+100));
		
		for(int i=0; i<passageways.size(); i++) {
			addMechanism(passageways.get(i));
		}
		
		board.mechanisms.add(new WallDoor(game,board,board.x_resol-BORDER_THICKNESS,board.y_resol/2-100,BORDER_THICKNESS/2,200,2));
	}
	
}
