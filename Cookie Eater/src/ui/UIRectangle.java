package ui;

import java.awt.*;

import ce3.*;

public class UIRectangle extends UIElement{
	
	private int wLen, hLen;
	private Color color;
	private boolean filled;

	public UIRectangle(Game frame, int x, int y, int w, int h, Color c, boolean fill) {
		super(frame,x,y);
		xPos = x;
		yPos = y;
		wLen = w;
		hLen = h;
		color = c;
		filled = fill;
	}
	
	public int getwLen() {return wLen;}
	public void setwLen(int wLen) {this.wLen = wLen;}
	
	public int gethLen() {return hLen;}
	public void sethLen(int hLen) {this.hLen = hLen;}

	public Color getColor() {return color;}
	public void setColor(Color color) {this.color = color;}

	public void paint(Graphics g) {
		g.setColor(color);
		if(filled) {
			g.fillRect(xPos,yPos,wLen,hLen);
		}else {
			g.drawRect(xPos,yPos,wLen,hLen);
		}
	}
	
	
}
