package ce3;

//import java.awt.*;
//import java.awt.event.*;

import javax.swing.*;

import cookies.*;
import items.*;
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
	public final int BORDER_THICKNESS = 20;
	public int score, scoreToWin; //cookies eaten and amount of cookies on board
	public double cash; //cookies to spend
	public int shields; //shields owned
	private final Level[] FLOOR_SEQUENCE = {new FloorEntrance(this), new Store1(this), new Floor2(this), new Floor3(this), new Floor4(this), new FloorBiggy(this)}; //order of floors
	private LinkedList<Level> floors;
	public Level currFloor;
	private long lastFrame; //time of last frame
	private UIFpsCount fps;
	private UIScoreCount scoreboard;
	private UIShields shieldDisp;
	private UIItemsAll itemDisp;
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
		draw.addUI(scoreboard = new UIScoreCount(this,X_RESOL-170,Y_RESOL-100));
		draw.addUI(shieldDisp = new UIShields(this,X_RESOL-50,90));
		draw.addUI(itemDisp = new UIItemsAll(this,50,Y_RESOL-50,player.getSpecialColors()));
		
		
		//run the game
		while(true)
			run(cycletime);
	}
	
	public int getCycle() {return cycletime;}
	public int getAdjustedCycle() {return true_cycle;}
	public void setCycle(int tim) {cycletime=tim;}
	
	
	//go back to first level
	public void resetGame() {
		//player.addItem(0, new ItemShield(this));
		//player.addItem(2, new ItemCircle(this));
		//player.addItem(1, new ItemHold(this));
		//player.addItem(1, new ItemBoost(this));
		//player.addItem(1, new ItemCookieChain(this));
		//player.addItem(0, new ItemRecycle(this));
		//player.addItem(0, new ItemSlowmo(this));
		//player.addItem(0, new ItemBounce(this));
		player.addItem(0, new ItemField(this));
		
		
		double pcash = cash;
		for(int i=0; i<cookies.size(); i++) {
			cookies.get(i).kill();
		}
		cash=pcash;
		walls = new ArrayList<Wall>();
		currFloor = floors.getLast();
		score = 0;
		cash = 0;
		shields = 1;
		buildBoard();
		
		cookies = new ArrayList<Cookie>();
		makeCookies();
		draw.updateBG();
	}
			
	//advances level
	public void nextLevel() {
		double pcash = cash;
		for(int i=0; i<cookies.size(); i++) {
			cookies.get(i).kill();
		}
		cash=pcash;
		walls = new ArrayList<Wall>();
		//shields+=cash/currFloor.getShieldCost();
		//cash=cash%currFloor.getShieldCost();
		currFloor=currFloor.getNext();
		score = 0;
		buildBoard();
		cookies = new ArrayList<Cookie>();
		makeCookies();
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
		//scoreboard
		scoreboard.update(cash,score,scoreToWin);
		//shields
		shieldDisp.update(shields);
		//items
		itemDisp.update(player.getItems(),player.getSpecialFrames(),player.getSpecialCooldown(),player.getSpecialLength());

	}
	
	//create walls
	public void buildBoard() {
		currFloor.build();
	}
	
	//add cookies to board
	public void makeCookies() {
		currFloor.placeCookies();
	}
	
	


}
