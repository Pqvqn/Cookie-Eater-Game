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
	private int[][] controlSchemes = {{KeyEvent.VK_UP,KeyEvent.VK_DOWN,KeyEvent.VK_LEFT,KeyEvent.VK_RIGHT,
			KeyEvent.VK_END},
		{KeyEvent.VK_W,KeyEvent.VK_S,KeyEvent.VK_A,KeyEvent.VK_D,
			KeyEvent.VK_2},
		{KeyEvent.VK_P,KeyEvent.VK_SEMICOLON,KeyEvent.VK_L,KeyEvent.VK_QUOTE,
			KeyEvent.VK_0},
		{KeyEvent.VK_Y,KeyEvent.VK_H,KeyEvent.VK_G,KeyEvent.VK_J,
			KeyEvent.VK_6},
		{KeyEvent.VK_NUMPAD8,KeyEvent.VK_NUMPAD5,KeyEvent.VK_NUMPAD4,KeyEvent.VK_NUMPAD6,
			KeyEvent.VK_SLASH}};
	public static final int UPKEY = 0, RIGHTKEY = 3, DOWNKEY = 1, LEFTKEY = 2, PAUSEKEY = 4;
	private int scheme;
	private int awaitKey; //keybind for key awaiting reassignment
	private MenuButton awaitingButton; //button that initiated key await
	
	public Controls(Board parent, Eater body, int c) {
		board = parent;
		player = body;
		scheme = c;
		awaitKey = -1;
	}
	
	public void keyPressed(KeyEvent e) {
		if(awaitKey!=-1) {
			setKeyBind(awaitKey,e.getKeyCode());
			if(awaitingButton!=null)awaitingButton.setCurrStateValue(""+KeyEvent.getKeyText(e.getKeyCode()));
			awaitKey = -1;
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
		}else if(key==controlSchemes[scheme][PAUSEKEY]) { 
			board.ui_set.show(!board.ui_set.isVisible(),player);
		}
		
		//send key to menus
		for(int i=board.menus.size()-1; i>=0; i--) {
			board.menus.get(i).keyPress(key);
		}

		boolean isP1 = scheme==0;
			
		//debug keys
		switch(key) {
			case KeyEvent.VK_BACK_SPACE:
				if(!isP1)break;
				player.kill();
				break;
			case KeyEvent.VK_ENTER:
				if(!isP1)break;
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
				if(!isP1)break;
				board.ui_set.show(!board.ui_set.isVisible());
				break;
		}
		
	}

	public void setKeyBind(int keyBind, int keyCode) {
		controlSchemes[scheme][keyBind] = keyCode;
	}
	public int getKeyBind(int keyBind) {
		return controlSchemes[scheme][keyBind];
	}
	public void awaitKeyBind(MenuButton button, int keyBind) {
		awaitingButton = button;
		awaitKey = keyBind;
	}
	
	public void keyReleased(KeyEvent e) {
		
	}

	public void keyTyped(KeyEvent e) {
		
	}

}
