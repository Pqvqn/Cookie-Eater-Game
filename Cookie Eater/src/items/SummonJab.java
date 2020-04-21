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
		thickness = 30 * board.currFloor.getScale();
		x=player.getX();
		y=player.getY();
		angle = playerDirAngle();
		state = EXTEND;
	}
	public void initialize() {
		distforward = 0;
		framecount = 0;
	}
	public void execute() {
		for(int i=0; i<board.cookies.size(); i++) {
			Cookie c = board.cookies.get(i);
			if(edgesHitCircle(c.getX(),c.getY(),c.getRadius())) {
				c.kill(true);}
		}
		x=player.getX();
		y=player.getY();
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
	}
	public void end(boolean interrupted) {
		
	}
	//if circle intersects an edge
	public boolean edgesHitCircle(double cX, double cY, double cR) {
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
	public void paint(Graphics2D g2) {
		g2.setColor(Color.WHITE);
		g2.rotate(angle,x,y);
		g2.fillRect(x,y-(int)(.5+thickness/2),distforward,(int)(.5+thickness));
		
		
	}
}
