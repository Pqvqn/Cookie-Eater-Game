package ce3;

import java.awt.*;
import java.io.IOException;
//import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import entities.*;
import ui.*;
import sprites.*;
import levels.*;

public class Draw extends JPanel{
	

	private static final long serialVersionUID = 1L;
	private Game game;
	private Board board;
	private ArrayList<Eater> players;
	private SpriteLevel boardImage;
	private ArrayList<UIElement> ui;
	private long lastMilliCount; //counting drawing framerate
	private boolean graphicsLevel; //if graphics are not reduced
	
	public Draw(Game frame) {
		super();
		game = frame;
		setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
		setBackground(Color.BLACK);
		ui = new ArrayList<UIElement>();
		lastMilliCount = System.currentTimeMillis();
		graphicsLevel = true;
	}
	
	public void setBoard(Board b) {
		board = b;
		players = board.players;
		boolean add_fps = ui.contains(game.ui_fps);
		ui = new ArrayList<UIElement>();
		if(add_fps)ui.add(game.ui_fps);
		try {
			boardImage = new SpriteLevel(board);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(board.currLevel!=null)updateBG();
	}
	
	public void update(Graphics g) {
		paint(g);
	}
	//repaint every cycle
	public void runUpdate() {
		repaint();
	}
	
	public void addUI(UIElement thing) {
		ui.add(thing);
	}
	public void addUI(UIElement thing,int index) {
		ui.add(index,thing);
	}
	public void removeUI(UIElement thing) {
		ui.remove(thing);
	}
	public ArrayList<UIElement> getUIList() {return ui;}
	
	//returns average of two colors
	public Color blendColors(Color a, Color b) {
		return new Color((int)(.5+(a.getRed()+b.getRed())/2),(int)(.5+(a.getGreen()+b.getGreen())/2),(int)(.5+(a.getBlue()+b.getBlue())/2));
	}
	
	//replaces floor color with correct one
	public void updateBG() {
		//setBackground(board.currFloor.getBGColor());
		try {
			boardImage.updateStuff(board.currLevel.walls,!graphicsLevel);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//converts screen coordinate to game engine location
	public Point convertPoint(Point b, boolean uiLayer) {
		SwingUtilities.convertPointFromScreen(b, this);
		Rectangle screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		int xp = (int)(.5+b.x * (Board.FRAME_X_RESOL/screen.getWidth()));
		int yp = (int)(.5+b.y * (Board.FRAME_Y_RESOL/screen.getHeight()));
		if(!uiLayer) {
			xp += (Board.FRAME_X_RESOL/2-board.player().getX());
			yp += (Board.FRAME_Y_RESOL/2-board.player().getY());
		}
		Point bb = new Point(xp,yp);
		return bb;
	}
	
	//reduce or bring back costly graphics
	public void setGraphicsLevel(boolean high) {
		graphicsLevel = high;
		updateBG();
	}
	public boolean getGraphicsLevel() {return graphicsLevel;}
	//draw all objects
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		//Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Rectangle screen_bounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		
		g2.scale(screen_bounds.getWidth()/Board.FRAME_X_RESOL,screen_bounds.getHeight()/Board.FRAME_Y_RESOL);
		
		// translate board to center on player
		double[] translate = null;
		if(board!=null && board.player()!=null) {
			translate = new double[] {Board.FRAME_X_RESOL/2-board.player().getX(),Board.FRAME_Y_RESOL/2-board.player().getY()};
		}
		if(translate!=null)g2.translate(translate[0],translate[1]);

		
		if(game!=null && (board==null || board.isPaused()) && game.ui_tis!=null && game.ui_tis.isVisible()) {
			game.ui_tis.paint(g);
			return;
		}
		
		boardImage.paint(g);
		
		Level lvl = board.currLevel;

		for(int i=0; i<lvl.enemies.size(); i++) {
			lvl.enemies.get(i).paint(g);
		}
		for(int i=0; i<lvl.presentnpcs.size(); i++) {
			lvl.presentnpcs.get(i).paint(g);
		}
		for(int i=0; i<players.size(); i++) {
			players.get(i).paint(g);
		}
		
		for(int i=0; i<lvl.mechanisms.size(); i++) {
			lvl.mechanisms.get(i).paint(g);
		}
		for(int i=lvl.cookies.size()-1; i>=0; i--) {
			if(i>=lvl.cookies.size())i=lvl.cookies.size()-1;
			if(i>=0)lvl.cookies.get(i).paint(g);
		}
		
		for(int i=0; i<lvl.effects.size(); i++) {
			lvl.effects.get(i).paint(g);
		}
		
		
		ArrayList<UIElement> upperUI = new ArrayList<UIElement>();
		for(int i=0; i<ui.size(); i++) {
			UIElement uie = ui.get(i);
			if(uie!=null) {
				if(uie.movesWithBoard()) {
					uie.paint(g);
				}else {
					upperUI.add(uie);
				}
			}
		}
		
		if(board.currLevel!=null)board.currLevel.paint(g);
		
		//	translate board back for constants on screen
		if(translate!=null)g2.translate(-translate[0],-translate[1]);
		
		for(int i=0; i<upperUI.size(); i++) {
			upperUI.get(i).paint(g);
		}
		

		
		//update fps counter
		if(board!=null && game.ui_fps!=null)game.ui_fps.update(lastMilliCount,System.currentTimeMillis());
		lastMilliCount = System.currentTimeMillis();
	}

}
