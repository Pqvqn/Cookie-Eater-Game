package items;

import java.awt.*;

import ce3.*;
import cookies.*;
import levels.*;

public class SummonProjectile extends Summon{
	
	private double speed, xSpeed, ySpeed;
	private double angle;
	private double angle_offset;
	private double radius;
	
	public SummonProjectile(Board frame, Eater summoner, double s, double offset) {
		super(frame, summoner);
		speed = s*board.currFloor.getScale();
		angle_offset = offset;
	}
	public void setSpeed(double s) {
		speed = s*board.currFloor.getScale();
	}
	public void prepare() {
		radius = user.getTotalRadius()*.3;
		x=user.getX();
		y=user.getY();
		angle = playerVelAngle()+angle_offset;
	}
	public void initialize() {
		xSpeed = Math.cos(angle)*speed;
		ySpeed = Math.sin(angle)*speed;
	}
	public void execute() {
		if(isDed())return;
		radius = user.getTotalRadius()*.3;
		x+=xSpeed;
		y+=ySpeed;
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
			int rx = w.getX(), ry = w.getY(), rw = w.getW(), rh = w.getH();
			if(y>ry+rh) {
				ySpeed*=-1;
			}else if(y<ry) {
				ySpeed*=-1;
			}else if(x>rx+rw) {
				xSpeed*=-1;
			}else if(x<rx) {
				xSpeed*=-1;
			}
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
		g2.rotate(angle,x,y);
		g2.fillOval((int)(.5+x-radius),(int)(.5+y-radius),(int)(.5+radius*2),(int)(.5+radius*2));
		
		
	}
}
