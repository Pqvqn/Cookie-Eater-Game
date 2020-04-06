package levels;

import ce3.*;

public abstract class Level{
	
	public double scale;
	private Level next;
	private Board board;
	public double startx;
	public double starty;
	public int cookieClearance;
	
	public Level(Board frame, Level nextFloor) {
		next = nextFloor;
		scale = 1;
		board = frame;
	}
	
	public void build() {
		
	}
	public void placeCookies() {
		
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
