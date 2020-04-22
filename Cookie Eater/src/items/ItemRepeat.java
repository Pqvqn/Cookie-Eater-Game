package items;
import java.util.*;

import ce3.*;

public class ItemRepeat extends Item{

	private int count;
	private int time;
	private int ratio;
	private int wait;
	private ArrayList<Item> items;
	
	public ItemRepeat(Board frame) {
		super(frame);
		ratio = 2;
		name = "Repeat";
	}
	public void prepare() {
		time = player.getSpecialLength()/ratio+1;
		wait = time/4;
		items = player.getItems().get(player.getCurrentSpecial());
	}
	public void initialize() {
		count = 0;
	}
	public void execute() {
		//count++;
		if(checkCanceled())return;
		if(count++>=time) {
			for(int i = items.indexOf(this)+1; i<items.size(); i++) {
				items.get(i).execute();
			}
			for(int i = 0; i<items.size(); i++) {
				items.get(i).end(true);
				items.get(i).cancelCycles(wait);
			}
			
			count=-5;
			return;
		}
		if(count<=-1) {
			for(int i = 0; i<items.size(); i++) {
				items.get(i).prepare();
			}
			for(int i = 0; i<items.size(); i++) {
				items.get(i).initialize();
			}
			count = 0;
		}
	}
	public void end(boolean interrupted) {
	}
	public void amplify() {
		super.amplify();
		ratio*=1.5;
	}
	public void deamplify() {
		super.deamplify();
		ratio/=1.5;
	}
}
