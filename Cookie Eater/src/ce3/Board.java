package ce3;

//import java.awt.*;
//import java.awt.event.*;

import javax.swing.*;

import cookies.*;
import enemies.*;
import levels.*;
import ui.*;

import java.awt.*;
import java.util.*;

public class Board extends JFrame{


	private static final long serialVersionUID = 1L;
	private Controls keyListener;
	public final int Y_RESOL = 1020, X_RESOL = 1920; //board dimensions
	public Eater player;
	public Draw draw;
	public ArrayList<Cookie> cookies;
	public ArrayList<Wall> walls;
	public ArrayList<Enemy> enemies;
	public final int BORDER_THICKNESS = 20;

	private final Level[] FLOOR_SEQUENCE = {new Store1(this),new Floor1(this),new Floor5(this),
			new Store2(this),new Floor2(this),new Floor2(this),
			new Store3(this),new Floor3(this),new Floor3(this),new Floor3(this), 
			new Store4(this),new Floor4(this),new Floor4(this),new Floor4(this),new Floor4(this),
			new FloorBiggy(this)}; //order of floors
	private LinkedList<Level> floors;
	public Level currFloor;
	private long lastFrame; //time of last frame
	private UIFpsCount fps;
	private int cycletime;
	private int fpscheck;
	private int true_cycle;
	
	public Board() {
		super("Cookie Eater");
		cycletime=5;
		fpscheck=100;
		//initializing classes
		
		player = new Eater(this,cycletime);
		draw = new Draw(this);
		
		cookies = new ArrayList<Cookie>();
		walls = new ArrayList<Wall>();
		enemies = new ArrayList<Enemy>();
		
		keyListener = new Controls(this);
		
		//window settings
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setUndecorated(false);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setVisible(true);
		setFocusable(true);
		addKeyListener(keyListener);
		requestFocus();
		//setBackground(Color.GRAY);
		//setForeground(Color.GRAY);
		
		add(draw);
		pack();
		
		//converting list of floors to linked list
		floors = new LinkedList<Level>();
		for(int i=FLOOR_SEQUENCE.length-1; i>=0; i--) {
			floors.add(FLOOR_SEQUENCE[i]);
			if(i<FLOOR_SEQUENCE.length-1) 
				FLOOR_SEQUENCE[i].setNext(FLOOR_SEQUENCE[i+1]);
		}
		
		//create floor 1
		resetGame();
		
		
		//ui
		draw.addUI(fps = new UIFpsCount(this,10,10,Color.WHITE));	
		
		//run the game
		while(true)
			run(cycletime);
	}
	
	public int getCycle() {return cycletime;}
	public int getAdjustedCycle() {return true_cycle;}
	public void setCycle(int tim) {cycletime=tim;}
	
	
	//go back to first level
	public void resetGame() {
		
		for(int i=0; i<cookies.size(); i++) {
			cookies.get(i).kill(false);
			i--;
		}
		enemies = new ArrayList<Enemy>();
		walls = new ArrayList<Wall>();
		currFloor = floors.getLast();
		buildBoard();
		
		cookies = new ArrayList<Cookie>();
		makeCookies();
		spawnEnemies();
		draw.updateBG();
	}
			
	//advances level
	public void nextLevel() {
		for(int i=0; i<cookies.size(); i++) {
			cookies.get(i).kill(false);
			i--;
		}
		enemies = new ArrayList<Enemy>();
		walls = new ArrayList<Wall>();
		//shields+=cash/currFloor.getShieldCost();
		//cash=cash%currFloor.getShieldCost();
		currFloor=currFloor.getNext();
		buildBoard();
		cookies = new ArrayList<Cookie>();
		makeCookies();
		spawnEnemies();
		draw.updateBG();
	}
		
	public void run(int time) {
		updateUI();
		draw.runUpdate(); //update all game objects
		try {
			Thread.sleep(time); //time between updates
		}catch(InterruptedException e){};
	}
	
	public void updateUI() {
		//fps counter
		if(fpscheck--<=0) {
			fps.update(lastFrame,System.currentTimeMillis());
			true_cycle=(int)(System.currentTimeMillis()-lastFrame)/100; 
			player.setCalibration(true_cycle/15.0); //give player more acurrate cycle time
			lastFrame = System.currentTimeMillis();
			fpscheck=100;
		}
		player.updateUI();

	}
	
	//create walls
	public void buildBoard() {
		currFloor.build();
	}
	
	//add cookies to board
	public void makeCookies() {
		currFloor.placeCookies();
	}
	
	//add enemies to board
	public void spawnEnemies() {
		currFloor.spawnEnemies();
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
	


}
