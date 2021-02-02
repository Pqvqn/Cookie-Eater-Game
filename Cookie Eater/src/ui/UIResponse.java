package ui;

import java.awt.*;

import ce3.*;

public class UIResponse extends UIElement{

	//private UIText text;
	private UIRectangle backing;
	private boolean selected;
	private int width;
	
	public UIResponse(Game frame, String words, int x, int y, int minWid) {
		super(frame,x,y);
		width = Math.max(words.length()*17,minWid);
		parts.add(new UIText(game,xPos+5,yPos+30,words,new Color(255,255,255,150),new Font("Arial",Font.BOLD,30))); //text
		parts.add(backing = new UIRectangle(game,xPos,yPos,width,40,new Color(0,0,0,50),true));
		selected = false;
	}
	
	public void select(boolean s) {
		selected = s;
		backing.setColor((selected)?new Color(255,255,255,50):new Color(0,0,0,50));
	}
	
	public int getWidth() {return width;}
	
	
}
