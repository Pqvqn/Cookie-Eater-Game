package items;
import java.util.*;

import ce3.*;

public class ItemRebound extends Item{

	private double consta;
	private int bounces;
	private int last_bounce;
	private int total;
	private ArrayList<Item> items;
	
	public ItemRebound(Board frame) {
		super(frame);
		consta = 2;
		name = "Rebound";
		desc="Amplifies items after bouncing`Amplify: Amplifies after less bounces";
	}
	public void prepare() {
		items = player.getItems().get(player.getCurrentSpecial());
		bounces=0;
		last_bounce=1;
		total = 0;
	}
	public void initialize() {
		
	}
	public void execute() {
		if(checkCanceled())return;
	}
	public void end(boolean interrupted) {
		for(int i=0; i<total; i++) {
			deamplifyAll();
		}
		player.updateUIItems();
		bounces=0;
		last_bounce=1;
		total = 0;
	}
	public void bounce(double x, double y) {
		bounces++;
		if((2+bounces-last_bounce) > Math.pow(last_bounce/consta*2, consta)) {
			amplifyAll();
			player.updateUIItems();
			last_bounce = bounces+1;
			total++;
		}
	}
	public void amplifyAll() {
		for(int i=0; i<items.size(); i++) {
			if(!items.get(i).equals(this)) {
			items.get(i).amplify();
			}
		}
	}
	public void deamplifyAll() {
		for(int i=0; i<items.size(); i++) {
			if(!items.get(i).equals(this)) {
			items.get(i).deamplify();
			}
		}
	}
	
	public void amplify() {
		super.amplify();
		consta-=.2;
	}
	public void deamplify() {
		super.deamplify();
		consta+=.2;
	}
}
