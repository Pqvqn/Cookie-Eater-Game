package entities;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

import ce3.*;
import cookies.*;
import items.*;
import levels.*;

public class Summon extends Entity{
	
	private Entity user;
	private boolean anchor; //whether item is anchored to the summoner
	private double homex,homey; //x and y position of edge 
	private Segment body;
	private double spawn; //multiplied by radius to produce distance from user that summon spawns
	
	public Summon(Game frame, Board gameboard, Entity summoner, int cycletime, boolean anchored, int shields) {
		super(frame,gameboard,cycletime);
		user = summoner;
		radius = user.getRadius()/3;
		mass = 70;
		anchor = anchored;
		if(anchor)setRelativeFrame(user.getX(),user.getY());
		
		spawn = 3.5;
		homex = user.getX();
		homey = user.getY();
		setX(homex);
		setY(homey);
		setShields(shields);
		friction = .2;
		calibrateStats();
		double rat = .001/(Level.lineLength(0,0,user.getXVel(true),user.getYVel(true))); //set vels to user's, but miniscule
		x_velocity = user.getXVel(true)*rat;
		y_velocity = user.getYVel(true)*rat;
		special_frames = user.getSpecialFrames();
		currSpecial = user.getCurrentSpecial();
		buildBody();
		orientParts();

	}
	public void runUpdate() {
		if(ded)return;
		super.runUpdate();
		orientParts();

	}
	
	public void doMovement() {
		if(getX() == homex && getY() == homey) { //if stacked
			double userAngle = Math.atan2(user.getYVel(true),user.getXVel(true));
			setX(user.getX()+Math.cos(userAngle)*user.getRadius()*spawn); //set x a little bit out from user
			setY(user.getY()+Math.sin(userAngle)*user.getRadius()*spawn);
		}
		if(anchor) { //if anchored to the user, move with user
			double relxv = getXVel(true);double relyv = getYVel(true);
			setRelativeFrame(user.getX(),user.getY());
			setRelativeVel(user.getXVel(),user.getYVel());
			setXVel(relxv,true);setYVel(relyv,true);
			homex = user.getX();
			homey = user.getY();

			x += getXVel();
			y += getYVel();
		}else {
			super.doMovement();
		}
	}
	
	public double getAim() {return getAngle();}
	public boolean getAnchored() {return anchor;}
	
	//take all items from user
	public void eatItems() {
		ArrayList<CookieItem> items = new ArrayList<CookieItem>();
		ArrayList<CookieItem> usersitems = user.getPowerups();
		for(int i=0; i<usersitems.size(); i++) {
			items.add(usersitems.get(i));
		}
		for(int i=0; i<items.size(); i++) {
			Item it = items.get(i).getItem();
			if(!(it instanceof ItemSummonMelee) && !(it instanceof ItemSummonProjectile)) {
				addItem(user.getCurrentSpecial(),items.get(i));
				user.removeItem(user.getCurrentSpecial(),items.get(i));
				it.setUser(this);
			}
		}
	}
	//give items back to user
	public void regurgitateItems() {
		ArrayList<CookieItem> items = new ArrayList<CookieItem>();
		for(CookieItem i : getPowerups())items.add(i);
		for(int i=0; i<items.size(); i++) {
			Item it = items.get(i).getItem();
			it.end(true);
			user.addItem(user.getCurrentSpecial(),items.get(i));
			removeItem(user.getCurrentSpecial(),items.get(i));
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
	
	//overrides normal collision; knocks user back if anchored
	public void collideAt(Object b, double xp, double yp, double oxv, double oyv, double om) {
		if(anchor) {
			user.setXVel(user.getXVel()+x_velocity);
			user.setYVel(user.getYVel()+y_velocity);
			user.collideAt(b,xp,yp,oxv,oyv,om);
			x = homex;
			y = homey;
			if(special) {
				ArrayList<CookieItem> powerups = getPowerups();
				for(int i=0; i<powerups.size(); i++) {
					powerups.get(i).getItem().bounce(b,xp,yp);
				}
			}
		}else {
			if(b!=null&&bumped.contains(b)&&b instanceof Area)return; //if already hit, don't hit again
			super.collideAt(b,xp,yp,oxv,oyv,om);
		}
	}
	
	
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
			parts.add(body = new SegmentRectangle(board,this,homex,homey,getThickness(),getLength(),getAngle(),"body"));
		}else {
			parts.add(body = new SegmentCircle(board,this,homex,homey,getThickness()/2,getAngle(),"body"));
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
