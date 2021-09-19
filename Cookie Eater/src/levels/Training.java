package levels;

import java.util.*;

import ce3.*;
import mechanisms.*;

public abstract class Training extends Level{
	
	public Training(Game frame, Board gameboard, Floor floor, String id) {
		super(frame,gameboard,floor,id);
		minDecay = Integer.MAX_VALUE;
		maxDecay = Integer.MAX_VALUE;
	}
	public Training(Game frame, Board gameboard, Floor floor, SaveData sd) {
		super(frame, gameboard, floor, sd);
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
