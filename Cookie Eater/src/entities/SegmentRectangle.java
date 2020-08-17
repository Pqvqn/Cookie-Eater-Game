package entities;

import java.awt.*;
import java.awt.geom.*;

import ce3.*;
import levels.*;
import items.*;

public class SegmentRectangle extends Segment{

	private double ratio; //length/width
	
	public SegmentRectangle(Board frame, Entity host, double x, double y, double wid, double len, double a) {
		super(frame,host,x,y,a);
		ratio = len/wid;
		size = wid;
	}
	public boolean collidesWithRect(boolean extra, double x, double y, double w, double h, double a) {
		return board.currFloor.collidesRectAndRect(xPos, yPos, (extra)?getTotalWidth():getWidth(), (extra)?getTotalLength():getLength(), angle, x, y, w, h, a);
	}
	public boolean collidesWithCircle(boolean extra, double x, double y, double r) {
		return board.currFloor.collidesCircleAndRect(x,y,r,xPos, yPos,  (extra)?getTotalWidth():getWidth(), (extra)?getTotalLength():getLength(), angle);
	}
	/*public boolean collidesWithSummon(boolean extra, Summon2 s) {
		return s.hitsCircle(xPos,yPos,(extra)?getTotalRadius():getRadius());
	}*/
	public double[] rectHitPoint(boolean extra, double rx, double ry, double rw, double rh, double ra) {
		return board.currFloor.rectAndRectHitPoint(xPos,yPos,(extra)?getTotalWidth():getWidth(),(extra)?getTotalLength():getLength(),angle,rx,ry,rw,rh,ra);
	}
	public double[] circHitPoint(boolean extra, double cx, double cy, double cr) {
		return board.currFloor.circAndRectHitPoint(cx,cy,cr,xPos,yPos, (extra)?getTotalWidth():getWidth(), (extra)?getTotalLength():getLength(),angle);
	}
	/*public double[] summonHitPoint(boolean extra, Summon2 s) {
		return s.circHitPoint(xPos, yPos, (extra)?getTotalRadius():getRadius());
	}*/
	public double getWidth() {return getSize();}
	public double getLength() {return getSize()*ratio;}
	public double getTotalWidth() {return getWidth()+extra_size*scale;}
	public double getTotalLength() {return getLength()+extra_size*scale;}
	public void setSize(double s) {
		super.setSize(s);
	}
	public double getCenterX() { //not actually center
		return xPos;
	}
	public double getCenterY() {
		return yPos;
	}
	public void setDims(double w, double l) {
		setSize(w);
		ratio = l/w;
	}
	
	public Area getArea() {
		Rectangle2D.Double c = new Rectangle2D.Double(xPos,yPos,getWidth(),getLength());
		return new Area(c);
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		
	}
	
}
