package ui;

import java.awt.*;
import java.util.*;

import ce3.*;

public class UIPurchaseDesc extends UIElement{

	private UIParagraph text;
	private boolean visible;
	private UIRectangle backing;
	private int separation;
	private int passageWidth; //amount of characters allowed
	
	public UIPurchaseDesc(Board frame, int x, int y) {
		super(frame,x,y);
		separation = 20;
		passageWidth = 28;
		parts.add(text = new UIParagraph(board,xPos-90,yPos-80,null,new Color(255,255,255,255),new Font("Arial",Font.ITALIC,15),separation));
		parts.add(backing = new UIRectangle(board,xPos-100,yPos-100,200,100,new Color(0,0,0,100))); //backing
		visible = true;
	}
	public void update(boolean show, String desc) {
		visible = show;
		ArrayList<String> send = new ArrayList<String>();
		for(int i=0; i<desc.length(); i++) {
			String line = "";
			while(line.length()<passageWidth && i<desc.length()) {
				line+=desc.charAt(i);
				i++;}
			while(line.length()>0 && !line.substring(line.length()-1,line.length()).equals(" ") && i>0) {
				line = line.substring(0,line.length()-1);
				i--;
			}
			send.add(line);
			line = "";
		}
		text.setTextLines(send);
		backing.sethLen(separation*send.size()+20);
	}
	public void paint(Graphics g) {
		if(visible)super.paint(g);
	}
}
