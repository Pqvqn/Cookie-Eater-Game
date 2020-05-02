package levels;


import java.awt.Color;

import ce3.*;
import enemies.*;

public class Floor1 extends Level{

	//public double scale;
	//private Level next;
	//private Board board;
	//public double startx;
	//public double starty;
	public Floor1(Board frame) {
		this(frame,null);
	}
	public Floor1(Board frame, Level nextFloor) {
		super(frame, nextFloor);
		next = nextFloor;
		scale = 1;
		board = frame;
		minDecay = 60;
		maxDecay = 1800;
		bgColor = new Color(40,70,40);
		wallColor = new Color(50,30,10);
	}
	
	public void build() {
		super.build();
		startx = board.X_RESOL/2; //start in middle of board
		starty = board.Y_RESOL/2;
	}
	
	public void placeCookies() {
		super.placeCookies(90,100);
		/*int cooks = 0;
		board.cookies.add(new Cookie(board,1400,400));
		cooks++;
		board.scoreToWin = cooks;*/
	}
	public void spawnEnemies() {
		board.enemies.add(new EnemyBlob(board,612,board.Y_RESOL/2));
	}

}
