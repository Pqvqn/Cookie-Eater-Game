package entities;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;

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
	
	public Effect(Game frame, Board gameboard, SaveData sd, int cycletime) {
		super(frame, gameboard, sd, cycletime);
		collides = sd.getBoolean("collides",0);
		initiator = board.connections.get(sd.getString("connectcode",0)).get(0);
	}
	public SaveData getSaveData() {
		SaveData data = super.getSaveData();
		data.addData("collides",collides);
		data.addData("type",this.getClass().getName());
		return data;
	}
	//return Effect created by SaveData, testing for correct type of Effect
	public static Effect loadFromData(Game frame, Board gameboard, SaveData sd, int cycle) {
		//enemy subclasses
		Class[] effecttypes = {EffectClone.class, EffectExplosion.class};
		String thistype = sd.getString("type",0);
		for(int i=0; i<effecttypes.length; i++) {
			//if class type matches type from file, instantiate and return it
			if(thistype.equals(effecttypes[i].getName())){
				try {
					return (Effect) (effecttypes[i].getDeclaredConstructor(Game.class, Board.class, SaveData.class, int.class).newInstance(frame, gameboard, sd, cycle));
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | NoSuchMethodException | SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
		//default to blob
		return new EffectExplosion(frame, gameboard, sd, cycle);
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
