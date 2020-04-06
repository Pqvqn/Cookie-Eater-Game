package ce3;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import java.util.*;


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
				player.setDir(player.UP);
				break;
			case KeyEvent.VK_DOWN:
				player.setDir(player.DOWN);
				break;
			case KeyEvent.VK_LEFT:
				player.setDir(player.LEFT);
				break;
			case KeyEvent.VK_RIGHT:
				player.setDir(player.RIGHT);
				break;
			case KeyEvent.VK_SPACE:
				player.reset();
				board.score = 0;
				board.cookies = new ArrayList<Cookie>();
				board.makeCookies(board.scoreToWin);
				break;
		}
		
	}

	public void keyReleased(KeyEvent e) {
		
	}

	public void keyTyped(KeyEvent e) {
		
	}

}
