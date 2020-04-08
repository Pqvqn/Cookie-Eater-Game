package levels;


import ce3.*;

public class FloorEntrance extends Level{

	//public double scale;
	//private Level next;
	//private Board board;
	//public double startx;
	//public double starty;
	
	public FloorEntrance(Board frame, Level nextFloor) {
		super(frame, nextFloor);
		next = nextFloor;
		scale = 1;
		board = frame;
		minDecay = 60;
		maxDecay = 1800;
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

}
