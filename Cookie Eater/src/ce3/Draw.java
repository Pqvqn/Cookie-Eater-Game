package ce3;

import java.awt.*;
import java.io.IOException;
//import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import cookies.*;
import enemies.*;
import ui.*;
import sprites.*;

public class Draw extends JPanel{
	

	private static final long serialVersionUID = 1L;
	private Board board;
	private ArrayList<Eater> players;
	private SpriteLevel boardImage;
	private ArrayList<UIElement> ui;
	
	public Draw(Board frame) {
		super();
		board = frame;
		setPreferredSize(new Dimension(board.X_RESOL, board.Y_RESOL));
		players = board.players;
		setBackground(Color.GRAY);
		ui = new ArrayList<UIElement>();
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
	//update all objects
	public void runUpdate() {
		for(int i=0; i<players.size(); i++) {
			players.get(i).runUpdate();
		}
		for(int i=0; i<board.cookies.size(); i++) {
			if(i<board.cookies.size()) {
				Cookie curr = board.cookies.get(i);
				if(curr!=null)
				curr.runUpdate();
				if(i<board.cookies.size()&&board.cookies.get(i)!=null&&curr!=null&&!board.cookies.get(i).equals(curr))
					i--;
			}
		}
		for(int i=0; i<board.enemies.size(); i++) {
			if(i<board.enemies.size()) {
				Enemy curr = board.enemies.get(i);
				curr.runUpdate();
				if(i<board.enemies.size()&&!board.enemies.get(i).equals(curr))
					i--;
			}
		}
		for(int i=0; i<players.size(); i++) {
			players.get(i).endCycle();
		}
		for(int i=0; i<board.enemies.size(); i++) {
			if(i<board.enemies.size()) {
				Enemy curr = board.enemies.get(i);
				curr.endCycle();
			}
		}
		repaint();
	}
	
	public void addUI(UIElement thing) {
		ui.add(thing);
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
	
	//draw all objects
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		boardImage.paint(g);
	
		for(int i=board.cookies.size()-1; i>=0; i--) {
			board.cookies.get(i).paint(g);
		}
		for(int i=0; i<board.enemies.size(); i++) {
			board.enemies.get(i).paint(g);
		}
		for(int i=0; i<players.size(); i++) {
			players.get(i).paint(g);
		}
		for(int i=0; i<ui.size(); i++) {
			ui.get(i).paint(g);
		}
		
	}

}
