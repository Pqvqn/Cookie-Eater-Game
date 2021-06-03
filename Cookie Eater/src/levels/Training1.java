package levels;

import java.util.*;

import ce3.*;
import cookies.*;

public class Training1 extends Training{

	public Training1(Game frame, Board gameboard) {
		super(frame,gameboard);
		name = "Forest Entrance";
		nameAbbrev = "for";
	}
	public Training1(Game frame, Board gameboard, ArrayList<Level> nextFloor, SaveData sd) {
		super(frame, gameboard, nextFloor, sd);
	}
	
	public void placeCookies(){
		super.placeCookies();
		board.player().setScoreToWin(27);
		//stats
		board.cookies.add(new CookieStat(game,board,800,500,0,0,0));
		
		board.cookies.add(new CookieStat(game,board,200,200,1,1,2));
		board.cookies.add(new CookieStat(game,board,200,400,1,1,1));
		board.cookies.add(new CookieStat(game,board,200,600,1,-1,1));
		board.cookies.add(new CookieStat(game,board,200,800,1,-1,2));
		
		board.cookies.add(new CookieStat(game,board,400,200,2,1,2));
		board.cookies.add(new CookieStat(game,board,400,400,2,1,1));
		board.cookies.add(new CookieStat(game,board,400,600,2,-1,1));
		board.cookies.add(new CookieStat(game,board,400,800,2,-1,2));
		
		board.cookies.add(new CookieStat(game,board,600,200,3,1,2));
		board.cookies.add(new CookieStat(game,board,600,400,3,1,1));
		board.cookies.add(new CookieStat(game,board,600,600,3,-1,1));
		board.cookies.add(new CookieStat(game,board,600,800,3,-1,2));
		
		int xr = board.x_resol;
		
		board.cookies.add(new CookieStat(game,board,xr-800,500,0,0,0));
		
		board.cookies.add(new CookieStat(game,board,xr-200,200,1,1,2));
		board.cookies.add(new CookieStat(game,board,xr-200,400,1,1,1));
		board.cookies.add(new CookieStat(game,board,xr-200,600,1,-1,1));
		board.cookies.add(new CookieStat(game,board,xr-200,800,1,-1,2));
		
		board.cookies.add(new CookieStat(game,board,xr-400,200,2,1,2));
		board.cookies.add(new CookieStat(game,board,xr-400,400,2,1,1));
		board.cookies.add(new CookieStat(game,board,xr-400,600,2,-1,1));
		board.cookies.add(new CookieStat(game,board,xr-400,800,2,-1,2));
		
		board.cookies.add(new CookieStat(game,board,xr-600,200,3,1,2));
		board.cookies.add(new CookieStat(game,board,xr-600,400,3,1,1));
		board.cookies.add(new CookieStat(game,board,xr-600,600,3,-1,1));
		board.cookies.add(new CookieStat(game,board,xr-600,800,3,-1,2));
	}
	
}
