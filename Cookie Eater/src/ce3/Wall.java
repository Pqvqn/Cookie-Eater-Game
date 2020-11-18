package ce3;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

public class Wall {
	
	//private Board board;
	int x,y;
	int w,h;
	double a;
	
	public Wall(Board frame, int xPos, int yPos, int width, int height, double angle) {
		//board = frame;
		x = xPos;
		y = yPos; 
		w = width;
		h = height;
		a = angle;
	}
	
	public Wall(Board frame, int xPos, int yPos, int width, int height) {
		//board = frame;
		x = xPos;
		y = yPos; 
		w = width;
		h = height;
		a = 0;
	}
	
	public int getAbsX() {return x;}
	public int getAbsY() {return y;}
	public int getW() {return w;}
	public int getH() {return h;}
	public double getA() {return a;}
	//x and y adjusted for rotation
	public double getX() {
		return (x+w/2) - Math.sqrt(w*w + h*h)/2 * Math.cos(a+Math.atan2(h,w));
	}
	public double getY() {
		return (y+h/2) - Math.sqrt(w*w + h*h)/2 * Math.sin(a+Math.atan2(h,w));
	}

	public Area getArea() {
		Rectangle2D.Double c = new Rectangle2D.Double(x,y,w,h);
		AffineTransform at = AffineTransform.getRotateInstance(a,x+w/2,y+h/2);
		Shape cc = at.createTransformedShape(c);
		return new Area(cc);
	}
	
}
