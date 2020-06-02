package enemies;

import java.awt.*;
import java.util.*;

import ce3.*;
import cookies.*;
import levels.*;
import items.*;

public abstract class Enemy {

	protected ArrayList<Segment> parts;
	protected double xPos,yPos;
	protected Board board;
	protected Eater player;
	protected double mass;
	protected double x_vel, y_vel;
	protected double fric, termVel, normVel, accel; //movement stats, adjusted for scale/cycle
	protected double friction, terminalVelocity, normalVelocity, acceleration; //unadjusted, constant stats
	protected int shields;
	protected int shield_duration;
	protected int shield_frames;
	protected boolean steals;
	protected boolean ded; //am i deceased
	protected ArrayList<Cookie> stash;
	protected ArrayList<String> imgs;
	protected ArrayList<Object> bumped; //things bumped into on this cycle
	
	public Enemy(Board frame, double x, double y) {
		board = frame;
		ded=false;
		xPos = x;
		yPos = y;
		player = board.player;
		parts = new ArrayList<Segment>();
		x_vel=0;
		y_vel=0;
		mass = 100;
		shield_duration = 60*board.getAdjustedCycle();
		shield_frames = 0;
		bumped = new ArrayList<Object>();
		stash = new ArrayList<Cookie>();
		imgs = new ArrayList<String>();
		buildBody();
		orientParts();
	}
	//transfer array into arraylist
	protected void setImgs(String[] imgList) {
		for(int i=0; i<imgList.length; i++)imgs.add(imgList[i]);
	}
	//create parts for the enemy
	protected void buildBody() {
		
	}
	//runs each cycle
	public void runUpdate() {
		if(ded)return;
		testCollisions();
		if(offStage())kill();
		fric = Math.pow(friction, 1/(double)board.getAdjustedCycle());
		termVel = terminalVelocity*board.currFloor.getScale()*board.getAdjustedCycle();
		normVel = normalVelocity*board.currFloor.getScale()*board.getAdjustedCycle();
		accel = acceleration*board.currFloor.getScale()/board.getAdjustedCycle();
	
		xPos+=x_vel;
		yPos+=y_vel;
		x_vel*=fric;
		y_vel*=fric;
		/*if(Math.abs(x_vel)>fric) {
			x_vel-=Math.signum(x_vel)*fric;
		}else {
			x_vel=0;
		}
		if(Math.abs(y_vel)>fric) {
			y_vel-=Math.signum(y_vel)*fric;
		}else {
			y_vel=0;
		}*/
		if(Math.abs(x_vel)>termVel) {
			x_vel=Math.signum(x_vel)*termVel;
		}
		if(Math.abs(y_vel)>termVel) {
			y_vel=Math.signum(y_vel)*termVel;
		}
		if(shield_frames>0)shield_frames++;
		if(shield_frames>=shield_duration)
			shield_frames=0;
		orientParts();
	}
	//given point, adjusts velocity for bouncing off from that point
	public void collideAt(Object b, double x, double y, double oxv, double oyv, double om) {
		if(bumped.contains(b)&&b.getClass()!=Wall.class)return; //don't collide if already hit this cycle
		bumped.add(b);
		double pvx = (x-xPos), pvy = (y-yPos);
		double oxm = oxv*om, oym = oyv*om;
		double txm = x_vel*mass, tym = y_vel*mass;
		double oProj = -(oxm*pvx+oym*pvy)/(pvx*pvx+pvy*pvy);
		double tProj = Math.abs((txm*pvx+tym*pvy)/(pvx*pvx+pvy*pvy));
		double projdx = (oProj+tProj)*pvx,projdy = (oProj+tProj)*pvy;
		
		double proejjjg = (x_vel*pvy+y_vel*-pvx)/(pvx*pvx+pvy*pvy);
		
		x_vel=pvy*proejjjg-projdx/mass;
		y_vel=-pvx*proejjjg-projdy/mass;
	}
	//when hit wall
	public void collideWall() {
		shield_duration = 60*board.getAdjustedCycle();
		if(shield_frames==0) {
			if(shields--<=0)kill();
			shield_frames++;
		}
	
	}
	//if offstage
	public boolean offStage() {
		return xPos<0||xPos>board.X_RESOL||yPos<0||yPos>board.Y_RESOL;
	}
	//tests all collisions
	public void testCollisions() {
		for(int j=0; j<parts.size(); j++) {
			if(ded)return;
			if((!player.getGhosted()||player.getShielded())&&parts.get(j).collidesWithCircle(player.getX(),player.getY(),player.getTotalRadius())) { //test if hits player
				double[] point = parts.get(j).circHitPoint(player.getX(),player.getY(),player.getTotalRadius());
				collideAt(player,point[0],point[1],player.getXVel(),player.getYVel(),player.getMass());
				player.collideAt(this,point[0],point[1],x_vel, y_vel, mass);
				while(parts.get(j).collidesWithCircle(player.getX(),player.getY(),player.getTotalRadius())) {
					double rat = 1/Math.sqrt(Math.pow(xPos-point[0],2)+Math.pow(yPos-point[1],2));
					xPos+=(xPos-point[0])*rat;
					yPos+=(yPos-point[1])*rat;
					orientParts();
				}
			}
			for(int i=0; i<board.cookies.size(); i++) { //for every cookie, test if any parts impact
				Cookie c = board.cookies.get(i);
				if(parts.get(j).collidesWithCircle(c.getX(),c.getY(),c.getRadius())) {
					stash.add(c);
					board.cookies.remove(c);
				}
			}
			for(int i=0; i<board.enemies.size(); i++) { //for every enemy, test if any parts impact
				Enemy e = board.enemies.get(i);
				if(!e.equals(this)) {
					for(int k=0; k<e.getParts().size(); k++) {
						Segment s = e.getParts().get(k);
						if(s.getClass()==SegmentCircle.class) {
							SegmentCircle s2 = (SegmentCircle)s;
							if(parts.get(j).collidesWithCircle(s2.getCenterX(),s2.getCenterY(),s2.getRadius())) {
								double bmass = mass;
								double bxv = x_vel;
								double byv = y_vel;
								double[] point = parts.get(j).circHitPoint(s2.getCenterX(),s2.getCenterY(),s2.getRadius());
								collideAt(e,point[0],point[1],e.getXVel(),e.getYVel(),e.getMass());
								e.collideAt(this,point[0],point[1],bxv,byv,bmass);
								while(parts.get(j).collidesWithCircle(s2.getCenterX(),s2.getCenterY(),s2.getRadius())) {
									double rat = 1/Math.sqrt(Math.pow(xPos-point[0],2)+Math.pow(yPos-point[1],2));
									xPos+=(xPos-point[0])*rat;
									yPos+=(yPos-point[1])*rat;
									orientParts();
									rat = 1/Math.sqrt(Math.pow(e.xPos-point[0],2)+Math.pow(e.yPos-point[1],2));
									e.xPos+=(e.xPos-point[0])*rat;
									e.yPos+=(e.yPos-point[1])*rat;
									e.orientParts();
								}
							}
						}
					}
				}
			}
			for(int i=0; i<board.player.getSummons().size(); i++) { //for every summon, test if any parts impact
				Summon s = board.player.getSummons().get(i);
				if(parts.get(j).collidesWithSummon(s) && !s.isDed()){
					if(!board.player.getGhosted()||board.player.getShielded()) {
						double[] point = parts.get(j).summonHitPoint(s);
						collideAt(s,point[0],point[1],s.getXVel(),s.getYVel(),s.getMass());
						s.collisionEntity(this,point[0],point[1],mass,x_vel,y_vel,board.player.getGhosted(),board.player.getShielded());
						while(parts.get(j).collidesWithSummon(s) && !s.isDed()) {
							double rat = 1/Math.sqrt(Math.pow(xPos-point[0],2)+Math.pow(yPos-point[1],2));
							xPos+=(xPos-point[0])*rat;
							yPos+=(yPos-point[1])*rat;
							orientParts();
						}
					}	
				}
			}
			for(int i=0; i<board.walls.size(); i++) { //for every wall, test if any parts impact
				Wall w = board.walls.get(i);
				if(parts.get(j).collidesWithRect(w.getX(),w.getY(),w.getW(),w.getH())){
					double[] point = parts.get(j).rectHitPoint(w.getX(),w.getY(),w.getW(),w.getH());
					collideAt(w,point[0],point[1],0,0,999999999);
					collideWall();
					while(parts.get(j).collidesWithRect(w.getX(),w.getY(),w.getW(),w.getH())) {
						double rat = 1/Math.sqrt(Math.pow(xPos-point[0],2)+Math.pow(yPos-point[1],2));
						xPos+=(xPos-point[0])*rat;
						yPos+=(yPos-point[1])*rat;
						orientParts();
					}
				}
			}
		}
	}
	//collides with anything other than cookies
	public boolean collidesWithAnything() {
		for(int j=0; j<parts.size(); j++) {
			if((!player.getGhosted()||player.getShielded())&&parts.get(j).collidesWithCircle(player.getX(),player.getY(),player.getTotalRadius())) { //test if hits player
				return true;
			}
			for(int i=0; i<board.enemies.size(); i++) { //for every enemy, test if any parts impact
				Enemy e = board.enemies.get(i);
				if(!e.equals(this)) {
					for(int k=0; k<e.getParts().size(); k++) {
						Segment s = e.getParts().get(k);
						if(s.getClass()==SegmentCircle.class) {
							SegmentCircle s2 = (SegmentCircle)s;
							if(parts.get(j).collidesWithCircle(s2.getCenterX(),s2.getCenterY(),s2.getRadius())) {
								return true;
							}
						}
					}
				}
			}
			for(int i=0; i<board.player.getSummons().size(); i++) { //for every summon, test if any parts impact
				Summon s = board.player.getSummons().get(i);
				if(parts.get(j).collidesWithSummon(s) && !s.isDed()){
					return true;
				}
			}
			for(int i=0; i<board.walls.size(); i++) { //for every wall, test if any parts impact
				Wall w = board.walls.get(i);
				if(parts.get(j).collidesWithRect(w.getX(),w.getY(),w.getW(),w.getH())){
					return true;
				}
			}
		}
		return false;
	}
	//resets at cycle end
	public void endCycle() {
		bumped = new ArrayList<Object>();
	}
	//accelerates towards target coordinate
	public void accelerateToTarget(double tarX, double tarY) {
		double rat = accel / Level.lineLength(xPos, yPos, tarX, tarY);
		if(Level.lineLength(xPos, yPos, tarX, tarY)==0) rat = 0;
		if(Math.abs(x_vel)<normVel)x_vel+=rat*(tarX-xPos);
		if(Math.abs(y_vel)<normVel)y_vel+=rat*(tarY-yPos);
	}
	//deletes this enemy
	public void kill() {
		while(!stash.isEmpty()) {
			double ang = Math.random()*Math.PI*2;
			double r=80*board.currFloor.getScale();
			double addx = r*Math.cos(ang), addy = r*Math.sin(ang);
			boolean hit = false;
			for(Wall w:board.walls) {
				if(Level.collidesCircleAndRect((int)(.5+xPos+addx),(int)(.5+yPos+addy),stash.get(0).getRadius(),w.getX(),w.getY(),w.getW(),w.getH())) {
					hit = true;
				}
			}
			if((int)(.5+xPos+addx)<0||(int)(.5+xPos+addx)>board.X_RESOL||(int)(.5+yPos+addy)<0||(int)(.5+yPos+addy)>board.Y_RESOL)hit=true;
			if(!hit) {
				stash.get(0).setPos((int)(.5+xPos+addx),(int)(.5+yPos+addy));
				Cookie remove = stash.remove(0);
				board.cookies.add(remove);
			}
		}
		board.enemies.remove(this);
		ded=true;
	}
	//sets positions of all segments relative to location
	public void orientParts() {
		
	}
	//is this in shield stun
	public boolean isShielded() {
		return shield_frames>0;
	}
	public double getX() {return xPos;}
	public double getY() {return yPos;}
	public double getXVel() {return x_vel;}
	public double getYVel() {return y_vel;}
	public double getMass() {return mass;}
	public ArrayList<Segment> getParts(){return parts;}
	//draws
	public void paint(Graphics g) {
		for(int i=0; i<parts.size(); i++) {
			parts.get(i).paint(g);
		}
	}
	public double totalVel() {
		return Math.sqrt(x_vel*x_vel+y_vel*y_vel);
	}
}
