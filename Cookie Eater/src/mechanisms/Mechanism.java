package mechanisms;

import java.awt.*;
import java.awt.geom.Area;

import ce3.*;

public class Mechanism {

	Board board;
	int x,y;
	
	public Mechanism(Board frame, int xPos, int yPos) {
		frame = board;
		x = xPos;
		y = yPos;
	}
	
	public void runUpdate() {
		
	}
	public int getX() {return x;}
	public int getY() {return y;}
	public Area getArea() {
		return new Area();
	}
	
	public void paint(Graphics g) {
		
	}
}
