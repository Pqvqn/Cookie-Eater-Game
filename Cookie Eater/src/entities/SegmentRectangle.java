package entities;

import java.awt.*;
import java.awt.geom.*;

import ce3.*;

public class SegmentRectangle extends Segment{

	private double ratio; //length/width
	
	public SegmentRectangle(Board frame, Entity host, double x, double y, double wid, double len, double a) {
		super(frame,host,x,y,a);
		ratio = len/wid;
		size = wid;
	}
	/*public boolean collidesWithRect(boolean extra, double x, double y, double w, double h, double a) {
		return board.currFloor.collidesRectAndRect(xPos, yPos, (extra)?getTotalWidth():getWidth(), (extra)?getTotalLength():getLength(), angle, x, y, w, h, a);
	}
	public boolean collidesWithCircle(boolean extra, double x, double y, double r) {
		return board.currFloor.collidesCircleAndRect(x,y,r,xPos, yPos,  (extra)?getTotalWidth():getWidth(), (extra)?getTotalLength():getLength(), angle);
	}
	/*public boolean collidesWithSummon(boolean extra, Summon2 s) {
		return s.hitsCircle(xPos,yPos,(extra)?getTotalRadius():getRadius());
	}
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
	public double getCenterX() {
		return xPos;
	}
	public double getCenterY() {
		return yPos;
	}
	/*public double getEdgeX() {
		return xPos-getWidth()/2;
	}
	public double getEdgeY() {
		return yPos-getLength()/2;
	}*/
	public void setDims(double w, double l) {
		setSize(w);
		ratio = l/w;
	}
	
	public Area getArea(boolean extra) {
		Rectangle2D.Double c = new Rectangle2D.Double(getCenterX()-(extra?getTotalWidth():getWidth())/2,getCenterY()-(extra?getTotalLength():getLength())/2,getWidth(),getLength());
		AffineTransform at = AffineTransform.getRotateInstance(angle,xPos,yPos);
		Shape cc = at.createTransformedShape(c);
		return new Area(cc);
	}
	public Rectangle getBounding(boolean extra) {
		//double rad = Math.sqrt(Math.pow(getWidth()/2,2)+Math.pow(getLength()/2,2));
		Rectangle2D.Double c = new Rectangle2D.Double(getCenterX()-(extra?getTotalWidth():getWidth())/2,getCenterY()-(extra?getTotalLength():getLength())/2,getWidth(),getLength());
		AffineTransform at = AffineTransform.getRotateInstance(angle,xPos,yPos);
		Shape cc = at.createTransformedShape(c);
		return cc.getBounds();
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2 = (Graphics2D)g;
		AffineTransform origt = g2.getTransform(); //transformation to reset to
		g2.rotate(getAngle(),xPos,yPos);
		g2.fillRect((int)(.5+xPos-getWidth()/2),(int)(.5+yPos-getLength()/2),(int)(.5+getWidth()),(int)(.5+getLength()));
		g2.setTransform(origt);
	}
	
}
