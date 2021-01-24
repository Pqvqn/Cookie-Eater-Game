package entities;

import java.awt.*;
import java.util.*;

import ce3.*;

public class EffectClone extends Effect{

	
	private int startx, starty, starta; //starting position of clone to reflect over
	private boolean flipx, flipy, flipa; //whether should: reflect x pos, reflect y pos, swap x/y values
	private boolean posLock; //whether to update position of this clone
	
	public EffectClone(Game frame, Board gameboard, int cycletime, Entity initiator, boolean fx, boolean fy, boolean fa) {
		super(frame,gameboard,cycletime,(int)(.5+initiator.getX()),(int)(.5+initiator.getY()),initiator);
		mass = initiator.getMass();
		startx = (int)(.5+initiator.getX(true));
		starty = (int)(.5+initiator.getY(true));
		starta = (int)(.5+initiator.getAim());
		flipx = fx;
		flipy = fy;
		flipa = fa;
		collides = false;
		buildBody();
		orientParts();
	}
	
	public void runUpdate() {
		if(ded)return;
		setShielded(initiator.getShielded());
		setGhost(initiator.getGhosted());
		if(!posLock || !shielded) {
			orientParts();
			super.runUpdate();
		}
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
		if(posLock) {
			boolean regen = initiator.getParts().size() != parts.size(); //whether list of parts needs to be regenerated
			for(int i=0; i<initiator.getParts().size() && !regen; i++) {
				Segment parentS = initiator.getParts().get(i);
				Segment thisS = parts.get(i);
				double[] posState = transformState(parentS.getCenterX(), parentS.getCenterY(), parentS.getAngle(),
						initiator.getX(),initiator.getY(),initiator.getAim());
				//set positions relative to original segment if same shape
				if(parentS.getClass().equals(thisS.getClass())) {
					thisS.setLocation(posState[0]-initiator.getX()+getX(), posState[1]-initiator.getY()+getY());
					thisS.setAngle(posState[2]);
					thisS.setSize(parentS.getSize()+parentS.getExtraSize());
					if(parentS instanceof SegmentRectangle){
						((SegmentRectangle)thisS).setDims(((SegmentRectangle)parentS).getTotalWidth(),((SegmentRectangle)parentS).getTotalLength());
					}
				}else { //if wrong shape, remake parts list
					regen = true;
				}
			}
			if(regen) {
				buildBody();
			}
		}else {
			double[] posState1 = transformState(initiator.getX(), initiator.getY(), initiator.getAim());
			setX(posState1[0]);
			setY(posState1[1]);
			
			boolean regen = initiator.getParts().size() != parts.size(); //whether list of parts needs to be regenerated
			for(int i=0; i<initiator.getParts().size() && !regen; i++) {
				Segment parentS = initiator.getParts().get(i);
				Segment thisS = parts.get(i);
				double[] posState = transformState(parentS.getCenterX(), parentS.getCenterY(), parentS.getAngle());
				//set positions relative to original segment if same shape
				if(parentS.getClass().equals(thisS.getClass())) {
					thisS.setLocation(posState[0], posState[1]);
					thisS.setAngle(posState[2]);
					thisS.setSize(parentS.getSize()+parentS.getExtraSize());
					if(parentS instanceof SegmentRectangle){
						((SegmentRectangle)thisS).setDims(((SegmentRectangle)parentS).getTotalWidth(),((SegmentRectangle)parentS).getTotalLength());
					}
				}else { //if wrong shape, remake parts list
					regen = true;
				}
			}
			if(regen) {
				buildBody();
			}
		}
		super.orientParts();
	}
	
	public double[] transformState(double ax, double ay, double aa) {
		return transformState(ax,ay,aa,startx,starty,starta);
	}
	
	//convert position state through transformations
	public double[] transformState(double ax, double ay, double aa, double sx, double sy, double sa) {
		double newAngle = aa;
		double newX = ax; //- initiator.getRelativeFrame()[0];
		double newY = ay; //- initiator.getRelativeFrame()[1];
		
		double absstartx = initiator.getRelativeFrame()[0] + sx; //start position on board adjusted for current relative frame
		double absstarty = initiator.getRelativeFrame()[1] + sy;
		
		//change position values for each transformation
		double changeX = (flipx) ? absstartx - ax : ax - absstartx;
		double changeY = (flipy) ? absstarty - ay : ay - absstarty;
		newX = absstartx + (flipa ? changeY : changeX);
		newY = absstarty + (flipa ? changeX : changeY);

		if(flipx) {
			newAngle = Math.PI - newAngle; //startaa??
		}
		if(flipy) {
			newAngle = 2*Math.PI - newAngle;
		}
		if(flipa) {
			//double yaxis = newAngle%(Math.PI*2)>Math.PI ? Math.PI*1.5 : Math.PI * .5;
			//double xaxis = newAngle%(Math.PI*2)>Math.PI*.5 && newAngle%(Math.PI*2)<Math.PI*1.5 ? Math.PI : 0;
			//newAngle = xaxis + yaxis - newAngle;
			
			//newAngle = Math.atan2(Math.cos(newAngle),Math.sin(newAngle));
			
			newAngle %= Math.PI * 2;
			int quads = (int)(newAngle / (Math.PI * .5));
			double rem = newAngle % (Math.PI * .5);
			newAngle = Math.PI * .5 - rem + quads * Math.PI * .5;
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
		if(shielded) {
			posLock = true;
		}else {
			kill();
		}
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
		g.setColor(new Color(flipx?255:0,flipy?255:0,flipa?255:0,100));
		for(int i=0; i<parts.size(); i++) {
			parts.get(i).update();
			parts.get(i).paint(g);
		}
	}
	
}
