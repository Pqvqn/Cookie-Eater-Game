package oldsummonstorage;

import java.awt.*;

import ce3.*;
import cookies.*;
import entities.Entity;
import levels.*;

public class SummonSlash extends Summon{
	
	private double range;
	private double angle;
	private double holdfor;
	private final int SWING = 1, HOLD = 0;
	private int state;
	private int framecount;
	private double targetangle;
	private double region;
	private double amountturn;
	private double maxamountturn;
	private double accelamountturn;
	private double thickness;
	
	public SummonSlash(Board frame, Entity summoner, double r) {
		super(frame, summoner);
		region = r;
		range= 200*board.currFloor.getScale();
		holdfor = 100/board.getAdjustedCycle();
		maxamountturn = (region/60)*board.getAdjustedCycle();
		amountturn =  maxamountturn/100;
		accelamountturn = maxamountturn/50;
		mass = 100;
	}
	public void setRegion(double r) {
		region = r;
	}
	public void prepare() {
		thickness = user.getTotalRadius()*.6;
		x=user.getX();
		y=user.getY();
		angle = userVelAngle()-region/2;
		targetangle = userVelAngle()+region/2;
		state = SWING;
	}
	public void initialize() {
		framecount = 0;
	}
	public void execute() {
		if(isDed())return;
		thickness = user.getTotalRadius()*.6;
		x=user.getX();
		y=user.getY();
		if(Math.abs(amountturn)<maxamountturn) {
			amountturn+=Math.signum(amountturn)*accelamountturn;
		}
		if(state==HOLD) {
			if(framecount++>holdfor) {
				state=SWING;
				framecount=0;
			}
		}else {
			angle+=amountturn;
			if((amountturn>0 && angle>targetangle) || (amountturn<0 && angle<targetangle)) {
				amountturn = -Math.signum(amountturn)*maxamountturn/100;
				targetangle=targetangle+Math.signum(amountturn)*region;
				state=HOLD;
			}
		}
		super.execute();
	}
	public void end(boolean interrupted) {
		
	}
	public void collisionCookie(Cookie c) {
		user.hitCookie(c);
	}
	public void collisionWall(Wall w, boolean ghost, boolean shield) {
		if(shield) {
			amountturn*=-1;
			targetangle=2*angle-targetangle;
			return;
		}
		if(ghost)return;
		kill();
	}
	public void kill() {
		ded = true;
	}
	//if circle intersects an edge
	public boolean hitsCircle(double cX, double cY, double cR) {
		double altX = x+(thickness/2 * Math.cos(angle+Math.PI/2));
		double altY = y+(thickness/2 * Math.sin(angle+Math.PI/2));
		
		double wX = range * Math.cos(angle);
		double wY = range * Math.sin(angle);
		double hX = thickness * Math.cos(angle-Math.PI/2);
		double hY = thickness * Math.sin(angle-Math.PI/2);
		
		return Level.collidesLineAndCircle(altX, altY, altX+wX, altY+wY, cX, cY, cR) || 
				Level.collidesLineAndCircle(altX, altY, altX+hX, altY+hY, cX, cY, cR) || 
				Level.collidesLineAndCircle(altX+wX, altY+wY, altX+wX+hX, altY+wY+hY, cX, cY, cR) || 
				Level.collidesLineAndCircle(altX+hX, altY+hY, altX+wX+hX, altY+wY+hY, cX, cY, cR);
		
	}
	//if rectangle intersects an edge
	public boolean hitsRect(double rX, double rY, double rW, double rH) {
		double altX = x+(thickness/2 * Math.cos(angle+Math.PI/2));
		double altY = y+(thickness/2 * Math.sin(angle+Math.PI/2));
		
		double wX = range * Math.cos(angle);
		double wY = range * Math.sin(angle);
		double hX = thickness * Math.cos(angle-Math.PI/2);
		double hY = thickness * Math.sin(angle-Math.PI/2);
		
		return Level.collidesLineAndRect(altX, altY, altX+wX, altY+wY, rX, rY, rW, rH) || 
				Level.collidesLineAndRect(altX, altY, altX+hX, altY+hY, rX, rY, rW, rH) || 
				Level.collidesLineAndRect(altX+wX, altY+wY, altX+wX+hX, altY+wY+hY, rX, rY, rW, rH) || 
				Level.collidesLineAndRect(altX+hX, altY+hY, altX+wX+hX, altY+wY+hY, rX, rY, rW, rH);
			
	}
	public void collisionEntity(Object b, double hx, double hy, double omass, double oxv, double oyv, boolean ghost, boolean shield) {
		if(shield) {
			amountturn*=-1;
			targetangle=2*angle-targetangle;
			super.collisionEntity(b, hx, hy, omass, oxv, oyv, ghost, shield);
			return;
		}
		if(ghost)return;
		super.collisionEntity(b, hx, hy, omass, oxv, oyv, ghost, shield);
		kill();
	}
	public double[] circHitPoint(double cx, double cy, double cr) {
		double[] ret = {x,y};
		double altX = x+(thickness/2 * Math.cos(angle+Math.PI/2));
		double altY = y+(thickness/2 * Math.sin(angle+Math.PI/2));
		
		double wX = range * Math.cos(angle);
		double wY = range * Math.sin(angle);
		double hX = thickness * Math.cos(angle-Math.PI/2);
		double hY = thickness * Math.sin(angle-Math.PI/2);
		
		double x1=altX, y1=altY, x2=altX, y2=altY;
		
		if(Level.collidesLineAndCircle(altX, altY, altX+wX, altY+wY, cx, cy, cr)) {
			x1 = altX;
			y1 = altY;
			x2 = altX+wX;
			y2 = altY+wY;
		}else if(Level.collidesLineAndCircle(altX, altY, altX+hX, altY+hY, cx, cy, cr)) {
			x1 = altX;
			y1 = altY;
			x2 = altX+hX;
			y2 = altY+hY;
		}else if(Level.collidesLineAndCircle(altX+wX, altY+wY, altX+wX+hX, altY+wY+hY, cx, cy, cr)) {
			x1 = altX+wX;
			y1 = altY+wY;
			x2 = altX+wX+hX;
			y2 = altY+wY+hY;
		}else if(Level.collidesLineAndCircle(altX+hX, altY+hY, altX+wX+hX, altY+wY+hY, cx, cy, cr)) {
			x1 = altX+hX;
			y1 = altY+hY;
			x2 = altX+wX+hX;
			y2 = altY+wY+hY;
		}
		
		double lM = (y2-y1)/(x2-x1);
		double dM = -1/lM;
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
	public double getEdgeX() {return x+range * Math.cos(angle);}
	public double getEdgeY() {return y+range * Math.sin(angle);}
	public double getXVel() {
		int mult = 0;
		switch(state) {
		case HOLD:
			mult = 0;
			break;
		case SWING:
			mult = 1;
			break;
		}
		return user.getXVel()+mult*range*amountturn*Math.cos(angle);
	}
	public double getYVel() {
		int mult = 0;
		switch(state) {
		case HOLD:
			mult = 0;
			break;
		case SWING:
			mult = 1;
			break;
		}
		return user.getYVel()+mult*range*amountturn*Math.sin(angle);
	}
	public void paint(Graphics2D g2) {
		if(ded)return;
		g2.setColor(Color.WHITE);
		if(user.getGhosted())g2.setColor(new Color(255,255,255,100));
		if(user.getShielded())g2.setColor(new Color(50,200,210));
		g2.rotate(angle,x,y);
		g2.fillRect((int)(.5+x),(int)(.5+y-thickness/2),(int)(.5+range),(int)(.5+thickness));
		
		
	}
}
