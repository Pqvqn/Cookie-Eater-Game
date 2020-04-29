package items;

import java.awt.*;

import ce3.*;
import cookies.*;
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
	
	public SummonSlash(Board frame, Eater summoner, double r) {
		super(frame, summoner);
		region = r;
		range= 200*board.currFloor.getScale();
		holdfor = 100/board.getAdjustedCycle();
		maxamountturn = (region/60)*board.getAdjustedCycle();
		amountturn =  maxamountturn/100;
		accelamountturn = maxamountturn/50;
	}
	public void setRegion(double r) {
		region = r;
	}
	public void prepare() {
		thickness = user.getTotalRadius()*.6;
		x=user.getX();
		y=user.getY();
		angle = playerVelAngle()-region/2;
		targetangle = playerVelAngle()+region/2;
		state = SWING;
	}
	public void initialize() {
		framecount = 0;
	}
	public void execute() {
		if(ded)return;
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
		c.kill(true);
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
		//System.out.println(angle);
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
		//System.out.println(angle);
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
	public void paint(Graphics2D g2) {
		if(ded)return;
		g2.setColor(Color.WHITE);
		if(user.getGhosted())g2.setColor(new Color(255,255,255,100));
		if(user.getShielded())g2.setColor(new Color(50,200,210));
		g2.rotate(angle,x,y);
		g2.fillRect((int)(.5+x),(int)(.5+y-thickness/2),(int)(.5+range),(int)(.5+thickness));
		
		
	}
}
