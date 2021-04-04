package cookies;

import java.awt.*;
import java.io.IOException;

import ce3.*;
import entities.*;
//import levels.*;
import sprites.*;

public class CookieStat extends CookieStore{

	public static final double[][] MR = {{.2,1},{5,15},{.05,.3}};
	private double accelChange;
	private double maxvelChange;
	private double fricChange;
	private final double increment = .05; //stat increment
	private SpriteStoreCookie sprite;
	
	public CookieStat(Game frame, Board gameboard, int startx, int starty) {
		super(frame,gameboard,startx,starty);
		int bonus = (int)(Math.random()+1.2);
		int direction = (int)(Math.signum(Math.random()-.5));
		int type = (int)(Math.random()*4);
		setStatChanges(type, direction, bonus);
	}
	public CookieStat(Game frame, Board gameboard, int startx, int starty, int type, int direction, int bonus) {
		super(frame,gameboard,startx,starty);
		setStatChanges(type, direction, bonus);
	}
	public CookieStat(Game frame, Board gameboard, SaveData sd) {
		super(frame,gameboard,sd);
		accelChange = sd.getDouble("stats",0);
		maxvelChange = sd.getDouble("stats",1);
		fricChange = sd.getDouble("stats",2);
		setImage();
	}
	public SaveData getSaveData() {
		SaveData data = super.getSaveData();
		data.addData("type","stat");
		data.addData("stats",accelChange,0);
		data.addData("stats",maxvelChange,1);
		data.addData("stats",fricChange,2);
		return data;
	}
	public void setStatChanges(int type, int direction, int bonus) {
		accelChange = 0;
		maxvelChange = 0;
		fricChange = 0;
		switch(type) {
		case 0:
			name = "Keep";
			desc = "";
			break;
		case 1:
			accelChange = increment*direction*(MR[0][1]-MR[0][0])*bonus;
			name = "Accel";
			desc = "Makes acceleration";
			break;
		case 2:
			maxvelChange = increment*direction*(MR[1][1]-MR[1][0])*bonus;
			name = "MaxVel";
			desc = "Makes maximum velocity";
			break;
		case 3:
			fricChange = increment*direction*(MR[2][1]-MR[2][0])*bonus;
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
		if(accelChange>increment*(MR[0][1]-MR[0][0])) {
			sprite = new SpriteStoreCookie(board,this,"cookieA++");
		}else if(accelChange>0) {
			sprite = new SpriteStoreCookie(board,this,"cookieA+");
		}else if(accelChange<-increment*(MR[0][1]-MR[0][0])) {
			sprite = new SpriteStoreCookie(board,this,"cookieA--");
		}else if(accelChange<0) {
			sprite = new SpriteStoreCookie(board,this,"cookieA-");
		}else if(maxvelChange>increment*(MR[1][1]-MR[1][0])) {
			sprite = new SpriteStoreCookie(board,this,"cookieV++");
		}else if(maxvelChange>0) {
			sprite = new SpriteStoreCookie(board,this,"cookieV+");
		}else if(maxvelChange<-increment*(MR[1][1]-MR[1][0])) {
			sprite = new SpriteStoreCookie(board,this,"cookieV--");
		}else if(maxvelChange<0) {
			sprite = new SpriteStoreCookie(board,this,"cookieV-");
		}else if(fricChange>increment*(MR[2][1]-MR[2][0])) {
			sprite = new SpriteStoreCookie(board,this,"cookieF++");
		}else if(fricChange>0) {
			sprite = new SpriteStoreCookie(board,this,"cookieF+");
		}else if(fricChange<-increment*(MR[2][1]-MR[2][0])) {
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
		sprite.paint(g);
	}
}
