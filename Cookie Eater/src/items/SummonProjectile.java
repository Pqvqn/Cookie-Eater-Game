package items;

import java.awt.*;

import ce3.*;
import cookies.*;
import levels.*;

public class SummonProjectile extends Summon{
	
	private double speed, xSpeed, ySpeed;
	private double angle;
	private double radius;
	private boolean ded;
	
	public SummonProjectile(Board frame, Eater summoner, double s) {
		super(frame, summoner);
		speed = s*board.currFloor.getScale();
		ded = false;
	}
	public void setSpeed(double s) {
		speed = s*board.currFloor.getScale();
	}
	public void prepare() {
		radius = player.getExtraRadius()*.3;
		x=player.getX();
		y=player.getY();
		angle = playerDirAngle();
	}
	public void initialize() {
		xSpeed = Math.cos(playerVelAngle())*speed;
		ySpeed = Math.sin(playerVelAngle())*speed;
	}
	public void execute() {
		if(ded)return;
		radius = player.getExtraRadius()*.3;
		for(int i=0; i<board.cookies.size(); i++) {
			Cookie c = board.cookies.get(i);
			if(circHitCircle(c.getX(),c.getY(),c.getRadius())) {
				c.kill(true);}
		}
		for(int i=0; i<board.walls.size(); i++) {
			Wall w = board.walls.get(i);
			if(circHitRect(w.getX(),w.getY(),w.getW(),w.getH())) {
				kill();
			}
		}
		x+=xSpeed;
		y+=ySpeed;
	}
	public void end(boolean interrupted) {
		
	}
	public void kill() {
		ded = true;
	}
	//if circle intersects an edge
	public boolean circHitCircle(double cX, double cY, double cR) {
		//System.out.println(angle);
		return Level.lineLength(x, y, cX, cY) <= cR+radius;
	}
	//if rectangle intersects an edge
	public boolean circHitRect(double rX, double rY, double rW, double rH) {
		//System.out.println(angle);
		return Level.collidesCircleAndRect(x,y,radius,rX,rY,rW,rH);
			
	}
	public void paint(Graphics2D g2) {
		if(ded)return;
		g2.setColor(Color.WHITE);
		g2.rotate(angle,x,y);
		g2.fillOval(x-(int)(.5+radius),y-(int)(.5+radius),(int)(.5+radius*2),(int)(.5+radius*2));
		
		
	}
}
