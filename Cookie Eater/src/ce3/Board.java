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
		
		buildBoard();
		score = 0;
		scoreToWin = 20;
		makeCookies(scoreToWin);
		
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
		walls.add(new Wall(this,0,0,X_RESOL,BORDER_THICKNESS));
		walls.add(new Wall(this,0,0,BORDER_THICKNESS,Y_RESOL));
		walls.add(new Wall(this,0,Y_RESOL-BORDER_THICKNESS,X_RESOL,BORDER_THICKNESS));
		walls.add(new Wall(this,X_RESOL-BORDER_THICKNESS,0,BORDER_THICKNESS,Y_RESOL));
		
		walls.add(new Wall(this,800,500,400,100));
	}
	
	public void makeCookies(int num) {
		int wid = (BORDER_THICKNESS+Cookie.DEFAULT_RADIUS);
		for(int i=0; i<num; i++) {
			cookies.add(new Cookie(this,
					(int)(Math.random()*(X_RESOL-2*wid))+wid,
					(int)(Math.random()*(Y_RESOL-2*wid))+wid));
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}


}
