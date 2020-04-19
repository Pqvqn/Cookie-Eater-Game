package levels;

import ce3.*;
import cookies.*;

public class Store1 extends Floor1{
	
	
	public Store1(Board frame) {
		super(frame);
		startx = board.X_RESOL/2;
		starty = board.Y_RESOL/2;
		minDecay = Integer.MAX_VALUE;
		maxDecay = Integer.MAX_VALUE;
		shieldCost = 30;
	}
	
	
	public boolean haltEnabled() {return true;}
	public boolean specialsEnabled() {return false;}
	
	
	public void placeCookies(){
	
		//stats
		board.cookies.add(new CookieStat(board,board.X_RESOL/2+200,board.Y_RESOL/2));
		board.cookies.add(new CookieStat(board,board.X_RESOL/2-200,board.Y_RESOL/2));
		board.cookies.add(new CookieStat(board,board.X_RESOL/2,board.Y_RESOL/2+200));
		board.cookies.add(new CookieStat(board,board.X_RESOL/2,board.Y_RESOL/2-200));
		
		
		board.player.setScoreToWin(2);
	}
	public void build() {
		board.walls.add(new Wall(board,0,0,board.X_RESOL,board.BORDER_THICKNESS)); //add border walls
		board.walls.add(new Wall(board,0,0,board.BORDER_THICKNESS,board.Y_RESOL));
		board.walls.add(new Wall(board,0,board.Y_RESOL-board.BORDER_THICKNESS,board.X_RESOL,board.BORDER_THICKNESS));
		board.walls.add(new Wall(board,board.X_RESOL-board.BORDER_THICKNESS,0,board.BORDER_THICKNESS,board.Y_RESOL));
		
		board.walls.add(new Wall(board,0,0,710,260));
		board.walls.add(new Wall(board,board.X_RESOL-710,0,board.X_RESOL-710,260));
		board.walls.add(new Wall(board,0,board.Y_RESOL-260,710,board.Y_RESOL-260));
		board.walls.add(new Wall(board,board.X_RESOL-710,board.Y_RESOL-260,board.X_RESOL-710,board.Y_RESOL-260));
		
		board.walls.add(new Wall(board,0,0,510,board.Y_RESOL));
		board.walls.add(new Wall(board,0,0,board.X_RESOL,60));
		board.walls.add(new Wall(board,board.X_RESOL-510,0,510,board.Y_RESOL));
		board.walls.add(new Wall(board,0,board.Y_RESOL-60,board.X_RESOL,60));
	}
	
}
