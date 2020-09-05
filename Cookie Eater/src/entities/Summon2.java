package entities;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

import ce3.*;
import cookies.*;
import items.*;
import levels.*;

public class Summon2 extends Entity{
	
	private Entity user;
	private boolean anchored; //whether item is anchored to the summoner
	private double homex,homey; //x and y position of edge 
	private SegmentRectangle body;
	
	public Summon2(Board frame, Entity summoner, int cycletime) {
		super(frame,cycletime);
		user = summoner;
		radius = user.getRadius()/3;
		anchored = true;
		x = user.getX();
		y = user.getY();
		double rat = .001/(Level.lineLength(0,0,user.getXVel(),user.getYVel())); //set vels to user's, but miniscule
		x_velocity = user.getXVel()*rat;
		y_velocity = user.getYVel()*rat;
		special_frames = user.getSpecialFrames();
		buildBody();
		orientParts();
	}
	public void runUpdate() {
		if(ded)return;
		super.runUpdate();
		if(anchored) { //if anchored to the user, move with user
			//setXVel(user.getXVel());
			//setYVel(user.getYVel());
			homex = user.getX();
			homey = user.getY();
			orientParts();
		}
		x+=x_velocity+user.getXVel();
		y+=y_velocity+user.getYVel();
	}
	
	public double getAim() {return user.getAim();}
	
	//take all items from user
	public void eatItems() {
		ArrayList<Item> items = new ArrayList<Item>();
		for(Item i : user.getItems().get(user.getCurrentSpecial()))items.add(i);
		for(int i=0; i<items.size(); i++) {
			Item it = items.get(i);
			if(!(it instanceof ItemSummon)) {
				addItem(user.getCurrentSpecial(),it);
				user.getItems().get(user.getCurrentSpecial()).remove(it);
				it.setUser(this);
			}
		}
	}
	//give items back to user
	public void regurgitateItems() {
		ArrayList<Item> items = new ArrayList<Item>();
		for(Item i : getItems().get(user.getCurrentSpecial()))items.add(i);
		for(int i=0; i<items.size(); i++) {
			Item it = items.get(i);
			user.addItem(user.getCurrentSpecial(),it);
			getItems().get(user.getCurrentSpecial()).remove(it);
			it.setUser(user);
		}
	}
	//give all cookies to user
	public void regurgitateCookies() {
		ArrayList<Cookie> stash = getStash();
		for(int i=0; i<stash.size(); i++) {
			user.giveCookie(stash.get(i));
		}
	}
	
	/*//prepares all items
	public void prepareItems() {
		for(int i=0; i<powerups.get(currSpecial).size(); i++) {
			powerups.get(currSpecial).get(i).prepare();
		}
	}*/
	
	public void setCalibration(double calrat) { //recalibrate everything that used cycle to better match current fps
		if(!board.check_calibration || calrat==calibration_ratio || board.getAdjustedCycle()/(double)board.getCycle()>2 || board.getAdjustedCycle()/(double)board.getCycle()<.5)return;
		
		calibration_ratio = calrat;
		
		shield_length = (int)(.5+60*(1/calibration_ratio));
		special_length = (int)(.5+60*(1/calibration_ratio));
		special_cooldown = (int)(.5+180*(1/calibration_ratio));
	}
	
	//code anchor points and whatnot
	//also all collision stuff
	public double getX() {return x;}
	public double getY() {return y;}
	public void setX(double xp) {x=xp;orientParts();}
	public void setY(double yp) {y=yp;orientParts();}
	
	public double getXVel() {return x_velocity;}
	public double getYVel() {return y_velocity;}
	public void setXVel(double a) {x_velocity = a;}
	public void setYVel(double a) {y_velocity = a;}
	
	public double getThickness() {
		return getTotalRadius()*2;
	}
	public double getLength() {
		return Level.lineLength(homex,homey,x,y);
	}
	public double getAngle() {
		return Math.atan2(y-homey,x-homex);
	}
	
	public Entity getUser() {return user;}
	
	protected void buildBody() {
		parts.add(body = new SegmentRectangle(board,this,x,y,getThickness(),getLength(),getAngle()));
	}
	public void orientParts() {
		body.setLocation((homex+x)/2,(homey+y)/2);
		body.setAngle(getAngle()+Math.PI/2);
		body.setDims(getThickness(),getLength());
		super.orientParts();
	}
	
	public void paint(Graphics2D g2) {
		g2.setColor(Color.WHITE);
		
		if(user.getGhosted())g2.setColor(new Color(255,255,255,100));
		if(user.getShielded())g2.setColor(new Color(50,200,210));
		body.paint(g2);
		AffineTransform at = g2.getTransform();
		g2.rotate(body.getAngle()-Math.PI/2,homex,homey);
		g2.fillRect((int)(.5+homex),(int)(.5+homey-getThickness()/2),(int)(.5+getLength()),(int)(.5+getThickness()));

		g2.setTransform(at);
	}
}
