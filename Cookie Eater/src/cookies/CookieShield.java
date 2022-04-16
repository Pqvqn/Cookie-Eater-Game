package cookies;

import java.awt.*;
import java.io.IOException;

import ce3.*;
import levels.*;
import sprites.*;
import entities.*;

public class CookieShield extends CookieStore{
	
	private SpriteStoreCookie sprite;
	
	public CookieShield(Game frame, Board gameboard, Level lvl, int startx, int starty, double cost) {
		super(frame,gameboard,lvl,startx,starty);
		price = cost;
		name = "+1 Shield";
		try {
			sprite = new SpriteStoreCookie(board,this,"cookieNewShield");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		desc = "One extra shield.`Allows player to bounce off of walls.";
		
	}
	public CookieShield(Game frame, Board gameboard, Level lvl, SaveData sd) {
		super(frame,gameboard,lvl,sd);
		try {
			sprite = new SpriteStoreCookie(board,this,"cookieNewShield");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public SaveData getSaveData() {
		SaveData data = super.getSaveData();
		data.addData("type","shield");
		return data;
	}
	public boolean purchase(Entity buyer) {
		if(buyer.getCash()>=price) {
			//board.player.addShields(1);
			if(vendor==null) {
				buyer.spend(price);
			}else {
				buyer.payCookies(vendor,price);
			}
			return true;}
		return false;
	}
	public Sprite getSprite() {return sprite;}
	public void paint(Graphics g) {
		sprite.paint(g);
	}
}
