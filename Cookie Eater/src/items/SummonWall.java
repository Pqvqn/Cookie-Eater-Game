package items;

import java.awt.*;

import ce3.*;
import cookies.*;
import entities.Entity;
import levels.*;

public class SummonWall extends Summon{
	
	private double range;
	private double angle;
	private double thickness;
	private double length;
	
	public SummonWall(Board frame, Entity summoner, double r) {
		super(frame, summoner);
		range = r*board.currFloor.getScale();
		mass = 999999999;
	}
	public void setRange(double r) {
		range = r*board.currFloor.getScale();
	}
	public void prepare() {
		thickness = user.getTotalRadius();
		length = user.getTotalRadius()*5;
		x=user.getX();
		y=user.getY();
		angle = userVelAngle();
	}
	public void initialize() {
		boolean good = true;
		while(good) {
			x+=Math.cos(angle);
			y+=Math.sin(angle);
			for(Wall w:board.walls) {
				if(!user.getGhosted() && hitsRect(w.getX(),w.getY(),w.getW(),w.getH())) good=false;
			}
			if(Math.sqrt(Math.pow(x-user.getX(), 2)+Math.pow(y-user.getY(), 2))>=range) {
				good = false;
			}
		}
		x-=Math.cos(angle);
		y-=Math.sin(angle);
	}
	public void execute() {
		thickness = user.getTotalRadius()*.6;
		if(hitsCircle(user.getX(),user.getY(),user.getTotalRadius())) {
			double[] point = circHitPoint(user.getX(),user.getY(),user.getTotalRadius());
			user.collideAt(this, point[0], point[1], getXVel(), getYVel(), mass);
		}
		super.execute();
	}
	public void end(boolean interrupted) {
		
	}
	public void collisionCookie(Cookie c) {
		user.hitCookie(c);
	}
	public void collisionWall(Wall w, boolean ghost, boolean shield) {
	
	}
	//if circle intersects an edge
	public boolean hitsCircle(double cX, double cY, double cR) {
		double altX = x+(length/2 * Math.cos(angle+Math.PI/2));
		double altY = y+(length/2 * Math.sin(angle+Math.PI/2));
		
		double wX = thickness * Math.cos(angle);
		double wY = thickness * Math.sin(angle);
		double hX = length * Math.cos(angle-Math.PI/2);
		double hY = length * Math.sin(angle-Math.PI/2);
		
		return Level.collidesLineAndCircle(altX, altY, altX+wX, altY+wY, cX, cY, cR) || 
				Level.collidesLineAndCircle(altX, altY, altX+hX, altY+hY, cX, cY, cR) || 
				Level.collidesLineAndCircle(altX+wX, altY+wY, altX+wX+hX, altY+wY+hY, cX, cY, cR) || 
				Level.collidesLineAndCircle(altX+hX, altY+hY, altX+wX+hX, altY+wY+hY, cX, cY, cR);
		
	}
	//if rectangle intersects an edge
	public boolean hitsRect(double rX, double rY, double rW, double rH) {
		double altX = x+(length/2 * Math.cos(angle+Math.PI/2));
		double altY = y+(length/2 * Math.sin(angle+Math.PI/2));
		
		double wX = thickness * Math.cos(angle);
		double wY = thickness * Math.sin(angle);
		double hX = length * Math.cos(angle-Math.PI/2);
		double hY = length * Math.sin(angle-Math.PI/2);
		
		return Level.collidesLineAndRect(altX, altY, altX+wX, altY+wY, rX, rY, rW, rH) || 
				Level.collidesLineAndRect(altX, altY, altX+hX, altY+hY, rX, rY, rW, rH) || 
				Level.collidesLineAndRect(altX+wX, altY+wY, altX+wX+hX, altY+wY+hY, rX, rY, rW, rH) || 
				Level.collidesLineAndRect(altX+hX, altY+hY, altX+wX+hX, altY+wY+hY, rX, rY, rW, rH);
			
	}
	public void collisionEntity(Object b, double hx, double hy, double omass, double oxv, double oyv, boolean ghost, boolean shield) {
		//if(shield)user.collideAt(b,hx,hy,oxv,oyv,omass);
		//if(ghost)return;
	}
	
	public double[] circHitPoint(double cx, double cy, double cr) {
		double[] ret = {x,y};
		double altX = x+(length/2 * Math.cos(angle+Math.PI/2));
		double altY = y+(length/2 * Math.sin(angle+Math.PI/2));
		
		double wX = thickness * Math.cos(angle);
		double wY = thickness * Math.sin(angle);
		double hX = length * Math.cos(angle-Math.PI/2);
		double hY = length * Math.sin(angle-Math.PI/2);
		
		double x1=altX, y1=altY, x2=altX, y2=altY;
		if(Level.collidesLineAndCircle(altX+wX, altY+wY, altX+wX+hX, altY+wY+hY, cx, cy, cr)) {
			x1 = altX+wX;
			y1 = altY+wY;
			x2 = altX+wX+hX;
			y2 = altY+wY+hY;
		}else if(Level.collidesLineAndCircle(altX, altY, altX+hX, altY+hY, cx, cy, cr)) {
			x1 = altX;
			y1 = altY;
			x2 = altX+hX;
			y2 = altY+hY;
		}else if(Level.collidesLineAndCircle(altX, altY, altX+wX, altY+wY, cx, cy, cr)) {
			x1 = altX;
			y1 = altY;
			x2 = altX+wX;
			y2 = altY+wY;
		}else if(Level.collidesLineAndCircle(altX+hX, altY+hY, altX+wX+hX, altY+wY+hY, cx, cy, cr)) {
			x1 = altX+hX;
			y1 = altY+hY;
			x2 = altX+wX+hX;
			y2 = altY+wY+hY;
		}else {
		
		}
		double dM = 0;
		if(x2-x1!=0) {
			double lM = (y2-y1)/(x2-x1);
			dM = -1/lM;
		}
		double dL = Math.sqrt(1+dM*dM);
		double ratio = cr/dL;
		double fX = ratio;
		double fY = ratio*dM;
		if(Level.collidesLineAndLine(x1,y1,x2,y2,cx+fX,cy+fY,cx-fX,cy-fY)) {
			ret = Level.lineIntersection(x1,y1,x2,y2,cx+fX,cy+fY,cx-fX,cy-fY);
		}else if(Level.lineLength(x1,y1,cx,cy)<=cr) {
			ret[0] = x1;
			ret[1] = y1;
		}else if(Level.lineLength(x2,y2,cx,cy)<=cr) {
			ret[0] = x2;
			ret[1] = y2;
		}
		return ret;
	}
	public double getEdgeX() {return 0;}
	public double getEdgeY() {return 0;}
	public double getXVel() {
		if(user.getShielded()) {
			return 5*board.currFloor.getScale()*Math.cos(angle);
		}else {
			return 0;
		}
	}
	public double getYVel() {
		if(user.getShielded()) {
			return 5*board.currFloor.getScale()*Math.sin(angle);
		}else {
			return 0;
		}
	}
	public void paint(Graphics2D g2) {
		g2.setColor(Color.WHITE);
		if(user.getGhosted())g2.setColor(new Color(255,255,255,100));
		if(user.getShielded())g2.setColor(new Color(50,200,210));
		g2.rotate(angle,x,y);
		g2.fillRect((int)(.5+x),(int)(.5+y-length/2),(int)(.5+thickness),(int)(.5+length));
		
		
	}
}
