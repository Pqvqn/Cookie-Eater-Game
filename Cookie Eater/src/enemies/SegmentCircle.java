package enemies;

import java.awt.*;

import ce3.*;
import levels.*;

public class SegmentCircle extends Segment{

	private double radius;
	
	public SegmentCircle(Board frame, Enemy host, double x, double y, double rad, double a, Color c) {
		super(frame,host,x,y,a,c);
		radius = rad;
	}
	public boolean collidesWithRect(double x, double y, double w, double h) {
		return Level.collidesCircleAndRect(xPos, yPos, radius*scale, x, y, w, h);
	}
	public boolean collidesWithCircle(double x, double y, double r) {
		return Level.lineLength(x, y, xPos, yPos) <= r+radius*scale;
	}
	public double[] rectHitPoint(double rx, double ry, double rw, double rh) {
		double[] ret = {xPos,yPos};
		boolean xB=false,yB=false;
		if(yPos>ry+rh) {
			ret[1] = yPos-radius*scale;
			yB=true;
		}if(yPos<ry) {
			ret[1] = yPos+radius*scale;
			yB=true;
		}if(xPos>rx+rw) {
			ret[0] = xPos-radius*scale;
			xB=true;
		}if(xPos<rx) {
			ret[0] = xPos+radius*scale;
			xB=true;
		}
		if(xB&&yB) {
			ret[1]=(yPos>ry+rh)?ry+rh:ry;
			ret[0]=(xPos>rx+rw)?rx+rw:rx;
		}
		return ret;
	}
	public double[] circHitPoint(double cx, double cy, double cr) {
		double[] ret = {xPos,yPos};
		double ratio = radius*scale/Level.lineLength(cx, cy, xPos, yPos);
		ret[0] = (cx-xPos)*ratio+xPos;
		ret[1] = (cy-yPos)*ratio+yPos;
		return ret;
	}
	public double getRadius() {return radius*scale;}
	public Color getColor() {return color;}
	public void paint(Graphics g) {
		super.paint(g);
		//g.fillOval((int)(.5+xPos-radius*scale), (int)(.5+yPos-radius*scale), (int)(.5+radius*scale*2), (int)(.5+radius*scale*2));
	}
	
}
