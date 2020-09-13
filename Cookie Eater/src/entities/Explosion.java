package entities;

import java.awt.*;

import ce3.*;

public class Explosion extends Entity{

	private SegmentCircle boom; //segment body
	private double inc; //amount to increase radius by per tick
	private double maxRad;
	
	public Explosion(Board frame, int cycletime, int xp, int yp, double rad, int time, Entity initiator) {
		super(frame,cycletime);
		x = xp;
		y = yp;
		inc = (double)cycletime/time * rad;
		maxRad = rad;
		buildBody();
		orientParts();
	}
	
	public void runUpdate() {
		if(ded)return;
		setRadius(getRadius()+inc); //increase radius
		if(getRadius()>maxRad)kill(); //kill if too large
		orientParts();
	}
	protected void buildBody() {
		parts.add(boom = new SegmentCircle(board,this,x,y,getRadius(),0));
	}
	//remove explosion
	public void kill() {
		ded = true;
		setRadius(0);
		//board.effects.remove(this);
	}
	
	public double getXVel() {return inc;}
	public double getYVel() {return inc;}
	
	public void orientParts() {
		boom.setLocation(x,y);
		boom.setSize(getRadius());
		super.orientParts();
	}
	public void paint(Graphics g) {
		int opac = 255-(int)(.5+((double)getRadius()/maxRad)*255);
		g.setColor(new Color(255,255,255,opac));
		g.fillOval((int)(.5+boom.getCenterX()-boom.getRadius()),(int)(.5+boom.getCenterY()-boom.getRadius()),(int)(.5+boom.getRadius()*2),(int)(.5+boom.getRadius()*2));
	}
	
}
