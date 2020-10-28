package entities;

import java.awt.*;
import java.util.*;

import ce3.*;
import cookies.*;

public class EffectClone extends Effect{

	
	private int startx, starty, starta; //starting position of clone to reflect over
	private boolean flipx, flipy, flipa; //whether should: reflect x pos, reflect y pos, swap x/y values
	
	public EffectClone(Board frame, int cycletime, int xp, int yp, int ap, Entity initiator, boolean fx, boolean fy, boolean fa) {
		super(frame,cycletime,xp,yp,initiator);
		mass = initiator.getMass();
		startx = xp;
		starty = yp;
		starta = ap;
		flipx = fx;
		flipy = fy;
		flipa = fa;
		collides = false;
		buildBody();
		orientParts();
	}
	
	public void runUpdate() {
		if(ded)return;
		super.runUpdate();
		orientParts();
	}
	protected void buildBody() {
		parts = new ArrayList<Segment>();
		for(Segment s:initiator.getParts()) {
			
			double[] posState = transformState(s.getCenterX(), s.getCenterY(), s.getAngle());
			
			if(s instanceof SegmentCircle) {
				SegmentCircle s2 = (SegmentCircle)s;
				parts.add(new SegmentCircle(board,initiator, posState[0], posState[1], s2.getRadius()/board.currFloor.getScale(), posState[2]));
			}else if(s instanceof SegmentRectangle) {
				SegmentRectangle s2 = (SegmentRectangle)s;
				parts.add(new SegmentRectangle(board,initiator, posState[0], posState[1], s2.getWidth(), s2.getLength(), posState[2]));
			}
		}
	}
	public void orientParts() {
		boolean regen = initiator.getParts().size() != parts.size(); //whether list of parts needs to be regenerated
		for(int i=0; i<initiator.getParts().size() && !regen; i++) {
			Segment parentS = initiator.getParts().get(i);
			Segment thisS = parts.get(i);
			double[] posState = transformState(parentS.getCenterX(), parentS.getCenterY(), parentS.getAngle());
			//set positions relative to original segment if same shape
			if(parentS.getClass().equals(thisS.getClass())) {
				thisS.setLocation(posState[0], posState[1]);
				thisS.setAngle(posState[2]);
				thisS.setSize(parentS.getSize());
				if(parentS instanceof SegmentRectangle){
					((SegmentRectangle)thisS).setDims(((SegmentRectangle)parentS).getWidth(),((SegmentRectangle)parentS).getLength());
				}
			}else { //if wrong shape, remake parts list
				regen = true;
			}
		}
		if(regen) {
			buildBody();
		}
		
		super.orientParts();
	}
	
	//convert position state through transformations
	public double[] transformState(double ax, double ay, double aa) {
		double newAngle = aa;
		double newX = initiator.getRelativeFrame()[0] + ax;
		double newY = initiator.getRelativeFrame()[1] + ay;
		
		double absstartx = initiator.getRelativeFrame()[0] + startx; //start position on board adjusted for current relative frame
		double absstarty = initiator.getRelativeFrame()[1] + starty;
		
		//change position values for each transformation
		double changeX = (flipx) ? absstartx - ax : ax - absstartx;
		double changeY = (flipy) ? absstarty - ay : ay - absstarty;
		newX = absstartx + (flipa ? changeY : changeX);
		newY = absstarty + (flipa ? changeX : changeY);

		if(flipx) {
			newAngle = Math.PI - aa; //startaa??
		}
		if(flipy) {
			newAngle = aa;
		}
		if(flipa) {
			newAngle = Math.PI/2 - newAngle;
			//newAngle = Math.atan2(Math.cos(newAngle),Math.sin(newAngle));
		}
		return new double[] {newX, newY, newAngle};
	}
	
	
	//remove clone
	public void kill() {
		super.kill();
		parts = new ArrayList<Segment>();
	}
	//clones do not bounce off of walls?????
	public void triggerShield() {
		kill();
	}
	
	public double getXVel() {
		double xv = initiator.getXVel();
		double yv = initiator.getYVel();
		if(flipx)xv*=-1;
		if(flipy)yv*=-1;
		if(flipa) {
			double sxv = xv;
			xv = yv;
			yv = sxv;
		}
		return xv;
	}
	public double getYVel() {
		double xv = initiator.getXVel();
		double yv = initiator.getYVel();
		if(flipx)xv*=-1;
		if(flipy)yv*=-1;
		if(flipa) {
			double sxv = xv;
			xv = yv;
			yv = sxv;
		}
		return yv;
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(new Color(255,0,0,100));
		for(int i=0; i<parts.size(); i++) {
			parts.get(i).update();
			parts.get(i).paint(g);
		}
	}
	
}
