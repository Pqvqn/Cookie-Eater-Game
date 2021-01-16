package ce3;

//import java.awt.*;
import java.awt.event.*;

import entities.*;
import items.*;
import menus.*;

//import javax.swing.*;


public class Controls implements KeyListener{

	private Board board;
	private Eater player;
	//controls for each player added
	private int[][] controlSchemes = {{KeyEvent.VK_UP,KeyEvent.VK_DOWN,KeyEvent.VK_LEFT,KeyEvent.VK_RIGHT},
			{KeyEvent.VK_W,KeyEvent.VK_S,KeyEvent.VK_A,KeyEvent.VK_D},
			{KeyEvent.VK_I,KeyEvent.VK_K,KeyEvent.VK_J,KeyEvent.VK_L},
			{KeyEvent.VK_NUMPAD8,KeyEvent.VK_NUMPAD5,KeyEvent.VK_NUMPAD4,KeyEvent.VK_NUMPAD6}};
	public static final int UPKEY = 0, RIGHTKEY = 3, DOWNKEY = 1, LEFTKEY = 2;
	private int scheme;
	private int[] awaitKey; //playerNum,keyBind for key awaiting reassignment
	private MenuButton awaitingButton; //button that initiated key await
	
	public Controls(Board parent, Eater body, int c) {
		board = parent;
		player = body;
		scheme = c;
		awaitKey = null;
	}
	
	public void keyPressed(KeyEvent e) {
		if(awaitKey!=null) {
			setKeyBind(awaitKey[0],awaitKey[1],e.getKeyCode());
			if(awaitingButton!=null)awaitingButton.setCurrStateValue(""+e.getKeyChar());
			awaitKey = null;
			awaitingButton = null;
			return;
		}
		
		if(board.show_title || board.getAdjustedCycle()<=0 || board.getAdjustedCycle()>=10000)return; //if isnt ready, don't allow input
		
		int key = e.getKeyCode();
		
		//test key against this player's controls
		if(key==controlSchemes[scheme][UPKEY]) { 
			player.setDir(Eater.UP);
		}else if(key==controlSchemes[scheme][RIGHTKEY]) {
			player.setDir(Eater.RIGHT);
		}else if(key==controlSchemes[scheme][DOWNKEY]) {
			player.setDir(Eater.DOWN);
		}else if(key==controlSchemes[scheme][LEFTKEY]) {
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
					player.averageVels(0,0,false);
				}
				break;
			case KeyEvent.VK_ESCAPE:
				board.ui_set.show(!board.ui_set.isVisible());
				break;
		}
		
	}

	public void setKeyBind(int playerNum, int keyBind, int keyCode) {
		controlSchemes[playerNum][keyBind] = keyCode;
	}
	public int getKeyBind(int playerNum, int keyBind) {
		return controlSchemes[playerNum][keyBind];
	}
	public void awaitKeyBind(MenuButton button, int playerNum, int keyBind) {
		awaitingButton = button;
		awaitKey = new int[2];
		awaitKey[0] = playerNum;
		awaitKey[1] = keyBind;
	}
	
	public void keyReleased(KeyEvent e) {
		
	}

	public void keyTyped(KeyEvent e) {
		
	}

}
