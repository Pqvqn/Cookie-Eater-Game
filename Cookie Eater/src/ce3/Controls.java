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
			case KeyEvent.VK_TAB:
				player.win();
				break;
		}
		
	}

	public void keyReleased(KeyEvent e) {
		
	}

	public void keyTyped(KeyEvent e) {
		
	}

}
