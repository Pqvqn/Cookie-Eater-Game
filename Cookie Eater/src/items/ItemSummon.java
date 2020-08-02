package items;

import java.util.*;

import ce3.*;
import entities.*;

public class ItemSummon extends Item{
	
	private Summon2 summon;
	
	public ItemSummon(Board frame) {
		super(frame);
		name = "Summon";
		desc="Items affect a separate summoned entity. `Amplify: Summon gains health";
	}
	public void prepare() {
		//user's items given to summon
		user.addSummon(summon);
		ArrayList<Item> items = user.getItems().get(user.getCurrentSpecial());
		for(int i=0; i<items.size(); i++) {
			if(!items.get(i).equals(this)) {
				user.getItems().get(user.getCurrentSpecial()).remove(items.get(i));
				summon.addItem(user.getCurrentSpecial(),items.get(i));
			}
		}
	}
	public void initialize() {
	
	}
	public void execute() {
		if(checkCanceled())return;
		//summon's item progress given to user
	}
	public void end(boolean interrupted) {
		//undo user summon thing
		ArrayList<Item> items = summon.getItems().get(user.getCurrentSpecial());
		for(int i=0; i<items.size(); i++) {
			summon.getItems().get(user.getCurrentSpecial()).remove(items.get(i));
			user.addItem(user.getCurrentSpecial(),items.get(i));
		}
	}
	public void amplify() {
		super.amplify();
		//hp up
	}
	public void deamplify() {
		super.deamplify();
		//hp down
	}
}
