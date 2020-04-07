package ce3;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class Draw extends JPanel{
	
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
