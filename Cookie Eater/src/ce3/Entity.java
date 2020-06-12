package ce3;

import java.util.ArrayList;

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
	
	public Entity(Board frame) {
		board = frame;
		scale = 1;
		summons = new ArrayList<Summon>();
		bumped = new ArrayList<Object>();
		special = false;
		check_calibration = true;
	}
	
	public void runUpdate() {
		countVels = 0;
		scale = board.currFloor.getScale();
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
	
}
