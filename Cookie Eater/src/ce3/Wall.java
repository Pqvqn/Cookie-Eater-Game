package ce3;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

import levels.Level;

public class Wall {
	
	//private Board board;
	int x,y;
	int w,h;
	double a;
	int ox,oy; //origin to rotate around
	
	public Wall(Board frame, int xPos, int yPos, int width, int height, double angle, int originX, int originY) {
		//board = frame;
		x = xPos;
		y = yPos; 
		w = width;
		h = height;
		a = angle;
		ox=originX;
		oy=originY;
	}
	
	public Wall(Board frame, int xPos, int yPos, int width, int height, double angle) {
		//board = frame;
		x = xPos;
		y = yPos; 
		w = width;
		h = height;
		a = angle;
		ox=x+w/2;
		oy=y+h/2;
	}
	
	public Wall(Board frame, int xPos, int yPos, int width, int height) {
		//board = frame;
		x = xPos;
		y = yPos; 
		w = width;
		h = height;
		a = 0;
		ox=x+w/2;
		oy=y+h/2;
	}
	
	public int getX() {return x;}
	public int getY() {return y;}
	public int getW() {return w;}
	public int getH() {return h;}
	public double getA() {return a;}
	//x and y adjusted for rotation
	public double getAdjX() {
		return ox - Level.lineLength(x,y,ox,oy) * Math.cos(a+Math.atan2(y-oy,x-ox));
	}
	public double getAdjY() {
		return oy - Level.lineLength(x,y,ox,oy) * Math.sin(a+Math.atan2(y-oy,x-ox));
	}
	public int getOX() {return ox;}
	public int getOY() {return oy;}

	public Area getArea() {
		Rectangle2D.Double c = new Rectangle2D.Double(x,y,w,h);
		AffineTransform at = AffineTransform.getRotateInstance(a,ox,oy);
		Shape cc = at.createTransformedShape(c);
		return new Area(cc);
	}
	
}
