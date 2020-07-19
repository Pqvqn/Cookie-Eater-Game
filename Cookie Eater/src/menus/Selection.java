package menus;

import java.util.*;

import ce3.*;

public class Selection extends Menu{

	protected ArrayList<String> options; //options to be selected from
	protected int hovered, chosen; //currently selected option
	protected int keyPrev, keyNext, keySelect; //keys for interacting
	
	public Selection(Board frame, ArrayList<String> optionList, int defaultOption, int kprev, int knext, int kselect) {
		super(frame);
		options = optionList;
		hovered = defaultOption;
		chosen = -1;
		keyPrev = kprev;
		keyNext = knext;
		keySelect = kselect;
	}
	
	public int getChosenIndex() {return chosen;}
	public Object getChosenOption() {return options.get(chosen);}
	public int getHoveredIndex() {return hovered;}
	
	public boolean hasChosen() {return chosen>=0;}
	
	public void keyPress(int key) {
		if(key == keyPrev)
			hovered = (hovered<=0) ? options.size()-1 : hovered-1;
		
		if(key == keyNext)
			hovered = (hovered>=options.size()-1) ? 0 : hovered+1;
		
		if(key == keySelect) {
			chosen = hovered;}
	}
	
	
}
