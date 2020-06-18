package ui;

import java.awt.*;
import java.util.*;

import ce3.*;

public class UIParagraph extends UIElement{
	
	private ArrayList<String> textLines;
	private Color color;
	private Font font;
	private int separation;

	public UIParagraph(Board frame, int x, int y, ArrayList<String> ts, Color c, Font f, int sep) {
		super(frame, x, y);
		if(ts==null) {
			textLines = new ArrayList<String>();
		}else {
			textLines = ts;
		}
		xPos = x;
		yPos = y;
		color = c;
		font = f;
		separation = sep;
		initiateLines();
	}

	public void initiateLines() {
		parts = new ArrayList<UIElement>();
		for(int i=0; i<textLines.size(); i++)
			parts.add(new UIText(board, xPos, yPos+separation*i, textLines.get(i), color, font));
	}
	
	public ArrayList<String> getLines() {return textLines;}
	public void setTextLines(ArrayList<String> textLines) {
		this.textLines = textLines;
		initiateLines();
		}

	public Color getColor() {return color;}
	public void setColor(Color color) {this.color = color;}

	public void paint(Graphics g) {
		g.setColor(color);
		g.setFont(font);
		super.paint(g);
	}
	
	
}
