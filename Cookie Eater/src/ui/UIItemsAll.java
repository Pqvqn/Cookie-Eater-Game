package ui;

import java.awt.*;
import java.util.*;

import ce3.*;
import items.*;

public class UIItemsAll extends UIElement{

	private ArrayList<UIItems> specials;
	
	public UIItemsAll(Game frame, int x, int y, int numSlots, int corner, ArrayList<Color> colors) {
		super(frame,x,y);
		specials = new ArrayList<UIItems>();
		
		//set item display positions based on corner of screen and number of slots
		boolean left = corner==1 || corner==2;
		//boolean right = corner==0 || corner ==3;
		boolean top = corner==1 || corner==3;
		//boolean bottom = corner==0 || corner==2;
		int[][] offsets = {{0,0},{0,150*(top?1:-1)},{250*(left?1:-1),0},{250*(left?1:-1),150*(top?1:-1)}};
		for(int i=0; i<numSlots; i++) {
			specials.add(new UIItems(frame, x+offsets[i][0], y+offsets[i][1], i, colors.get(i%colors.size())));
		}
		
		for(int i=0; i<specials.size(); i++) {
			parts.add(specials.get(i));
		}
	}
	
	public void update(boolean listOut, ArrayList<ArrayList<Item>> pItems, ArrayList<Double> frames, int cooldown, int duration, ArrayList<Boolean> activated) {
		for(int i=0; i<specials.size(); i++) {
			specials.get(i).update(listOut, pItems, frames, cooldown, duration, activated);
		}
	}
	
}
