package ce3;

import java.awt.*;
//import java.awt.event.*;

import javax.swing.*;

public class Draw extends JPanel{
	

	private static final long serialVersionUID = 1L;
	private Board board;
	private Eater player;
	
	public Draw(Board frame) {
		super();
		board = frame;
		setPreferredSize(new Dimension(board.X_RESOL, board.Y_RESOL));
		player = board.player;
		setBackground(Color.GRAY);
		
	}
	
	//update all objects
	public void runUpdate() {
		player.runUpdate();
		for(int i=0; i<board.cookies.size(); i++) {
			board.cookies.get(i).runUpdate();
		}
		repaint();
	}
	
	//returns average of two colors
	public Color blendColors(Color a, Color b) {
		return new Color((int)(.5+(a.getRed()+b.getRed())/2),(int)(.5+(a.getGreen()+b.getGreen())/2),(int)(.5+(a.getBlue()+b.getBlue())/2));
	}
	
	//replaces floor color with correct one
	public void updateBG() {setBackground(board.currFloor.getBGColor());}
	
	//draw all objects
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		player.paint(g);
		
		for(int i=0; i<board.cookies.size(); i++) {
			board.cookies.get(i).paint(g);
		}
		for(int i=0; i<board.walls.size(); i++) {
			board.walls.get(i).paint(g);
		}
		
		
	}

}
