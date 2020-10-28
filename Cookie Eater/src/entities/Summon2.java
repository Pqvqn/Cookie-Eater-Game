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
	private boolean anchor; //whether item is anchored to the summoner
	private double homex,homey; //x and y position of edge 
	private Segment body;
	private double spawn; //multiplied by radius to produce distance from user that summon spawns
	private double fric; //friction subtracted from velocity each cycle (for anchored only)
	
	public Summon2(Board frame, Entity summoner, int cycletime, boolean anchored, int shields) {
		super(frame,cycletime);
		user = summoner;
		radius = user.getRadius()/3;
		mass = 100;
		anchor = anchored;
		//x = user.getX();
		//y = user.getY();
		double userAngle = Math.atan2(user.getYVel(true),user.getXVel(true));
		spawn = 3.5;
		setX(user.getX()+Math.cos(userAngle)*user.getRadius()*spawn); //set x a little bit out from user
		setY(user.getY()+Math.sin(userAngle)*user.getRadius()*spawn);
		homex = user.getX();
		homey = user.getY();
		setShields(shields);
		double rat = .001/(Level.lineLength(0,0,user.getXVel(true),user.getYVel(true))); //set vels to user's, but miniscule
		x_velocity = user.getXVel(true)*rat;
		y_velocity = user.getYVel(true)*rat;
		special_frames = user.getSpecialFrames();
		currSpecial = user.getCurrentSpecial();
		fric = .1;
		buildBody();
		orientParts();
	}
	public void runUpdate() {
		if(ded)return;
		super.runUpdate();
		if(anchor) { //if anchored to the user, move with user
			//setXVel(user.getXVel());
			//setYVel(user.getYVel());
			setRelativeFrame(user.getX(),user.getY());
			setRelativeVel(user.getXVel(),user.getYVel());
			homex = user.getX();
			homey = user.getY();
			x += getXVel();
			y += getYVel();
			/*relx+=x_velocity;
			rely+=y_velocity;
			x = user.getX()+relx;
			y = user.getY()+rely;
			x_velocity = 0;
			y_velocity = 0;*/
		}else {
			x+=x_velocity;
			y+=y_velocity;
			if(x_velocity>fric) {
				x_velocity-=fric;
			}else if(x_velocity<-fric) {
				x_velocity+=fric;
			}else {
				x_velocity = 0;
			}
			if(y_velocity>fric) {
				y_velocity-=fric;
			}else if(y_velocity<-fric) {
				y_velocity+=fric;
			}else {
				y_velocity = 0;
			}
		}

		orientParts();
		//x+=x_velocity+user.getXVel();
		//y+=y_velocity+user.getYVel();

	}
	
	public double getAim() {return getAngle();}
	public boolean getAnchored() {return anchor;}
	
	//take all items from user
	public void eatItems() {
		ArrayList<Item> items = new ArrayList<Item>();
		ArrayList<Item> usersitems = user.getItems().get(user.getCurrentSpecial());
		for(int i=0; i<usersitems.size(); i++) {
			items.add(usersitems.get(i));
		}
		for(int i=0; i<items.size(); i++) {
			Item it = items.get(i);
			if(!(it instanceof ItemSummonMelee) && !(it instanceof ItemSummonProjectile)) {
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
			Cookie c = stash.get(i);
			//don't give stat and shield cookies
			if(!(c instanceof CookieShield) && !(c instanceof CookieStat)) {
				user.giveCookie(c);
			}
		}
	}
	
	//don't install items
	public void hitCookie(Cookie c) {
		if(ded)return;
		if(!(c instanceof CookieStore) || ((CookieStore)c).purchase(this)) {
			c.kill(this);
			if(c instanceof CookieItem && !board.currFloor.installPickups()) {
				user.pickupItem((CookieItem)c);
			}else {
				giveCookie(c);
			}
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
	
	//overrides normal collision; knocks user back if anchored
	public void collideAt(Object b, double xp, double yp, double oxv, double oyv, double om) {
		if(anchor) {
			user.setXVel(user.getXVel()+x_velocity);
			user.setYVel(user.getYVel()+y_velocity);
			user.collideAt(b,xp,yp,oxv,oyv,om);
			//halt = true;
			//x_velocity = 0;
			//y_velocity = 0;
			x = homex;
			y = homey;
			if(special) {
				for(int i=0; i<powerups.get(currSpecial).size(); i++) {
					powerups.get(currSpecial).get(i).bounce(b,xp,yp);
				}
			}
		}else {
			if(b!=null&&bumped.contains(b)&&b instanceof Area)return; //if already hit, don't hit again
			super.collideAt(b,xp,yp,oxv,oyv,om);
		}
	}
	
	
	/*//position based on if anchored to player
	public double getX() {
		if(anchor) {
			return relx;
		}else {
			return super.getX();
		}
	}
	public double getY() {
		if(anchor) {
			return rely;
		}else {
			return super.getY();
		}
	}
	public void setX(double xp) {
		if(anchor) {
			relx=xp;
			x=user.getX()+relx;
		}else {
			super.setX(xp);
		}
		if(body!=null)orientParts();
	}
	public void setY(double yp) {
		if(anchor) {
			rely=yp;
			y=user.getY()+rely;
		}else {
			super.setY(yp);
		}
		if(body!=null)orientParts();
	}*/
	
	/*public double getXVel() {return x_velocity;}
	public double getYVel() {return y_velocity;}
	public void setXVel(double a) {x_velocity = a;}
	public void setYVel(double a) {y_velocity = a;}*/
	public double getThickness() {
		return getTotalRadius()*2;
	}
	public double getLength() {
		if(anchor) {
			return Level.lineLength(homex,homey,x,y);
		}else {
			return getTotalRadius()*2;
		}
		
	}
	public double getAngle() {
		return Math.atan2(y-homey,x-homex);
	}
	
	public Entity getUser() {return user;}
	
	protected void buildBody() {
		//rectangle for anchored, circle for projectile
		if(anchor) {
			parts.add(body = new SegmentRectangle(board,this,homex,homey,getThickness(),getLength(),getAngle()));
		}else {
			parts.add(body = new SegmentCircle(board,this,homex,homey,getThickness()/2,getAngle()));
		}
	}
	public void orientParts() {
		if(ded)return;
		if(anchor) {
			body.setLocation((homex+x)/2,(homey+y)/2);
			body.setAngle(getAngle()+Math.PI/2);
		}else {
			body.setLocation(x,y);
		}
		if(body instanceof SegmentRectangle) {
			((SegmentRectangle)body).setDims(getThickness(),getLength());
		}else {
			body.setSize(radius);
		}
		super.orientParts();
	}
	
	//die
	public void kill() {
		super.kill();
		ded = true;
		parts = new ArrayList<Segment>();
		body = null;
	}
	
	public void paint(Graphics2D g2) {
		if(ded)return;
		g2.setColor(Color.WHITE);
		
		if(getGhosted())g2.setColor(new Color(255,255,255,100));
		if(getShielded())g2.setColor(new Color(50,200,210));
		body.update();
		
		//draw based on type of segment
		if(body instanceof SegmentRectangle) {
			AffineTransform at = g2.getTransform();
			g2.rotate(body.getAngle()-Math.PI/2,homex,homey);
			g2.fillRect((int)(.5+homex),(int)(.5+homey-getThickness()/2),(int)(.5+getLength()),(int)(.5+getThickness()));
			g2.setTransform(at);
		}else if(body instanceof SegmentCircle) {
			g2.fillOval((int)(.5+x-getTotalRadius()),(int)(.5+y-getTotalRadius()),(int)(.5+getTotalRadius()*2),(int)(.5+getTotalRadius()*2));
		}


	}
}
