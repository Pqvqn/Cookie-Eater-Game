package mechanisms;


import java.awt.event.*;
import java.util.*;

import ce3.*;
import menus.*;

public class WallCase extends Wall{

	private Selection confirmation;
	
	public WallCase(Game frame, Board gameboard, int xPos, int yPos, int width, int height) {
		super(frame, gameboard, xPos, yPos, width, height);
		setUpConfirm();
	}
	
	public WallCase(Game frame, Board gameboard, int xPos, int yPos, int radius) {
		super(frame, gameboard, xPos, yPos, radius);
		setUpConfirm();  
	}

	//creates purchase confirmation selector
	public void setUpConfirm() {
		ArrayList<String> opts = new ArrayList<String>();
		opts.add("Accept");
		opts.add("Deny");
		confirmation = new Selection(board, opts, 0, -1, KeyEvent.VK_SPACE, KeyEvent.VK_ENTER);
	}
	
	public void runUpdate() {
		
		
	}
	
}