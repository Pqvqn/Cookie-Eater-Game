package ce3;

import java.awt.Color;
import java.util.ArrayList;

import cookies.*;
import enemies.Enemy;
import enemies.Segment;
import enemies.SegmentCircle;
import items.*;

public abstract class Entity {

	//parent class of moving, colliding objects on the stage
	
	protected Board board; //main board
	protected double scale; //zoom in/out of screen
	protected double x, y; //position
	protected double x_velocity, y_velocity; //speed
	protected double mass; //weight
	protected double radius; //represents size
	protected double countVels; //number of averaged velocities in cycle
	protected boolean lock; //can entity control its movement
	protected boolean shielded; //in stun after shield use
	protected boolean ghost; //if the entity is in ghost mode
	protected double extra_radius; //area outside of the model, interacts with everything other than walls
	protected ArrayList<Summon> summons; //constructed objects owned by entity
	protected ArrayList<Object> bumped; //all things bumped into during this cycle
	protected boolean special; //whether a special is active
	protected boolean check_calibration;
	protected double calibration_ratio; //framerate ratio
	protected ArrayList<ArrayList<Item>> powerups;
	protected int currSpecial;
	protected int special_length; //how long special lasts
	protected ArrayList<Double> special_frames; //counting how deep into special
	protected int special_cooldown; //frames between uses of special
	protected double special_use_speed; //"frames" passed per frame of special use
	protected ArrayList<Boolean> special_activated; //if special is triggerable
	protected ArrayList<Color> special_colors; //color associated with each special
	protected ArrayList<Cookie> stash; //cookies eaten
	protected double decayed_value; //value of spoiled cookies
	protected ArrayList<Segment> parts; //segments that make up this entity
	protected boolean ded; //am i deceased
	
	public Entity(Board frame) {
		board = frame;
		scale = 1;
		summons = new ArrayList<Summon>();
		bumped = new ArrayList<Object>();
		stash = new ArrayList<Cookie>();
		parts = new ArrayList<Segment>();
		special = false;
		check_calibration = true;
		special_length = (int)(.5+60*(1/calibration_ratio));
		special_frames = new ArrayList<Double>();
		special_cooldown = (int)(.5+180*(1/calibration_ratio));
		special_use_speed = 1;
		special_colors = new ArrayList<Color>();
		special_colors.add(new Color(0,255,255));special_colors.add(new Color(255,0,255));special_colors.add(new Color(255,255,0));
		special_activated = new ArrayList<Boolean>();
		powerups = new ArrayList<ArrayList<Item>>();
		for(int i=0; i<3; i++) {
			powerups.add(new ArrayList<Item>());
			special_frames.add(0.0);
			special_activated.add(false);
		}
		currSpecial = -1;
		decayed_value = 0;
	}
	
	public void runUpdate() {
		if(ded)return;
		countVels = 0;
		scale = board.currFloor.getScale();
		if(special) {
			for(int i=0; i<powerups.get(currSpecial).size(); i++) {
				powerups.get(currSpecial).get(i).execute();
			}
			special_frames.set(currSpecial,special_frames.get(currSpecial)+special_use_speed); //increase special timer
			if(special_frames.get(currSpecial)>special_length) {
				special = false;
				for(int i=0; i<powerups.get(currSpecial).size(); i++) {
					powerups.get(currSpecial).get(i).end(false);
				}
				currSpecial = -1;
			}
		}
		for(int i=0; i<special_frames.size(); i++) {
			if(special_frames.get(i)>special_length) {
				special_frames.set(i,special_frames.get(i)+1);
				if(special_frames.get(i)>special_length+special_cooldown) {
					special_frames.set(i,0.0);
				}
			}
		}
		for(int i=0; i<stash.size(); i++) {
			stash.get(i).runUpdate();
		}
		testCollisions();
	}
	//tests all collisions
	public void testCollisions() {
		for(int j=0; j<parts.size(); j++) {
			if(ded)return;
			
			for(int i=0; i<board.cookies.size(); i++) { //for every cookie, test if any parts impact
				Cookie c = board.cookies.get(i);
				if(parts.get(j).collidesWithCircle(true,c.getX(),c.getY(),c.getRadius())) {
					if(!c.getClass().getSuperclass().equals(CookieStore.class) || ((CookieStore)c).purchase()) {
						giveCookie(c);
						c.kill(this);
					}
				}
			}
			
			ArrayList<Entity> entities = new ArrayList<Entity>();
			for(Entity e : board.players)entities.add(e);
			for(Entity e : board.enemies)entities.add(e);
			for(int i=0; i<entities.size(); i++) { //for every entity and its summons, test if any parts impact
				Entity e = entities.get(i);
				if(!e.equals(this)) {
					for(int k=0; k<e.getSummons().size(); k++) { //for every summon, test if any parts impact
						Summon s = e.getSummons().get(k);
						if(!ghost && parts.get(j).collidesWithSummon(true,s) && !s.isDed()){
							if(!e.getGhosted()||e.getShielded()) {
								double[] point = parts.get(j).summonHitPoint(true,s);
								collideAt(s,point[0],point[1],s.getXVel(),s.getYVel(),s.getMass());
								s.collisionEntity(this,point[0],point[1],mass,x_velocity,y_velocity,e.getGhosted(),e.getShielded());
								while(parts.get(j).collidesWithSummon(true,s) && !s.isDed()) {
									double rat = 1/Math.sqrt(Math.pow(x-point[0],2)+Math.pow(y-point[1],2));
									x+=(x-point[0])*rat;
									y+=(y-point[1])*rat;
									orientParts();
								}
							}	
						}
					}
					if(!e.getGhosted() && !ghost) {
						for(int k=0; k<e.getParts().size(); k++) {
							Segment s = e.getParts().get(k);
							if(s.getClass()==SegmentCircle.class) {
								SegmentCircle s2 = (SegmentCircle)s;
								if(parts.get(j).collidesWithCircle(true,s2.getCenterX(),s2.getCenterY(),s2.getTotalRadius())) {
									double bmass = mass;
									double bxv = x_velocity;
									double byv = y_velocity;
									double[] point = parts.get(j).circHitPoint(true,s2.getCenterX(),s2.getCenterY(),s2.getTotalRadius());
									collideAt(e,point[0],point[1],e.getXVel(),e.getYVel(),e.getMass());
									e.collideAt(this,point[0],point[1],bxv,byv,bmass);
									while(parts.get(j).collidesWithCircle(true,s2.getCenterX(),s2.getCenterY(),s2.getTotalRadius())) {
										double rat = 1/Math.sqrt(Math.pow(x-point[0],2)+Math.pow(y-point[1],2));
										x+=(x-point[0])*rat;
										y+=(y-point[1])*rat;
										orientParts();
										rat = 1/Math.sqrt(Math.pow(e.x-point[0],2)+Math.pow(e.y-point[1],2));
										e.x+=(e.x-point[0])*rat;
										e.y+=(e.y-point[1])*rat;
										e.orientParts();
									}
								}
							}
						}
					}
				}
			}
			
			
			for(int i=0; i<board.walls.size(); i++) { //for every wall, test if any parts impact
				Wall w = board.walls.get(i);
				if(!ghost && parts.get(j).collidesWithRect(false,w.getX(),w.getY(),w.getW(),w.getH())){
					double[] point = parts.get(j).rectHitPoint(false,w.getX(),w.getY(),w.getW(),w.getH());
					collideAt(w,point[0],point[1],0,0,999999999);
					bounce(w,w.getX(),w.getY(),w.getW(),w.getH());
					while(parts.get(j).collidesWithRect(false,w.getX(),w.getY(),w.getW(),w.getH())) {
						double rat = 1/Math.sqrt(Math.pow(x-point[0],2)+Math.pow(y-point[1],2));
						x+=(x-point[0])*rat;
						y+=(y-point[1])*rat;
						orientParts();
					}
				}
			}
			
		}
	}
	//hits wall
	public void bounce(Wall w,int rx,int ry,int rw,int rh) {
		
	}
	//collides with anything other than cookies
	public boolean collidesWithAnything() {
		for(int j=0; j<parts.size(); j++) {
			for(int i=0; i<board.players.size(); i++) { //for every player, test if any parts impact
				Eater player = board.players.get(i);
				if((!player.getGhosted()||player.getShielded())&&parts.get(j).collidesWithCircle(true,player.getX(),player.getY(),player.getTotalRadius())) { //test if hits player
					return true;
				}
			}
			for(int i=0; i<board.enemies.size(); i++) { //for every enemy, test if any parts impact
				Enemy e = board.enemies.get(i);
				if(!e.equals(this)) {
					for(int k=0; k<e.getParts().size(); k++) {
						Segment s = e.getParts().get(k);
						if(s.getClass()==SegmentCircle.class) {
							SegmentCircle s2 = (SegmentCircle)s;
							if(parts.get(j).collidesWithCircle(true,s2.getCenterX(),s2.getCenterY(),s2.getTotalRadius())) {
								return true;
							}
						}
					}
				}
			}
			for(int i=0; i<board.player.getSummons().size(); i++) { //for every summon, test if any parts impact
				Summon s = board.player.getSummons().get(i);
				if(parts.get(j).collidesWithSummon(true,s) && !s.isDed()){
					return true;
				}
			}
			for(int i=0; i<board.walls.size(); i++) { //for every wall, test if any parts impact
				Wall w = board.walls.get(i);
				if(parts.get(j).collidesWithRect(false,w.getX(),w.getY(),w.getW(),w.getH())){
					return true;
				}
			}
		}
		return false;
	}
	//create parts for the enemy
	protected void buildBody() {
		
	}
	//sets positions of all segments relative to location
	public void orientParts() {
			
	}
	public ArrayList<Segment> getParts(){return parts;}
	
	public double getX() {return x;}
	public double getY() {return y;}
	public void setX(double xp) {x=xp;}
	public void setY(double yp) {y=yp;}
	
	public double getXVel() {return x_velocity;}
	public double getYVel() {return y_velocity;}
	public void setXVel(double a) {x_velocity = a;}
	public void setYVel(double a) {y_velocity = a;}
	
	public double getMass() {return mass;}
	
	//direction accelerating towards
	public double getAim() {return 0.0;}
	
	public void lockControl(boolean l) {lock = l;}
	
	//takes velocity changes from items and averages them
	public void averageVels(double xVel, double yVel) {
		if(countVels==0) {
			setXVel(0);
			setYVel(0);
		}
		countVels++;
		setXVel((getXVel()*(countVels-1)+xVel)/countVels);
		setYVel((getYVel()*(countVels-1)+yVel)/countVels);
	}
	
	public void setShielded(boolean s) {shielded = s;}
	public boolean getShielded() {return shielded;}
	public void setGhost(boolean g) {ghost = g;}
	public boolean getGhosted() {return ghost;}
	
	public void setExtraRadius(double er) {extra_radius=er/scale;}
	public double getExtraRadius() {return extra_radius*scale;}
	public void setRadius(double r) {radius=r/scale;}
	public double getRadius() {return radius*scale;}
	public double getTotalRadius() {return (radius+extra_radius)*scale;}
	
	public void addSummon(Summon s) {summons.add(s);}
	public void removeSummon(Summon s) {summons.remove(s);}
	public ArrayList<Summon> getSummons() {return summons;}
	
	public void addBump(Object b) {bumped.add(b);}
	
	//bounces accoridng to collision with moving mass at point 
	public void collideAt(Object b, double xp, double yp, double oxv, double oyv, double om) {
		if(b!=null&&bumped.contains(b)&&b.getClass()!=Wall.class)return; //if already hit, don't hit again
		bumped.add(b);
		double actual_mass = mass;
		for(Summon s: summons)actual_mass+=s.getMass();
		double pvx = (xp-x), pvy = (yp-y);
		double oxm = oxv*om, oym = oyv*om;
		double txm = x_velocity*actual_mass, tym = y_velocity*actual_mass;
		double oProj = Math.abs((oxm*pvx+oym*pvy)/(pvx*pvx+pvy*pvy));
		double tProj = Math.abs((txm*pvx+tym*pvy)/(pvx*pvx+pvy*pvy));
		double projdx = (oProj+tProj)*pvx,projdy = (oProj+tProj)*pvy;
	
		double proejjjg = (x_velocity*pvy+y_velocity*-pvx)/(pvx*pvx+pvy*pvy);
		
		x_velocity=pvy*proejjjg-projdx/actual_mass;
		y_velocity=-pvx*proejjjg-projdy/actual_mass;
			
		if(special) {
			for(int i=0; i<powerups.get(currSpecial).size(); i++) {
				powerups.get(currSpecial).get(i).bounce(xp,yp);
			}
		}
	}
	public void setCalibration(double calrat) { //recalibrate everything that used cycle to better match current fps
	
	}
	public boolean getCalibCheck() {return check_calibration;}
	public void setCalibCheck(boolean cc) {check_calibration = cc;}
	
	//activates special A (all powerups tied to A)
	public void special(int index) {
		if(board.currFloor.specialsEnabled()) {
			if(special || special_frames.get(index)!=0 || board.player.getDir()==Eater.NONE || !special_activated.get(index))return;
			special=true;
			special_activated.set(index, false);
			currSpecial = index;
			for(int i=0; i<powerups.get(index).size(); i++) {
				powerups.get(index).get(i).prepare();
			}
			for(int i=0; i<powerups.get(index).size(); i++) {
				powerups.get(index).get(i).initialize();
			}
		}else {
			currSpecial = index;

		}
		}
	public boolean getSpecialActivated(int s) {return special_activated.get(s);}
	public boolean getSpecialActivated() {return special;}
	public void activateSpecials() {
		for(int i=0; i<special_activated.size(); i++) {
			if(special_frames.get(i)==0)
				special_activated.set(i, true);
		}
	}
	public void addItem(int index, Item i) {
		if(index<0)index=0;
		boolean add = true;
		for(int j=0; j<powerups.get(index).size(); j++) { //see if item already in list
			Item test = powerups.get(index).get(j);
			if(i.getName().equals(test.getName())) { //if duplicate, amplify instead of adding
				add = false;
				test.amplify();
			}
		}
		if(add) {
			powerups.get(index).add(i);
			i.setUser(this);
		}
	}
	public ArrayList<ArrayList<Item>> getItems() {return powerups;}
	public void extendSpecial(double time) {
		for(int i=0; i<special_frames.size(); i++) {
			if(special_frames.get(i)<special_length && special_frames.get(i)!=0) {
				if(special_frames.get(i)>time) {
					special_frames.set(i,special_frames.get(i)-time);
				}else {
					special_frames.set(i, 1.0);
				}
			}
		}
	}
	public int getSpecialLength() {return special_length;}
	public int getSpecialCooldown() {return special_cooldown;}
	public ArrayList<Double> getSpecialFrames() {return special_frames;}
	public ArrayList<Color> getSpecialColors() {return special_colors;}
	public int getCurrentSpecial() {return currSpecial;}
	public double getSpecialUseSpeed() {return special_use_speed;}
	public void setSpecialUseSpeed(double sus) {special_use_speed = sus;}
	
	public void giveCookie(Cookie c) {
		stash.add(c);
		if(c.getClass().equals(CookieItem.class)) {
			addItem(getCurrentSpecial(), ((CookieItem)c).getItem());
		}
		if((decayed_value>0 && c.getDecayed()) || !c.getDecayed())
			activateSpecials();
	}
	public ArrayList<Cookie> getStash() {return stash;}
	public double getDecayedValue() {return decayed_value;}
	public void setDecayedValue(double dv) {decayed_value=dv;}
	
}
