package ce3;

//import java.awt.*;
//import java.awt.event.*;

import javax.swing.*;

import cookies.*;
import entities.*;
import levels.*;
import ui.*;
import menus.*;
import menus.Menu;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

public class Board extends JFrame{


	private static final long serialVersionUID = 1L;
	public int mode;
	public final int Y_RESOL = 1020, X_RESOL = 1920; //board dimensions
	public Eater player;
	public Draw draw;
	public ArrayList<Cookie> cookies;
	public ArrayList<Wall> walls;
	public Area wallSpace;
	public ArrayList<Effect> effects;
	public ArrayList<Enemy> enemies;
	public ArrayList<Eater> players;
	public ArrayList<Explorer> npcs;
	public ArrayList<Explorer> present_npcs; //npcs that exist on current level
	public ArrayList<Controls> controls;
	public ArrayList<Menu> menus;
	public final int BORDER_THICKNESS = 20;

	private final Level[] FLOOR_SEQUENCE = {new Store1(this),new Floor1(this),
			new Store2(this),new Floor2(this),new Floor2(this),
			new Store3(this),new Floor3(this),new Floor3(this),new Floor3(this), 
			new Store4(this),new Floor4(this),new Floor4(this),new Floor4(this),new Floor4(this),new Floor5(this)}; //order of floors
			//new FloorBiggy(this)}; 
	public LinkedList<Level> floors;
	public Level currFloor;
	private long lastFrame; //time of last frame
	public UIFpsCount fps;
	private UILevelInfo lvl;
	private UIDialogue dia;
	private int cycletime;
	private int fpscheck;
	private int true_cycle;
	private int skipframes;
	public int playerCount;
	public boolean check_calibration;
	
	public Board(int m) {
		super("Cookie Eater");
		mode = m;
		cycletime=5;
		fpscheck=100;
		skipframes = 0;
		check_calibration = true;
		//initializing classes
		players = new ArrayList<Eater>();
		playerCount = 4;
		if(mode==Main.LEVELS) {
			players.add(player = new Eater(this,0,cycletime));
		}else if(mode==Main.PVP) { //add number of players
			for(int i=0; i<playerCount; i++)
				players.add(new Eater(this,i,cycletime));
		}
		draw = new Draw(this);
		
		cookies = new ArrayList<Cookie>();
		walls = new ArrayList<Wall>();
		wallSpace = new Area();
		enemies = new ArrayList<Enemy>();
		npcs = new ArrayList<Explorer>();
		present_npcs = new ArrayList<Explorer>();
		effects = new ArrayList<Effect>();
		
		controls = new ArrayList<Controls>();
		for(int i=0; i<players.size(); i++) {
			controls.add(new Controls(this,players.get(i),i));
		}
		menus = new ArrayList<Menu>();
		
		//window settings
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setUndecorated(false);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setVisible(true);
		setFocusable(true);
		for(int i=0; i<controls.size(); i++) {
			addKeyListener(controls.get(i));
		}
		requestFocus();
		setSize(Toolkit.getDefaultToolkit().getScreenSize());
		//setBackground(Color.GRAY);
		//setForeground(Color.GRAY);
		
		add(draw);
		pack();
		
		//converting list of floors to linked list
		floors = new LinkedList<Level>();
		if(mode==Main.LEVELS) {
			for(int i=FLOOR_SEQUENCE.length-1; i>=0; i--) {
				floors.add(FLOOR_SEQUENCE[i]);
				if(i<FLOOR_SEQUENCE.length-1) 
					FLOOR_SEQUENCE[i].setNext(FLOOR_SEQUENCE[i+1]);
			}
		}else if(mode==Main.PVP) {
			floors.add(new Floor1(this));
		}
		
		//create all of this game's npcs
		createNpcs(cycletime);
		//create floor 1
		resetGame();

		
		
		//ui
		draw.addUI(fps = new UIFpsCount(this,10,10,Color.WHITE));	
		draw.addUI(lvl = new UILevelInfo(this,X_RESOL/2,30));	
		
		//run the game
		while(true)
			run(cycletime);
	}
	
	public int getCycle() {return cycletime;}
	public double getAdjustedCycle() {return true_cycle/100.0;}
	public void setCycle(int tim) {cycletime=tim;}
	
	
	//go back to first level
	public void resetGame() {
		currFloor = floors.getLast();
		currFloor.removeNpcs();
		for(int i=0; i<cookies.size(); i++) {
			cookies.get(i).kill(null);
			i--;
		}
		enemies = new ArrayList<Enemy>();
		walls = new ArrayList<Wall>();
		effects = new ArrayList<Effect>();

		buildBoard();
		
		for(int i=0; i<players.size(); i++)
			players.get(i).reset();
		
		for(int i=0; i<npcs.size(); i++)
			npcs.get(i).runEnds();
		
		cookies = new ArrayList<Cookie>();
		if(mode==Main.LEVELS) {
		makeCookies();}
		spawnEnemies();
		for(int i=0; i<npcs.size(); i++) {
			if(npcs.get(i).getResidence().equals(currFloor)) {
				present_npcs.add(npcs.get(i));
				npcs.get(i).spawn();
			}else if(present_npcs.contains(npcs.get(i))) {
				present_npcs.remove(npcs.get(i));
			}
		}
		setDialogue(null,null);
		spawnNpcs();
	}
			
	//advances level
	public void nextLevel() {
		for(int i=0; i<present_npcs.size(); i++) {
			present_npcs.get(i).levelComplete();
		}
		currFloor.removeNpcs();
		for(int i=0; i<cookies.size(); i++) {
			cookies.get(i).kill(null);
			i--;
		}
		enemies = new ArrayList<Enemy>();
		walls = new ArrayList<Wall>();
		effects = new ArrayList<Effect>();
		//shields+=cash/currFloor.getShieldCost();
		//cash=cash%currFloor.getShieldCost();
		currFloor=currFloor.getNext();
		buildBoard();
		cookies = new ArrayList<Cookie>();
		makeCookies();
		spawnEnemies();
		present_npcs = new ArrayList<Explorer>();
		for(int i=0; i<npcs.size(); i++) {
			if(npcs.get(i).getResidence().equals(currFloor)) {
				present_npcs.add(npcs.get(i));
				npcs.get(i).spawn();
			}
		}
		setDialogue(null,null);
		spawnNpcs();
	}
		
	public void run(int time) {
		if(skipframes>0) {
			skipframes--;
		}else {
			updateUI();
			draw.runUpdate(); //update all game objects
		}
		try {
			Thread.sleep(time); //time between updates
		}catch(InterruptedException e){};
	}
	public void freeze(int time) { //freeze-frame for length of time
		time/=getAdjustedCycle();
		skipframes+=time;
	}
	
	public void setCalibrations(double cycle) {
		for(int i=0; i<players.size(); i++) {
			players.get(i).setCalibration(cycle/15.0); //give player more accurate cycle time
		}
		for(int i=0; i<enemies.size(); i++) {
			enemies.get(i).setCalibration(cycle); //give enemies more accurate cycle time
		}
		for(int i=0; i<effects.size(); i++) {
			effects.get(i).setCalibration(cycle); //give enemies more accurate cycle time
		}
		for(int i=0; i<present_npcs.size(); i++) {
			present_npcs.get(i).setCalibration(cycle/15.0); //give npcs more accurate cycle time
		}
		for(int i=0; i<cookies.size(); i++) {
			cookies.get(i).setCalibration(cycle); //give cookies more accurate cycle time
		}
	}
	public void updateUI() {
		//fps counter
		if(fpscheck--<=0) {
			//fps.update(lastFrame,System.currentTimeMillis());
			true_cycle=(int)(System.currentTimeMillis()-lastFrame); 
			if(check_calibration){
				setCalibrations(getAdjustedCycle());
			}
			lastFrame = System.currentTimeMillis();
			fpscheck=100;
		}
		//level display
		lvl.update(currFloor.getName());
		
		//dialogue
		if(dia!=null)dia.update();
		
		//player's ui
		for(int i=0; i<players.size(); i++) {
			players.get(i).updateUI();
		}

	}
	
	//create walls
	public void buildBoard() {
		currFloor.build();
		draw.updateBG();
		wallSpace = new Area();
		for(Wall w : walls) {
			wallSpace.add(new Area(new Rectangle(w.getX(),w.getY(),w.getW(),w.getH())));
		}
	}
	
	//add cookies to board
	public void makeCookies() {
		currFloor.placeCookies();
	}
	
	//add enemies to board
	public void spawnEnemies() {
		currFloor.spawnEnemies();
	}
	
	//add enemies to board
	public void spawnNpcs() {
		currFloor.spawnNpcs();
	}
	
	//creates all the non-player characters and puts them in their starting levels
	public void createNpcs(int cycle) {
		npcs.add(new ExplorerMechanic(this,cycletime));
		npcs.add(new ExplorerShopkeep(this,cycletime));
		npcs.add(new ExplorerVendor(this,cycletime));
		npcs.add(new ExplorerSidekick(this,cycletime));
		npcs.add(new ExplorerMystery(this,cycletime));
		for(int i=0; i<npcs.size(); i++) {
			npcs.get(i).chooseResidence();
			npcs.get(i).createStash();
		}
	}
	
	//returns nearest cookie to a given point on the board
	public Cookie nearestCookie(double x, double y) {
		double bestDist = Integer.MAX_VALUE;
		Cookie save = null;
		for(int i=0; i<cookies.size(); i++) {
			if(cookies.get(i)!=null){
				double thisDist = Level.lineLength(cookies.get(i).getX(),cookies.get(i).getY(),x,y);
				if(thisDist<bestDist&&thisDist!=0) {
					save = cookies.get(i);
					bestDist = thisDist;
				}
			}
		}
		return save;
	}
	
	public void setDialogue(Entity speaker, Conversation convo) { //sets the current dialogue
		draw.removeUI(dia);
		if(convo==null) {
			dia = null;
		}else {
			dia = new UIDialogue(this,convo.currentLine(),convo.getOptions());
			draw.addUI(dia);
		}
	}
	public boolean inConvo() { //if player is in dialogue
		return dia != null;
	}

}
