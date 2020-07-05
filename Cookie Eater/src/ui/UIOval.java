package ui;

import java.awt.*;

import ce3.*;

public class UIOval extends UIElement{
	
	private int wRad, hRad;
	private Color color;

	public UIOval(Board frame, int x, int y, int w, int h, Color c) {
		super(frame,x,y);
		xPos = x;
		yPos = y;
		wRad = w;
		hRad = h;
		color = c;
	}
	
	public int getwRad() {return wRad;}
	public void setwRad(int wRad) {this.wRad = wRad;}
	
	public int gethRad() {return hRad;}
	public void sethRad(int hRad) {this.hRad = hRad;}

	public Color getColor() {return color;}
	public void setColor(Color color) {this.color = color;}

	public void paint(Graphics g) {
		g.setColor(color);
		g.fillOval(xPos-wRad/2,yPos-hRad/2,wRad,hRad);
	}
	
	
}
