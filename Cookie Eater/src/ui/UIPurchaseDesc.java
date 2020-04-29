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
		passageWidth = 25;
		parts.add(backing = new UIRectangle(board,xPos-100,yPos-100,200,100,new Color(0,0,0,150))); //backing
		parts.add(text = new UIParagraph(board,xPos-90,yPos-75,null,new Color(255,255,255,255),new Font("Arial",Font.ITALIC,15),separation));
		visible = true;
	}
	public void update(boolean show, String desc) {
		visible = show;
		ArrayList<String> send = new ArrayList<String>();
		int i=0;
		while(i<desc.length()) { //split string into lines
			String line = " ";
			while(i<desc.length() && line.length()<=passageWidth && !line.substring(line.length()-1,line.length()).equals("`")) { //go until line full
				line+=desc.charAt(i);
				i++;
			}
			if(line.substring(line.length()-1,line.length()).equals("`")) { //if extra line character, add line
				line = line.substring(0,line.length()-1);
			}else { //otherwise cut line back to last space
				if(line.length()>passageWidth) {
					while(line.length()>0 && !line.substring(line.length()-1,line.length()).equals(" ") && i>0) {
						line = line.substring(0,line.length()-1);
						i--;
					}
				}
			}
			send.add(line); //add line
		}
		text.setTextLines(send);
		backing.sethLen(separation*send.size()+20);
	}
	public void paint(Graphics g) {
		if(visible)super.paint(g);
	}
}
