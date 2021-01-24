package ce3;

import java.awt.*;
import java.io.IOException;
//import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import entities.*;
import ui.*;
import sprites.*;

public class Draw extends JPanel{
	

	private static final long serialVersionUID = 1L;
	private Game game;
	private Board board;
	private ArrayList<Eater> players;
	private SpriteLevel boardImage;
	private ArrayList<UIElement> ui;
	private long lastMilliCount; //counting drawing framerate
	
	public Draw(Game frame) {
		super();
		game = frame;
		setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
		setBackground(Color.BLACK);
		ui = new ArrayList<UIElement>();
		lastMilliCount = System.currentTimeMillis();
	}
	
	public void setBoard(Board b) {
		board = b;
		players = board.players;
		try {
			boardImage = new SpriteLevel(board,board.walls);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
			boardImage.updateStuff(board.walls);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//converts screen coordinate to game engine location
	public Point convertPoint(Point b) {
		SwingUtilities.convertPointFromScreen(b, this);
		Rectangle screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		Point bb = new Point((int)(.5+b.x * (board.X_RESOL/screen.getWidth())),(int)(.5+b.y * (board.Y_RESOL/screen.getHeight())));
		return bb;
	}
	
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
		
		g2.scale(screen_bounds.getWidth()/board.X_RESOL,screen_bounds.getHeight()/board.Y_RESOL);
		boardImage.paint(g);
		
		if(board.isPaused() && game.ui_tis!=null && game.ui_tis.isVisible()) {
			game.ui_tis.paint(g);
			return;
		}
		
		for(int i=0; i<board.mechanisms.size(); i++) {
			board.mechanisms.get(i).paint(g);
		}
		for(int i=board.cookies.size()-1; i>=0; i--) {
			if(i>=board.cookies.size())i=board.cookies.size()-1;
			board.cookies.get(i).paint(g);
		}
		for(int i=0; i<board.enemies.size(); i++) {
			board.enemies.get(i).paint(g);
		}
		for(int i=0; i<board.present_npcs.size(); i++) {
			board.present_npcs.get(i).paint(g);
		}
		for(int i=0; i<players.size(); i++) {
			players.get(i).paint(g);
		}
		
		for(int i=0; i<board.effects.size(); i++) {
			board.effects.get(i).paint(g);
		}
		
		for(int i=0; i<ui.size(); i++) {
			if(ui.get(i)!=null)ui.get(i).paint(g);
		}
		
		if(board.currFloor!=null)board.currFloor.paint(g);
		
		//update fps counter
		if(board!=null && game.ui_fps!=null)game.ui_fps.update(lastMilliCount,System.currentTimeMillis());
		lastMilliCount = System.currentTimeMillis();
	}

}
