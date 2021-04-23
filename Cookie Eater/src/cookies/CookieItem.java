package cookies;

import java.awt.*;
import java.io.IOException;

import items.*;
import sprites.*;
import ce3.*;
import entities.*;
//import levels.*;

public class CookieItem extends CookieStore{
	
	private Item myItem;
	private SpriteStoreCookie sprite;
	
	public CookieItem(Game frame, Board gameboard, int startx, int starty, Item i, double cost) {
		super(frame,gameboard,startx,starty);
		myItem = i;
		price = cost;
		if(myItem!=null) {
			name = myItem.getName();
			try {
				sprite = new SpriteStoreCookie(board,this,"cookie"+myItem.getName());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			desc = myItem.getDesc();
		}
	}
	public CookieItem(Game frame, Board gameboard, SaveData sd) {
		super(frame,gameboard,sd);
		SaveData itemsd = sd.getSaveDataList("item").get(0);
		myItem = Item.generateItem(game,board,itemsd.getString("name",0));
		if(myItem!=null) {
			myItem.loadFromData(itemsd);
			try {
				sprite = new SpriteStoreCookie(board,this,"cookie"+myItem.getName());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public SaveData getSaveData() {
		SaveData data = super.getSaveData();
		data.addData("type","item");
		data.addData("item",myItem.getSaveData());
		return data;
	}
	public void setPrice(double p) {price = p;}
	public boolean purchase(Entity buyer) {
		if(buyer.getCash()>=price) {
			
			//player.addItem(player.getCurrentSpecial(), myItem);
			//player.giveCookie(this);
			if(vendor==null) {
				buyer.spend(price);
			}else {
				buyer.payCookies(vendor,price);
			}
			return true;}
		return false;
	}
	public void paint(Graphics g) {
		if(sprite!=null) {
			sprite.paint(g);
		}else {
			g.fillOval((int)(.5+x-radius*board.currFloor.getScale()), (int)(.5+y-radius*board.currFloor.getScale()), (int)(.5+radius*board.currFloor.getScale()*2), (int)(.5+radius*board.currFloor.getScale()*2));
	}}
	public Item getItem() {
		return myItem;
	}
}
