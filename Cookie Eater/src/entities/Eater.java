package entities;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
//import java.awt.event.*;

import ce3.*;

import java.io.*;

import items.*;

//import javax.swing.*;

import levels.*;
import sprites.*;
import ui.*;
import cookies.*;

public class Eater extends Entity{
	
	//private Queue<Double> x_positions; //previous positions for trail effect
	//private Queue<Double> y_positions;
	//private final int TRAIL_LENGTH = 10;
	private int id;
	public static final int DEFAULT_RADIUS = 40;
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
	private double minRecoil; //how fast player bounces off wall (min and max)
	private double maxRecoil;

	private double[][] MR = {{.2,1},{5,15},{.05,.25}}; //accel min,max-min; maxvel min,max-min; fric min,max-min
	private Color coloration;
	private boolean dO; //continue movement
	public int score, scoreToWin; //cookies eaten and amount of cookies on board
	public double cash; //cookies to spend
	private ArrayList<CookieItem> pickups; //items picked up but not activated
	public static final int LIVE = 0, DEAD =-1, WIN = 1; //states
	private int state;
	private UIItemsAll itemDisp; //ui parts
	private UIScoreCount scoreboard;
	private UIShields shieldDisp;
	private SpriteEater sprite;
	private SegmentCircle part;
	private boolean nearCookie;

	public Eater(Board frame, int num, int cycletime) {
		super(frame);
		id = num;
		calibration_ratio = cycletime/15.0;
		dO= true;
		ded = false;
		board = frame;
		x=board.X_RESOL/2;
		y=board.Y_RESOL/2;
		direction = NONE;
		x_velocity = 0;
		y_velocity = 0;
		radius=DEFAULT_RADIUS;
		coloration = Color.blue.brighter();
		acceleration = .5*calibration_ratio*calibration_ratio;
		max_velocity = 10*calibration_ratio;
		terminal_velocity = 50*calibration_ratio;
		friction = .1*calibration_ratio*calibration_ratio;
		averageStats();
		accel = acceleration*scale;
		maxvel = max_velocity*scale;
		termvel = terminal_velocity*scale;
		fric = friction*scale;
		score = 0;
		cash = 0;
		pickups = new ArrayList<CookieItem>();
		addShields(3);
		shielded = false;
		
		minRecoil = 10*calibration_ratio;
		maxRecoil = 50*calibration_ratio;
		state = LIVE;
		
		extra_radius = 0;
		ghost = false;
		mass = 200;
		check_calibration = true;
		/*x_positions = new LinkedList<Double>();
		y_positions = new LinkedList<Double>();
		for(int i=0; i<=TRAIL_LENGTH; i++) {
			x_positions.add(START_X);
			y_positions.add(START_Y);
		}*/
		try {
			sprite = new SpriteEater(board,this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	public void setDir(int dir) {direction = dir;}
	public double getMaxVel() {return maxvel;}
	public int getState() {return state;}
	public int getScore() {return score;}
	public void addScore(int s) {score+=s;}
	public int getScoreToWin() {return scoreToWin;}
	public void setScoreToWin(int s) {scoreToWin=s;}
	public double getCash() {return cash;}
	public void addCash(double c) {cash+=c;}
	public ArrayList<CookieItem> getPickups() {return pickups;}
	public void pickupItem(CookieItem i) {
		pickups.add(i);
	}
	public void addItem(int index, Item i) {
		super.addItem(index, i);
		itemDisp.update(true, getItems(),getSpecialFrames(),getSpecialCooldown(),getSpecialLength(),special_activated);
	}
	public double getFriction() {return fric;}
	
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
	public double getMinRecoil() {return minRecoil;}
	public void setMinRecoil(double r) {minRecoil = r;}
	public double getMaxRecoil() {return maxRecoil;}
	public void setMaxRecoil(double r) {maxRecoil = r;}
	public double[][] getMovementRand() {return MR;}
	public void addToMovement(double a, double v, double f) {
		acceleration += a*calibration_ratio*calibration_ratio;
		if(acceleration>(MR[0][0]+MR[0][1])*calibration_ratio*calibration_ratio)acceleration=(MR[0][0]+MR[0][1])*calibration_ratio*calibration_ratio;
		if(acceleration<(MR[0][0])*calibration_ratio*calibration_ratio)acceleration=(MR[0][0])*calibration_ratio*calibration_ratio;
		max_velocity += v*calibration_ratio;
		if(max_velocity>(MR[1][0]+MR[1][1])*calibration_ratio)max_velocity=(MR[1][0]+MR[1][1])*calibration_ratio;
		if(max_velocity<(MR[1][0])*calibration_ratio)max_velocity=(MR[1][0])*calibration_ratio;
		friction += f*calibration_ratio*calibration_ratio;
		if(friction>(MR[2][0]+MR[2][1])*calibration_ratio*calibration_ratio)friction=(MR[2][0]+MR[2][1])*calibration_ratio*calibration_ratio;
		if(friction<(MR[2][0])*calibration_ratio*calibration_ratio)friction=(MR[2][0])*calibration_ratio*calibration_ratio;
		coloration = new Color((int)((friction/calibration_ratio/calibration_ratio-MR[2][0])/MR[2][1]*255),(int)((max_velocity/calibration_ratio-MR[1][0])/MR[1][1]*255),(int)((acceleration/calibration_ratio/calibration_ratio-MR[0][0])/MR[0][1]*255));
	}
	public boolean getNearCookie() {return nearCookie;}
	public void setNearCookie(boolean n) {nearCookie = n;}
	public void spend(double amount) {
		addCash(-amount);
		removeCookies(amount);
	}
	public void pay(double amount) {
		addCash(amount);
		addCookies(amount);
	}
	//currently unused trail stuff
	/*public int getTrailX() {
		if(x_positions.peek()==null) {
			return -1;
		}
		double curr = x_positions.remove();
		x_positions.add(curr);
		return (int)(curr+.5);
	}
	public int getTrailY() {
		if(y_positions.peek()==null) {
			return -1;
		}
		double curr = y_positions.remove();
		y_positions.add(curr);
		return (int)(curr+.5);
	}
	public int getTrailLength() {
		return TRAIL_LENGTH;
	}

	public String getTrailLists() {
		String ret = "";
		for(int i=0; i<=TRAIL_LENGTH; i++) {
			double yes = x_positions.remove();
			ret+=yes+" ";
			x_positions.add(yes);
		}
		ret+=" - - ";
		for(int i=0; i<=TRAIL_LENGTH; i++) {
			double yes = y_positions.remove();
			ret+=yes+" ";
			y_positions.add(yes);
		}
		return ret;
	}*/
	
	//tests if hits rectangle
	public boolean collidesWithRect(boolean extra, int oX, int oY, int oW, int oH) {
		/*return (x + radius > oX && x - radius < oX + oW) &&
				(y + radius > oY && y - radius < oY + oH);*/
		boolean hit = false;
		for(int i=0; i<parts.size(); i++) {
			if(parts.get(i).collidesWithRect(extra,oX,oY,oW,oH))hit=true;
		}
		return hit;
		//return Level.collidesCircleAndRect((int)(x+.5),(int)(y+.5),radius*scale,oX,oY,oW,oH);
			/*(Math.abs(x - oX) <= radius && y>=oY && y<=oY+oH) ||
				(Math.abs(x - (oX+oW)) <= radius && y>=oY && y<=oY+oH)||
				(Math.abs(y - oY) <= radius && x>=oX && x<=oX+oW) ||
				(Math.abs(y - (oY+oH)) <= radius && x>=oX && x<=oX+oW) ||
				(Math.sqrt((x-oX)*(x-oX) + (y-oY)*(y-oY))<=radius) ||
				(Math.sqrt((x-(oX+oW))*(x-(oX+oW)) + (y-oY)*(y-oY))<=radius) ||
				(Math.sqrt((x-oX)*(x-oX) + (y-(oY+oH))*(y-(oY+oH)))<=radius) ||
				(Math.sqrt((x-(oX+oW))*(x-(oX+oW)) + (y-(oY+oH))*(y-(oY+oH)))<=radius);*/
						
	}
	//reset back to first level
	public void kill() {
		//coloration = Color.black;
		if(special) {
			for(int i=0; i<powerups.get(currSpecial).size(); i++) //stop special
				powerups.get(currSpecial).get(i).end(true);
		}
		for(int i=0; i<powerups.size(); i++)powerups.set(i, new ArrayList<Item>());
		pickups = new ArrayList<CookieItem>();
		state = DEAD;
		special = false;
		board.draw.repaint();
		x_velocity = 0;
		y_velocity = 0;
		dO = false;
		//if levels, reset
		if(board.mode==Main.LEVELS) {
			try {
				Thread.sleep(200); //movement freeze
			}catch(InterruptedException e){};
			board.resetGame();
			score = 0;
			cash = 0;
			wipeStash();
			setShields(3);
			//randomizeStats();
			
			decayed_value = 0;
			extra_radius = 0;
			ghost = false;
			offstage = 0;
			averageStats();
			reset();
		}
	}
	//kill, but only if no bounce
	public void bounce(Wall w,int rx,int ry,int rw,int rh) {
		if(!shielded && shield_stash.size()<=0 && board.currFloor.takeDamage()) { //kill if no shields, otherwise bounce
			kill();
			return;
		}else if (!shielded && board.currFloor.takeDamage()){//only remove shields if not in stun and shield to be broken
			removeShields(1);
		}
		bounceShield(w,rx,ry,rw,rh);
		
	}
	//move to next level
	public void win() {
		//coloration = Color.green;
		if(special) {
			for(int i=0; i<powerups.get(currSpecial).size(); i++) //stop special
				powerups.get(currSpecial).get(i).end(true);
		}
		state = WIN;
		special = false;
		board.draw.repaint();
		x_velocity = 0;
		y_velocity = 0;
		dO = false;
		try { //movement freeze
			Thread.sleep(200);
		}catch(InterruptedException e){};
		if(board.mode == Main.LEVELS) {
			board.nextLevel();
			score = 0;
			reset();
		}else if(board.mode == Main.PVP) {
			try { //movement freeze
				Thread.sleep(200);
			}catch(InterruptedException e){};
			for(Eater e : board.players) {
				e.revive();
			}
		}
	}
	//resets player to floor-beginning state
	public void reset() {
		state = LIVE;
		special = false;
		currSpecial = -1;
		shielded = false;
		shield_frames = 0;
		for(int i=0; i<special_frames.size(); i++)special_frames.set(i,0.0);
		for(int i=0; i<special_activated.size(); i++)special_activated.set(i,false);
		coloration = new Color((int)((friction/calibration_ratio/calibration_ratio-MR[2][0])/MR[2][1]*255),(int)((max_velocity/calibration_ratio-MR[1][0])/MR[1][1]*255),(int)((acceleration/calibration_ratio/calibration_ratio-MR[0][0])/MR[0][1]*255));
		x_velocity=0;
		y_velocity=0;
		if(board.mode == Main.LEVELS) {
			x = board.currFloor.getStartX();
			y = board.currFloor.getStartY();
		}else if (board.mode==Main.PVP) {
			x = board.currFloor.getStarts()[id][0];
			y = board.currFloor.getStarts()[id][1];
		}
		scale = board.currFloor.getScale();
		accel = acceleration*scale;
		maxvel = max_velocity*scale;
		termvel = terminal_velocity*scale;
		fric = friction*scale;
		radius = DEFAULT_RADIUS;
		dO = true;
		direction = NONE;
	}
	//resets killed players
	public void revive() {
		score = 0;
		cash = 0;
		setShields(3);
		//randomizeStats();
		
		decayed_value = 0;
		extra_radius = 0;
		ghost = false;
		offstage = 0;
		averageStats();
		reset();
	}
	//uses shield instead of killing
	public void bounceShield(Wall w,int rx,int ry,int rw,int rh) {
		shielded = true;
		double[] point = Level.circAndRectHitPoint(x,y,radius*scale,rx,ry,rw,rh);
		//collideAt(w,point[0],point[1],0,0,999999999);
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
	//gives the player a random set of movement stats and colors accordingly
	public void randomizeStats() {
		acceleration = Math.random()*MR[0][1]+MR[0][0];
		max_velocity = Math.random()*MR[1][1]+MR[1][0];
		friction = Math.random()*MR[2][1]+MR[2][0];
		coloration = new Color((int)((friction-MR[2][0])/MR[2][1]*255),(int)((max_velocity-MR[1][0])/MR[1][1]*255),(int)((acceleration-MR[0][0])/MR[0][1]*255));
		acceleration*=calibration_ratio*calibration_ratio;
		max_velocity*=calibration_ratio;
		friction*=calibration_ratio*calibration_ratio;
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
	public void buildBody() {
		parts.add(part = new SegmentCircle(board,this,x,y,radius,0));
	}
	public void orientParts() {
		part.setLocation(x,y);
		part.setSize(radius);
	}
	public void runUpdate() {
		super.runUpdate();
		if(parts.isEmpty())buildBody();
		if(state == DEAD) { //if dead in multiplayer
			x_velocity = 0; //reset speeds
			y_velocity = 0;
		}
		if(!dO)return; //if paused
		if(board.mode == Main.PVP) {
			boolean allDead = true;
			for(Eater e : board.players) { //check if any players aren't dead or winning
				if(!e.equals(this) && e.getState() != DEAD) {
					allDead = false;
				}
			}
			if(allDead) { //win if last man standing
				win();
			}
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
		/*x_positions.add(x);
		y_positions.add(y);
		x_positions.remove();
		y_positions.remove();*/
		if(score>=scoreToWin&&board.mode==Main.LEVELS) //win if all cookies eaten
			win();
		

		/*if(!ghost) {
			for(int i=0; i<board.walls.size(); i++) { //test collision with all walls, die if hit one
				Wall rect = board.walls.get(i);
				if(collidesWithRect(rect.getX(), rect.getY(), rect.getW(), rect.getH())) {
					i=board.walls.size();
					killBounce(rect,!shielded);
				}
			}
			for(int i=0; i<board.players.size(); i++) { //test collisions with players
				Eater other = board.players.get(i);
				if(!other.equals(this)) {
					if(collidesWithCircle(other.getX(),other.getY(),other.getTotalRadius()))	{
						double xv = x_velocity; //storing velocities
						double yv = y_velocity;
						//collide for this one and then other one
						double[] point = Level.circAndCircHitPoint(x,y,getTotalRadius(),other.getX(),other.getY(),other.getTotalRadius());
						collideAt(other,point[0],point[1],other.getXVel(),other.getYVel(),other.getMass());
						other.collideAt(this, point[0],point[1],xv,yv, mass);
						while(collidesWithCircle(other.getX(),other.getY(),other.getTotalRadius())) {
							double rat = 1/Math.sqrt(Math.pow(x-point[0],2)+Math.pow(y-point[1],2));
							x+=(x-point[0])*rat; //move out of other
							y+=(y-point[1])*rat;
							rat = 1/Math.sqrt(Math.pow(other.getX()-point[0],2)+Math.pow(other.getY()-point[1],2));
							other.setX(other.getX()+(other.getX()-point[0])*rat); //move out
							other.setY(other.getY()+(other.getY()-point[1])*rat);
						}
					}
				}
			}
		}*/
		orientParts();
	}
	//resets after cycle end
	public void endCycle() {
		bumped = new ArrayList<Object>();
	}
	public void initUI() {
		board.draw.addUI(itemDisp = new UIItemsAll(board,50,board.Y_RESOL-50,getSpecialColors()));
		board.draw.addUI(scoreboard = new UIScoreCount(board,board.X_RESOL-170,board.Y_RESOL-100));
		board.draw.addUI(shieldDisp = new UIShields(board,board.X_RESOL-50,90+60*id));
	}
	public void updateUI() {
		
		//items
		if(itemDisp==null)initUI();
		itemDisp.update(false, getItems(),getSpecialFrames(),getSpecialCooldown(),getSpecialLength(),special_activated);
		//scoreboard
		scoreboard.update(cash,score,scoreToWin);
		//shields
		shieldDisp.update(shield_stash.size());
	}
	public void updateUIItems() {
		itemDisp.update(true, getItems(),getSpecialFrames(),getSpecialCooldown(),getSpecialLength(),special_activated);
	}
	
	public void paint(Graphics g) {
		if(part!=null)part.paint(g);
		Graphics2D g2 = (Graphics2D)g;
		AffineTransform origt = g2.getTransform();
		for(int i=0; i<summons.size(); i++) { //draw summons
			summons.get(i).paint(g2);
			g2.setTransform(origt);
		}
		
		
		
		//sprite
		sprite.setColor(coloration);
		sprite.paint(g);
		nearCookie = false;
		
		/*int rate = 5;
		int x=0, y=0;
		int diam = player.getRadius()*2-player.getTrailLength()*rate;
		double alphaChange = 255/(player.getTrailLength());
		for(int i=0; i<=player.getTrailLength(); i++) {
			Color color = new Color(200,100,0,255);//(int)(i*alphaChange)
			g.setColor(color);
			g.fillOval(player.getTrailX()-(diam+i*rate)/2, player.getTrailY()-(diam+i*rate)/2, diam+i*rate, diam+i*rate);}*/
		
	}
;}
