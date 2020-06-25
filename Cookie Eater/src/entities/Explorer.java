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
	
	public static final int NONE=-1, UP=0, RIGHT=1, DOWN=2, LEFT=3;
	private int direction;
	private double acceleration; //added to dimensional speed depending on direction
	private double max_velocity; //cap on accelerated-to dimensional speed
	private double terminal_velocity; //maximum possible dimensional speed
	private double friction; //removed from dimensional speed
	private double accel; //scalable movement stats
	private double maxvel;
	private double termvel;
	private double fric;
	//private double minRecoil; //how fast bounces off wall (min and max)
	//private double maxRecoil;

	
	public Explorer(Board frame) {
		super(frame);
		to_sell = new ArrayList<CookieStore>();
		on_display = new ArrayList<CookieStore>();
		name = "Unknown";
		chooseResidence();
		state = VENDOR;
		
		acceleration = .5*calibration_ratio*calibration_ratio;
		max_velocity = 10*calibration_ratio;
		terminal_velocity = 50*calibration_ratio;
		friction = .1*calibration_ratio*calibration_ratio;
		accel = acceleration*scale;
		maxvel = max_velocity*scale;
		termvel = terminal_velocity*scale;
		fric = friction*scale;
		//minRecoil = 10*calibration_ratio;
		//maxRecoil = 50*calibration_ratio;
		direction = NONE;
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
		
		if(state == VENDOR) { //if selling
			x_velocity = 0; //reset speeds
			y_velocity = 0;
			x = shop_spots[0][0];
			y = shop_spots[0][1];
		}
		
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
		orientParts();
	}
	//die on a floor
	public void kill() {
		//item drop code here :)
	}
	//chooses which level to go to on game start
	public void chooseResidence() {
		
	}
	//given a level's name, finds the "num+1"th level of that type from the board's level progression - uses backup as index if this doesn't exist
	public Level findFloor(String type, boolean store, int num, int backup) {
		int count = 0;
		for(int i=0; i<board.floors.size(); i++) {
			if(board.floors.get(i).getName().equals(type) && store==board.floors.get(i) instanceof Store) {
				count++;
				if(count>num)return board.floors.get(i);
			}
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
		//minRecoil /= calibration_ratio;
		//maxRecoil /= calibration_ratio;
		
		calibration_ratio = calrat;
		
		shield_length = (int)(.5+60*(1/calibration_ratio));
		special_length = (int)(.5+60*(1/calibration_ratio));
		special_cooldown = (int)(.5+180*(1/calibration_ratio));
		//minRecoil *= calibration_ratio;
		//maxRecoil *= calibration_ratio;
		acceleration*=calibration_ratio*calibration_ratio;
		max_velocity*=calibration_ratio;
		terminal_velocity*=calibration_ratio;
		friction*=calibration_ratio*calibration_ratio;
		//coloration = new Color((int)((friction/calibration_ratio/calibration_ratio-MR[2][0])/MR[2][1]*255),(int)((max_velocity/calibration_ratio-MR[1][0])/MR[1][1]*255),(int)((acceleration/calibration_ratio/calibration_ratio-MR[0][0])/MR[0][1]*255));
	}
	public void paint(Graphics g) {}
}
