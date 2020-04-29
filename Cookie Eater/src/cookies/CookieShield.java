package cookies;

import java.awt.*;
import java.io.IOException;

import ce3.*;
//import levels.*;
import sprites.SpriteStoreCookie;

public class CookieShield extends CookieStore{
	
	private SpriteStoreCookie sprite;
	
	public CookieShield(Board frame, int startx, int starty) {
		super(frame,startx,starty);
		price = board.currFloor.getShieldCost();
		name = "+1 Shield";
		try {
			sprite = new SpriteStoreCookie(board,this,"cookieNewShield");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		desc = "One extra shield.`Allows player to bounce off of walls.";
		
	}
	public boolean purchase() {
		if(board.player.getCash()>=price) {
			board.player.addShields(1);
			board.player.addCash(-price);
			return true;}
		return false;
	}
	public void paint(Graphics g) {
		//g.setColor(new Color(20,170,180,100));
		//g.fillOval((int)(.5+x-radius*board.currFloor.getScale()), (int)(.5+y-radius*board.currFloor.getScale()), (int)(.5+radius*board.currFloor.getScale()*2), (int)(.5+radius*board.currFloor.getScale()*2));
		sprite.paint(g);
	}
}
