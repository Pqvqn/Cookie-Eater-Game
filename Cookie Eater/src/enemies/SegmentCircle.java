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
		size = rad;
	}
	public boolean collidesWithRect(boolean extra, double x, double y, double w, double h) {
		return Level.collidesCircleAndRect(xPos, yPos, (extra)?getRadius():getTotalRadius(), x, y, w, h);
	}
	public boolean collidesWithCircle(boolean extra, double x, double y, double r) {
		return Level.lineLength(x, y, xPos, yPos) <= r+((extra)?getRadius():getTotalRadius());
	}
	public boolean collidesWithSummon(boolean extra, Summon s) {
		return s.hitsCircle(xPos,yPos,(extra)?getRadius():getTotalRadius());
	}
	public double[] rectHitPoint(boolean extra, double rx, double ry, double rw, double rh) {
		return Level.circAndRectHitPoint(xPos,yPos,(extra)?getRadius():getTotalRadius(),rx,ry,rw,rh);
	}
	public double[] circHitPoint(boolean extra, double cx, double cy, double cr) {
		return Level.circAndCircHitPoint(xPos,yPos,(extra)?getRadius():getTotalRadius(),cx,cy,cr);
	}
	public double[] summonHitPoint(boolean extra, Summon s) {
		return s.circHitPoint(xPos, yPos, (extra)?getRadius():getTotalRadius());
	}
	
	public double getRadius() {return radius*scale;}
	public void setSize(int s) {
		super.setSize(s);
		radius=s;}
	public double getTotalRadius() {return getRadius()+extra_size*scale;}
	public Color getColor() {return color;}
	public void paint(Graphics g) {
		super.paint(g);
		//g.fillOval((int)(.5+xPos-radius*scale), (int)(.5+yPos-radius*scale), (int)(.5+radius*scale*2), (int)(.5+radius*scale*2));
	}
	
}
