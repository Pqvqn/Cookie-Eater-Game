package cookies;

import ce3.*;
import levels.*;
import ui.*;

public abstract class StoreCookie extends Cookie{
	
	protected Eater player;
	protected UIPurchaseInfo info;
	protected String name;
	protected double price;
	protected int region;
	
	public StoreCookie(Board frame, int startx, int starty) {
		super(frame,startx,starty);
		decayTime = Integer.MAX_VALUE;
		player = board.player;
		region = 200;
		price = 0;
		name = "";
		info = new UIPurchaseInfo(board,x,y-50);
		board.draw.addUI(info);
	}
	public void kill() {
		board.draw.removeUI(info);
		board.cookies.remove(board.cookies.indexOf(this));
	}
	public void runUpdate() {
		super.runUpdate();
		info.update(Level.lineLength(x,y,player.getX(),player.getY())<=region,price,price<=board.cash,name);
	}
}
