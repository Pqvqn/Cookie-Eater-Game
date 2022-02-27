package cookies;

import java.io.IOException;

import items.*;

import sprites.*;
import ce3.*;
import entities.*;
import levels.*;

public class CookieItem extends CookieStore{
	
	private Item myItem;
	
	public CookieItem(Game frame, Board gameboard, Level lvl, int startx, int starty, Item i, double cost) {
		super(frame,gameboard,lvl,startx,starty);
		myItem = i;
		price = cost;
		if(myItem!=null) {
			name = myItem.getName();
			try {
				sprite = new SpriteCookie(board,this,1+Item.itemIndex(myItem.getName()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			desc = myItem.getDesc();
		}
	}
	public CookieItem(Game frame, Board gameboard, Level lvl, SaveData sd) {
		super(frame,gameboard,lvl,sd);
		SaveData itemsd = sd.getSaveDataList("item").get(0);
		myItem = Item.generateItem(game,board,itemsd.getString("name",0));
		if(myItem!=null) {
			myItem.loadFromData(itemsd);
			try {
				sprite = new SpriteCookie(board,this,1+Item.itemIndex(myItem.getName()));
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
			
			if(vendor==null) {
				buyer.spend(price);
			}else {
				buyer.payCookies(vendor,price);
			}
			return true;}
		return false;
	}
	
	public Item getItem() {
		return myItem;
	}
}
