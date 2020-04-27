package items;

import java.awt.*;

import ce3.*;
import cookies.*;
import levels.*;

public class SummonJab extends Summon{
	
	private double range;
	private double angle;
	private int distforward;
	private double amountforward;
	private double holdfor;
	private final int EXTEND = 1, RETRACT = -1, SHEATHED = 0, HOLD = 2;
	private int state;
	private int framecount;
	private double thickness;
	
	public SummonJab(Board frame, Eater summoner, double r) {
		super(frame, summoner);
		range = r*board.currFloor.getScale();
		amountforward = (range/100)*board.getAdjustedCycle();
		holdfor = 300/board.getAdjustedCycle();
	}
	public void setRange(double r) {
		range = r*board.currFloor.getScale();
	}
	public void prepare() {
		thickness = user.getTotalRadius()*.6;
		x=user.getX();
		y=user.getY();
		angle = playerDirAngle();
		state = EXTEND;
	}
	public void initialize() {
		distforward = 0;
		framecount = 0;
	}
	public void execute() {
		thickness = user.getTotalRadius()*.6;
		x=user.getX();
		y=user.getY();
		switch(state) {
		case SHEATHED:
			distforward=0;
			break;
		case EXTEND:
			if(distforward>=range)
				state=HOLD;
			distforward+=amountforward;
			break;
		case HOLD:
			if(framecount++>holdfor)
				state = RETRACT;
			break;
		case RETRACT:
			if(distforward<0)
				state=SHEATHED;
			distforward-=amountforward;
			break;
		}
		super.execute();
	}
	public void end(boolean interrupted) {
		
	}
	public void collisionCookie(Cookie c) {
		c.kill(true);
	}
	public void collisionWall(Wall w, boolean ghost, boolean shield) {
		if(shield)user.bounce(w.getX(),w.getY(),w.getW(),w.getH());
		if(ghost)return;
		state = HOLD;
		distforward-=amountforward;
	}
	//if circle intersects an edge
	public boolean hitsCircle(double cX, double cY, double cR) {
		//System.out.println(angle);
		double altX = x+(thickness/2 * Math.cos(angle+Math.PI/2));
		double altY = y+(thickness/2 * Math.sin(angle+Math.PI/2));
		
		double wX = distforward * Math.cos(angle);
		double wY = distforward * Math.sin(angle);
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
		
		double wX = distforward * Math.cos(angle);
		double wY = distforward * Math.sin(angle);
		double hX = thickness * Math.cos(angle-Math.PI/2);
		double hY = thickness * Math.sin(angle-Math.PI/2);
		
		return Level.collidesLineAndRect(altX, altY, altX+wX, altY+wY, rX, rY, rW, rH) || 
				Level.collidesLineAndRect(altX, altY, altX+hX, altY+hY, rX, rY, rW, rH) || 
				Level.collidesLineAndRect(altX+wX, altY+wY, altX+wX+hX, altY+wY+hY, rX, rY, rW, rH) || 
				Level.collidesLineAndRect(altX+hX, altY+hY, altX+wX+hX, altY+wY+hY, rX, rY, rW, rH);
			
	}
	public void paint(Graphics2D g2) {
		g2.setColor(Color.WHITE);
		if(user.getGhosted())g2.setColor(new Color(255,255,255,100));
		if(user.getShielded())g2.setColor(new Color(50,200,210));
		g2.rotate(angle,x,y);
		g2.fillRect((int)(.5+x),(int)(.5+y-thickness/2),distforward,(int)(.5+thickness));
		
		
	}
}
