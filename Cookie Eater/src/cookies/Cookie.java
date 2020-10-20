package cookies;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.io.IOException;

import ce3.*;
import entities.*;
import levels.*;
import sprites.*;

public class Cookie {

	protected int x,y;
	public static final int DEFAULT_RADIUS=30;
	protected int radius;
	protected Board board;
	protected boolean accessible;
	protected int decayTime; //frames passed before decaying
	protected double adjustedDecayTime; //decay time adjusted for fps
	protected double decayCounter; //frames passed before decaying
	protected boolean decayed; //if cookies is decayed (unable to earn currency from)
	private SpriteCookie sprite;
	private double value;
	
	public Cookie(Board frame, int startx, int starty) {
		board = frame;
		
		x=startx;
		y=starty;
		radius=DEFAULT_RADIUS;
		accessible=false;
		setDecayTime();
		value = 1;
		try {
			sprite = new SpriteCookie(board,this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void runUpdate() {
		if(board.cookies.contains(this)) {
			//delete self on collision with player
			if(collidesWithCircle(board.player.getX(),board.player.getY(),board.player.getRadius()*2)) { 
				board.player.setNearCookie(true);
			}
		}
		if(board.player.getDir()!=Eater.NONE && decayCounter++>=adjustedDecayTime){	
			decayed=true;
		}
	}
	/*public void recalibrate() {
		double farthestCorner = Math.max(Math.max(Level.lineLength(0,0,board.currFloor.getStartX(),board.currFloor.getStartY()), //length to farthest corner from player
				Level.lineLength(0,board.Y_RESOL,board.currFloor.getStartX(),board.currFloor.getStartY())),
				Math.max(Level.lineLength(board.X_RESOL,0,board.currFloor.getStartX(),board.currFloor.getStartY()), 
						Level.lineLength(board.X_RESOL,board.Y_RESOL,board.currFloor.getStartX(),board.currFloor.getStartY())));
		decayTime = (int)(.5+(1-(Level.lineLength(board.currFloor.getStartX(),board.currFloor.getStartY(),x,y)/farthestCorner))
				*((board.currFloor.getMaxDecay()-board.currFloor.getMinDecay())+board.currFloor.getMinDecay())
				*(15.0/board.getAdjustedCycle()));
	}*/
	//how long until cookie decays
	public void setDecayTime() {
		double farthestCorner = Math.max(Math.max(Level.lineLength(0,0,board.currFloor.getStartX(),board.currFloor.getStartY()), //length to farthest corner from player
				Level.lineLength(0,board.Y_RESOL,board.currFloor.getStartX(),board.currFloor.getStartY())),
				Math.max(Level.lineLength(board.X_RESOL,0,board.currFloor.getStartX(),board.currFloor.getStartY()), 
						Level.lineLength(board.X_RESOL,board.Y_RESOL,board.currFloor.getStartX(),board.currFloor.getStartY())));
		decayTime = (int)(.5+(1-(Level.lineLength(board.currFloor.getStartX(),board.currFloor.getStartY(),x,y)/farthestCorner))
				*((board.currFloor.getMaxDecay()-board.currFloor.getMinDecay())+board.currFloor.getMinDecay()));
		decayCounter = 0;
		adjustedDecayTime = decayTime;
		decayed=false;
	}
	public void setCalibration(double calrat) {
		decayCounter = (decayCounter/adjustedDecayTime) * decayTime*(15.0/calrat);
		adjustedDecayTime = decayTime*(15.0/calrat);
	}
	//test if collides with a circle
	public boolean collidesWithCircle(double oX, double oY, double oRad) {
		double xDiff = Math.abs(oX - x);
		double yDiff = Math.abs(oY - y);
		return (Math.sqrt(xDiff*xDiff + yDiff*yDiff)) < oRad + radius*board.currFloor.getScale();
	}
	
	public void kill(Entity consumer) {
		kill(consumer,-1);
	}
	//delete self and increase score
	public void kill(Entity consumer, double decayValue) {
		if(consumer!=null) {
			if(decayed) { //less value for decayed cookies
				if(decayValue>=0) {
					value = decayValue;
				}else {
					value = consumer.getDecayedValue();
				}
			}
			if(consumer instanceof Eater) {
				Eater player = (Eater)consumer;
				player.addScore(1);
				board.player.addCash(value);
			}
			if(consumer instanceof Explorer) {
				Eater player = board.player;
				player.addScore(1);
			}
			if(consumer instanceof Summon2) {
				if(((Summon2)consumer).getUser()!=null) {
					kill(((Summon2)consumer).getUser(),consumer.getDecayedValue());
					return;
				}
			}
			if(consumer instanceof Effect) {
				Entity initiator = ((Effect)consumer).getInitiator();
				if(initiator!=null && !initiator.isDed()) {
					kill(initiator);
					return;
				}else {
					Eater player = board.player;
					player.addScore(1);
				}
			}
			//consumer.giveCookie(this);
		}
		if(!decayed && consumer!=null) {
			consumer.activateSpecials();
		}
		board.cookies.remove(this);
	}
	
	public void setAccess(boolean a) {accessible = a;}
	
	public boolean getAccess() {return accessible;}
	public int getX() {return x;}
	public int getY() {return y;}
	public double getRadius() {return radius*board.currFloor.getScale();}
	public void shift(int xS, int yS) {
		x+=xS;
		y+=yS;
	}
	public void setPos(int xS, int yS) {
		x=xS;
		y=yS;
	}
	public boolean getDecayed() {
		return decayed;
	}
	public void setValue(double v) {value = v;}
	public double getValue() {return value;}
	
	public Rectangle getBounds() {
		return new Rectangle((int)(.5+x-getRadius()),(int)(.5+y-getRadius()),(int)(.5+getRadius()*2),(int)(.5+getRadius()*2));
	}
	public Area getArea() {
		double r = getRadius();
		Ellipse2D.Double c = new Ellipse2D.Double(x-r,y-r,r*2,r*2);
		return new Area(c);
	}
	
	public void paint(Graphics g) {
		sprite.paint(g);
	}
}
