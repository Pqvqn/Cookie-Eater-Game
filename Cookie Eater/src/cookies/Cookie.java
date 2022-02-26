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
	protected Game game;
	//protected Board board;
	protected Level level;
	protected boolean accessible;
	protected int decayTime; //frames passed before decaying
	protected double adjustedDecayTime; //decay time adjusted for fps
	protected double decayCounter; //frames passed before decaying
	protected boolean decayed; //if cookies is decayed (unable to earn currency from)
	protected SpriteCookie sprite;
	private double value;
	
	public Cookie(Game frame, Board gameboard, Level lvl, int startx, int starty, boolean basic) { //basic = is a general cookie to be collected for points
		game = frame;
		board = gameboard;
		level = lvl;
		
		x=startx;
		y=starty;
		radius=DEFAULT_RADIUS;
		if(basic) {
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
	}
	
	public Cookie(Game frame, Board gameboard, Level lvl, SaveData sd, boolean basic) {
		game = frame;
		board = gameboard;
		level = lvl;
		x = sd.getInteger("position",0);
		y = sd.getInteger("position",1);
		radius = sd.getInteger("radius",0);
		value = sd.getDouble("value",0);
		if(basic) {
			decayTime = sd.getInteger("decaytime",0);
			decayCounter = 0;
			adjustedDecayTime = decayTime;
			decayed=false;
			try {
				sprite = new SpriteCookie(board,this);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public SaveData getSaveData() {
		SaveData data = new SaveData();
		data.addData("position",x,0);
		data.addData("position",y,1);
		data.addData("radius",radius);
		data.addData("value",value);
		data.addData("decaytime",decayTime);
		return data;
	}
	//return Cookie created by SaveData, testing for correct type of Cookie
	public static Cookie loadFromData(Game frame, Board gameboard, SaveData sd) {
		String s = sd.getString("type",0);
		if(s==null)return new Cookie(frame, gameboard, sd, true);
		switch(s) {
		case "item":
			return new CookieItem(frame, gameboard, sd);
		case "shield":
			return new CookieShield(frame, gameboard, sd);
		case "stat":
			return new CookieStat(frame, gameboard, sd);
		default:
			return new Cookie(frame, gameboard, sd, true);
		}
	}
	public void runUpdate() {
		if(!board.isPaused() && decayCounter++>=adjustedDecayTime){	
			decayed=true;
		}
	}

	//how long until cookie decays
	public void setDecayTime() {
		double sx = board.player().startx;
		double sy = board.player().starty;
		double farthestCorner = Math.max(Math.max(Level.lineLength(0,0,sx,sy), //length to farthest corner from player
				Level.lineLength(0,level.y_resol,sx,sy)),
				Math.max(Level.lineLength(level.x_resol,0,sx,sy), 
						Level.lineLength(level.x_resol,level.y_resol,sx,sy)));
		decayTime = (int)(.5+(1-(Level.lineLength(sx,sy,x,y)/farthestCorner))
				*((level.getMaxDecay()-level.getMinDecay())+level.getMinDecay()));
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
		return (Math.sqrt(xDiff*xDiff + yDiff*yDiff)) < oRad + radius*level.getScale();
	}
	
	public void kill(Entity consumer) {
		kill(consumer,-1);
	}
	//delete self and increase score
	public void kill(Entity consumer, double decayValue) {
		if(consumer!=null) {
			game.audio.playSound("chomp",-10f);
			if(decayed) { //less value for decayed cookies
				if(decayValue>=0) {
					value = decayValue;
				}else {
					value = consumer.getDecayedValue();
				}
			}
			if(consumer instanceof Eater) {
				Eater player = (Eater)consumer;
				level.addScore(1);
				player.addCash(value);
			}
			if(consumer instanceof Explorer) {
				level.addScore(1);
			}
			if(consumer instanceof Summon) {
				if(((Summon)consumer).getUser()!=null) {
					kill(((Summon)consumer).getUser(),consumer.getDecayedValue());
					return;
				}
			}
			if(consumer instanceof Effect) {
				Entity initiator = ((Effect)consumer).getInitiator();
				if(initiator!=null && !initiator.isDed()) {
					kill(initiator);
					return;
				}else {
					level.addScore(1);
				}
			}
		}
		if(!decayed && consumer!=null) {
			consumer.activateSpecials();
		}
		level.cookies().remove(this);
	}
	
	public void setAccess(boolean a) {accessible = a;}
	
	public boolean getAccess() {return accessible;}
	public int getX() {return x;}
	public int getY() {return y;}
	public double getRadius() {return radius*level.getScale();}
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
		if(sprite!=null) {
			sprite.paint(g);
		}else {
			double scaledr = radius*level.getScale();
			g.fillOval((int)(.5+x-scaledr), (int)(.5+y-scaledr), (int)(.5+scaledr*2), (int)(.5+scaledr*2));
		}
	}
}
