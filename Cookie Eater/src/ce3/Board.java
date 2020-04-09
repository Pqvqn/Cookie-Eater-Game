package ce3;

//import java.awt.*;
//import java.awt.event.*;

import javax.swing.*;

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
	public int cash; //cookies to spend
	public int shields; //shields owned
	private final Level[] FLOOR_SEQUENCE = {new FloorEntrance(this), new Floor2(this), new Floor3(this), new Floor4(this)}; //order of floors
	private LinkedList<Level> floors;
	public Level currFloor;
	private long lastFrame; //time of last frame
	private UIFpsCount fps;
	private UIScoreCount scoreboard;
	private UIShields shieldDisp;
	
	public Board() {
		super("Cookie Eater");
	
		//initializing classes
		player = new Eater(this);
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
		currFloor = floors.getLast(); //set pointer
		
		//create floor 1
		buildBoard();
		shields = 3;
		score = 0;
		scoreToWin = 20;
		makeCookies();
		
		//ui
		draw.addUI(fps = new UIFpsCount(this,10,10,Color.WHITE));
		draw.addUI(scoreboard = new UIScoreCount(this,X_RESOL-150,Y_RESOL-100));
		draw.addUI(shieldDisp = new UIShields(this,X_RESOL-150,100));
		
		//run the game
		while(true)
			run(15);
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
		fps.update(lastFrame,System.currentTimeMillis());
		lastFrame = System.currentTimeMillis();
		//scoreboard
		scoreboard.update(cash,score,scoreToWin);
		//shields
		shieldDisp.update(shields);
	}
	
	//create walls
	public void buildBoard() {
		currFloor.build();
	}
	
	//add cookies to board
	public void makeCookies() {
		currFloor.placeCookies();
	}
	
	//advances level
	public void nextLevel() {
		walls = new ArrayList<Wall>();
		currFloor=currFloor.getNext();
		score = 0;
		buildBoard();
		cookies = new ArrayList<Cookie>();
		makeCookies();
		draw.updateBG();
	}
	
	//go back to first level
	public void resetLevel() {
		walls = new ArrayList<Wall>();
		currFloor = floors.getLast();
		score = 0;
		cash = 0;
		shields = 3;
		buildBoard();
		cookies = new ArrayList<Cookie>();
		makeCookies();
		draw.updateBG();
	}


}
