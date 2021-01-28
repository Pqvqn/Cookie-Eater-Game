 package entities;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
//import java.awt.event.*;

import ce3.*;

import java.io.*;

import items.*;

//import javax.swing.*;

import sprites.*;
import ui.*;
import cookies.*;

public class Eater extends Entity{
	
	private int id;
	public static final int DEFAULT_RADIUS = 40;
	public static final int CORPSE=-2, NONE=-1, UP=0, RIGHT=1, DOWN=2, LEFT=3;
	private int direction;


	private double[][] MR = {{.2,1},{5,15},{.05,.3}}; //accel min,max-min; maxvel min,max-min; fric min,max-min
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
	public Controls controls; //covers inputs
	private int startShields;
	
	public Eater(Game frame, Board gameboard, int num, int cycletime) {
		super(frame,gameboard,cycletime);
		id = num;
		name = "Player "+id;
		dO= true;
		ded = false;
		x=board.X_RESOL/2;
		y=board.Y_RESOL/2;
		direction = NONE;
		x_velocity = 0;
		y_velocity = 0;
		radius=DEFAULT_RADIUS;
		coloration = Color.blue.brighter();
		controls = new Controls(game, board, this, id);
		
		averageStats();
		
		score = 0;
		cash = 0;
		pickups = new ArrayList<CookieItem>();
		startShields = 3;
		addShields(startShields);
		shielded = false;
		
		state = LIVE;
		
		//set special colors to be unique in pvp
		if(board.mode == Board.PVP) {
			Color[] specialColorOrder = {Color.CYAN,Color.MAGENTA,Color.YELLOW,Color.GREEN};
			special_colors.set(0,specialColorOrder[id]);
		}
		
		extra_radius = 0;
		ghost = false;
		mass = 200;
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
	
	public double[][] getMovementRand() {return MR;}
	public void addToMovement(double a, double v, double f) {
		acceleration += a;
		if(acceleration>(MR[0][0]+MR[0][1]))acceleration=(MR[0][0]+MR[0][1]);
		if(acceleration<(MR[0][0]))acceleration=(MR[0][0]);
		max_velocity += v;
		if(max_velocity>(MR[1][0]+MR[1][1]))max_velocity=(MR[1][0]+MR[1][1]);
		if(max_velocity<(MR[1][0]))max_velocity=(MR[1][0]);
		friction += f;
		if(friction>(MR[2][0]+MR[2][1]))friction=(MR[2][0]+MR[2][1]);
		if(friction<(MR[2][0]))friction=(MR[2][0]);
		colorize();
		calibrateStats();
	}
	public Color getColor() {return coloration;}
	public void spend(double amount) {
		addCash(-amount);
		removeCookies(amount);
	}
	public void pay(double amount) {
		addCash(amount);
		addCookies(amount);
	}
	public void payCookies(Entity recipient, double num) {
		addCash(-num);
		while(num>0 && !cash_stash.isEmpty()) {
			Cookie chosen = cash_stash.get(0);
			if(chosen.getValue()>num) {
				chosen.setValue(chosen.getValue()-num);
				num = 0;
			}else {
				num -= chosen.getValue();
				cash_stash.remove(chosen);
				recipient.giveCookie(chosen);
			}

		}
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
		direction = CORPSE;
		game.draw.runUpdate();
		x_velocity = 0;
		y_velocity = 0;
		dO = false;
		//if levels, reset
		if(board.mode==Board.LEVELS) {
			try {
				Thread.sleep(200); //movement freeze
			}catch(InterruptedException e){};
			board.resetGame();
			score = 0;
			cash = 0;
			wipeStash();
			setShields(startShields);
			//randomizeStats();
			
			decayed_value = 0;
			extra_radius = 0;
			ghost = false;
			offstage = 0;
			averageStats();
			reset();
		}
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
		game.draw.runUpdate();
		x_velocity = 0;
		y_velocity = 0;
		dO = false;
		try { //movement freeze
			Thread.sleep(200);
		}catch(InterruptedException e){};
		if(board.mode == Board.LEVELS) {
			board.nextLevel();
			score = 0;
			reset();
		}else if(board.mode == Board.PVP) {
			try { //movement freeze
				Thread.sleep(200);
			}catch(InterruptedException e){};
			for(Eater e : board.players) {
				e.revive();
			}
			board.resetGame();
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
		colorize();
		x_velocity=0;
		y_velocity=0;
		if(board.mode == Board.LEVELS) {
			x = board.currFloor.getStartX();
			y = board.currFloor.getStartY();
		}else if (board.mode==Board.PVP) {
			x = board.currFloor.getStarts()[id][0];
			y = board.currFloor.getStarts()[id][1];
		}
		scale = board.currFloor.getScale();
		calibrateStats();
		radius = DEFAULT_RADIUS;
		dO = true;
		direction = NONE;
		score = 0;
	}
	//resets killed players
	public void revive() {
		score = 0;
		cash = 0;
		setShields(startShields);
		//randomizeStats();
		direction = NONE;
		
		decayed_value = 0;
		extra_radius = 0;
		ghost = false;
		offstage = 0;
		averageStats();
		reset();
	}
	
	//gives the player a random set of movement stats and colors accordingly
	public void randomizeStats() {
		acceleration = Math.random()*MR[0][1]+MR[0][0];
		max_velocity = Math.random()*MR[1][1]+MR[1][0];
		friction = Math.random()*MR[2][1]+MR[2][0];
		colorize();
		calibrateStats();
	}
	//gives player average of each stat
	public void averageStats() {
		acceleration=MR[0][1]/2+MR[0][0];
		max_velocity=MR[1][1]/2+MR[1][0];
		friction=MR[2][1]/2+MR[2][0];
		colorize();
		calibrateStats();
	}

	//creates player color based on stats
	public void colorize() {
		coloration = new Color((int)((friction-MR[2][0])/MR[2][1]*255),(int)((max_velocity-MR[1][0])/MR[1][1]*255),(int)((acceleration-MR[0][0])/MR[0][1]*255));
	}
	public void buildBody() {
		parts.add(part = new SegmentCircle(board,this,x,y,radius,0));
	}
	public void orientParts() {
		part.setLocation(x,y);
		part.setSize(radius);
		super.orientParts();
	}
	public void runUpdate() {
		super.runUpdate();
		if(parts.isEmpty())buildBody();
		if(state == DEAD) { //if dead in multiplayer
			orientParts(); //orient parts to keep colliding
			//lock = true;
		}
		if(!dO)return; //if paused
		if(board.mode == Board.PVP) {
			boolean allDead = true;
			for(Eater e : board.players) { //check if any players aren't dead or winning
				if(!e.equals(this) && e.getState() != DEAD) {
					allDead = false;
				}
			}
			if(allDead) { //win if last man standing
				//lock = true;
				win();
			}
		}
		
		if(score>=scoreToWin&&board.mode==Board.LEVELS) //win if all cookies eaten
			win();

		orientParts();
	}
	public void doMovement() {
		if(!lock) {
			switch(direction) {
				case UP: //if up
					if(y_velocity>-maxvel) //if below speed cap
						y_velocity-=accel; //increase speed upward
						y_velocity/=fric;
					break;
				case RIGHT:
					if(x_velocity<maxvel)
						x_velocity+=accel;
						x_velocity/=fric;
					break;
				case DOWN:
					if(y_velocity<maxvel)
						y_velocity+=accel;
						y_velocity/=fric;
					break;
				case LEFT:
					if(x_velocity>-maxvel)
						x_velocity-=accel;
						x_velocity/=fric;
					break;
			}
		}
		super.doMovement();
	}
	
	public void initUI() {
		if(board.mode == Board.LEVELS) {
			game.draw.addUI(itemDisp = new UIItemsAll(game,50,board.Y_RESOL-50,3,2,getSpecialColors()));
			game.draw.addUI(scoreboard = new UIScoreCount(game,board.X_RESOL-170,board.Y_RESOL-100));
			game.draw.addUI(shieldDisp = new UIShields(game,board.X_RESOL-80,90+60*id,3));
		}else if(board.mode == Board.PVP) {
			game.draw.addUI(itemDisp = new UIItemsAll(game,
					(id==1||id==2)?50:board.X_RESOL-250,(id==1||id==3)?150:board.Y_RESOL-50,
					1,id,getSpecialColors()));
			game.draw.addUI(shieldDisp = new UIShields(game,(id==1||id==2)?350:board.X_RESOL-350,(id==1||id==3)?130:board.Y_RESOL-70,id));
			game.draw.addUI(scoreboard = new UIScoreCount(game,(id==1||id==2)?50:board.X_RESOL-100,(id==1||id==3)?200:board.Y_RESOL-200));
		}
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
	public int getID() {return id;}
	
	public void paint(Graphics g) {
		if(part!=null)part.update();
		Graphics2D g2 = (Graphics2D)g;
		AffineTransform origt = g2.getTransform();
		for(int i=0; i<summons.size(); i++) { //draw summons
			summons.get(i).paint(g2);
			g2.setTransform(origt);
		}
		
		//sprite
		sprite.setColor(coloration);
		sprite.paint(g);
		

		
	}
}
