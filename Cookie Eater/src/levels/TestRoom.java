package levels;

import ce3.*;
import cookies.Cookie;

public class TestRoom extends Level{

	//public double scale;
	//private Level next;
	//private Board board;
	//public double startx;
	//public double starty;
	
	public TestRoom(Board frame, Level nextFloor) {
		super(frame, nextFloor);
		next = nextFloor;
		scale = 1;
		board = frame;
	}
	
	public void build() {
		startx = board.player.getX();
		starty = board.player.getY();
		board.walls.add(new Wall(board,0,0,board.X_RESOL,board.BORDER_THICKNESS));
		board.walls.add(new Wall(board,0,0,board.BORDER_THICKNESS,board.Y_RESOL));
		board.walls.add(new Wall(board,0,board.Y_RESOL-board.BORDER_THICKNESS,board.X_RESOL,board.BORDER_THICKNESS));
		board.walls.add(new Wall(board,board.X_RESOL-board.BORDER_THICKNESS,0,board.BORDER_THICKNESS,board.Y_RESOL));

		board.walls.add(new Wall(board,800,500,400,100));
		board.walls.add(new Wall(board,200,300,300,500));
	}
	
	public void placeCookies() {
		int wid = (board.BORDER_THICKNESS+Cookie.DEFAULT_RADIUS);
		for(int i=0; i<20; i++) {
			board.cookies.add(new Cookie(board,
					(int)(Math.random()*(board.X_RESOL-2*wid))+wid,
					(int)(Math.random()*(board.Y_RESOL-2*wid))+wid));
		}
		board.scoreToWin = 20;
	}
}
