package entities;

import java.awt.Graphics;
import java.util.*;

import ce3.*;
import levels.*;
import cookies.*;

public class Explorer extends Entity{

	protected Level residence; //which room this explorer is on
	protected String name;
	protected int state;
	protected static final int VENDOR = 0, VENTURE = 1;
	protected ArrayList<CookieStore> to_sell;
	protected ArrayList<CookieStore> on_display;
	
	public Explorer(Board frame) {
		super(frame);
		name = "Unknown";
		chooseResidence();
		
	}
	
	public Level getResidence() {return residence;}
	public String getName() {return name;}
	//make changes after player ends a run
	public void runEnds() {
		
	}
	//updates every cycle
	public void runUpdate() {
		super.runUpdate();
	}
	//die on a floor
	public void kill() {
		//item drop code here :)
	}
	//chooses which level to go to on game start
	public void chooseResidence() {
		
	}
	//creates a completely new stash of items
	public void createStash() {
		
	}
	//puts all items to sell out on display
	public void sellWares(int[][] positions) {
		for(int i=0; !to_sell.isEmpty() && i<positions.length; i++) {
			CookieStore c = to_sell.remove(0);
			c.setPos(positions[i][0],positions[i][1]);
			board.cookies.add(c);
			on_display.add(c);
		}
	}
	//removes items from display and re-stashes them
	public void packUp() {
		for(int i=on_display.size(); i>0; i--) {
			if(board.cookies.contains(on_display.get(i))){
				to_sell.add(on_display.remove(i));
			}else {
				on_display.remove(i);
			}
		}
	}
	public void paint(Graphics g) {}
}
