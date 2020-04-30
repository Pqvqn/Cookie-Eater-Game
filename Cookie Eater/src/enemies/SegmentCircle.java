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
	public void paint(Graphics g) {
		super.paint(g);
		g.fillOval((int)(.5+xPos-radius), (int)(.5+yPos-radius), (int)(.5+radius*2), (int)(.5+radius*2));
	}
	
}
