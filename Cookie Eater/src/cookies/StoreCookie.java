package cookies;

import ce3.Board;

public abstract class StoreCookie extends Cookie{
	
	public StoreCookie(Board frame, int startx, int starty) {
		super(frame,startx,starty);
		decayTime = Integer.MAX_VALUE;
	}
	public void kill() {
		board.cookies.remove(board.cookies.indexOf(this));
	}
}
