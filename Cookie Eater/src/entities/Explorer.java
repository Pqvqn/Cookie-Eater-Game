package entities;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.*;

import ce3.*;
import levels.*;
import cookies.*;
import items.*;
import sprites.*;
import menus.*;

public class Explorer extends Entity{

	protected Level residence; //which room this explorer is on
	protected int state;
	public static final int VENDOR = 0, VENTURE = 1, STAND = 2, STOP = 3;
	protected int[][] shop_spots;
	protected ArrayList<CookieStore> to_sell;
	protected ArrayList<CookieStore> on_display;
	protected int min_cat, max_cat;
	protected SpriteExplorer sprite;
	
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
	protected Color coloration;
	protected Segment tester; //segment used to test possible movement paths to choose optimal one
	protected double input_speed; //how many frames must pass between inputs
	protected int input_counter; //counts the above
	protected ArrayList<CookieItem> pickups; //items picked up but not activated
	protected int start_shields;
	protected int speaking; //how long been speaking for
	protected Conversation convo;
	protected Cookie target;
	
	public Explorer(Board frame, int cycletime) {
		super(frame,cycletime);
		calibration_ratio = cycletime/15.0;
		to_sell = new ArrayList<CookieStore>();
		on_display = new ArrayList<CookieStore>();
		pickups = new ArrayList<CookieItem>();
		name = getName();
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
		direction = NONE;
		coloration = Color.gray;
		input_counter = 0;
		speaking = 0;
		setShields(start_shields);
		buildBody();
		try {
			sprite = new SpriteExplorer(board,this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Level getResidence() {return residence;}
	public String getName() {return "Unknown";}
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
			if(shop_spots!=null) {
				x = shop_spots[0][0];
				y = shop_spots[0][1];
			}
			lock = true;
		}else if(state == STOP) { //if staying in shop
			x_velocity = 0; //reset speeds
			y_velocity = 0;
			lock = true;
		}else if(state == STAND){
			//traverse to cookie on board to purchase it
			if(target==null || !board.cookies.contains(target)) {
				target = choosePurchase(board.cookies);
			}
			if(target!=null) {
				traverseShop(target);
			}else {
				if(shop_spots!=null)traverseShop(shop_spots[0][0],shop_spots[0][1],getRadius());
			}
			lock = false;
		}else if (state == VENTURE) {
			if(input_counter++>=input_speed) {
				input_counter = 0;
				chooseDir();
			}
			lock = false;
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
		int spec = (special)?-1:doSpecial();
		if(spec!=-1 && special_activated.get(spec)) {
			special(spec);
		}
		orientParts();
	}

	//dies on floor
	public void kill() {
		if(!ded)System.out.println("oops i died but there no code");
		ded = true;
		board.present_npcs.remove(this);
		if(special) {
			for(int i=0; i<powerups.get(currSpecial).size(); i++) //stop special
				powerups.get(currSpecial).get(i).end(true);
		}
		for(int i=0; i<powerups.size(); i++)powerups.set(i, new ArrayList<Item>());
		pickups = new ArrayList<CookieItem>();
		special = false;
		x_velocity = 0;
		y_velocity = 0;
		wipeStash();
		to_sell = new ArrayList<CookieStore>();
		setShields(start_shields);
		//randomizeStats();
			
		decayed_value = 0;
		extra_radius = 0;
		ghost = false;
		offstage = 0;
		averageStats();
		reset();
		chooseResidence();
	}
	//reset npc for new FLOOR
	public void reset() {
		special = false;
		currSpecial = -1;
		shielded = false;
		shield_frames = 0;
		for(int i=0; i<special_frames.size(); i++)special_frames.set(i,0.0);
		for(int i=0; i<special_activated.size(); i++)special_activated.set(i,false);
		x_velocity=0;
		y_velocity=0;
		direction = NONE;
		scale = board.currFloor.getScale();
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
			c.setVendor(this);
			board.cookies.add(c);
			on_display.add(c);
		}
	}
	//puts self in shop
	public void standBy(int[] position) {
		int[][] b = {position};
		shop_spots = b;
		setX(position[0]); //put explorer in place
		setY(position[1]);
		state = STOP;
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
		if(!board.check_calibration || calrat==calibration_ratio || board.getAdjustedCycle()/(double)board.getCycle()>2 || board.getAdjustedCycle()/(double)board.getCycle()<.5)return;
		acceleration/=calibration_ratio*calibration_ratio;
		max_velocity/=calibration_ratio;
		terminal_velocity/=calibration_ratio;
		friction/=calibration_ratio*calibration_ratio;
		minRecoil /= calibration_ratio;
		maxRecoil /= calibration_ratio;
		input_speed *= calibration_ratio;
		
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
		input_speed /= calibration_ratio;
		coloration = new Color((int)((friction/calibration_ratio/calibration_ratio-MR[2][0])/MR[2][1]*255),(int)((max_velocity/calibration_ratio-MR[1][0])/MR[1][1]*255),(int)((acceleration/calibration_ratio/calibration_ratio-MR[0][0])/MR[0][1]*255));
	
		//calibrate summons
		for(int i=0; i<summons.size(); i++) {
			summons.get(i).setCalibration(calrat);
		}
	}
	/*//uses shield instead of killing
	public void bounceShield(Wall w,int rx,int ry,int rw,int rh) {
		shielded = true;
		double[] point = board.currFloor.circAndRectHitPoint(x,y,radius*scale,rx,ry,rw,rh);
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
	}*/
	/*public void bounce(Wall w,int rx,int ry,int rw,int rh) {
		if(!shielded && shield_stash.size()<=0 && board.currFloor.takeDamage()) { //kill if no shields, otherwise bounce
			kill();
			return;
		}else if (!shielded && board.currFloor.takeDamage()){//only remove shields if not in stun and shield to be broken
			removeShields(1);
		}
		bounceShield(w,rx,ry,rw,rh);
		
	}*/
	/*//tests if hits rectangle
	public boolean collidesWithRect(boolean extra, int oX, int oY, int oW, int oH) {
		boolean hit = false;
		for(int i=0; i<parts.size(); i++) {
			if(parts.get(i).collidesWithRect(extra,oX,oY,oW,oH,0))hit=true;
		}
		return hit;
							
	}*/
	
	public void doFunction(String f, String[] args) {
		switch(f) {
		case "Give": //pays player $ {dollars}
			double dollars = Double.parseDouble(args[0]);
			payCookies(board.player,dollars);
			break;
		case "Take": //takes $ from player {dollars}
			double dollars2 = Double.parseDouble(args[0]);
			board.player.payCookies(this,dollars2);
			break;
		}
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
	//moves tester to where this explorer would be if they press dir right now
	public void testDirection(int dir) {
		double xv = x_velocity, yv = y_velocity; //used to find average x/y velocities over time period
		switch(dir) { //change velocity based on direction accelerating in
			case UP:
				xv-=Math.signum(xv)*(((input_speed-Math.max(input_speed-Math.abs(xv/fric), 0))*fric)/2);
				yv+=-1*(((input_speed-Math.max(input_speed-(Math.abs(-1*maxvel-yv)/accel), 0))*accel)/2);
				break;
			case DOWN:
				xv-=Math.signum(xv)*(((input_speed-Math.max(input_speed-Math.abs(xv/fric), 0))*fric)/2);
				yv+=1*(((input_speed-Math.max(input_speed-(Math.abs(1*maxvel-yv)/accel), 0))*accel)/2);
				break;
			case LEFT:
				yv-=Math.signum(yv)*(((input_speed-Math.max(input_speed-Math.abs(yv/fric), 0))*fric)/2);
				xv+=-1*(((input_speed-Math.max(input_speed-(Math.abs(-1*maxvel-xv)/accel), 0))*accel)/2);
				break;
			case RIGHT:
				yv-=Math.signum(yv)*(((input_speed-Math.max(input_speed-Math.abs(yv/fric), 0))*fric)/2);
				xv+=1*(((input_speed-Math.max(input_speed-(Math.abs(1*maxvel-xv)/accel), 0))*accel)/2);
				break;
		}
		tester.setLocation(x+xv*input_speed,y+yv*input_speed); //move tester to predicted location
	}
	//returns which special to do, -1 if none
	public int doSpecial() {
		return -1;
	}
	
	public void traverseShop(Cookie targ) {
		traverseShop(targ.getX(),targ.getY(),targ.getRadius());
	}
	//set direction to get to target cookie in shop
	public void traverseShop(double xt, double yt, double rt) {
		int dir = NONE;
		if(Math.sqrt(Math.pow(x-xt,2)+Math.pow(y-yt,2))<getRadius()+rt){

		}else if(Math.abs(x-xt)<getRadius()) {
			dir = y<yt ? DOWN : UP; 
		}else if(Math.abs(y-board.Y_RESOL/2)<getRadius()) {
			dir = x>xt ? LEFT : RIGHT; 
		}else {
			dir = y<board.Y_RESOL/2 ? DOWN : UP; 
		}
		if(dir!=direction && direction!=NONE) {
			direction = NONE;
			averageVels(0,0);
		}else {
			direction = dir;
		}
	}
	//returns chosen cookie to try to buy from options
	public Cookie choosePurchase(ArrayList<Cookie> options) {
		return null;
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
		ded = false;
		reset();
		setConvo();
	}
	//choose conversation for this level
	public void setConvo() {
		 convo = null;
	}
	public void levelComplete() {
		if(state == VENTURE || state == STAND) { //move on if in correct state
			residence = board.currFloor.getNext();
			state = VENTURE;
		}
		//reset special
		if(special) {
			for(int i=0; i<powerups.get(currSpecial).size(); i++) //stop special
				powerups.get(currSpecial).get(i).end(true);
		}
		special = false;
		currSpecial = -1;
	}
	public int getState() {return state;}
	//sets words in text box at bottom of screen - null for removing box
	public void speak(Conversation conversation) {
		if(conversation!=null) {conversation.setDisplayed(true);
		}else {convo.setDisplayed(false);}
		board.setDialogue(this, conversation);
		if(conversation!=null)conversation.test();
	}
	public Color getColor() {return coloration;}
	public SpriteExplorer getSprite() {return sprite;}
	public void paint(Graphics g) {
		
		for(int i=0; i<parts.size(); i++) {
			if(parts.get(i)!=null)parts.get(i).paint(g);
		}
		if(tester!=null)tester.paint(g);
		g.setColor(coloration);
		g.fillOval((int)(.5+x-getRadius()), (int)(.5+y-getRadius()), (int)(.5+getRadius()*2), (int)(.5+getRadius()*2));
		
		Graphics2D g2 = (Graphics2D)g;
		AffineTransform origt = g2.getTransform();
		for(int i=0; i<summons.size(); i++) { //draw summons
			summons.get(i).paint(g2);
			g2.setTransform(origt);
		}
		//sprite
		if(sprite==null)return;
		sprite.setColor(coloration);
		sprite.paint(g);
	}
}
