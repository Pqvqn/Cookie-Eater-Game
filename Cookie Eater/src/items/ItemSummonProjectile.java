package items;

import java.util.*;

import ce3.*;
import entities.*;

public class ItemSummonProjectile extends Item{
	
	private Summon2 summon;
	private int hitpoints;
	
	public ItemSummonProjectile(Board frame) {
		super(frame);
		hitpoints = 0;
		name = "Projectile";
		desc="Items affect a separate summoned entity that is free from the player. `Amplify: Summon gains health";
	}
	public void prepare() {
		//user's items given to summon
		user.addSummon(summon = new Summon2(board,user,board.getCycle(),false,hitpoints));
		summon.eatItems();
		summon.activateSpecials();
		summon.special(user.getCurrentSpecial());
	}
	public void initialize() {
		//summon.prepareItems();
	}
	public void execute() {
		if(checkCanceled())return;
		if(summon==null)prepare(); //sometimes prepare is skipped?
		if(!summon.isDed()) {
			//summon's item progress given to user
			ArrayList<Double> kep = new ArrayList<Double>();
			ArrayList<Double> las = summon.getSpecialFrames();
			for(int i=0; i<las.size(); i++)kep.add(las.get(i));
			user.setSpecialFrames(kep); //keep player special use same as summon's
		}else {
			//dead summons don't set player counter
		}
	}
	public void end(boolean interrupted) {
		//undo user summon thing
		summon.regurgitateItems();
		summon.regurgitateCookies();
		user.removeSummon(summon);
	}
	public void amplify() {
		super.amplify();
		hitpoints++;
	}
	public void deamplify() {
		super.deamplify();
		hitpoints--;
	}
}
