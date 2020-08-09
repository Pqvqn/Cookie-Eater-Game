package entities;

import java.awt.*;

import ce3.*;
import levels.*;

public class Summon2 extends Entity{
	
	private Entity user;
	private boolean anchored; //whether item is anchored to the summoner
	private int edgex,edgey; //x and y position of edge 
	private SegmentRectangle body;
	
	public Summon2(Board frame, Entity summoner) {
		super(frame);
		user = summoner;
		anchored = true;
		x = user.getX();
		y = user.getY();
		special_frames = user.getSpecialFrames();
		buildBody();
		orientParts();
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
	
	public double getThickness() {
		return getRadius()*2;
	}
	public double getLength() {
		return Level.lineLength(edgex,edgey,x,y);
	}
	public double getAngle() {
		return Math.atan2(edgey-y,edgex-x);
	}

	protected void buildBody() {
		parts.add(body = new SegmentRectangle(board,this,x,y,getThickness(),getLength(),getAngle()));
	}
	public void orientParts() {
		body.setLocation(x,y);
		body.setAngle(getAngle());
		body.setDims(getThickness(),getLength());
	}
	
	public void paint(Graphics2D g2) {
		g2.setColor(Color.WHITE);
		if(user.getGhosted())g2.setColor(new Color(255,255,255,100));
		if(user.getShielded())g2.setColor(new Color(50,200,210));
		g2.rotate(getAngle(),x,y);
		g2.fillRect((int)(.5+x),(int)(.5+y-getRadius()),(int)(.5+getLength()),(int)(.5+getRadius()*2));
		
		
	}
}
