package items;

import java.awt.*;

import ce3.*;
import cookies.*;
import levels.*;

public class SummonExplosion extends Summon{
	
	private double maximum_radius;
	private double expansion_amount;
	private double radius;
	private double opacity;
	private double opacity_decrease;

	public SummonExplosion(Board frame, Eater summoner, double maxRad, double xP, double yP) {
		super(frame, summoner);
		maximum_radius = maxRad*board.currFloor.getScale();
		expansion_amount = maximum_radius/300*board.getAdjustedCycle();
		opacity = 150;
		opacity_decrease = opacity/100*board.getAdjustedCycle();
		x=xP;
		y=yP;
	}
	public void setMax(double mr) {
		maximum_radius = mr;
	}
	public void prepare() {
		radius = 0;
	}
	public void initialize() {
	}
	public void execute() {
		if(radius>maximum_radius) {
			opacity-=opacity_decrease;
			if(opacity<=0)
			kill();
		}
		if(isDed())return;
		radius+=expansion_amount;
		super.execute();
	}
	public void end(boolean interrupted) {
		
	}
	public void kill() {
		ded = true;
	}
	public void collisionCookie(Cookie c) {
		c.kill(true);
	}
	public void collisionWall(Wall w, boolean ghost, boolean shield) {
	}
	//if circle intersects an edge
	public boolean hitsCircle(double cX, double cY, double cR) {
		//System.out.println(angle);
		return Level.lineLength(x, y, cX, cY) <= cR+radius;
	}
	//if rectangle intersects an edge
	public boolean hitsRect(double rX, double rY, double rW, double rH) {
		//System.out.println(angle);
		return Level.collidesCircleAndRect(x,y,radius,rX,rY,rW,rH);
			
	}
	public void paint(Graphics2D g2) {
		
		if(ded)return;
		g2.setColor(new Color(255,255,255,(int)(.5+opacity)));
		g2.fillOval((int)(.5+x-radius),(int)(.5+y-radius),(int)(.5+radius*2),(int)(.5+radius*2));
		
		
	}
}
