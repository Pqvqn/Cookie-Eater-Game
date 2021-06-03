package levels;

import java.util.*;

import ce3.*;
import mechanisms.*;

public abstract class Training extends Level{
	
	public Training(Game frame, Board gameboard) {
		super(frame,gameboard);
		startx = board.x_resol/2;
		starty = board.y_resol/2;
		minDecay = Integer.MAX_VALUE;
		maxDecay = Integer.MAX_VALUE;
	}
	public Training(Game frame, Board gameboard, ArrayList<Level> nextFloor, SaveData sd) {
		super(frame, gameboard, nextFloor, sd);
	}
	
	public boolean haltEnabled() {return true;}
	public boolean specialsEnabled() {return true;}
	public boolean installPickups() {return true;}
	public boolean takeDamage() {return false;}
	
	//places needed cookies for level on the board
	public void placeCookies(){
		board.player().setScoreToWin(2);
	}
	
	
	public void build() {
		board.walls.add(new Wall(game,board,0,0,board.x_resol,BORDER_THICKNESS)); //add border walls
		board.walls.add(new Wall(game,board,0,0,BORDER_THICKNESS,board.y_resol));
		board.walls.add(new Wall(game,board,0,board.y_resol-BORDER_THICKNESS,board.x_resol,BORDER_THICKNESS));
		board.walls.add(new Wall(game,board,board.x_resol-BORDER_THICKNESS,0,BORDER_THICKNESS,board.y_resol));
	}
	
	public void spawnEnemies() {}
	
}
