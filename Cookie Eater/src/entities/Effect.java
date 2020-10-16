package entities;

import java.awt.*;

import ce3.*;
import cookies.*;

public class Effect extends Entity{

	protected Entity initiator; //entity that created and profits from this effect
	protected boolean collides; //whether this effect collides with entities
	
	public Effect(Board frame, int cycletime, int xp, int yp, Entity owner) {
		super(frame,cycletime);
		x = xp;
		y = yp;
		initiator = owner;
		collides = true;
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
	//whether this entity collides
	public boolean doesCollision() {return collides;}
	//cookies go to initiator
	public void giveCookie(Cookie c) {
		if(initiator!=null)initiator.giveCookie(c);
	}
	public Entity getInitiator() {return initiator;}
	
	public void paint(Graphics g) {
	}
	
}
