package ui;

import java.awt.*;

import ce3.*;

public class UIText  extends UIElement{
	
	private String text;
	private int xPos, yPos;
	private Color color;
	private Font font;

	public UIText(Board frame, int x, int y, String t, Color c, Font f) {
		super(frame);
		text = t;
		xPos = x;
		yPos = y;
		color = c;
		font = f;
	}
	
	public int getxPos() {return xPos;}
	public void setxPos(int xPos) {this.xPos = xPos;}

	public int getyPos() {return yPos;}
	public void setyPos(int yPos) {this.yPos = yPos;}
	
	public String getText() {return text;}
	public void setText(String text) {this.text = text;}

	public Color getColor() {return color;}
	public void setColor(Color color) {this.color = color;}

	public void paint(Graphics g) {
		g.setColor(color);
		g.setFont(font);
		g.drawString(text,xPos,yPos);
	}
	
	
}
