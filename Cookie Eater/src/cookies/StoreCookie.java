package cookies;

import ce3.*;

public abstract class StoreCookie extends Cookie{
	
	protected Eater player;
	
	public StoreCookie(Board frame, int startx, int starty) {
		super(frame,startx,starty);
		decayTime = Integer.MAX_VALUE;
		player = board.player;
	}
	public void kill() {
		board.cookies.remove(board.cookies.indexOf(this));
	}
}
