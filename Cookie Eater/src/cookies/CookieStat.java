package cookies;

import java.awt.*;
import java.io.IOException;

import ce3.*;
import entities.*;
//import levels.*;
import sprites.*;

public class CookieStat extends CookieStore{

	private double[][] mr;
	private double accelChange;
	private double maxvelChange;
	private double fricChange;
	private final double increment = .05; //stat increment
	private SpriteStoreCookie sprite;
	
	public CookieStat(Board frame, int startx, int starty) {
		super(frame,startx,starty);
		mr = player.getMovementRand();
		accelChange = 0;
		maxvelChange = 0;
		fricChange = 0;
		int bonus = (int)(Math.random()+1.2);
		int direction = (int)(Math.signum(Math.random()-.5));
		switch((int)(Math.random()*4)) {
		case 0:
			name = "Keep";
			desc = "";
			break;
		case 1:
			accelChange = increment*direction*(mr[0][1]-mr[0][0])*bonus;
			name = "Accel";
			desc = "Makes acceleration";
			break;
		case 2:
			maxvelChange = increment*direction*(mr[1][1]-mr[1][0])*bonus;
			name = "MaxVel";
			desc = "Makes maximum velocity";
			break;
		case 3:
			fricChange = increment*direction*(mr[2][1]-mr[2][0])*bonus;
			name = "Friction";
			desc = "Makes friction";
			break;
		}
		price = 0;
		if(name!="Keep") {
			if(direction>0) {
				if(bonus>1) {
					name+="+";
					desc+=" a lot";
				}
				name+="+";
				desc+=" higher";
			}else {
				if(bonus>1) {
					name+="-";
					desc+=" a lot";
				}
				name+="-";
				desc+=" lower";
			}
		}else {
			desc = "Changes no movement stats";
		}
		setImage();
		
	}
	public boolean purchase(Entity buyer) {
		if(buyer.getCash()>=price) {
			if(buyer instanceof Eater) {
				((Eater)buyer).addToMovement(accelChange,maxvelChange,fricChange);
				//player.win();
				((Eater)buyer).addScore(1);
			}
			if(vendor==null) {
				buyer.spend(price);
			}else {
				buyer.payCookies(vendor,price);
			}
			return true;}
		return false;
	}
	public void setImage() {
		try {
		if(accelChange>increment*(mr[0][1]-mr[0][0])) {
			sprite = new SpriteStoreCookie(board,this,"cookieA++");
		}else if(accelChange>0) {
			sprite = new SpriteStoreCookie(board,this,"cookieA+");
		}else if(accelChange<-increment*(mr[0][1]-mr[0][0])) {
			sprite = new SpriteStoreCookie(board,this,"cookieA--");
		}else if(accelChange<0) {
			sprite = new SpriteStoreCookie(board,this,"cookieA-");
		}else if(maxvelChange>increment*(mr[1][1]-mr[1][0])) {
			sprite = new SpriteStoreCookie(board,this,"cookieV++");
		}else if(maxvelChange>0) {
			sprite = new SpriteStoreCookie(board,this,"cookieV+");
		}else if(maxvelChange<-increment*(mr[1][1]-mr[1][0])) {
			sprite = new SpriteStoreCookie(board,this,"cookieV--");
		}else if(maxvelChange<0) {
			sprite = new SpriteStoreCookie(board,this,"cookieV-");
		}else if(fricChange>increment*(mr[2][1]-mr[2][0])) {
			sprite = new SpriteStoreCookie(board,this,"cookieF++");
		}else if(fricChange>0) {
			sprite = new SpriteStoreCookie(board,this,"cookieF+");
		}else if(fricChange<-increment*(mr[2][1]-mr[2][0])) {
			sprite = new SpriteStoreCookie(board,this,"cookieF--");
		}else if(fricChange<0) {
			sprite = new SpriteStoreCookie(board,this,"cookieF-");
		}else {
			sprite = new SpriteStoreCookie(board,this,"cookieK");
		}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void paint(Graphics g) {
		/*Color c;
		if(accelChange>increment*(mr[0][1]-mr[0][0])) {
			c = new Color(180,180,255);
		}else if(accelChange>0) {
			c = new Color(60,60,255);
		}else if(accelChange<-increment*(mr[0][1]-mr[0][0])) {
			c = new Color(0,0,25);
		}else if(accelChange<0) {
			c = new Color(0,0,75);
		}else if(maxvelChange>increment*(mr[1][1]-mr[1][0])) {
			c = new Color(180,255,180);
		}else if(maxvelChange>0) {
			c = new Color(60,255,60);
		}else if(maxvelChange<-increment*(mr[1][1]-mr[1][0])) {
			c = new Color(0,25,0);
		}else if(maxvelChange<0) {
			c = new Color(0,75,0);
		}else if(fricChange>increment*(mr[2][1]-mr[2][0])) {
			c = new Color(255,180,180);
		}else if(fricChange>0) {
			c = new Color(255,60,60);
		}else if(fricChange<-increment*(mr[2][1]-mr[2][0])) {
			c = new Color(25,0,0);
		}else if(fricChange<0) {
			c = new Color(75,0,0);
		}else {
			c = new Color(120,120,120);
		}*/
		//g.setColor(c);
		//g.fillOval((int)(.5+x-radius*board.currFloor.getScale()), (int)(.5+y-radius*board.currFloor.getScale()), (int)(.5+radius*board.currFloor.getScale()*2), (int)(.5+radius*board.currFloor.getScale()*2));
		sprite.paint(g);
	}
}
