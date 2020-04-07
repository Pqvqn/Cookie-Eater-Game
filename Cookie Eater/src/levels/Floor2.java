package levels;


import ce3.*;

public class Floor2 extends Level{

	//public double scale;
	private Level next;
	private Board board;
	//public double startx;
	//public double starty;
	
	
	public Floor2(Board frame, Level nextFloor) {
		super(frame, nextFloor);
		next = nextFloor;
		scale = .95;
		board = frame;
		cookieClearance = 75;
	}
	
	public void build() {
		startx = board.player.getX();
		starty = board.player.getY();
		board.walls.add(new Wall(board,0,0,board.X_RESOL,board.BORDER_THICKNESS));
		board.walls.add(new Wall(board,0,0,board.BORDER_THICKNESS,board.Y_RESOL));
		board.walls.add(new Wall(board,0,board.Y_RESOL-board.BORDER_THICKNESS,board.X_RESOL,board.BORDER_THICKNESS));
		board.walls.add(new Wall(board,board.X_RESOL-board.BORDER_THICKNESS,0,board.BORDER_THICKNESS,board.Y_RESOL));
		genWalls(2);
		
	}
	public void genWalls(int num) {
		for(int i=0; i<num; i++) {
			int x=-1,y=-1,w=-1,h=-1;
			while(x<0 || collidesCircleAndRect((int)(startx+.5),(int)(starty+.5),board.player.getRadius()*3,(int)(x-.5*w+.5),(int)(y-.5*h+.5),w,h)) {
				x=(int)(Math.random()*board.X_RESOL);
				y=(int)(Math.random()*board.Y_RESOL);
				w=(int)(Math.random()*300)+200;
				h=(int)(Math.random()*300)+200;
			}
			board.walls.add(new Wall(board,(int)(x-.5*w+.5),(int)(y-.5*h+.5),w,h));
		}
	}
	public void placeCookies() {
		int cooks = 0;
		for(int pY = board.BORDER_THICKNESS+50; pY<board.Y_RESOL-board.BORDER_THICKNESS-75; pY+=90) {
			for(int pX = board.BORDER_THICKNESS+50; pX<board.X_RESOL-board.BORDER_THICKNESS-75; pX+=90) {
				boolean place = true;
				for(Wall w : board.walls) {
					if(collidesCircleAndRect(pX,pY,(int)(Cookie.DEFAULT_RADIUS*scale+cookieClearance+.5),w.getX(),w.getY(),w.getW(),w.getH())) 
						place = false;
					
				}
				if(place) {
					board.cookies.add(new Cookie(board,pX,pY));
					cooks++;
				}
			}
		}
		board.scoreToWin = cooks;
	}

}
