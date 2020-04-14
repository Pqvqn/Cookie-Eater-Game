package ce3;

//import java.awt.*;
import java.awt.event.*;

//import javax.swing.*;

//import java.util.*;


public class Controls implements KeyListener{

	private Board board;
	private Eater player;
	
	
	public Controls(Board parent) {
		board = parent;
		player = board.player;
	}
	
	public void keyPressed(KeyEvent e) {
		if(board.getAdjustedCycle()==0 || board.getAdjustedCycle()>=10000)return; //if isnt ready, don't allow input
		
		
		
		
		
		
		switch(e.getKeyCode()) {
			case KeyEvent.VK_UP:
				player.setDir(Eater.UP);
				break;
			case KeyEvent.VK_DOWN:
				player.setDir(Eater.DOWN);
				break;
			case KeyEvent.VK_LEFT:
				player.setDir(Eater.LEFT);
				break;
			case KeyEvent.VK_RIGHT:
				player.setDir(Eater.RIGHT);
				break;
			case KeyEvent.VK_SPACE:
				player.kill();
				break;
			case KeyEvent.VK_ENTER:
				player.win();
				break;
			case KeyEvent.VK_SHIFT:
				e.consume();
				player.special(0); 
				break;
			case KeyEvent.VK_CONTROL:
				e.consume();
				player.special(1); 
				break;
			case KeyEvent.VK_ALT:
				e.consume();
				player.special(2); 
				break;
			case KeyEvent.VK_C:
				board.cash++;
		}
		
	}

	public void keyReleased(KeyEvent e) {
		
	}

	public void keyTyped(KeyEvent e) {
		
	}

}
