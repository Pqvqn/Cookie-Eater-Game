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
	private double acceleration = .5; //added to dimensional speed depending on direction
	private double max_velocity = 10; //cap on dimensional speed
	private double friction = .1; //removed from dimensional speed
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
	private int special_frames; //counting how deep into shield
	private int special_cooldown; //frames between uses of special
	private double recoil; //recoil speed from hit
	private final int LIVE = 0, DEAD =-1, WIN = 1, SPECIALA = 2; //states
	private int state;
	private ArrayList<Item> powerups;
	boolean lock; //if player can move
	
	private Board board;
	
	public Eater(Board frame) {
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
		accel = acceleration*scale;
		maxvel = max_velocity*scale;
		fric = friction*scale;
		shielded = false;
		shield_length = 60;
		shield_frames = 0;
		special_length = 60;
		special_frames = 0;
		special_cooldown = 60;
		recoil = 10;
		state = LIVE;
		powerups = new ArrayList<Item>();
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
	
	
	public void addItem(Item i) {powerups.add(i);}
	public ArrayList<Item> getItems() {return powerups;}
	public void lockControl(boolean l) {lock = l;}
	public double getFriction() {return friction;}
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
	public void specialA() {
		if(special_frames>special_length || direction==NONE)return;
		for(int i=0; i<powerups.size(); i++) {
			powerups.get(i).initialize();
		}
		state=SPECIALA;
		//special_frames=0;
	}
	//reset back to first level
	public void kill() {
		//coloration = Color.black;
		for(int i=0; i<powerups.size(); i++) //stop special
			powerups.get(i).end(true);
		powerups = new ArrayList<Item>();
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
		for(int i=0; i<powerups.size(); i++) //stop special
			powerups.get(i).end(true);
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
		special_frames = 0;
		coloration = new Color((int)((friction-.05)/.25*255),(int)((max_velocity-5)/15*255),(int)((acceleration-.2)/1*255));
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
		if(y>w.getY()+w.getH()) {
			y_velocity=recoil*scale;
			y+=y_velocity;
		}else if(y<w.getY()) {
			y_velocity=-recoil*scale;
			y+=y_velocity;
		}else if(x>w.getX()+w.getW()) {
			x_velocity=recoil*scale;
			x+=x_velocity;
		}else if(x<w.getX()) {
			x_velocity=-recoil*scale;
			x+=x_velocity;
		}
	}
	//gives the player a random set of movement stats and colors accordingly
	public void randomizeStats() {
		acceleration = Math.random()*1+.2;
		max_velocity = Math.random()*15+5;
		friction = Math.random()*.25+.05;
		coloration = new Color((int)((friction-.05)/.25*255),(int)((max_velocity-5)/15*255),(int)((acceleration-.2)/1*255));
	}
	
	public void runUpdate() {
		if(!dO)return; //if paused
		if(state == SPECIALA) {
			for(int i=0; i<powerups.size(); i++) {
				powerups.get(i).execute();
			}
			if(special_frames++>special_length) {
				state = LIVE;
				for(int i=0; i<powerups.size(); i++) {
					powerups.get(i).end(false);
				}
			}
		}
		if(special_frames>special_length) {
			special_frames++;
			if(special_frames>special_length+special_cooldown) {
				special_frames=0;
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
		}else if(state==SPECIALA) {
			g.setColor(new Color(coloration.getRed(),coloration.getGreen(),coloration.getBlue(),100));
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
