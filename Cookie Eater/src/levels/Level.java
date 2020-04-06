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
		
	}
	public void placeCookies() {
		
	}
	public Level getNext() {
		return next;
	}
	public void setNext(Level newNext) {
		next = newNext;
	}
}
