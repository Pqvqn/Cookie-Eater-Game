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
	
	public CookieItem(Board frame, int startx, int starty, Item i, double cost) {
		super(frame,startx,starty);
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
