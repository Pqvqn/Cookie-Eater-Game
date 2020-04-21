package cookies;

import java.awt.Color;
import java.awt.Graphics;

import ce3.Board;
import ce3.Eater;
import levels.Level;

public class Cookie {

	protected int x,y;
	public static final int DEFAULT_RADIUS=30;
	protected int radius;
	protected Board board;
	protected boolean accessible;
	protected int decayTime; //frames passed before decaying
	protected boolean decayed; //if cookies is decayed (unable to earn currency from)
	
	public Cookie(Board frame, int startx, int starty) {
		board = frame;
		
		x=startx;
		y=starty;
		radius=DEFAULT_RADIUS;
		accessible=false;
		double farthestCorner = Math.max(Math.max(Level.lineLength(0,0,board.currFloor.getStartX(),board.currFloor.getStartY()), //length to farthest corner from player
				Level.lineLength(0,board.Y_RESOL,board.currFloor.getStartX(),board.currFloor.getStartY())),
				Math.max(Level.lineLength(board.X_RESOL,0,board.currFloor.getStartX(),board.currFloor.getStartY()), 
						Level.lineLength(board.X_RESOL,board.Y_RESOL,board.currFloor.getStartX(),board.currFloor.getStartY())));
		decayTime = (int)(.5+(1-(Level.lineLength(board.currFloor.getStartX(),board.currFloor.getStartY(),startx,starty)/farthestCorner))
				*((board.currFloor.getMaxDecay()-board.currFloor.getMinDecay())+board.currFloor.getMinDecay())
				*(15.0/board.getAdjustedCycle()));
		decayed=false;
	}
	
	public void runUpdate() {
		//delete self on collision with player
		if(collidesWithCircle(board.player.getX(),board.player.getY(),board.player.getExtraRadius())) { 
			kill(true);
		}
		if(board.player.getDir()!=Eater.NONE && decayTime--<=0){	
			decayed=true;
		}
	}
	public void recalibrate() {
		double farthestCorner = Math.max(Math.max(Level.lineLength(0,0,board.currFloor.getStartX(),board.currFloor.getStartY()), //length to farthest corner from player
				Level.lineLength(0,board.Y_RESOL,board.currFloor.getStartX(),board.currFloor.getStartY())),
				Math.max(Level.lineLength(board.X_RESOL,0,board.currFloor.getStartX(),board.currFloor.getStartY()), 
						Level.lineLength(board.X_RESOL,board.Y_RESOL,board.currFloor.getStartX(),board.currFloor.getStartY())));
		decayTime = (int)(.5+(1-(Level.lineLength(board.currFloor.getStartX(),board.currFloor.getStartY(),x,y)/farthestCorner))
				*((board.currFloor.getMaxDecay()-board.currFloor.getMinDecay())+board.currFloor.getMinDecay())
				*(15.0/board.getAdjustedCycle()));
	}
	//test if collides with a circle
	public boolean collidesWithCircle(int oX, int oY, int oRad) {
		int xDiff = Math.abs(oX - x);
		int yDiff = Math.abs(oY - y);
		return (Math.sqrt(xDiff*xDiff + yDiff*yDiff)) < oRad + radius*board.currFloor.getScale();
	}
	
	//delete self and increase score
	public void kill(boolean consumed) {
		if(consumed) {
			board.player.addScore(1);
			if(!decayed) {
				board.player.addCash(1);
				board.player.activateSpecials();
			}else { //less value for decayed cookies
				board.player.addCash(board.player.getDecayedValue());
				if(board.player.getDecayedValue()>0)
					board.player.activateSpecials();
			}
		}
		board.cookies.remove(board.cookies.indexOf(this));
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
	
	public void paint(Graphics g) {
		if(decayed) {g.setColor(board.draw.blendColors(new Color(140,100,40),board.currFloor.getBGColor()));}
		else {g.setColor(new Color(140,100,40));}
		g.fillOval((int)(.5+x-radius*board.currFloor.getScale()), (int)(.5+y-radius*board.currFloor.getScale()), (int)(.5+radius*board.currFloor.getScale()*2), (int)(.5+radius*board.currFloor.getScale()*2));
	}
}
