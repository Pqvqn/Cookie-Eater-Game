package ce3;

//import java.awt.*;
import java.awt.event.*;

import entities.*;
import items.*;

//import javax.swing.*;


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
		
		int key = e.getKeyCode();
		
		//test key against this player's controls
		if(key==CONTROLSCHEMES[scheme][UPKEY]) { 
			player.setDir(Eater.UP);
		}else if(key==CONTROLSCHEMES[scheme][RIGHTKEY]) {
			player.setDir(Eater.RIGHT);
		}else if(key==CONTROLSCHEMES[scheme][DOWNKEY]) {
			player.setDir(Eater.DOWN);
		}else if(key==CONTROLSCHEMES[scheme][LEFTKEY]) {
			player.setDir(Eater.LEFT);
		}
		
		//send key to menus
		for(int i=board.menus.size()-1; i>=0; i--) {
			board.menus.get(i).keyPress(key);
		}

				
		//debug keys
		switch(key) {
			case KeyEvent.VK_BACK_SPACE:
				player.kill();
				break;
			case KeyEvent.VK_ENTER:
				if(!board.inConvo())player.win();
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
				player.pay(1);
				break;
			case KeyEvent.VK_X:
				player.addShields(1);
				break;
			case KeyEvent.VK_K:
				player.pay(100);
				break;
			case KeyEvent.VK_O:
				player.addItem(0, new ItemSummon(board));
				player.addItem(0, new ItemCircle(board));
				//player.addItem(0, new ItemField(board));
				//player.addItem(0, new ItemBoost(board));
				//player.addItem(0, new ItemCookieChain(board));
				//player.addItem(0, new ItemRepeat(board));
				//player.addItem(0, new ItemReturn(board));
				//player.addItem(0, new ItemTeleport(board));
				//player.addItem(0, new ItemSlowmo(board));
				player.addItem(0, new ItemRicochet(board));
				break;
		}
		
	}

	public void keyReleased(KeyEvent e) {
		
	}

	public void keyTyped(KeyEvent e) {
		
	}

}
