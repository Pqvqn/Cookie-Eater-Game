package ce3;

import java.awt.*;
import java.util.*;
//import java.awt.event.*;

import items.*;

//import javax.swing.*;

import levels.*;

public class Eater{
	
	//private Queue<Double> x_positions; //previous positions for trail effect
	//private Queue<Double> y_positions;
	//private final int TRAIL_LENGTH = 10;
	private int radius;
	public static final int DEFAULT_RADIUS = 40;
	private double x, y;
	public static final int NONE=-1, UP=0, RIGHT=1, DOWN=2, LEFT=3;
	private int direction;
	private double x_velocity, y_velocity; //current dimensional speed
	private double acceleration; //added to dimensional speed depending on direction
	private double max_velocity; //cap on dimensional speed
	private double friction; //removed from dimensional speed
	private double accel; //scalable movement stats
	private double maxvel;
	private double fric;
	private Color coloration;
	private boolean dO; //continue movement
	private double scale; //zoom in/out of screen
	private boolean shielded; //in stun after shield use
	private int shield_length; //stun length
	private int shield_frames; //counting how deep into shield
	private int special_length; //stun length
	private ArrayList<Integer> special_frames; //counting how deep into shield
	private int special_cooldown; //frames between uses of special
	private ArrayList<Color> special_colors; //color associated with each special
	private double recoil; //recoil speed from hit
	private final int LIVE = 0, DEAD =-1, WIN = 1, SPECIAL = 2; //states
	private int state;
	private ArrayList<ArrayList<Item>> powerups;
	private int currSpecial;
	private boolean lock; //if player can move
	private int countVels;
	private double calibration_ratio; //framerate ratio
	private boolean grabDecayed;
	
	private Board board;
	
	public Eater(Board frame, int cycletime) {
		calibration_ratio = cycletime/15.0;
		dO= true;
		board = frame;
		x=board.X_RESOL/2;
		y=board.Y_RESOL/2;
		direction = NONE;
		x_velocity = 0;
		y_velocity = 0;
		radius=DEFAULT_RADIUS;
		coloration = Color.blue.brighter();
		scale = 1;
		randomizeStats();
		acceleration = .5*calibration_ratio*calibration_ratio;
		max_velocity = 10*calibration_ratio;
		friction = .1*calibration_ratio*calibration_ratio;
		accel = acceleration*scale;
		maxvel = max_velocity*scale;
		fric = friction*scale;
		shielded = false;
		shield_length = (int)(.5+60*(1/calibration_ratio));
		shield_frames = 0;
		special_length = (int)(.5+60*(1/calibration_ratio));
		special_frames = new ArrayList<Integer>();
		special_cooldown = (int)(.5+180*(1/calibration_ratio));
		special_colors = new ArrayList<Color>();
		special_colors.add(new Color(0,255,255));special_colors.add(new Color(255,0,255));special_colors.add(new Color(255,255,0));
		recoil = 15*calibration_ratio;
		state = LIVE;
		powerups = new ArrayList<ArrayList<Item>>();
		for(int i=0; i<3; i++) {
			powerups.add(new ArrayList<Item>());
			special_frames.add(0);
		}
		currSpecial = -1;
		grabDecayed = false;
		/*x_positions = new LinkedList<Double>();
		y_positions = new LinkedList<Double>();
		for(int i=0; i<=TRAIL_LENGTH; i++) {
			x_positions.add(START_X);
			y_positions.add(START_Y);
		}*/
	}
	
	public int getX() {return (int)(x+.5);}
	public void setX(double xP) {x=xP;}
	public int getY() {return (int)(y+.5);}
	public void setY(double yP) {x=yP;}
	public int getDir() {return direction;}
	public void setDir(int dir) {direction = dir;}
	public int getRadius() {return radius;}
	public double getMaxVel() {return maxvel;}
	public double getXVel() {return x_velocity;}
	public void setXVel(double a) {x_velocity = a;}
	public double getYVel() {return y_velocity;}
	public void setYVel(double a) {y_velocity = a;}
	public void setShielded(boolean s) {shielded = s;}
	
	
	public void addItem(int index, Item i) {powerups.get(index).add(i);}
	public ArrayList<ArrayList<Item>> getItems() {return powerups;}
	public void lockControl(boolean l) {lock = l;}
	public double getFriction() {return fric;}
	public void extendSpecial(double time) {
		for(int i=0; i<special_frames.size(); i++) {
			if(special_frames.get(i)<special_length && special_frames.get(i)!=0) {
				if(special_frames.get(i)>time) {
					special_frames.set(i,(int)(.5+special_frames.get(i)-time));
				}else {
					special_frames.set(i, 1);
				}
			}
		}
	}
	public int getSpecialLength() {return special_length;}
	public int getSpecialCooldown() {return special_cooldown;}
	public ArrayList<Integer> getSpecialFrames() {return special_frames;}
	public ArrayList<Color> getSpecialColors() {return special_colors;}
	
	public void setCalibration(double calrat) { //recalibrate everything that used cycle to better match current fps
		if((int)calrat==(int)calibration_ratio)return;
		acceleration/=calibration_ratio*calibration_ratio;
		max_velocity/=calibration_ratio;
		friction/=calibration_ratio*calibration_ratio;
		recoil /= calibration_ratio;
		calibration_ratio = calrat;
		shield_length = (int)(.5+60*(1/calibration_ratio));
		special_length = (int)(.5+60*(1/calibration_ratio));
		special_cooldown = (int)(.5+180*(1/calibration_ratio));
		recoil *= calibration_ratio;
		acceleration*=calibration_ratio*calibration_ratio;
		max_velocity*=calibration_ratio;
		friction*=calibration_ratio*calibration_ratio;
		for(int i=0; i<board.cookies.size(); i++) {
			board.cookies.get(i).recalibrate();
		}
	}
	public boolean getGrabDecay() {return grabDecayed;}
	public void setGrabDecay(boolean d) {grabDecayed=d;}
	public double getRecoil() {return recoil*scale;}
	public void setRecoil(double r) {recoil = r;}
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
	public boolean collidesWithRect(int oX, int oY, int oW, int oH) {
		/*return (x + radius > oX && x - radius < oX + oW) &&
				(y + radius > oY && y - radius < oY + oH);*/
		return Level.collidesCircleAndRect((int)(x+.5),(int)(y+.5),radius,oX,oY,oW,oH);
			/*(Math.abs(x - oX) <= radius && y>=oY && y<=oY+oH) ||
				(Math.abs(x - (oX+oW)) <= radius && y>=oY && y<=oY+oH)||
				(Math.abs(y - oY) <= radius && x>=oX && x<=oX+oW) ||
				(Math.abs(y - (oY+oH)) <= radius && x>=oX && x<=oX+oW) ||
				(Math.sqrt((x-oX)*(x-oX) + (y-oY)*(y-oY))<=radius) ||
				(Math.sqrt((x-(oX+oW))*(x-(oX+oW)) + (y-oY)*(y-oY))<=radius) ||
				(Math.sqrt((x-oX)*(x-oX) + (y-(oY+oH))*(y-(oY+oH)))<=radius) ||
				(Math.sqrt((x-(oX+oW))*(x-(oX+oW)) + (y-(oY+oH))*(y-(oY+oH)))<=radius);*/
						
	}
	//activates special A (all powerups tied to A)
	public void special(int index) {
		if(state==SPECIAL || special_frames.get(index)!=0 || direction==NONE)return;
		for(int i=0; i<powerups.get(index).size(); i++) {
			powerups.get(index).get(i).initialize();
		}
		state=SPECIAL;
		currSpecial = index;
		//special_frames=0;
	}
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
	//reset back to first level
	public void kill() {
		//coloration = Color.black;
		if(state==SPECIAL) {
			for(int i=0; i<powerups.get(currSpecial).size(); i++) //stop special
				powerups.get(currSpecial).get(i).end(true);
		}
		for(int i=0; i<powerups.size(); i++)powerups.set(i, new ArrayList<Item>());
		state = DEAD;
		board.draw.repaint();
		x_velocity = 0;
		y_velocity = 0;
		dO = false;
		try {
			Thread.sleep(200); //movement freeze
		}catch(InterruptedException e){};
		board.resetGame();
		randomizeStats();
		reset();
	}
	//kill, but only if no bounce
	public void killBounce(Wall rect, boolean breakShield) {
		if(!shielded && board.shields<=0) { //kill if no shields, otherwise bounce
			kill();
		}else if(breakShield){//only remove shields if not in stun and shield to be broken
			board.shields--;
			bounce(rect);
		}else {
			bounce(rect);
		}
	}
	//move to next level
	public void win() {
		//coloration = Color.green;
		if(state==SPECIAL) {
			for(int i=0; i<powerups.get(currSpecial).size(); i++) //stop special
				powerups.get(currSpecial).get(i).end(true);
		}
		state = WIN;
		board.draw.repaint();
		x_velocity = 0;
		y_velocity = 0;
		dO = false;
		try { //movement freeze
			Thread.sleep(200);
		}catch(InterruptedException e){};
		board.nextLevel();
		reset();
	}
	//resets player to floor-beginning state
	public void reset() {
		state = LIVE;
		shielded = false;
		shield_frames = 0;
		for(int i=0; i<special_frames.size(); i++)special_frames.set(i,0);
		coloration = new Color((int)((friction/calibration_ratio/calibration_ratio-.05)/.25*255),(int)((max_velocity/calibration_ratio-5)/15*255),(int)((acceleration/calibration_ratio/calibration_ratio-.2)/1*255));
		x_velocity=0;
		y_velocity=0;
		x = board.currFloor.getStartX();
		y = board.currFloor.getStartY();
		scale = board.currFloor.getScale();
		accel = acceleration*scale;
		maxvel = max_velocity*scale;
		fric = friction*scale;
		radius = (int)(.5+DEFAULT_RADIUS*scale);
		dO = true;
		direction = NONE;
	}
	//uses shield instead of killing
	public void bounce(Wall w) {
		shielded = true;
		boolean xB=false,yB=false;
		if(y>w.getY()+w.getH()) {
			y_velocity=recoil*scale;
			y+=y_velocity;
			yB=true;
		}else if(y<w.getY()) {
			y_velocity=-recoil*scale;
			y+=y_velocity;
			yB=true;
		}else if(x>w.getX()+w.getW()) {
			x_velocity=recoil*scale;
			x+=x_velocity;
			xB=true;
		}else if(x<w.getX()) {
			x_velocity=-recoil*scale;
			x+=x_velocity;
			xB=true;
		}
		if(state==SPECIAL) {
			for(int i=0; i<powerups.get(currSpecial).size(); i++) {
				powerups.get(currSpecial).get(i).bounce(xB,yB);
			}
		}
	}
	//gives the player a random set of movement stats and colors accordingly
	public void randomizeStats() {
		acceleration = Math.random()*1+.2;
		max_velocity = Math.random()*15+5;
		friction = Math.random()*.25+.05;
		coloration = new Color((int)((friction-.05)/.25*255),(int)((max_velocity-5)/15*255),(int)((acceleration-.2)/1*255));
		acceleration*=calibration_ratio*calibration_ratio;
		max_velocity*=calibration_ratio;
		friction*=calibration_ratio*calibration_ratio;
	}
	
	public void runUpdate() {
		if(!dO)return; //if paused
		countVels=0;
		if(state == SPECIAL) {
			for(int i=0; i<powerups.get(currSpecial).size(); i++) {
				powerups.get(currSpecial).get(i).execute();
			}
			special_frames.set(currSpecial,special_frames.get(currSpecial)+1);
			if(special_frames.get(currSpecial)>special_length) {
				state = LIVE;
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
					special_frames.set(i,0);
				}
			}
		}
		if(shielded) {
			if(shield_frames++>shield_length) {
				shielded=false;
				shield_frames = 0;
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
		if(board.score>=board.scoreToWin) //win if all cookies eaten
			win();
		
		for(int i=0; i<board.walls.size(); i++) { //test collision with all walls, die if hit one
			Wall rect = board.walls.get(i);
			if(collidesWithRect(rect.getX(), rect.getY(), rect.getW(), rect.getH())) {
				i=board.walls.size();
				killBounce(rect,!shielded);
			}
		}
		
	}
	
	public void paint(Graphics g) {
		if(state==DEAD) {
			g.setColor(new Color(0,0,0,100));
			g.fillOval((int)(.5+x-2*radius), (int)(.5+y-2*radius), 4*radius, 4*radius);
		}else if(state==WIN) {
			g.setColor(new Color(255,255,255,100));
			g.fillOval((int)(.5+x-2*radius), (int)(.5+y-2*radius), 4*radius, 4*radius);
		}else if(state==SPECIAL) {
			Color meh = special_colors.get(currSpecial);
			g.setColor(new Color(meh.getRed(),meh.getGreen(),meh.getBlue(),100));
			g.fillOval((int)(.5+x-1.5*radius), (int)(.5+y-1.5*radius), 3*radius, 3*radius);
		}
		if(shielded) { //invert color if shielded
			g.setColor(new Color(255-coloration.getRed(),255-coloration.getGreen(),255-coloration.getBlue()));
		}else {
			g.setColor(coloration);
		}
		g.fillOval((int)(.5+x-radius), (int)(.5+y-radius), 2*radius, 2*radius);
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
