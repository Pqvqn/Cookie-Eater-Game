package ui;

import java.awt.*;

import ce3.*;
import cookies.*;

public class UIPurchaseDesc extends UIElement{

	private UIParagraph text;
	private boolean visible;
	private UIRectangle backing;
	private int separation;
	private int passageWidth; //amount of characters allowed
	private CookieStore user;
	
	public UIPurchaseDesc(Game frame, CookieStore c) {
		super(frame,0,0);
		unpinned = true;
		user=c;
		xPos=user.getX();
		yPos=user.getY()+60;
		separation = 20;
		passageWidth = 25;
		parts.add(backing = new UIRectangle(game,xPos-100,yPos-100,200,100,new Color(0,0,0,150),true)); //backing
		parts.add(text = new UIParagraph(game,xPos-90,yPos-75,null,new Color(255,255,255,255),new Font("Arial",Font.ITALIC,15),separation,passageWidth));
		visible = true;
	}
	public void update(boolean show, String desc) {
		visible = show;
		xPos=user.getX();
		yPos=user.getY()+60;
		backing.setxPos(xPos-100);backing.setyPos(yPos-100);
		text.setxPos(xPos-90);text.setyPos(yPos-75);
		text.setTextLines(desc);
		backing.sethLen(separation*text.getLines().size()+20);
	}
	public void setVisible(boolean v) {visible = v;}
	public void paint(Graphics g) {
		if(visible)super.paint(g);
	}
}
