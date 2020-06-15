package ce3;

//import java.awt.*;
import java.awt.event.*;

import items.*;

//import javax.swing.*;

//import java.util.*;


public class Controls implements KeyListener{

	private Board board;
	private Eater player;
	//controls for each player added
	private final int[][] CONTROLSCHEMES = {{KeyEvent.VK_UP,KeyEvent.VK_DOWN,KeyEvent.VK_LEFT,KeyEvent.VK_RIGHT},
			{KeyEvent.VK_W,KeyEvent.VK_S,KeyEvent.VK_A,KeyEvent.VK_D},
			{KeyEvent.VK_I,KeyEvent.VK_K,KeyEvent.VK_J,KeyEvent.VK_L},
			{KeyEvent.VK_NUMPAD8,KeyEvent.VK_NUMPAD5,KeyEvent.VK_NUMPAD4,KeyEvent.VK_NUMPAD6}};
	private final int UPKEY = 0, RIGHTKEY = 3, DOWNKEY = 1, LEFTKEY = 2;
	private int scheme;
	
	
	public Controls(Board parent, Eater body, int c) {
		board = parent;
		player = body;
		scheme = c;
	}
	
	public void keyPressed(KeyEvent e) {
		if(board.getAdjustedCycle()<=0 || board.getAdjustedCycle()>=10000)return; //if isnt ready, don't allow input
		
		if(e.getKeyCode()==CONTROLSCHEMES[scheme][UPKEY]) {
			player.setDir(Eater.UP);
		}else if(e.getKeyCode()==CONTROLSCHEMES[scheme][RIGHTKEY]) {
			player.setDir(Eater.RIGHT);
		}else if(e.getKeyCode()==CONTROLSCHEMES[scheme][DOWNKEY]) {
			player.setDir(Eater.DOWN);
		}else if(e.getKeyCode()==CONTROLSCHEMES[scheme][LEFTKEY]) {
			player.setDir(Eater.LEFT);
		}
		
		
		switch(e.getKeyCode()) {
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
			case KeyEvent.VK_X:
				player.addShields(1);
				break;
			case KeyEvent.VK_K:
				player.addCash(100);
				break;
			case KeyEvent.VK_O:
				player.addItem(0, new ItemJab(board));
				break;
		}
		
	}

	public void keyReleased(KeyEvent e) {
		
	}

	public void keyTyped(KeyEvent e) {
		
	}

}
