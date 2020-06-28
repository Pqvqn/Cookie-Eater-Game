package entities;

import java.awt.*;
import java.util.*;

import ce3.*;
import levels.*;
import cookies.*;

public class Explorer extends Entity{

	protected Level residence; //which room this explorer is on
	protected String name;
	protected int state;
	protected static final int VENDOR = 0, VENTURE = 1;
	protected int[][] shop_spots;
	protected ArrayList<CookieStore> to_sell;
	protected ArrayList<CookieStore> on_display;
	protected int min_cat, max_cat;
	
	protected double[][] MR = {{.2,1},{5,15},{.05,.25}}; //accel min,max-min; maxvel min,max-min; fric min,max-min
	public static final int NONE=-1, UP=0, RIGHT=1, DOWN=2, LEFT=3;
	protected int direction;
	protected double acceleration; //added to dimensional speed depending on direction
	protected double max_velocity; //cap on accelerated-to dimensional speed
	protected double terminal_velocity; //maximum possible dimensional speed
	protected double friction; //removed from dimensional speed
	protected double accel; //scalable movement stats
	protected double maxvel;
	protected double termvel;
	protected double fric;
	protected double minRecoil; //how fast bounces off wall (min and max)
	protected double maxRecoil;
	protected Color coloration;

	
	public Explorer(Board frame) {
		super(frame);
		calibration_ratio = 60/15.0;
		to_sell = new ArrayList<CookieStore>();
		on_display = new ArrayList<CookieStore>();
		name = "Unknown";
		chooseResidence();
		state = VENDOR;
		
		acceleration = .5*calibration_ratio*calibration_ratio;
		max_velocity = 10*calibration_ratio;
		terminal_velocity = 50*calibration_ratio;
		friction = .1*calibration_ratio*calibration_ratio;
		averageStats();
		accel = acceleration*scale;
		maxvel = max_velocity*scale;
		termvel = terminal_velocity*scale;
		fric = friction*scale;
		minRecoil = 10*calibration_ratio;
		maxRecoil = 50*calibration_ratio;
		direction = NONE;
		coloration = Color.gray;
		buildBody();
	}
	
	public Level getResidence() {return residence;}
	public String getName() {return name;}
	//make changes after player ends a run
	public void runEnds() {
		
	}
	//updates every cycle
	public void runUpdate() {
		super.runUpdate();
		if(parts.isEmpty())buildBody();
		setState();
		if(state == VENDOR) { //if selling
			x_velocity = 0; //reset speeds
			y_velocity = 0;
			x = shop_spots[0][0];
			y = shop_spots[0][1];
		}else if (state == VENTURE) {
			chooseDir();
		}
		scale = board.currFloor.getScale();
		accel = acceleration*scale;
		maxvel = max_velocity*scale;
		termvel = terminal_velocity*scale;
		fric = friction*scale;
		if(!lock) {
			switch(direction) {
				case UP: //if up
					if(y_velocity>-maxvel) //if below speed cap
						y_velocity-=accel+fric; //increase speed upward
					break;
				case RIGHT:
					if(x_velocity<maxvel)
						x_velocity+=accel+fric;
					break;
				case DOWN:
					if(y_velocity<maxvel)
						y_velocity+=accel+fric;
					break;
				case LEFT:
					if(x_velocity>-maxvel)
						x_velocity-=accel+fric;
					break;
			}
		}
		if(Math.abs(x_velocity)>termvel)x_velocity = termvel * Math.signum(x_velocity); //make sure it's not too fast
		if(Math.abs(y_velocity)>termvel)y_velocity = termvel * Math.signum(y_velocity);
		x+=x_velocity; //move
		y+=y_velocity;
		if(Math.abs(x_velocity)<fric){ //if speed is less than what friction removes, set to 0
			x_velocity=0;
		}else if(x_velocity>0) { //if positive speed, subtract friction
			x_velocity-=fric;
		}else if(x_velocity<0) { //if negative speed, add friction
			x_velocity+=fric;
		}
		if(Math.abs(y_velocity)<fric){
			y_velocity=0;
		}else if(y_velocity>0) {
			y_velocity-=fric;
		}else if(y_velocity<0) {
			y_velocity+=fric;
		}
		int spec = doSpecial();
		if(spec!=-1) {
			special(spec);
		}
		orientParts();
	}
	//die on a floor
	public void kill() {
		//item drop code here :)
		System.out.println("oops i died but there no code");
	}
	//chooses which level to go to on game start
	public void chooseResidence() {
		
	}
	//given a level's name, finds the "num+1"th level of that type from the board's level progression - uses backup as index if this doesn't exist
	public Level findFloor(String type, boolean store, int num, int backup) {
		int count = 0;
		Level point = board.floors.getLast();
		while(point.getNext()!=null) {
			if(point.getName().equals(type) && store==point instanceof Store) {
				count++;
				if(count>num)return point;
			}
			point = point.getNext();
		}
		return board.floors.get(backup);
	}
	//creates a completely new stash of items
	public void createStash() {
		to_sell = new ArrayList<CookieStore>();
	}
	//adds item to a random point in to_sell list
	public void addRandomly(CookieStore c) {
		to_sell.add((int)(Math.random()*(to_sell.size()+1)),c);
	}
	//removes random item from to_sell list
	public void removeRandomly() {
		if(!to_sell.isEmpty())
			to_sell.remove((int)(Math.random()*(to_sell.size())));
	}
	//puts all items to sell out on display
	public void sellWares(int[][] positions) {
		shop_spots = positions;
		setX(shop_spots[0][0]); //put explorer in place
		setY(shop_spots[0][1]);
		for(int i=1; !to_sell.isEmpty() && i<shop_spots.length; i++) { //put all cookies in place
			CookieStore c = to_sell.remove(0);
			c.setPos(shop_spots[i][0],shop_spots[i][1]);
			board.cookies.add(c);
			on_display.add(c);
		}
	}
	//removes items from display and re-stashes them
	public void packUp() {
		for(int i=on_display.size()-1; i>=0; i--) {
			if(board.cookies.contains(on_display.get(i))){
				to_sell.add(on_display.remove(i));
			}else {
				on_display.remove(i);
			}
		}
	}
	
	public void setCalibration(double calrat) { //recalibrate everything that used cycle to better match current fps
		if(!check_calibration || calrat==calibration_ratio || board.getAdjustedCycle()/(double)board.getCycle()>2 || board.getAdjustedCycle()/(double)board.getCycle()<.5)return;
		acceleration/=calibration_ratio*calibration_ratio;
		max_velocity/=calibration_ratio;
		terminal_velocity/=calibration_ratio;
		friction/=calibration_ratio*calibration_ratio;
		minRecoil /= calibration_ratio;
		maxRecoil /= calibration_ratio;
		
		calibration_ratio = calrat;
		
		shield_length = (int)(.5+60*(1/calibration_ratio));
		special_length = (int)(.5+60*(1/calibration_ratio));
		special_cooldown = (int)(.5+180*(1/calibration_ratio));
		minRecoil *= calibration_ratio;
		maxRecoil *= calibration_ratio;
		acceleration*=calibration_ratio*calibration_ratio;
		max_velocity*=calibration_ratio;
		terminal_velocity*=calibration_ratio;
		friction*=calibration_ratio*calibration_ratio;
		coloration = new Color((int)((friction/calibration_ratio/calibration_ratio-MR[2][0])/MR[2][1]*255),(int)((max_velocity/calibration_ratio-MR[1][0])/MR[1][1]*255),(int)((acceleration/calibration_ratio/calibration_ratio-MR[0][0])/MR[0][1]*255));
	}
	//uses shield instead of killing
	public void bounceShield(Wall w,int rx,int ry,int rw,int rh) {
		shielded = true;
		double[] point = Level.circAndRectHitPoint(x,y,radius*scale,rx,ry,rw,rh);
		if(Math.sqrt(x_velocity*x_velocity+y_velocity*y_velocity)<minRecoil*scale){
			double rat = (minRecoil*scale)/Math.sqrt(x_velocity*x_velocity+y_velocity*y_velocity);
			x_velocity *= rat;
			y_velocity *= rat;
		}
		if(Math.sqrt(x_velocity*x_velocity+y_velocity*y_velocity)>maxRecoil*scale){
			double rat = (maxRecoil*scale)/Math.sqrt(x_velocity*x_velocity+y_velocity*y_velocity);
			x_velocity *= rat;
			y_velocity *= rat;
		}
		while(collidesWithRect(false,rx,ry,rw,rh)) {
			double rat = 1/Math.sqrt(Math.pow(x-point[0],2)+Math.pow(y-point[1],2));
			x+=(x-point[0])*rat; //move out of other
			y+=(y-point[1])*rat;
			orientParts();
		}
		if(special) {
			for(int i=0; i<powerups.get(currSpecial).size(); i++) {
				powerups.get(currSpecial).get(i).bounce(point[0],point[1]);
			}
		}
	}
	public void bounce(Wall w,int rx,int ry,int rw,int rh) {
		if(!shielded && shield_stash.size()<=0 && board.currFloor.takeDamage()) { //kill if no shields, otherwise bounce
			kill();
			return;
		}else if (!shielded && board.currFloor.takeDamage()){//only remove shields if not in stun and shield to be broken
			removeShields(1);
		}
		bounceShield(w,rx,ry,rw,rh);
		
	}
	//tests if hits rectangle
	public boolean collidesWithRect(boolean extra, int oX, int oY, int oW, int oH) {
		boolean hit = false;
		for(int i=0; i<parts.size(); i++) {
			if(parts.get(i).collidesWithRect(extra,oX,oY,oW,oH))hit=true;
		}
		return hit;
							
	}
	public int getDir() {return direction;}
	public double getAim() {
		switch(direction) {
		case UP:
			return 3*Math.PI/2;
		case RIGHT:
			return 0;
		case DOWN:
			return Math.PI/2;
		case LEFT:
			return Math.PI;
		default:
			return Math.PI/2;
		}}
	//chooses a direction to accelerate to
	public void chooseDir() {
		
	}
	//returns which special to do, -1 if none
	public int doSpecial() {
		return -1;
	}
	//returns which special to do, -1 if none
	public void setState() {
		if(state!=VENDOR && board.currFloor instanceof Store) {
			state = VENDOR;
		}
		if(state!=VENTURE && !(board.currFloor instanceof Store)) {
			state = VENTURE;
		}
	}
	public void spend(double amount) {
		removeCookies(amount);
	}
	public void pay(double amount) {
		addCookies(amount);
	}
	public void averageStats() {
		acceleration=MR[0][1]/2+MR[0][0];
		max_velocity=MR[1][1]/2+MR[1][0];
		friction=MR[2][1]/2+MR[2][0];
		coloration = new Color((int)((friction-MR[2][0])/MR[2][1]*255),(int)((max_velocity-MR[1][0])/MR[1][1]*255),(int)((acceleration-MR[0][0])/MR[0][1]*255));
		acceleration*=calibration_ratio*calibration_ratio;
		max_velocity*=calibration_ratio;
		friction*=calibration_ratio*calibration_ratio;
	}
	//prepares explorer at start of new level
	public void spawn() {
		scale = board.currFloor.getScale();
	}
	public void paint(Graphics g) {}
}
