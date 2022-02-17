package ui;

import java.awt.*;

import ce3.*;

public class UIText extends UIElement{
	
	private String text;
	private Color color;
	private Font font;
	private int width; //width of string on screen

	public UIText(Game frame, int x, int y, String t, Color c, Font f) {
		super(frame, x, y);
		text = t;
		xPos = x;
		yPos = y;
		color = c;
		font = f;
	}
	public UIText(Game frame, int x, int y, String t, Color c, Font f, boolean unpin) {
		this(frame,x,y,t,c,f);
		unpinned = unpin;
	}

	public String getText() {return text;}
	public void setText(String text) {this.text = text;}

	public Color getColor() {return color;}
	public void setColor(Color color) {this.color = color;}

	public int textWidth() {return width;}
	public void center(int x) {
		Graphics g = game.getGraphics();
		if(g!=null) {
			g.setFont(font);
			width = g.getFontMetrics().stringWidth(text);
		}
		xPos = x - (int)(.5+textWidth()/2.0);
	}
	
	public void paint(Graphics g) {
		g.setColor(color);
		g.setFont(font);
		if(text!=null)width = g.getFontMetrics().stringWidth(text);
		if(text!=null)g.drawString(text,xPos,yPos);
	}
	
	
}
