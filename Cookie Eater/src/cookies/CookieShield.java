package cookies;

import java.awt.*;
import java.io.IOException;

import ce3.*;
//import levels.*;
import sprites.*;
import entities.*;

public class CookieShield extends CookieStore{
	
	private SpriteStoreCookie sprite;
	
	public CookieShield(Game frame, Board gameboard, int startx, int starty, double cost) {
		super(frame,gameboard,startx,starty);
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
	public CookieShield(Game frame, Board gameboard, SaveData sd) {
		super(frame,gameboard,sd);
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
	public void paint(Graphics g) {
		//g.setColor(new Color(20,170,180,100));
		//g.fillOval((int)(.5+x-radius*board.currFloor.getScale()), (int)(.5+y-radius*board.currFloor.getScale()), (int)(.5+radius*board.currFloor.getScale()*2), (int)(.5+radius*board.currFloor.getScale()*2));
		sprite.paint(g);
	}
}
