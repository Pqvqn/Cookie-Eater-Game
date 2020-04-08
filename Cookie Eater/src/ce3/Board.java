package ce3;

import java.awt.*;
//import java.awt.event.*;

import javax.swing.*;

import levels.*;

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
	private final Level[] FLOOR_SEQUENCE = {new FloorEntrance(this,null), new Floor2(this,null)}; //order of floors
	private LinkedList<Level> floors;
	public Level currFloor;
	
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
		setBackground(Color.GRAY);
		setForeground(Color.GRAY);
		
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
		score = 0;
		scoreToWin = 20;
		makeCookies();
		
		//run the game
		while(true)
			run(15);
	}
	
	public void run(int time) {
		draw.runUpdate(); //update all game objects
		try {
			Thread.sleep(time); //time between updates
		}catch(InterruptedException e){};
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
	}
	
	//go back to first level
	public void resetLevel() {
		walls = new ArrayList<Wall>();
		currFloor = floors.getLast();
		score = 0;
		buildBoard();
		cookies = new ArrayList<Cookie>();
		makeCookies();
	}


}
