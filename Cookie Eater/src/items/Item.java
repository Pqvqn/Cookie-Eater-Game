package items;

import ce3.*;
import entities.Eater;
import entities.Entity;

public abstract class Item {

	protected Board board;
	protected Entity user;
	protected int amps;
	protected String name;
	protected boolean cancel;
	protected int waiting;
	protected String desc;
	protected boolean isplayer;
	
	public Item(Board frame) {
		board = frame;
		user = null;
		amps=0;
		cancel = false;
		desc = "";
		isplayer = false;
	}
	//give the item something to use it
	public void setUser(Entity u) {
		user = u;
		isplayer = user instanceof Eater;}
	//set all vars before other items change them
	public void prepare() {
		
	}
	//run on special start
	public void initialize() {
		
	}
	//run while special is active
	public void execute() {
	}
	//run when special ends
	public void end(boolean interrupted) {
		
	}
	//string that names this item
	public String name() {
		String ret = name;
		for(int i=0; i<amps; i++)ret+="+";
		return ret;
	}
	//returns name without modifiers
	public String getName() {
		return name;
	}
	//what to do when bounced off wall (mainly movement items) with x/y pos of bounce point
	public void bounce(Object bouncedOff, double x, double y) {
		
	}
	public void amplify() {
		amps++;
	}
	public void deamplify() {
		amps--;
	}
	public void cancelCycles(int c) {
		cancel = true;
		waiting = c;
	}
	public boolean checkCanceled() {
		if(cancel) {
			if(waiting--<=0) {
				cancel = false;
			}//if execute must be skipped
			return true;
		}
		return false;
	}
	public String getDesc() {return desc;}
}
