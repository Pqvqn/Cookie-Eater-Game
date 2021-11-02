 package entities;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
//import java.awt.event.*;

import ce3.*;

import java.io.*;

//import javax.swing.*;

import sprites.*;
import ui.*;
import cookies.*;
import mechanisms.*;

public class Eater extends Entity{
	
	private int id;
	public static final int DEFAULT_RADIUS = 40;
	public static final int CORPSE=-2, NONE=-1, UP=0, RIGHT=1, DOWN=2, LEFT=3;
	private int direction;


	private double[][] mr; //accel min,max; maxvel min,max; fric min,max
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
	public int startx,starty; //where on the board the player entered from
	
	public Eater(Game frame, Board gameboard, int num, int cycletime) {
		super(frame,gameboard,cycletime);
		id = num;
		name = "Player "+id;
		dO= true;
		ded = false;
		x=board.x_resol/2;
		y=board.y_resol/2;
		direction = NONE;
		x_velocity = 0;
		y_velocity = 0;
		radius=DEFAULT_RADIUS;
		coloration = Color.blue.brighter();
		controls = new Controls(game, board, this, id);
		
		mr = CookieStat.MR;
		
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
	public Eater(Game frame, Board gameboard, SaveData sd, int cycle) {
		super(frame,gameboard,sd,cycle);
		id = sd.getInteger("id",0);
		direction = sd.getInteger("direction",0);
		startShields = sd.getInteger("startshields",0);
		state = sd.getInteger("state",0);
		cash = sd.getDouble("cash",0);
		coloration = new Color(sd.getInteger("color",0));
		dO = sd.getBoolean("do",0);
		score = sd.getInteger("score",0);
		scoreToWin = sd.getInteger("score",1);
		
		ArrayList<SaveData> pickup_data = sd.getSaveDataList("pickupstash");
		if(pickup_data!=null) {
			pickups = new ArrayList<CookieItem>();
			for(int i=0; i<pickup_data.size(); i++) {
				pickups.add(new CookieItem(game, board, pickup_data.get(i)));
			}
		}
	
		mr = new double[3][2];
		ArrayList<Object> stats = sd.getData("statranges");
		for(int i=0; i<6; i++) {
			mr[i/2][i%2] = Double.parseDouble(stats.get(i).toString());
		}
		
		for(Segment testPart : parts){
			if(testPart.name.equals("body")) {
				part = (SegmentCircle)testPart;
			}
		}
		controls = new Controls(game, board, this, sd.getSaveDataList("controls").get(0));
		try {
			sprite = new SpriteEater(board,this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public SaveData getSaveData() {
		SaveData data = super.getSaveData();
		data.addData("id",id);
		data.addData("direction",direction);
		data.addData("startshields",startShields);
		data.addData("state",state);
		data.addData("cash",cash);
		data.addData("color",coloration.getRGB());
		data.addData("do",dO);
		data.addData("score",score,0);
		data.addData("score",scoreToWin,1);
		
		for(int i=0; i<6; i++) {
			data.addData("statranges",mr[i/2][i%2],i);
		}
		int j;
		for(j=0; j<pickups.size(); j++) {
			data.addData("pickupstash",pickups.get(j).getSaveData(),j);
		}
		data.addData("pickupstash","null",j);
		data.addData("controls",controls.getSaveData());
		return data;
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
	public void addItem(int index, CookieItem i) {
		super.addItem(index, i);
		itemDisp.update(true, getItems(),getSpecialFrames(),getSpecialCooldown(),getSpecialLength(),special_activated);
	}
	
	public double[][] getMovementRand() {return mr;}
	public void addToMovement(double a, double v, double f) {
		acceleration += a;
		if(acceleration>Math.max(mr[0][0],mr[0][1]))acceleration=Math.max(mr[0][0],mr[0][1]);
		if(acceleration<Math.min(mr[0][0],mr[0][1]))acceleration=Math.min(mr[0][0],mr[0][1]);
		max_velocity += v;
		if(max_velocity>Math.max(mr[1][0],mr[1][1]))max_velocity=Math.max(mr[1][0],mr[1][1]);
		if(max_velocity<Math.min(mr[1][0],mr[1][1]))max_velocity=Math.min(mr[1][0],mr[1][1]);
		friction += f;
		if(friction>Math.max(mr[2][0],mr[2][1]))friction=Math.max(mr[2][0],mr[2][1]);
		if(friction<Math.min(mr[2][0],mr[2][1]))friction=Math.min(mr[2][0],mr[2][1]);
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
			ArrayList<CookieItem> powerups = getPowerups();
			for(int i=0; i<powerups.size(); i++) //stop special
				powerups.get(i).getItem().end(true);
		}
		pickups = new ArrayList<CookieItem>();
		state = DEAD;
		special = false;
		direction = CORPSE;
		game.draw.runUpdate();
		x_velocity = 0;
		y_velocity = 0;
		dO = false;
		lock = true;
		//if levels, reset
		if(board.mode==Board.LEVELS) {
			try {
				Thread.sleep(200); //movement freeze
			}catch(InterruptedException e){};
			board.resetLevels();
			board.resetGame();
			score = 0;
			cash = 0;
			wipeStash();
			setShields(startShields);
			//randomizeStats();
			
			decayed_value = 0;
			extra_radius = 0;
			ghost = false;
			offstage = 60;
			averageStats();
			reset(null);
		}
	}
	
	//go back to previously loaded level
	public void backtrack(Passage p) {
		state = WIN;
		game.draw.runUpdate();
		dO = false;
		if(board.mode == Board.LEVELS) {
			reset(p);
			board.backLevel();
		}
	}
	
	//move to next level
	public void win(Passage p) {
		state = WIN;
		game.draw.runUpdate();
		dO = false;
		try { //movement freeze
			Thread.sleep(200);
		}catch(InterruptedException e){};
		if(board.mode == Board.LEVELS) {
			reset(p);
			board.nextLevel();
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
	public void reset(Passage p) {
		state = LIVE;
		special = false;
		currSpecial = -1;
		shielded = false;
		shield_frames = 0;
		if(special) {
			ArrayList<CookieItem> powerups = getPowerups();
			for(int i=0; i<powerups.size(); i++) //stop special
				powerups.get(i).getItem().end(true);
		}
		for(int i=0; i<special_frames.size(); i++)special_frames.set(i,0.0);
		for(int i=0; i<special_activated.size(); i++)special_activated.set(i,false);
		colorize();
		x_velocity=0;
		y_velocity=0;
		if(board.mode == Board.LEVELS) {
			if(p!=null) {
				int[] coords = p.oppositeCoordinates();
				x = coords[0] + (board.x_resol/2-coords[0])/15;
				y = coords[1] + (board.y_resol/2-coords[1])/15;
			}else {
				x = board.x_resol/2;
				y = board.y_resol/2;
			}
			startx = (int)x;
			starty = (int)y;
		}else if (board.mode==Board.PVP) {
			x = board.currLevel.getStarts()[id][0];
			y = board.currLevel.getStarts()[id][1];
		}
		scale = board.currLevel.getScale();
		calibrateStats();
		radius = DEFAULT_RADIUS;
		dO = true;
		lock = false;
		direction = NONE;
		score = 0;
		if(parts.isEmpty())buildBody();
		orientParts();
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
		offstage = 60;
		averageStats();
		reset(null);
	}
	
	//gives the player a random set of movement stats and colors accordingly
	public void randomizeStats() {
		acceleration = Math.random()*(mr[0][1]-mr[0][0])+mr[0][0];
		max_velocity = Math.random()*(mr[1][1]-mr[1][0])+mr[1][0];
		friction = Math.random()*(mr[2][1]-mr[2][0])+mr[2][0];
		colorize();
		calibrateStats();
	}
	//gives player average of each stat
	public void averageStats() {
		acceleration=(mr[0][1]+mr[0][0])/2;
		max_velocity=(mr[1][1]+mr[1][0])/2;
		friction=(mr[2][1]+mr[2][0])/2;
		colorize();
		calibrateStats();
	}

	//creates player color based on stats
	public void colorize() {
		coloration = new Color((int)((friction-mr[2][0])/(mr[2][1]-mr[2][0])*255),(int)((max_velocity-mr[1][0])/(mr[1][1]-mr[1][0])*255),(int)((acceleration-mr[0][0])/(mr[0][1]-mr[0][0])*255));
	}
	public void buildBody() {
		parts.add(part = new SegmentCircle(board,this,x,y,radius,0,"body"));
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
				win(null);
			}
		}
		
		//if(score>=scoreToWin&&board.mode==Board.LEVELS) //win if all cookies eaten
		//	win();

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
			game.draw.addUI(itemDisp = new UIItemsAll(game,50,board.y_resol-50,3,2,getSpecialColors()));
			game.draw.addUI(scoreboard = new UIScoreCount(game,board.x_resol-170,board.y_resol-100));
			game.draw.addUI(shieldDisp = new UIShields(game,board.x_resol-80,90+60*id,3));
		}else if(board.mode == Board.PVP) {
			game.draw.addUI(itemDisp = new UIItemsAll(game,
					(id==1||id==2)?50:board.x_resol-250,(id==1||id==3)?150:board.y_resol-50,
					1,id,getSpecialColors()));
			game.draw.addUI(shieldDisp = new UIShields(game,(id==1||id==2)?350:board.x_resol-350,(id==1||id==3)?130:board.y_resol-70,id));
			game.draw.addUI(scoreboard = new UIScoreCount(game,(id==1||id==2)?50:board.x_resol-100,(id==1||id==3)?200:board.y_resol-200));
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
