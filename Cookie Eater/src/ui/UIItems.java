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
	private UIRectangle list_backing;
	//private UIRectangle bar_backing;
	//private UIRectangle bar_unfill;
	private UIRectangle bar;
	private int barHeight, barY;
	
	public UIItems(Board frame, int x, int y) {
		super(frame,x,y);
		itemDisplays = new ArrayList<UIText>();
		parts.add(list_backing = new UIRectangle(board,xPos+50,yPos+10,SIZE*5,SIZE,new Color(0,0,0,100)));
		parts.add(new UIRectangle(board,xPos-10,yPos+10-100,50,100,new Color(0,0,0,100))); //bar backing
		parts.add(new UIRectangle(board,xPos,yPos+20-100,30,80,new Color(0,255,255,10))); //bar unfill
		parts.add(bar = new UIRectangle(board,xPos,yPos+20-100,30,80,new Color(0,255,255,100)));
		barHeight = bar.gethLen();
		barY = bar.getyPos();
	}
	public void update(ArrayList<Item> pItems, int frames, int cooldown, int duration) {
		if(pItems.size()!=itemDisplays.size()) { //add all items into list
			list_backing.sethLen(pItems.size()*(SEPARATION)+10);
			list_backing.setyPos(yPos+10-list_backing.gethLen());
			for(int i=0; i<itemDisplays.size(); i++) {
				parts.remove(itemDisplays.remove(i));
			}
			for(int i=0; i<pItems.size(); i++) {
				UIText newOne = new UIText(board,xPos+60,yPos-i*SEPARATION,pItems.get(i).name(),new Color(255,255,255,75),new Font("Arial",Font.BOLD,SIZE));
				itemDisplays.add(newOne);
				parts.add(newOne);
			}
		}
		if(frames == 0) { //special charged
			bar.setColor(new Color(200,255,255,200));
			bar.sethLen(barHeight);
		}else if(frames <= duration) { //special use
			bar.setColor(new Color(0,255,255,150));
			if(duration>0)
				bar.sethLen((int)(.5+barHeight-((double)frames/duration)*barHeight));
		}else { //special recharge
			bar.setColor(new Color(100,255,255,50));
			if(cooldown>0)
				bar.sethLen((int)(.5+(((double)frames-duration)/cooldown)*barHeight));
		}
		bar.setyPos(barY+barHeight-bar.gethLen());
	}
}
