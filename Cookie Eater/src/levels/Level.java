package levels;

import ce3.*;

public abstract class Level{
	
	public double scale;
	private Level next;
	private Board board;
	public double startx;
	public double starty;
	
	public Level(Board frame, Level nextFloor) {
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
		
	}
	public void placeCookies() {
		placeCookies(100,100);
	}
	public void placeCookies(int clearance, int separation) {
		int cooks = 0;
		for(int pY = board.BORDER_THICKNESS+clearance+10; pY<board.Y_RESOL-board.BORDER_THICKNESS-clearance; pY+=separation) {
			for(int pX = board.BORDER_THICKNESS+clearance+10; pX<board.X_RESOL-board.BORDER_THICKNESS-clearance; pX+=separation) {
				boolean place = true;
				for(Wall w : board.walls) {
					if(collidesCircleAndRect(pX,pY,(int)(Cookie.DEFAULT_RADIUS*scale+clearance+.5),w.getX(),w.getY(),w.getW(),w.getH())) 
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
	public Level getNext() {
		return next;
	}
	public void setNext(Level newNext) {
		next = newNext;
	}
	public static boolean collidesCircleAndRect(int cX, int cY, int cR, int rX, int rY, int rW, int rH) {
		return (Math.abs(cX - rX) <= cR && cY>=rY && cY<=rY+rH) ||
				(Math.abs(cX - (rX+rW)) <= cR && cY>=rY && cY<=rY+rH)||
				(Math.abs(cY - rY) <= cR && cX>=rX && cX<=rX+rW) ||
				(Math.abs(cY - (rY+rH)) <= cR && cX>=rX && cX<=rX+rW) ||
				(Math.sqrt((cX-rX)*(cX-rX) + (cY-rY)*(cY-rY))<=cR) ||
				(Math.sqrt((cX-(rX+rW))*(cX-(rX+rW)) + (cY-rY)*(cY-rY))<=cR) ||
				(Math.sqrt((cX-rX)*(cX-rX) + (cY-(rY+rH))*(cY-(rY+rH)))<=cR) ||
				(Math.sqrt((cX-(rX+rW))*(cX-(rX+rW)) + (cY-(rY+rH))*(cY-(rY+rH)))<=cR) ||
				(cX >= rX && cX <= rX+rW && cY >= rY && cY <= rY+rH);
	}
}
