package ui;

import java.awt.*;
import java.util.*;

import ce3.Game;
import menus.*;

public class UIConfirmation extends UIElement{

	//private UIRectangle backing;
	//private UIText question;
	private UIRectangle highlight;
	private ArrayList<UIResponse> responses;
	private Selection selector;
	
	public UIConfirmation(Game frame, int x, int y, String text, Selection s, boolean unpin) {
		super(frame, x, y);
		selector = s;
		parts.add(new UIRectangle(game,xPos-100,yPos-100,200,200,new Color(0,0,0,200),true)); //backing
		parts.add(new UIText(game,xPos-80,yPos-50,text,new Color(255,255,255,150),new Font("Arial",Font.BOLD,30))); //question
		parts.add(highlight = new UIRectangle(game,0,0,0,40,new Color(255,255,255,50),true));
		
		responses = new ArrayList<UIResponse>();
		ArrayList<String> options = selector.getOptions();
		int wid = 0;
		for(int i=0; i<options.size(); i++) {
			UIResponse a = new UIResponse(game,options.get(i),xPos-100+wid,yPos,40);
			responses.add(a);
			parts.add(a);
			wid += a.getWidth() + 10;
		}
		unpinned = unpin;
	}
	
	public boolean sameSelection(Selection s) {return selector==s;}
	public Selection getSelection() {return selector;}
	
	public void update() {
		for(int i=0; i<responses.size(); i++) {
			responses.get(i).select(i == selector.getChosenIndex());
		}
		
		int hovered = selector.getHoveredIndex();
		if(hovered>=0 && !responses.isEmpty()) {
			if(!parts.contains(highlight))parts.add(highlight);
			highlight.setxPos(responses.get(hovered).getxPos());
			highlight.setyPos(responses.get(hovered).getyPos());
			highlight.setwLen(responses.get(hovered).getWidth());
		}else {
			parts.remove(highlight);
		}
	}

}
