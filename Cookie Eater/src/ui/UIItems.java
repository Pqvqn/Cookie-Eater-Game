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
	private int myIndex;
	private Color theme_color;
	
	public UIItems(Board frame, int x, int y, int index, Color c) {
		super(frame,x,y);
		itemDisplays = new ArrayList<UIText>();
		parts.add(list_backing = new UIRectangle(board,xPos+50,yPos-SEPARATION,SIZE*5,SEPARATION+10,new Color(0,0,0,100)));
		parts.add(new UIRectangle(board,xPos-10,yPos+10-100,50,100,new Color(0,0,0,100))); //bar backing
		parts.add(new UIRectangle(board,xPos,yPos+20-100,30,80,new Color(0,255,255,10))); //bar unfill
		parts.add(bar = new UIRectangle(board,xPos,yPos+20-100,30,80,new Color(0,0,0,100)));
		barHeight = bar.gethLen();
		barY = bar.getyPos();
		myIndex = index;
		theme_color = c;
	}
	public void update(boolean listOut, ArrayList<ArrayList<Item>> pItems, ArrayList<Integer> frames, int cooldown, int duration, ArrayList<Boolean> activated) {
		ArrayList<Item> list = pItems.get(myIndex);
		int framecount = frames.get(myIndex);
		if(listOut || list.size()!=itemDisplays.size()) { //add all items into list
			list_backing.sethLen((list.size()>0) ? list.size()*(SEPARATION)+10 : SEPARATION+10);
			list_backing.setyPos(yPos+10-list_backing.gethLen());
			for(int i=0; i<itemDisplays.size(); i++) {
				parts.remove(itemDisplays.remove(i));
				i--;
			}
			for(int i=0; i<list.size(); i++) {
				UIText newOne = new UIText(board,xPos+60,yPos-i*SEPARATION,list.get(i).name(),new Color(255,255,255,75),new Font("Arial",Font.BOLD,SIZE));
				itemDisplays.add(newOne);
				parts.add(newOne);
			}
		}
		int red = theme_color.getRed();
		int green = theme_color.getGreen();
		int blue = theme_color.getBlue();
		if(framecount == 0 && activated.get(myIndex)) { //special charged
			bar.setColor(new Color((red+100>255)?255:red+100,(green+100>255)?255:green+100,(blue+100>255)?255:blue+100,250));
			bar.sethLen(barHeight);
		}else if(framecount <= duration) { //special use
			bar.setColor(new Color(red,green,blue,150));
			if(duration>0)
				bar.sethLen((int)(.5+barHeight-((double)framecount/duration)*barHeight));
		}else { //special recharge
			bar.setColor(new Color(red,green,blue,50).darker());
			if(cooldown>0)
				bar.sethLen((int)(.5+(((double)framecount-duration)/cooldown)*barHeight));
		}
		bar.setyPos(barY+barHeight-bar.gethLen());
	}
}
