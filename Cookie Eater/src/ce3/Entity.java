package ce3;

import java.awt.Color;
import java.util.ArrayList;

import cookies.Cookie;
import items.Item;
import items.Summon;

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
	protected ArrayList<Cookie> stash;
	
	public Entity(Board frame) {
		board = frame;
		scale = 1;
		summons = new ArrayList<Summon>();
		bumped = new ArrayList<Object>();
		stash = new ArrayList<Cookie>();
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
	}
	
	public void runUpdate() {
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
	}
	
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
			
		/*if(special) {
			for(int i=0; i<powerups.get(currSpecial).size(); i++) {
				powerups.get(currSpecial).get(i).bounce(xp,yp);
			}
		}*/
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
		if(add)powerups.get(index).add(i);
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
	
	public void giveCookie(Cookie c) {stash.add(c);}
	public ArrayList<Cookie> getStash() {return stash;}
}
