package ui;

import java.awt.*;
import java.util.*;

import ce3.*;

public class UIParagraph extends UIElement{
	
	private ArrayList<String> textLines;
	private Color color;
	private Font font;
	private int separation, passageWidth;

	public UIParagraph(Game frame, int x, int y, ArrayList<String> ts, Color c, Font f, int sep, int wid) {
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
		passageWidth = wid;
		initiateLines();
	}

	public void initiateLines() {
		parts = new ArrayList<UIElement>();
		for(int i=0; i<textLines.size(); i++)
			parts.add(new UIText(board, xPos, yPos+separation*i, textLines.get(i), color, font));
	}
	
	public ArrayList<String> getLines() {return textLines;}
	public void setTextLines(String text) {
		textLines = new ArrayList<String>();
		int i=0;
		while(i<text.length()) { //split string into lines
			String line = " ";
			while(i<text.length() && line.length()<=passageWidth && !line.substring(line.length()-1,line.length()).equals("`")) { //go until line full
				line+=text.charAt(i);
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
			textLines.add(line); //add line
		}
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
