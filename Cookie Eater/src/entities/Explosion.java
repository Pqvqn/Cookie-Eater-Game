package entities;

import java.awt.*;

import ce3.*;

public class Explosion extends Entity{

	private SegmentCircle boom; //segment body
	private double inc; //amount to increase radius by per tick
	
	public Explosion(Board frame, int cycletime, int xp, int yp, double rad, int time, Entity initiator) {
		super(frame,cycletime);
		x = xp;
		y = yp;
		inc = (double)cycletime/time * rad;
		buildBody();
		orientParts();
	}
	
	public void runUpdate() {
		setRadius(getRadius()+inc);
		orientParts();
	}
	protected void buildBody() {
		parts.add(boom = new SegmentCircle(board,this,x,y,getRadius(),0));
	}
	public void orientParts() {
		boom.setLocation(x,y);
		boom.setSize(getRadius());
		super.orientParts();
	}
	public void paint(Graphics g) {
		g.setColor(new Color(255,255,255,100));
		g.fillOval((int)(.5+boom.getCenterX()-boom.getRadius()),(int)(.5+boom.getCenterY()-boom.getRadius()),(int)(.5+boom.getRadius()*2),(int)(.5+boom.getRadius()*2));
	}
	
}
