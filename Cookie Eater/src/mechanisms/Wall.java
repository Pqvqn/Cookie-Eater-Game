package mechanisms;

import java.awt.*;
import java.awt.geom.*;

import ce3.Board;
import levels.Level;

public class Wall extends Mechanism{
	
	double w,h;
	double a;
	double ox,oy; //origin to rotate around
	double r;
	int shape;
	static final int CIRCLE = 0, RECTANGLE = 1;
	
	public Wall(Board frame, int xPos, int yPos, int width, int height, double angle, int originX, int originY) {
		super(frame, xPos, yPos);
		shape = RECTANGLE;
		w = width;
		h = height;
		a = angle;
		ox=originX;
		oy=originY;
	}
	
	public Wall(Board frame, int xPos, int yPos, int width, int height, double angle) {
		super(frame, xPos, yPos);
		shape = RECTANGLE; 
		w = width;
		h = height;
		a = angle;
		ox=x+w/2;
		oy=y+h/2;
	}
	
	public Wall(Board frame, int xPos, int yPos, int width, int height) {
		super(frame, xPos, yPos);
		shape = RECTANGLE;
		w = width;
		h = height;
		a = 0;
		ox=x+w/2;
		oy=y+h/2;
	}
	
	public Wall(Board frame, int xPos, int yPos, int radius) {
		super(frame, xPos, yPos);
		shape = CIRCLE;
		r = radius;
		a = 0;
		ox=x;
		oy=y;
	}
	
	public double getW() {return w;}
	public double getH() {return h;}
	public double getA() {return a;}
	public double getR() {return r;}
	//x and y adjusted for rotation
	public double getAdjX() {
		return ox - Level.lineLength(x,y,ox,oy) * Math.cos(a+Math.atan2(y-oy,x-ox));
	}
	public double getAdjY() {
		return oy - Level.lineLength(x,y,ox,oy) * Math.sin(a+Math.atan2(y-oy,x-ox));
	}
	public double getOX() {return ox;}
	public double getOY() {return oy;}
	
	public void move(double dx, double dy) {
		x+=dx;
		y+=dy;
		ox+=dx;
		oy+=dy;
	}

	public Area getArea() {
		if(shape == RECTANGLE) {
			Rectangle2D.Double c = new Rectangle2D.Double(x,y,w,h);
			AffineTransform at = AffineTransform.getRotateInstance(a+Math.PI,ox,oy);
			Shape cc = at.createTransformedShape(c);
			return new Area(cc);
		}else if(shape == CIRCLE) {
			Ellipse2D.Double c = new Ellipse2D.Double(x-r,y-r,r*2,r*2);
			return new Area(c);
		}else {
			return new Area();
		}
	}
	
	public void runUpdate() {
		
	}
}
