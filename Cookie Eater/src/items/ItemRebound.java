package items;
import java.util.*;

import ce3.*;
import cookies.*;
import entities.*;

public class ItemRebound extends Item{

	private double consta;
	private int bounces;
	private int last_bounce;
	private int total;
	private ArrayList<CookieItem> items;
	
	public ItemRebound(Game frame, Board gameboard) {
		super(frame,gameboard);
		consta = 2;
		name = "Rebound";
		desc="Amplifies items after bouncing`Amplify- Amplifies after less bounces";
	}
	public void prepare() {
		items = user.getPowerups();
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
		if(isplayer) {
			((Eater)user).updateUIItems();
		}
		bounces=0;
		last_bounce=1;
		total = 0;
	}
	public void bounce(Object bouncedOff, double x, double y) {
		bounces++;
		if((2+bounces-last_bounce) > Math.pow(last_bounce/consta*2, consta)) {
			amplifyAll();
			if(isplayer) {
				((Eater)user).updateUIItems();
			}
			last_bounce = bounces+1;
			total++;
		}
	}
	public void amplifyAll() {
		for(int i=0; i<items.size(); i++) {
			if(!items.get(i).getItem().equals(this)) {
			items.get(i).getItem().amplify();
			}
		}
	}
	public void deamplifyAll() {
		for(int i=0; i<items.size(); i++) {
			if(!items.get(i).getItem().equals(this)) {
			items.get(i).getItem().deamplify();
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
