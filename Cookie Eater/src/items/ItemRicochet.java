package items;
import java.awt.geom.*;
import java.util.*;

import ce3.*;
import entities.*;

public class ItemRicochet extends Item{
	
	private double maxRad;
	private int duration;
	private ArrayList<EffectExplosion> booms;
	
	public ItemRicochet(Game frame) {
		super(frame);
		maxRad = 150;
		duration = 250;
		booms = new ArrayList<EffectExplosion>();
		name = "Ricochet";
		desc="Creates explosion on impact.`Amplify: Larger explosions";
	}
	public void prepare() {

	}
	public void initialize() {

	}
	public void execute() {
		if(checkCanceled())return;
		//for(int i=0; i<booms.size(); i++) {
			//booms.get(i).runUpdate();
		//}
	}
	public void end(boolean interrupted) {
		for(int i=0; i<booms.size(); i++) {
			//booms.get(i).kill();
			//board.effects.remove(booms.get(i));
			booms.remove(i);
			i--;
		}
	}
	public void bounce(Object bouncedOff, double x, double y) {
		if(!(bouncedOff instanceof EffectExplosion)) { //dont create explosions when moved by explosions
			EffectExplosion boom;
			booms.add(0,boom = new EffectExplosion(game,game.getCycle(),(int)(.5+x),(int)(.5+y),maxRad*board.currFloor.getScale(),duration,user));
			board.effects.add(boom);
			
			//if colliding with an area instead of an entity, get out of area before exploding
			if(bouncedOff instanceof Area) {
				double rat = 1/Math.sqrt(Math.pow(user.getX()-x,2)+Math.pow(user.getY()-y,2));
				double xD = (user.getX()-x) * rat; //amount to move by
				double yD = (user.getY()-y) * rat;
				boom.setRadius(.0001); //radius to .0001 for collision
				boom.orientParts();
				//move out of area
				while(boom.collidesWithArea(false, (Area)bouncedOff)) {
					boom.setX(boom.getX()+xD);
					boom.setY(boom.getY()+yD);
					boom.orientParts();
				}
				boom.setRadius(0); //reset radius
				boom.orientParts();
			}
		}
	}

	public void amplify() {
		super.amplify();
		maxRad+=100;
	}
	public void deamplify() {
		super.deamplify();
		maxRad-=100;
	}
}
