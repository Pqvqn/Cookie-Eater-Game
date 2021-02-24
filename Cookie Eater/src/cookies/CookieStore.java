package cookies;


import ce3.*;
import entities.*;
import levels.*;
import ui.*;

public abstract class CookieStore extends Cookie{
	
	protected UIPurchaseInfo info;
	protected String name;
	protected double price;
	protected int region;
	protected String desc;
	protected Entity vendor;
	
	public CookieStore(Game frame, Board gameboard, int startx, int starty) {
		super(frame,gameboard,startx,starty,false);
		decayTime = Integer.MAX_VALUE;
		region = 200;
		price = 0;
		name = "";
		desc = "";
		info = new UIPurchaseInfo(game,this);
		if(board==null || board.cookies!=null && board.cookies.contains(this))
			game.draw.addUI(info);
	}
	public CookieStore(Game frame, Board gameboard, SaveData sd) {
		super(frame,gameboard,sd,false);
		decayTime = Integer.MAX_VALUE;
		name = sd.getString("name",0);
		desc = sd.getString("description",0);
		price = sd.getInteger("price",0);
		region = sd.getInteger("region",0);
		info = new UIPurchaseInfo(game,this);
		if(board==null || board.cookies!=null && board.cookies.contains(this))
			game.draw.addUI(info);
	}
	public SaveData getSaveData() {
		SaveData data = super.getSaveData();
		data.addData("name",name);
		data.addData("description",desc);
		data.addData("price",price);
		data.addData("region",region);
		return data;
	}
	
	//attempt to kill cookie - consumed if being eaten
	public void kill(Entity consumer) {
		game.draw.removeUI(info);
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
	public String getName() {return name;}
	//update cookie
	public void runUpdate() {
		super.runUpdate();
		if(board.cookies.contains(this)) {
			Eater player = board.player();
			if(!game.draw.getUIList().contains(info))game.draw.addUI(info);
			info.update(Level.lineLength(x,y,player.getX(),player.getY())<=region,
					Level.lineLength(x,y,player.getX(),player.getY())<=region*.75 && player.getXVel(true)==0 && player.getYVel(true)==0
					&& board.nearestCookie(player.getX(),player.getY()).equals(this),
					price,price<=board.player().getCash(),name,desc);
		}else {
			info.setVisible(false);
		}
		
	}
}
