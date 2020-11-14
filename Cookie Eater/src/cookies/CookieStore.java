package cookies;


import ce3.*;
import entities.*;
import levels.*;
import ui.*;

public abstract class CookieStore extends Cookie{
	
	protected Eater player;
	protected UIPurchaseInfo info;
	protected String name;
	protected double price;
	protected int region;
	protected String desc;
	protected Entity vendor;
	
	public CookieStore(Board frame, int startx, int starty) {
		super(frame,startx,starty);
		decayTime = Integer.MAX_VALUE;
		player = board.player;
		region = 200;
		price = 0;
		name = "";
		desc = "";
		info = new UIPurchaseInfo(board,this);
		if(board.cookies!=null && board.cookies.contains(this))
			board.draw.addUI(info);
	}
	//attempt to kill cookie - consumed if being eaten
	public void kill(Entity consumer) {
		board.draw.removeUI(info);
		board.cookies.remove(board.cookies.indexOf(this));
	}
	//purchase cookie, return if purchase is successful or not
	public boolean purchase(Entity buyer) {
		return true;
	}
	public double price() {return price;}
	public void setPrice(double p) {price = p;}
	public void setDecayTime() {
		decayCounter = 0;
		decayed=false;
	}
	//entity selling this
	public void setVendor(Entity v) {
		vendor = v;
	}
	public Entity getVendor() {
		return vendor;
	}
	//update cookie
	public void runUpdate() {
		super.runUpdate();
		if(board.cookies.contains(this)) {
			if(!board.draw.getUIList().contains(info))board.draw.addUI(info);
			info.update(Level.lineLength(x,y,player.getX(),player.getY())<=region,
					Level.lineLength(x,y,player.getX(),player.getY())<=region*.75 && player.getXVel(true)==0 && player.getYVel(true)==0
					&& board.nearestCookie(player.getX(),player.getY()).equals(this),
					price,price<=board.player.getCash(),name,desc);
		}else {
			info.setVisible(false);
		}
		
	}
}
