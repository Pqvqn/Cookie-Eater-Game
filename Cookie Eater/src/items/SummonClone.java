package items;

import java.awt.*;

import ce3.*;
import cookies.*;
import entities.Entity;
import levels.*;

public class SummonClone extends Summon{
	
	private double startx,starty,prevx,prevy;
	private double radius;
	private boolean invertx, inverty, swapxy;
	
	public SummonClone(Board frame, Entity summoner, double sx, double sy, boolean xInv, boolean yInv, boolean swap) {
		super(frame, summoner);
		startx = sx;
		starty = sy;
		prevx = sx;
		prevy = sy;
		invertx = xInv;
		inverty = yInv;
		swapxy = swap;
		mass = user.getMass();
	}
	public void prepare() {
		radius = user.getTotalRadius();
		x=user.getX();
		y=user.getY();
	}
	public void initialize() {
	}
	public void execute() {
		if(isDed())return;
		radius = user.getTotalRadius();
		prevx = x;
		prevy = y;
		if(swapxy) {
			y = (inverty) ? starty-(user.getX()-startx) : starty+(user.getX()-startx);
			x = (invertx) ? startx-(user.getY()-starty) : startx+(user.getY()-starty);
		}else {
			x = (invertx) ? 2*startx-user.getX() : user.getX();
			y = (inverty) ? 2*starty-user.getY() : user.getY();
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
		if(shield) {
			x = prevx;
			y = prevy;
			return;
		}
		if(ghost)return;
		kill();
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
		if(shield) {
			x = prevx;
			y = prevy;
			super.collisionEntity(b, hx, hy, omass, oxv, oyv, ghost, shield);
			return;
		}
		if(ghost)return;
		super.collisionEntity(b, hx, hy, omass, oxv, oyv, ghost, shield);
		kill();
	}
	public double[] circHitPoint(double cx, double cy, double cr) {
		double[] ret = {x,y};
		double ratio = radius/Level.lineLength(cx, cy, x, y);
		ret[0] = (cx-x)*ratio+x;
		ret[1] = (cy-y)*ratio+y;
		return ret;
	}
	public double getXVel() {
		if(swapxy) {
			return (invertx) ? -user.getYVel() : user.getYVel();
		}else {
			return (invertx) ? -user.getXVel() : user.getXVel();
		}
	}
	public double getYVel() {
		if(swapxy) {
			return (inverty) ? -user.getXVel() : user.getXVel();
		}else {
			return (inverty) ? -user.getYVel() : user.getYVel();
		}
	}
	public void paint(Graphics2D g2) {
		if(ded)return;
		g2.setColor(Color.WHITE);
		if(user.getGhosted())g2.setColor(new Color(255,255,255,100));
		if(user.getShielded())g2.setColor(new Color(50,200,210));
		g2.fillOval((int)(.5+x-radius),(int)(.5+y-radius),(int)(.5+radius*2),(int)(.5+radius*2));
		
		
	}
}
