package ce3;

public class Floor1_Entrance extends Level{

	private double scale;
	private Level next;
	private Board board;
	
	public Floor1_Entrance(Board frame, Level nextFloor) {
		super(frame, nextFloor);
		next = nextFloor;
		scale = 1;
		board = frame;
	}
	
	public void build() {
		board.walls.add(new Wall(board,0,0,board.X_RESOL,board.BORDER_THICKNESS));
		board.walls.add(new Wall(board,0,0,board.BORDER_THICKNESS,board.Y_RESOL));
		board.walls.add(new Wall(board,0,board.Y_RESOL-board.BORDER_THICKNESS,board.X_RESOL,board.BORDER_THICKNESS));
		board.walls.add(new Wall(board,board.X_RESOL-board.BORDER_THICKNESS,0,board.BORDER_THICKNESS,board.Y_RESOL));
	}
	
	public void placeCookies() {
		int cooks = 0;
		for(int pY = board.BORDER_THICKNESS+500; pY<board.Y_RESOL-board.BORDER_THICKNESS-50; pY+=1000) {
			for(int pX = board.BORDER_THICKNESS+50; pX<board.X_RESOL-board.BORDER_THICKNESS-50; pX+=50) {
				board.cookies.add(new Cookie(board,pX,pY));
				cooks++;
			}
		}
		board.scoreToWin = cooks;;
	}

}
