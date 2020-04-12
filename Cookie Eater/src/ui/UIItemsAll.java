package ui;

import java.awt.*;
import java.util.*;

import ce3.Board;
import items.Item;

public class UIItemsAll extends UIElement{

	private ArrayList<UIItems> specials;
	
	public UIItemsAll(Board frame, int x, int y, ArrayList<Color> colors) {
		super(frame,x,y);
		specials = new ArrayList<UIItems>();
		specials.add(new UIItems(frame, x, y-150, 0, colors.get(0)));
		specials.add(new UIItems(frame, x, y, 1, colors.get(1)));
		specials.add(new UIItems(frame, x+250, y, 2, colors.get(2)));
		
		for(int i=0; i<specials.size(); i++) {
			parts.add(specials.get(i));
		}
	}
	
	public void update(ArrayList<ArrayList<Item>> pItems, ArrayList<Integer> frames, int cooldown, int duration) {
		for(int i=0; i<specials.size(); i++) {
			specials.get(i).update(pItems, frames, cooldown, duration);
		}
	}
	
}
