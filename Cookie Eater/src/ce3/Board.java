package ce3;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import java.util.*;

public class Board extends JFrame implements ActionListener{

	//private JPanel board;
	private Controls keyListener;
	public final int Y_RESOL = 1020, X_RESOL = 1920;
	public Eater player;
	public Draw draw;
	public ArrayList<Cookie> cookies;
	public ArrayList<Wall> walls;
	public final int BORDER_THICKNESS = 20;
	public int score, scoreToWin;
	private LinkedList<Level> floors;
	public Level currFloor;
	
	public Board() {
		super("Cookie Eater");
	
		player = new Eater(this);
		draw = new Draw(this);
		
		cookies = new ArrayList<Cookie>();
		walls = new ArrayList<Wall>();
		
		keyListener = new Controls(this);
		
		
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
		
		floors = new LinkedList<Level>();
		currFloor = new TestRoom(this, null);
		floors.add(currFloor);
		currFloor = new Floor1_Entrance(this,currFloor);
		floors.add(currFloor);
		
		
		buildBoard();
		score = 0;
		scoreToWin = 20;
		makeCookies();
		
		while(true)
			run(15);
	}
	
	public void run(int time) {
		draw.runUpdate();
		try {
			Thread.sleep(time);
		}catch(InterruptedException e){};
	}
	
	public void buildBoard() {
		currFloor.build();
	}
	
	public void makeCookies() {
		currFloor.placeCookies();
	}
	public void nextLevel() {
		walls = new ArrayList<Wall>();
		currFloor=currFloor.getNext();
		score = 0;
		buildBoard();
		cookies = new ArrayList<Cookie>();
		makeCookies();
	}
	public void resetLevel() {
		walls = new ArrayList<Wall>();
		currFloor = floors.getLast();
		score = 0;
		buildBoard();
		cookies = new ArrayList<Cookie>();
		makeCookies();
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}


}
