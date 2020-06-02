package enemies;

import java.awt.*;

import ce3.*;
import levels.*;
import items.*;

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
	public boolean collidesWithSummon(Summon s) {
		return s.hitsCircle(xPos,yPos,radius*scale);
	}
	public double[] rectHitPoint(double rx, double ry, double rw, double rh) {
		return Level.circAndRectHitPoint(xPos,yPos,radius*scale,rx,ry,rw,rh);
	}
	public double[] circHitPoint(double cx, double cy, double cr) {
		return Level.circAndCircHitPoint(xPos,yPos,radius*scale,cx,cy,cr);
	}
	public double[] summonHitPoint(Summon s) {
		return s.circHitPoint(xPos, yPos, radius*scale);
	}
	
	public double getRadius() {return radius*scale;}
	public Color getColor() {return color;}
	public void paint(Graphics g) {
		super.paint(g);
		//g.fillOval((int)(.5+xPos-radius*scale), (int)(.5+yPos-radius*scale), (int)(.5+radius*scale*2), (int)(.5+radius*scale*2));
	}
	
}
