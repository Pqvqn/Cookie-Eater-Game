package levels;


import ce3.*;

public class FloorEntrance extends Level{

	//public double scale;
	private Level next;
	private Board board;
	//public double startx;
	//public double starty;
	
	public FloorEntrance(Board frame, Level nextFloor) {
		super(frame, nextFloor);
		next = nextFloor;
		scale = 1;
		board = frame;
	}
	
	public void build() {
		startx = board.X_RESOL/2;
		starty = board.Y_RESOL/2;
		board.walls.add(new Wall(board,0,0,board.X_RESOL,board.BORDER_THICKNESS));
		board.walls.add(new Wall(board,0,0,board.BORDER_THICKNESS,board.Y_RESOL));
		board.walls.add(new Wall(board,0,board.Y_RESOL-board.BORDER_THICKNESS,board.X_RESOL,board.BORDER_THICKNESS));
		board.walls.add(new Wall(board,board.X_RESOL-board.BORDER_THICKNESS,0,board.BORDER_THICKNESS,board.Y_RESOL));
		
	}
	
	public void placeCookies() {
		int cooks = 0;
		/*for(int pY = board.BORDER_THICKNESS+70; pY<board.Y_RESOL-board.BORDER_THICKNESS-75; pY+=100) {
			for(int pX = board.BORDER_THICKNESS+70; pX<board.X_RESOL-board.BORDER_THICKNESS-75; pX+=100) {
				board.cookies.add(new Cookie(board,pX,pY));
				cooks++;
			}
		}*/
		board.cookies.add(new Cookie(board,1400,400));
		cooks++;
		board.scoreToWin = cooks;
	}

}
