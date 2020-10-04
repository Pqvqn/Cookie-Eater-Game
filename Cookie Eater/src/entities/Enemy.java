package entities;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

import ce3.*;
import cookies.*;
import levels.*;

public abstract class Enemy extends Entity{

	protected Eater player;
	protected double fric, termVel, normVel, accel; //movement stats, adjusted for scale/cycle
	protected double friction, terminalVelocity, normalVelocity, acceleration; //unadjusted, constant stats
	protected boolean steals;
	protected ArrayList<String> imgs;
	protected double targetx, targety;
	protected int shieldBounces; //number of bounces before this shield expires
	
	public Enemy(Board frame, int cycletime, double xp, double yp) {
		super(frame,cycletime);
		calibration_ratio = cycletime;
		board = frame;
		ded=false;
		x = xp;
		y = yp;
		radius = 30;
		player = board.player;
		x_velocity=0;
		y_velocity=0;
		mass = 100;
		imgs = new ArrayList<String>();
		shieldBounces = 0;
		buildBody();
		orientParts();
		createStash();
		
		fric = Math.pow(friction, calibration_ratio);
		termVel = terminalVelocity*board.currFloor.getScale()*calibration_ratio;
		normVel = normalVelocity*board.currFloor.getScale()*calibration_ratio;
		accel = acceleration*board.currFloor.getScale()*calibration_ratio*calibration_ratio;
	}
	//transfer array into arraylist
	protected void setImgs(String[] imgList) {
		for(int i=0; i<imgList.length; i++)imgs.add(imgList[i]);
	}
	//runs each cycle
	public void runUpdate() {
		super.runUpdate();
		if(ded)return;
		setCalibration(board.getAdjustedCycle());
	
		x+=x_velocity;
		y+=y_velocity;
		x_velocity*=fric;
		y_velocity*=fric;
		if(Math.random()>.999 && !powerups.get(0).isEmpty()) {
			special(0); 
		}
		/*if(Math.abs(x_velocity)>fric) {
			x_velocity-=Math.signum(x_velocity)*fric;
		}else {
			x_velocity=0;
		}
		if(Math.abs(y_velocity)>fric) {
			y_velocity-=Math.signum(y_velocity)*fric;
		}else {
			y_velocity=0;
		}*/
		if(Math.abs(x_velocity)>termVel) {
			x_velocity=Math.signum(x_velocity)*termVel;
		}
		if(Math.abs(y_velocity)>termVel) {
			y_velocity=Math.signum(y_velocity)*termVel;
		}
		orientParts();
	}
	public void setCalibration(double calrat) {
		if(!board.check_calibration || calrat==calibration_ratio || board.getAdjustedCycle()/(double)board.getCycle()>2 || board.getAdjustedCycle()/(double)board.getCycle()<.5)return;
		fric = Math.pow(friction, calrat);
		termVel = terminalVelocity*board.currFloor.getScale()*calrat;
		normVel = normalVelocity*board.currFloor.getScale()*calrat;
		accel = acceleration*board.currFloor.getScale()*calrat*calrat;
		minRecoil = 10*board.currFloor.getScale()*(calrat/15.0);
		maxRecoil = 50*board.currFloor.getScale()*(calrat/15.0);
		shield_length = (int)(.5+60*calrat);
		special_length = (int)(.5+60*(1/(calibration_ratio/15)));
		special_cooldown = (int)(.5+180*(1/(calibration_ratio/15)));
		shield_length = (int)(.5+60*(1/(calibration_ratio/15)));
		calibration_ratio = calrat;
		
		//calibrate summons
		for(int i=0; i<summons.size(); i++) {
			summons.get(i).setCalibration(calrat/15.0);
		}
	}
	/*//when hit wall
	public void bounce(Wall w,int rx,int ry,int rw,int rh) {
		shielded=true;
		shield_length = (int)(.5+60*board.getAdjustedCycle());
		if(shield_frames==0) {
			if(shield_stash.size()<=0) {
				kill();
			}else {
				removeShields(1);
			}
			shield_frames++;
		}
	}*/
	//accelerates towards target coordinate
	public void accelerateToTarget(double tarX, double tarY) {
		targetx = tarX;
		targety = tarY;
		if(lock)return;
		double rat = accel / Level.lineLength(x, y, tarX, tarY);
		if(Level.lineLength(x, y, tarX, tarY)==0) rat = 0;
		if(Math.abs(x_velocity)<normVel)x_velocity+=rat*(tarX-x);
		if(Math.abs(y_velocity)<normVel)y_velocity+=rat*(tarY-y);
	}
	//deletes this enemy
	public void kill() {
		ArrayList<Cookie> stash = getStash();
		while(!stash.isEmpty()) {
			double ang = Math.random()*Math.PI*2;
			double r=80*board.currFloor.getScale();
			if(stash.get(0) instanceof CookieStore)r*=2;
			double addx = r*Math.cos(ang), addy = r*Math.sin(ang);
			boolean hit = false;
			/*for(int i=0; i<board.walls.size(); i++) {
				Wall w = board.walls.get(i);
				if(Level.collidesCircleAndRect((int)(.5+x+addx),(int)(.5+y+addy),stash.get(0).getRadius(),w.getX(),w.getY(),w.getW(),w.getH())) {
					hit = true;
				}
			}*/
			double rr = stash.get(0).getRadius();
			Ellipse2D.Double c = new Ellipse2D.Double((int)(.5+x+addx-rr),(int)(.5+y+addy-rr),(int)(.5+rr*2),(int)(.5+rr*2));
			Area b = new Area(c);
			b.intersect(board.wallSpace);
			hit = !b.isEmpty();
			if((int)(.5+x+addx)<0||(int)(.5+x+addx)>board.X_RESOL||(int)(.5+y+addy)<0||(int)(.5+y+addy)>board.Y_RESOL)hit=true;
			if(!hit) {
				Cookie remove = stash.remove(0);
				remove.setPos((int)(.5+x+addx),(int)(.5+y+addy));
				board.cookies.add(remove);
			}
		}
		board.enemies.remove(this);
		ded=true;
		wipeStash();
	}
	
	public void triggerShield() {
		//increment bounces while still shielded
		if(shielded) {
			shieldBounces++;
		}else {
			shieldBounces = 0;
		}
		super.triggerShield();
		//kill if bounced too many times on one shield
		if(shieldBounces >= getShields()*3) {
			kill();
		}
	}
	
	//puts cookies in stash on spawn
	public void createStash() {
		
	}
	//draws
	public void paint(Graphics g) {
		for(int i=0; i<parts.size(); i++) {
			parts.get(i).paint(g);
		}
		if(currSpecial!=-1) {
			Color meh = special_colors.get(currSpecial);
			g.setColor(new Color(meh.getRed(),meh.getGreen(),meh.getBlue(),100));
			g.fillOval((int)(.5+x-1.5*radius), (int)(.5+y-1.5*radius), (int)(.5+3*radius), (int)(.5+3*radius));
		}
		Graphics2D g2 = (Graphics2D)g;
		AffineTransform origt = g2.getTransform();
		for(int i=0; i<summons.size(); i++) { //draw summons
			summons.get(i).paint(g2);
			g2.setTransform(origt);
		}
	}
	
	//overriding to not delete spoiled cookies and to not install pickups
	public void giveCookie(Cookie c) {
		if(c instanceof CookieItem) {
			addItem(getCurrentSpecial(), ((CookieItem)c).getItem());
			item_stash.add((CookieItem)c);
		}else if(c instanceof CookieShield) {
			shield_stash.add(((CookieShield)c));
		}else if(c instanceof CookieStat) {
			stat_stash.add(((CookieStat)c));
		}else{
			cash_stash.add(c);
		}
		if(c.getValue()>0 || !c.getDecayed())
			activateSpecials();
	}
	
	public double totalVel() {
		return Math.sqrt(x_velocity*x_velocity+y_velocity*y_velocity);
	}
	public double getAim() {
		return Math.atan2((targety-y),(targetx-x));
	}
}
