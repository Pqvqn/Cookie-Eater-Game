package items;

import java.awt.*;
import java.util.*;

import ce3.*;
import cookies.*;
import entities.*;
import levels.*;

public class SummonHook extends Summon{
	
	private double speed, xSpeed, ySpeed;
	private double angle;
	private double radius;
	private int state;
	private final int REACH = 0, PULL = 1;
	private Entity tracked;
	private double xOffset,yOffset;
	
	public SummonHook(Board frame, Entity summoner, double s) {
		super(frame, summoner);
		speed = s*board.currFloor.getScale();
		mass = 0;
	}
	public void setSpeed(double s) {
		speed = s*board.currFloor.getScale();
	}
	public void prepare() {
		radius = user.getTotalRadius()*.3;
		//mass = (user.getTotalRadius()/user.getRadius())*50;
		x=user.getX();
		y=user.getY();
		angle = userVelAngle();
	}
	public void initialize() {
		xSpeed = Math.cos(angle)*speed;
		ySpeed = Math.sin(angle)*speed;
		user.collideAt(this,x+xSpeed,y+ySpeed,xSpeed,ySpeed,mass);
		state = REACH;
	}
	public void execute() {
		if(isDed())return;
		radius = user.getTotalRadius()*.3;
		if(state==REACH) {
			x+=xSpeed;
			y+=ySpeed;
		}else if(state==PULL) {
			if(tracked!=null) {
				x = tracked.getX()+xOffset;
				y = tracked.getY()+yOffset;
			}
			boolean hits = false; //pull until user gets to hook
			ArrayList<Segment> s = user.getParts();
			for(int i=0; i<s.size(); i++) {
				if(s.get(i).collidesWithCircle(true,x,y,radius))hits = true;
			}
			if(hits) {
				ded = true;
				user.averageVels(0,0);
			}else {
				double rat = speed/Math.sqrt(Math.pow(user.getX()-x, 2)+Math.pow(user.getY()-y, 2));
				user.averageVels(rat*(x-user.getX()),rat*(y-user.getY()));
			}
			
		}
		super.execute();
	}
	public void end(boolean interrupted) {
		
	}
	public void kill() {
		ded = true;
	}
	public void collisionCookie(Cookie c) {
		user.hitCookie(c);
	}
	public void collisionWall(Wall w, boolean ghost, boolean shield) {
		state = PULL;
		x-=xSpeed;
		y-=ySpeed;
	}
	//if circle intersects an edge
	public boolean hitsCircle(double cX, double cY, double cR) {
		return Level.lineLength(x, y, cX, cY) <= cR+radius;
	}
	//if rectangle intersects an edge
	public boolean hitsRect(double rX, double rY, double rW, double rH) {
		return Level.collidesCircleAndRect(x,y,radius,rX,rY,rW,rH);		
	}
	public void collisionEntity(Object b, double hx, double hy, double omass, double oxv, double oyv, boolean ghost, boolean shield) {
		state = PULL;
		x-=xSpeed;
		y-=ySpeed;
		if(b instanceof Entity) {
			tracked = (Entity)b;
			xOffset = x-tracked.getX();
			yOffset = y-tracked.getY();
		}
	}
	public double[] circHitPoint(double cx, double cy, double cr) {
		double[] ret = {x,y};
		double ratio = radius/Level.lineLength(cx, cy, x, y);
		ret[0] = (cx-x)*ratio+x;
		ret[1] = (cy-y)*ratio+y;
		return ret;
	}
	public double getXVel() {return xSpeed;}
	public double getYVel() {return ySpeed;}
	public void paint(Graphics2D g2) {
		if(ded)return;
		g2.setColor(Color.WHITE);
		if(user.getGhosted())g2.setColor(new Color(255,255,255,100));
		if(user.getShielded())g2.setColor(new Color(50,200,210));
		g2.drawLine((int)(.5+user.getX()),(int)(.5+user.getY()),(int)(.5+x),(int)(.5+y));
		g2.rotate(angle,x,y);
		g2.fillOval((int)(.5+x-radius),(int)(.5+y-radius),(int)(.5+radius*2),(int)(.5+radius*2));
		
	}
}
