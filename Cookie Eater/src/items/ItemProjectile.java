package items;
import java.util.*;

import ce3.*;

public class ItemProjectile extends Item{
	
	private double speed;
	private ArrayList<SummonProjectile> proj;
	
	public ItemProjectile(Board frame) {
		super(frame);
		speed = 8;
		proj = new ArrayList<SummonProjectile>();
		name = "Projectile";
	}
	public void prepare() {
		proj.add(new SummonProjectile(board,board.player,speed));
		for(int i=0; i<proj.size(); i++) {
			player.addSummon(proj.get(i));
			proj.get(i).prepare();
		}
	}
	public void initialize() {
		proj.get(proj.size()-1).setSpeed(speed);
		proj.get(proj.size()-1).initialize();
	}
	public void execute() {
		if(checkCanceled())return;
		for(int i=0; i<proj.size(); i++) {
			proj.get(i).execute();
		}
	}
	public void end(boolean interrupted) {
		for(int i=0; i<proj.size(); i++) {
			proj.get(i).end(false);
			player.removeSummon(proj.get(i));
			proj.remove(i);}
	}
	public void amplify() {
		super.amplify();
		speed+=4;
		for(int i=0; i<proj.size(); i++) {
			proj.get(i).setSpeed(speed);
		}
	}
	public void deamplify() {
		super.deamplify();
		speed-=4;
		for(int i=0; i<proj.size(); i++) {
			proj.get(i).setSpeed(speed);
		}
	}
}
