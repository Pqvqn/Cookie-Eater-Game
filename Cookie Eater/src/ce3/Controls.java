package ce3;

//import java.awt.*;
import java.awt.event.*;

import items.*;

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
		if(board.getAdjustedCycle()<=0 || board.getAdjustedCycle()>=10000)return; //if isnt ready, don't allow input
		
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
			case KeyEvent.VK_BACK_SPACE:
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
			case KeyEvent.VK_SPACE:
				if(board.currFloor.haltEnabled()) {
					player.setDir(Eater.NONE);
					player.averageVels(0,0);
				}
				break;
			case KeyEvent.VK_C:
				player.addCash(1);
				break;
			case KeyEvent.VK_K:
				player.addCash(100);
				break;
			case KeyEvent.VK_O:
				player.addItem(0, new ItemRepeat(board));
				player.addItem(0, new ItemJab(board));
				//player.addItem(0, new ItemHold(board));
				break;
		}
		
	}

	public void keyReleased(KeyEvent e) {
		
	}

	public void keyTyped(KeyEvent e) {
		
	}

}
