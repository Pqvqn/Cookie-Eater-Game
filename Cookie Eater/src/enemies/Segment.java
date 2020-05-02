package enemies;

import java.awt.*;

import ce3.*;
import sprites.*;

public abstract class Segment {

	protected Sprite sprite;
	protected double xPos,yPos;
	protected Board board;
	protected Eater player;
	protected Enemy owner;
	protected double angle;
	protected Color color;
	
	public Segment(Board frame, Enemy host, double x, double y, double a, Color c) {
		board = frame;
		xPos = x;
		yPos = y;
		angle = a;
		player = board.player;
		owner = host;
		color = c;
	}
	public boolean collidesWithRect(double x, double y, double w, double h) {return false;}
	public boolean collidesWithCircle(double x, double y, double r) {return false;}
	public double[] rectHitPoint(double rx, double ry, double rw, double rh) {return null;}
	public double[] circHitPoint(double cx, double cy, double cr) {return null;}
	public void setLocation(double x, double y) {
		xPos = x;
		yPos = y;
	}
	public void paint(Graphics g) {
		g.setColor(color);
	}
}
