package ce3;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
//import java.awt.event.*;

import java.io.*;

import items.*;

//import javax.swing.*;

import levels.*;
import sprites.*;
import ui.UIItemsAll;
import ui.UIScoreCount;
import ui.UIShields;

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
	private double[][] MR = {{.2,1},{5,15},{.05,.25}}; //accel min,max-min; maxvel min,max-min; fric min,max-min
	private Color coloration;
	private boolean dO; //continue movement
	private double scale; //zoom in/out of screen
	public int score, scoreToWin; //cookies eaten and amount of cookies on board
	public double cash; //cookies to spend
	public int shields; //shields owned
	private boolean shielded; //in stun after shield use
	private int shield_length; //stun length
	private int shield_frames; //counting how deep into shield
	private boolean shield_tick; //countdown shield
	private int special_length; //stun length
	private ArrayList<Integer> special_frames; //counting how deep into shield
	private int special_cooldown; //frames between uses of special
	private ArrayList<Boolean> special_activated; //if special is triggerable
	private ArrayList<Color> special_colors; //color associated with each special
	private ArrayList<Summon> summons; //constructed objects owned by player
	private double recoil; //recoil speed from hit
	public static final int LIVE = 0, DEAD =-1, WIN = 1, SPECIAL = 2; //states
	private int state;
	private ArrayList<ArrayList<Item>> powerups;
	private int currSpecial;
	private boolean lock; //if player can move
	private int countVels;
	private double calibration_ratio; //framerate ratio
	private double decayedValue;
	private int extra_radius;
	private UIItemsAll itemDisp; //ui parts
	private UIScoreCount scoreboard;
	private UIShields shieldDisp;
	private boolean ghost; //if the player is in ghost mode
	private int offstage; //how far player can go past the screen's edge before getting hit
	private SpriteEater sprite;
	private boolean nearCookie;
	private double mass;
	
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
		acceleration = .5*calibration_ratio*calibration_ratio;
		max_velocity = 10*calibration_ratio;
		friction = .1*calibration_ratio*calibration_ratio;
		averageStats();
		accel = acceleration*scale;
		maxvel = max_velocity*scale;
		fric = friction*scale;
		score = 0;
		cash = 0;
		shields = 3;
		shielded = false;
		shield_length = (int)(.5+60*(1/calibration_ratio));
		shield_frames = 0;
		shield_tick = true;
		special_length = (int)(.5+60*(1/calibration_ratio));
		special_frames = new ArrayList<Integer>();
		special_cooldown = (int)(.5+180*(1/calibration_ratio));
		special_colors = new ArrayList<Color>();
		special_colors.add(new Color(0,255,255));special_colors.add(new Color(255,0,255));special_colors.add(new Color(255,255,0));
		special_activated = new ArrayList<Boolean>();
		recoil = 10*calibration_ratio;
		state = LIVE;
		powerups = new ArrayList<ArrayList<Item>>();
		summons = new ArrayList<Summon>();
		for(int i=0; i<3; i++) {
			powerups.add(new ArrayList<Item>());
			special_frames.add(0);
			special_activated.add(false);
		}
		currSpecial = -1;

		decayedValue = 0;
		extra_radius = 0;
		ghost = false;
		offstage = 0;
		mass = 100;
		try {
			sprite = new SpriteEater(board,this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	public void setY(double yP) {y=yP;}
	public int getDir() {return direction;}
	public void setDir(int dir) {direction = dir;}
	public int getRadius() {return radius;}
	public void setExtraRadius(int er) {extra_radius=er;}
	public void addExtraRadius(int er) {extra_radius+=er;}
	public int getTotalRadius() {return radius+(int)(.5+extra_radius*scale);}
	public double getMaxVel() {return maxvel;}
	public double getXVel() {return x_velocity;}
	public void setXVel(double a) {x_velocity = a;}
	public double getYVel() {return y_velocity;}
	public void setYVel(double a) {y_velocity = a;}
	public double getMass() {return mass;}
	public void setShielded(boolean s) {shielded = s;shield_tick=!s;}
	public boolean getShielded() {return shielded;}
	public void setGhost(boolean g) {ghost = g;}
	public boolean getGhosted() {return ghost;}
	public boolean getSpecialActivated(int s) {return special_activated.get(s);}
	public void activateSpecials() {
		for(int i=0; i<special_activated.size(); i++) {
			if(special_frames.get(i)==0)
				special_activated.set(i, true);
		}
	}
	public int getState() {return state;}
	public int getScore() {return score;}
	public void addScore(int s) {score+=s;}
	public int getScoreToWin() {return scoreToWin;}
	public void setScoreToWin(int s) {scoreToWin=s;}
	public double getCash() {return cash;}
	public void addCash(double c) {cash+=c;}
	public int getShields() {return shields;}
	public void addShields(int s) {shields+=s;}
	
	
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
		itemDisp.update(true, getItems(),getSpecialFrames(),getSpecialCooldown(),getSpecialLength(),special_activated);
	}
	public ArrayList<ArrayList<Item>> getItems() {return powerups;}
	public void addSummon(Summon s) {summons.add(s);}
	public void removeSummon(Summon s) {summons.remove(s);}
	public ArrayList<Summon> getSummons() {return summons;}
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
	public int getCurrentSpecial() {return currSpecial;}
	
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
		coloration = new Color((int)((friction/calibration_ratio/calibration_ratio-MR[2][0])/MR[2][1]*255),(int)((max_velocity/calibration_ratio-MR[1][0])/MR[1][1]*255),(int)((acceleration/calibration_ratio/calibration_ratio-MR[0][0])/MR[0][1]*255));
	}
	public double getDecayedValue() {return decayedValue;}
	public void setDecayedValue(double dv) {decayedValue=dv;}
	public double getRecoil() {return recoil;}
	public void setRecoil(double r) {recoil = r;}
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
	public int getOffstage() {return offstage;}
	public void setOffstage(int d) {offstage=d;}
	public boolean getNearCookie() {return nearCookie;}
	public void setNearCookie(boolean n) {nearCookie = n;}
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
	//tests if off screen
	public boolean outOfBounds() {
		return x<0-offstage || x>board.X_RESOL+offstage || y<0-offstage || y>board.Y_RESOL+offstage;
	}
	
	//activates special A (all powerups tied to A)
	public void special(int index) {
		if(board.currFloor.specialsEnabled()) {
			if(state!=LIVE || special_frames.get(index)!=0 || direction==NONE || !special_activated.get(index))return;
			state=SPECIAL;
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
		score = 0;
		cash = 0;
		shields = 3;
		//randomizeStats();
		
		decayedValue = 0;
		extra_radius = 0;
		ghost = false;
		offstage = 0;
		averageStats();
		reset();
	}
	//kill, but only if no bounce
	public void killBounce(Wall rect, boolean breakShield) {
		if(!shielded && shields<=0) { //kill if no shields, otherwise bounce
			kill();
			return;
		}else if(breakShield){//only remove shields if not in stun and shield to be broken
			shields--;
		}
		bounce(rect.getX(),rect.getY(),rect.getW(),rect.getH());
		
	}
	//kill, but only if no bounce (on edge)
	public void killBounceEdge(boolean breakShield) {
		if(!shielded && shields<=0) { //kill if no shields, otherwise bounce
			kill();
			return;
		}else if(breakShield){//only remove shields if not in stun and shield to be broken
			shields--;
		}
		if(x<0) {
			bounce(-100-offstage,-100,100-(int)(.5+DEFAULT_RADIUS*scale),board.Y_RESOL+100);
		}else if(x>board.X_RESOL) {
			bounce(board.X_RESOL+(int)(.5+DEFAULT_RADIUS*scale)+offstage,-100,100-(int)(.5+DEFAULT_RADIUS*scale),board.Y_RESOL+1000);
		}else if(y<0) {
			bounce(-100,-100-offstage,board.X_RESOL+100,100-(int)(.5+DEFAULT_RADIUS*scale));
		}else if(y>board.Y_RESOL) {
			bounce(-100,board.Y_RESOL+(int)(.5+DEFAULT_RADIUS*scale)+offstage,board.X_RESOL+100,100-(int)(.5+DEFAULT_RADIUS*scale));
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
		score = 0;
		reset();
	}
	//resets player to floor-beginning state
	public void reset() {
		state = LIVE;
		currSpecial = -1;
		shielded = false;
		shield_frames = 0;
		for(int i=0; i<special_frames.size(); i++)special_frames.set(i,0);
		for(int i=0; i<special_activated.size(); i++)special_activated.set(i,false);
		coloration = new Color((int)((friction/calibration_ratio/calibration_ratio-MR[2][0])/MR[2][1]*255),(int)((max_velocity/calibration_ratio-MR[1][0])/MR[1][1]*255),(int)((acceleration/calibration_ratio/calibration_ratio-MR[0][0])/MR[0][1]*255));
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
	public void bounce(int rx,int ry,int rw,int rh) {
		shielded = true;
		boolean xB=false,yB=false;
		if(y>ry+rh) {
			y_velocity=recoil*scale;
			y+=y_velocity;
			yB=true;
		}else if(y<ry) {
			y_velocity=-recoil*scale;
			y+=y_velocity;
			yB=true;
		}else if(x>rx+rw) {
			x_velocity=recoil*scale;
			x+=x_velocity;
			xB=true;
		}else if(x<rx) {
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
		if(shielded && shield_tick) {
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
		if(score>=scoreToWin) //win if all cookies eaten
			win();
		
		if(outOfBounds()) {
			killBounceEdge(!shielded);
		}
		if(!ghost) {
			for(int i=0; i<board.walls.size(); i++) { //test collision with all walls, die if hit one
				Wall rect = board.walls.get(i);
				if(collidesWithRect(rect.getX(), rect.getY(), rect.getW(), rect.getH())) {
					i=board.walls.size();
					killBounce(rect,!shielded);
				}
			}
		}
	}
	public void initUI() {
		board.draw.addUI(itemDisp = new UIItemsAll(board,50,board.Y_RESOL-50,getSpecialColors()));
		board.draw.addUI(scoreboard = new UIScoreCount(board,board.X_RESOL-170,board.Y_RESOL-100));
		board.draw.addUI(shieldDisp = new UIShields(board,board.X_RESOL-50,90));
	}
	public void updateUI() {
		
		//items
		if(itemDisp==null)initUI();
		itemDisp.update(false, getItems(),getSpecialFrames(),getSpecialCooldown(),getSpecialLength(),special_activated);
		//scoreboard
		scoreboard.update(cash,score,scoreToWin);
		//shields
		shieldDisp.update(shields);
	}
	public void updateUIItems() {
		itemDisp.update(true, getItems(),getSpecialFrames(),getSpecialCooldown(),getSpecialLength(),special_activated);
	}
	
	public void paint(Graphics g) {
		
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
