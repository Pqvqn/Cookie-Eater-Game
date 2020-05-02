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
		return Level.collidesCircleAndRect(xPos, yPos, radius, x, y, w, h);
	}
	public boolean collidesWithCircle(double x, double y, double r) {
		return Level.lineLength(x, y, xPos, yPos) <= r+radius;
	}
	public double[] rectHitPoint(double rx, double ry, double rw, double rh) {
		double[] ret = {xPos,yPos};
		boolean xB=false,yB=false;
		if(yPos>ry+rh) {
			ret[1] = yPos-radius;
			yB=true;
		}else if(yPos<ry) {
			ret[1] = yPos+radius;
			yB=true;
		}else if(xPos>rx+rw) {
			ret[0] = xPos-radius;
			xB=true;
		}else if(xPos<rx) {
			ret[0] = xPos+radius;
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
		double ratio = radius/Level.lineLength(cx, cy, xPos, yPos);
		ret[0] = (cx-xPos)*ratio+xPos;
		ret[1] = (cy-yPos)*ratio+yPos;
		return ret;
	}
	public void paint(Graphics g) {
		super.paint(g);
		g.fillOval((int)(.5+xPos-radius), (int)(.5+yPos-radius), (int)(.5+radius*2), (int)(.5+radius*2));
	}
	
}
