package ce3;

//import java.awt.*;
import java.awt.event.*;

import entities.*;
import menus.*;

//import javax.swing.*;


public class Controls implements KeyListener{

	private Game game;
	private Board board;
	private Eater player;
	//controls for each player added
	private int[][] controlSchemes = {{KeyEvent.VK_UP,KeyEvent.VK_DOWN,KeyEvent.VK_LEFT,KeyEvent.VK_RIGHT,
			KeyEvent.VK_SHIFT,KeyEvent.VK_END},
		{KeyEvent.VK_W,KeyEvent.VK_S,KeyEvent.VK_A,KeyEvent.VK_D,
			KeyEvent.VK_Q,KeyEvent.VK_2},
		{KeyEvent.VK_P,KeyEvent.VK_SEMICOLON,KeyEvent.VK_L,KeyEvent.VK_QUOTE,
			KeyEvent.VK_O,KeyEvent.VK_0},
		{KeyEvent.VK_Y,KeyEvent.VK_H,KeyEvent.VK_G,KeyEvent.VK_J,
			KeyEvent.VK_T,KeyEvent.VK_6},
		{KeyEvent.VK_NUMPAD8,KeyEvent.VK_NUMPAD5,KeyEvent.VK_NUMPAD4,KeyEvent.VK_NUMPAD6,
			KeyEvent.VK_NUMPAD7,KeyEvent.VK_SLASH}};
	public static final int UPKEY = 0, RIGHTKEY = 3, DOWNKEY = 1, LEFTKEY = 2, SPECIALKEY = 4, PAUSEKEY = 5;
	private int scheme;
	private int awaitKey; //keybind for key awaiting reassignment
	private MenuButton awaitingButton; //button that initiated key await
	
	public Controls(Game frame, Board gameboard, Eater body, int c) {
		game = frame;
		game.addControls(this);
		board = gameboard;
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
		
		
		int key = e.getKeyCode();
		boolean isP1 = scheme==0;
		
		//settings menus and keys that must register even when game is paused
		if(key == KeyEvent.VK_ESCAPE && isP1) {
			game.ui_set.show(!game.ui_set.isVisible());
		}else if(key==controlSchemes[scheme][PAUSEKEY]) { 
			game.ui_set.show(!game.ui_set.isVisible(),player);
		}

		//if isnt ready, don't allow input
		if((board.isPaused() && !board.awaitingStart()) || game.getAdjustedCycle()<=0 || game.getAdjustedCycle()>=10000)return; 
		
		//test key against this player's controls
		if(key==controlSchemes[scheme][UPKEY]) { 
			player.setDir(Eater.UP);
		}else if(key==controlSchemes[scheme][RIGHTKEY]) {
			player.setDir(Eater.RIGHT);
		}else if(key==controlSchemes[scheme][DOWNKEY]) {
			player.setDir(Eater.DOWN);
		}else if(key==controlSchemes[scheme][LEFTKEY]) {
			player.setDir(Eater.LEFT);
		}else if(key==controlSchemes[scheme][SPECIALKEY]) {
			if(board.mode == Board.PVP)
				player.special(0);
		}
		
		//send key to menus
		if(isP1) {
			for(int i=board.menus.size()-1; i>=0; i--) {
				board.menus.get(i).keyPress(key);
			}
		}
			
		//debug keys
		switch(key) {
			case KeyEvent.VK_BACK_SPACE:
				if(!isP1)break;
				player.kill();
				break;
			case KeyEvent.VK_ENTER:
				if(!isP1)break;
				//if(!board.inConvo())player.win();
				break;
			case KeyEvent.VK_SPACE:
				if(board.currFloor.haltEnabled()) {
					player.setDir(Eater.NONE);
					player.averageVels(0,0,false);
				}
				break;
			case KeyEvent.VK_SHIFT:
				if(game.board.mode==Board.PVP)break;
				e.consume();
				player.special(1); 
				break;
			case KeyEvent.VK_CONTROL:
				if(game.board.mode==Board.PVP)break;
				e.consume();
				player.special(0); 
				break;
			case KeyEvent.VK_ALT:
				if(game.board.mode==Board.PVP)break;
				e.consume();
				player.special(2); 
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
