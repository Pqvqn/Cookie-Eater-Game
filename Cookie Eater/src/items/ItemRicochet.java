package items;
import java.util.*;

import ce3.*;
import entities.*;

public class ItemRicochet extends Item{
	
	private double maxRad;
	private int duration;
	private ArrayList<Explosion> booms;
	
	public ItemRicochet(Board frame) {
		super(frame);
		maxRad = 150;
		duration = 250;
		booms = new ArrayList<Explosion>();
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
		if(!(bouncedOff instanceof Explosion)) { //dont create explosions when moved by explosions
			booms.add(0,new Explosion(board,board.getCycle(),(int)(.5+x),(int)(.5+y),maxRad*board.currFloor.getScale(),duration,user));
			board.effects.add(booms.get(0));
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
