package entities;

import java.awt.*;

import ce3.*;
import cookies.*;

public class Effect extends Entity{

	protected Entity initiator;
	
	public Effect(Board frame, int cycletime, int xp, int yp, Entity owner) {
		super(frame,cycletime);
		x = xp;
		y = yp;
		initiator = owner;
	}
	
	public void runUpdate() {
		if(ded)return;
		super.runUpdate();
	}
	protected void buildBody() {
	}
	//remove from board
	public void kill() {
		ded = true;
		board.effects.remove(this);
	}
	//cookies go to initiator
	public void giveCookie(Cookie c) {
		if(initiator!=null)initiator.giveCookie(c);
	}
	public Entity getInitiator() {return initiator;}
	
	public void paint(Graphics g) {
	}
	
}
