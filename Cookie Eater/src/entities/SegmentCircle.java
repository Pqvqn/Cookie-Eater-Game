package entities;

import java.awt.*;

import ce3.*;
import levels.*;
import items.*;

public class SegmentCircle extends Segment{

	private double radius;
	
	public SegmentCircle(Board frame, Entity host, double x, double y, double rad, double a) {
		super(frame,host,x,y,a);
		radius = rad;
		size = rad;
	}
	public boolean collidesWithRect(boolean extra, double x, double y, double w, double h) {
		return Level.collidesCircleAndRect(xPos, yPos, (extra)?getTotalRadius():getRadius(), x, y, w, h);
	}
	public boolean collidesWithCircle(boolean extra, double x, double y, double r) {
		return Level.lineLength(x, y, xPos, yPos) <= r+((extra)?getTotalRadius():getRadius());
	}
	public boolean collidesWithSummon(boolean extra, Summon s) {
		return s.hitsCircle(xPos,yPos,(extra)?getTotalRadius():getRadius());
	}
	public double[] rectHitPoint(boolean extra, double rx, double ry, double rw, double rh) {
		return Level.circAndRectHitPoint(xPos,yPos,(extra)?getTotalRadius():getRadius(),rx,ry,rw,rh);
	}
	public double[] circHitPoint(boolean extra, double cx, double cy, double cr) {
		return Level.circAndCircHitPoint(xPos,yPos,(extra)?getTotalRadius():getRadius(),cx,cy,cr);
	}
	public double[] summonHitPoint(boolean extra, Summon s) {
		return s.circHitPoint(xPos, yPos, (extra)?getTotalRadius():getRadius());
	}
	
	public double getRadius() {return radius*scale;}
	public void setSize(int s) {
		super.setSize(s);
		radius=s;}
	public double getTotalRadius() {return getRadius()+extra_size*scale;}
	
	public void paint(Graphics g) {
		super.paint(g);
		//g.fillOval((int)(.5+xPos-radius*scale), (int)(.5+yPos-radius*scale), (int)(.5+radius*scale*2), (int)(.5+radius*scale*2));
	}
	
}
