package entities;

import java.awt.*;

import ce3.*;
import cookies.*;

public abstract class Effect extends Entity{

	protected Entity initiator; //entity that created and profits from this effect
	protected boolean collides; //whether this effect collides with entities
	
	public Effect(Game frame, Board gameboard, int cycletime, int xp, int yp, Entity owner) {
		super(frame,gameboard,cycletime);
		x = xp;
		y = yp;
		initiator = owner;
		collides = true;
	}
	
	public Effect(Game frame, Board gameboard, SaveData sd, int cycletime, Entity owner) {
		super(frame, gameboard, sd, cycletime);
		collides = sd.getBoolean("collides",0);
		initiator = owner;
	}
	public SaveData getSaveData() {
		SaveData data = super.getSaveData();
		data.addData("collides",collides);
		return data;
	}
	//return Effect created by SaveData, testing for correct type of Effect
	public static Effect loadFromData(Game frame, Board gameboard, Entity owner, SaveData sd, int cycle) {
		switch(sd.getString("type",0)) {
		case "clone":
			return new EffectClone(frame, gameboard, sd, cycle, owner);
		case "explosion":
			return new EffectExplosion(frame, gameboard, sd, cycle, owner);
		default:
			return new EffectExplosion(frame, gameboard, sd, cycle, owner);
		}
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
