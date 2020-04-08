package ce3;

import java.awt.Color;
import java.awt.Graphics;

import levels.Level;

public class Cookie {

	private int x,y;
	public static final int DEFAULT_RADIUS=30;
	private int radius;
	private Board board;
	private boolean accessible;
	private int decayTime; //frames passed before decaying
	private boolean decayed; //if cookies is decayed (unable to earn currency from)
	
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
				*(board.currFloor.getMaxDecay()-board.currFloor.getMinDecay())+board.currFloor.getMinDecay());
		decayed=false;
	}
	
	public void runUpdate() {
		//delete self on collision with player
		if(collidesWithCircle(board.player.getX(),board.player.getY(),board.player.getRadius())) { 
			kill();
		}
		if(board.player.getDir()!=Eater.NONE && decayTime--<=0){	
			decayed=true;
		}
	}
	
	//test if collides with a circle
	public boolean collidesWithCircle(int oX, int oY, int oRad) {
		int xDiff = Math.abs(oX - x);
		int yDiff = Math.abs(oY - y);
		return (Math.sqrt(xDiff*xDiff + yDiff*yDiff)) < oRad + radius*board.currFloor.getScale();
	}
	
	//delete self and increase score
	public void kill() {
		board.score++;
		if(!decayed)
			board.cash++;
		board.cookies.remove(board.cookies.indexOf(this));
	}
	
	public void setAccess(boolean a) {accessible = a;}
	
	public boolean getAccess() {return accessible;}
	public int getX() {return x;}
	public int getY() {return y;}
	
	public void paint(Graphics g) {
		if(decayed) {g.setColor(new Color(120,80,20,100));}
		else {g.setColor(new Color(120,80,20,255));}
		g.fillOval((int)(.5+x-radius*board.currFloor.getScale()), (int)(.5+y-radius*board.currFloor.getScale()), (int)(.5+radius*board.currFloor.getScale()*2), (int)(.5+radius*board.currFloor.getScale()*2));
	}
}
