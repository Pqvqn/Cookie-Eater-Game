package mechanisms;


import java.awt.event.*;
import java.util.*;

import ce3.*;
import entities.*;
import levels.*;
import menus.*;

public class WallCase extends Wall{

	private int dist; //distance from player to ask for confirmation from
	private Selection confirmation; //menu confirmation to remove case
	
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
		
		dist = 80;
		ArrayList<String> opts = new ArrayList<String>();
		opts.add("Deny");
		opts.add("Accept");
		confirmation = new Selection(board, opts, 0, 0, KeyEvent.VK_SPACE, KeyEvent.VK_ENTER);
		board.menus.remove(confirmation);
	}
	
	public void remove() {
		super.remove();
		board.endConfirmation(confirmation);
	}
	
	public void runUpdate() {
		//check if confirmation should display because player is within range
		for(int i=0; i<board.players.size(); i++){
			Eater p = board.players.get(i);
			if(Level.lineLength(getOX(),getOY(),p.getX(),p.getY())<dist) {
				board.requestConfirmation(confirmation,(int)getOX(),(int)getOY(),"Open Case?");
			}
		}
		//if confirmation is accepted, remove case
		if(confirmation.hasChosen()) {
			if(confirmation.getChosenOption().equals("Accept")) {
				board.mechanisms.remove(this);
				board.endConfirmation(confirmation);
			}else if(confirmation.getChosenOption().equals("Deny")) {
				board.endConfirmation(confirmation);
				confirmation.resetChosen();
			}
		}
		
	}
	
}