package ui;

import java.awt.*;

import ce3.*;

public class UIText extends UIElement{
	
	private String text;
	private Color color;
	private Font font;

	public UIText(Game frame, int x, int y, String t, Color c, Font f) {
		super(frame, x, y);
		text = t;
		xPos = x;
		yPos = y;
		color = c;
		font = f;
	}

	public String getText() {return text;}
	public void setText(String text) {this.text = text;}

	public Color getColor() {return color;}
	public void setColor(Color color) {this.color = color;}

	public void paint(Graphics g) {
		g.setColor(color);
		g.setFont(font);
		if(text!=null)g.drawString(text,xPos,yPos);
	}
	
	
}
