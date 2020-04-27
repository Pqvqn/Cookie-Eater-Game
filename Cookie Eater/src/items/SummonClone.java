package items;

import java.awt.*;

import ce3.*;
import cookies.*;
import levels.*;

public class SummonClone extends Summon{
	
	private double x,y,startx,starty,prevx,prevy;
	private double radius;
	private boolean ded;
	private boolean invertx, inverty, swapxy;
	
	public SummonClone(Board frame, Eater summoner, double sx, double sy, boolean xInv, boolean yInv, boolean swap) {
		super(frame, summoner);
		startx = sx;
		starty = sy;
		prevx = sx;
		prevy = sy;
		ded = false;
		invertx = xInv;
		inverty = yInv;
		swapxy = swap;
	}
	public void prepare() {
		radius = user.getTotalRadius();
		x=user.getX();
		y=user.getY();
	}
	public void initialize() {
	}
	public void execute() {
		if(ded)return;
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
		c.kill(true);
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
		g2.setColor(Color.WHITE);
		if(user.getGhosted())g2.setColor(new Color(255,255,255,100));
		if(user.getShielded())g2.setColor(new Color(50,200,210));
		g2.fillOval((int)(.5+x-radius),(int)(.5+y-radius),(int)(.5+radius*2),(int)(.5+radius*2));
		
		
	}
}
