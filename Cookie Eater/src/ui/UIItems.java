package ui;

import java.awt.*;
import java.util.*;

import ce3.*;
import items.*;

public class UIItems extends UIElement{

	//UIText shields;
	private ArrayList<UIText> itemDisplays;
	private final int SEPARATION = 30;
	private final int SIZE = 30;
	private UIRectangle backing;
	
	public UIItems(Board frame, int x, int y) {
		super(frame,x,y);
		itemDisplays = new ArrayList<UIText>();
		parts.add(backing = new UIRectangle(board,xPos-10,yPos+10,SIZE*5,SIZE,new Color(0,0,0,50)));
	}
	public void update(ArrayList<Item> pItems) {
		if(pItems.size()!=itemDisplays.size()) {
			backing.sethLen(pItems.size()*(SEPARATION)+10);
			backing.setyPos(yPos+10-backing.gethLen());
			for(int i=0; i<pItems.size(); i++) {
				UIText newOne = new UIText(board,xPos,yPos-i*SEPARATION,pItems.get(i).name(),new Color(255,255,255,50),new Font("Arial",Font.BOLD,SIZE));
				itemDisplays.add(newOne);
				parts.add(newOne);
			}
		}
	}
}
