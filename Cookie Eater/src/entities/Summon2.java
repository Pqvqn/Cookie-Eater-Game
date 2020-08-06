package entities;

import java.awt.*;

import ce3.*;
import levels.*;

public class Summon2 extends Entity{
	
	private Entity user;
	private boolean anchored; //whether item is anchored to the summoner
	private int edgex,edgey; //x and y position of edge 
	
	public Summon2(Board frame, Entity summoner) {
		super(frame);
		user = summoner;
		anchored = true;
		x = user.getX();
		y = user.getY();
		special_frames = user.getSpecialFrames();
	}
	public void runUpdate() {
		if(ded)return;
		super.runUpdate();
		if(anchored) { //if anchored to the user, move with user
			setXVel(user.getXVel());
			setYVel(user.getYVel());
		}
		user.setSpecialFrames(special_frames); //keep player special use same as summon's
	}
	//code anchor points and whatnot
	//also all collision stuff
	public double getX() {return x;}
	public double getY() {return y;}
	public void setX(double xp) {x=xp;}
	public void setY(double yp) {y=yp;}
	
	public double getXVel() {return x_velocity;}
	public double getYVel() {return y_velocity;}
	public void setXVel(double a) {x_velocity = a;}
	public void setYVel(double a) {y_velocity = a;}
	
	//if circle intersects an edge
	public boolean hitsCircle(double cX, double cY, double cR) {
		double angle = Math.atan2(edgey-y,edgex-x);
		double length = Level.lineLength(edgex,edgey,x,y);
		double thickness = getRadius();
		
		double altX = x+(thickness/2 * Math.cos(angle+Math.PI/2));
		double altY = y+(thickness/2 * Math.sin(angle+Math.PI/2));
		
		double wX = length * Math.cos(angle);
		double wY = length * Math.sin(angle);
		double hX = thickness * Math.cos(angle-Math.PI/2);
		double hY = thickness * Math.sin(angle-Math.PI/2);
		
		return Level.collidesLineAndCircle(altX, altY, altX+wX, altY+wY, cX, cY, cR) || 
				Level.collidesLineAndCircle(altX, altY, altX+hX, altY+hY, cX, cY, cR) || 
				Level.collidesLineAndCircle(altX+wX, altY+wY, altX+wX+hX, altY+wY+hY, cX, cY, cR) || 
				Level.collidesLineAndCircle(altX+hX, altY+hY, altX+wX+hX, altY+wY+hY, cX, cY, cR);
		
	}
	//if rectangle intersects an edge
	public boolean hitsRect(double rX, double rY, double rW, double rH) {
		double angle = Math.atan2(edgey-y,edgex-x);
		double length = Level.lineLength(edgex,edgey,x,y);
		double thickness = getRadius();
		
		double altX = x+(thickness/2 * Math.cos(angle+Math.PI/2));
		double altY = y+(thickness/2 * Math.sin(angle+Math.PI/2));
		
		double wX = length * Math.cos(angle);
		double wY = length * Math.sin(angle);
		double hX = thickness * Math.cos(angle-Math.PI/2);
		double hY = thickness * Math.sin(angle-Math.PI/2);
		
		return Level.collidesLineAndRect(altX, altY, altX+wX, altY+wY, rX, rY, rW, rH) || 
				Level.collidesLineAndRect(altX, altY, altX+hX, altY+hY, rX, rY, rW, rH) || 
				Level.collidesLineAndRect(altX+wX, altY+wY, altX+wX+hX, altY+wY+hY, rX, rY, rW, rH) || 
				Level.collidesLineAndRect(altX+hX, altY+hY, altX+wX+hX, altY+wY+hY, rX, rY, rW, rH);
			
	}
	public double[] circHitPoint(double cx, double cy, double cr) {
		double angle = Math.atan2(edgey-y,edgex-x);
		double length = Level.lineLength(edgex,edgey,x,y);
		double thickness = getRadius();
		
		double[] ret = {x,y};
		double altX = x+(thickness/2 * Math.cos(angle+Math.PI/2));
		double altY = y+(thickness/2 * Math.sin(angle+Math.PI/2));
		
		double wX = length * Math.cos(angle);
		double wY = length * Math.sin(angle);
		double hX = thickness * Math.cos(angle-Math.PI/2);
		double hY = thickness * Math.sin(angle-Math.PI/2);
		
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

	public void paint(Graphics2D g2) {
		g2.setColor(Color.WHITE);
		if(user.getGhosted())g2.setColor(new Color(255,255,255,100));
		if(user.getShielded())g2.setColor(new Color(50,200,210));
		double angle = Math.atan2(edgey-y,edgex-x);
		double length = Level.lineLength(edgex,edgey,x,y);
		g2.rotate(angle,x,y);
		g2.fillRect((int)(.5+x),(int)(.5+y-getRadius()/2),(int)(.5+length),(int)(.5+getRadius()));
		
		
	}
}
