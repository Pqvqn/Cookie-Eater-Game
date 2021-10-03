package items;
import java.util.*;

import ce3.*;
import cookies.*;

public class ItemRepeat extends Item{

	private int count;
	private int time;
	private int ratio;
	private int wait;
	private ArrayList<CookieItem> items;
	
	public ItemRepeat(Game frame, Board gameboard) {
		super(frame,gameboard);
		ratio = 2;
		name = "Repeat";
		desc="Pauses and restarts all items.`Amplify- Number of restarts increases";
	}
	public void prepare() {
		time = user.getSpecialLength()/ratio+1;
		wait = time;
		items = user.getItems().get(user.getCurrentSpecial());
	}
	public void initialize() {
		count = 0;
	}
	public void execute() {
		if(count++>=time) {
			//execute the items after this one before ending all
			int index;
			for(index=0;index<items.size() && !(items.get(index).getItem() instanceof ItemRepeat);index++);
			for(int i = index+1; i<items.size(); i++) {
				items.get(i).getItem().execute();
			}
			//end all items
			for(int i = 0; i<items.size(); i++) {
				items.get(i).getItem().end(true);
				items.get(i).getItem().cancelCycles(wait);
			}
			
			count=-wait+1;
			return;
		}
		//keep special held
		if(count<-1) {
			user.extendSpecial(1);
		}
		//restart special
		if(count==-1) {
			for(int i = 0; i<items.size(); i++) {
				items.get(i).getItem().prepare();
			}
			for(int i = 0; i<items.size(); i++) {
				items.get(i).getItem().initialize();
			}
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
