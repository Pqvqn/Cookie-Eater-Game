package mechanisms;

import java.awt.*;
import java.awt.geom.Area;

import ce3.*;

public class Mechanism {

	Game game;
	Board board;
	double x,y;
	double mass;
	
	public Mechanism(Game frame, Board gameboard, int xPos, int yPos) {
		game = frame;
		board = gameboard;
		x = xPos;
		y = yPos;
		mass = 100;
	}
	
	public void runUpdate() {
		
	}
	public double getX() {return x;}
	public double getY() {return y;}
	public double getXVel() {return 0;}
	public double getYVel() {return 0;}
	public double getMass() {return mass;}
	public Area getArea() {
		return new Area();
	}
	
	public void paint(Graphics g) {
		
	}
}
