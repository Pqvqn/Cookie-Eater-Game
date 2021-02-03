package levels;

import ce3.*;
import mechanisms.*;

public abstract class Training extends Level{
	
	public Training(Game frame, Board gameboard) {
		super(frame,gameboard);
		startx = board.X_RESOL/2;
		starty = board.Y_RESOL/2;
		minDecay = Integer.MAX_VALUE;
		maxDecay = Integer.MAX_VALUE;
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
		board.walls.add(new Wall(game,board,0,0,board.X_RESOL,BORDER_THICKNESS)); //add border walls
		board.walls.add(new Wall(game,board,0,0,BORDER_THICKNESS,board.Y_RESOL));
		board.walls.add(new Wall(game,board,0,board.Y_RESOL-BORDER_THICKNESS,board.X_RESOL,BORDER_THICKNESS));
		board.walls.add(new Wall(game,board,board.X_RESOL-BORDER_THICKNESS,0,BORDER_THICKNESS,board.Y_RESOL));
	}
	
	public void spawnEnemies() {}
	
}
