package items;

import java.awt.*;

import ce3.*;
import cookies.*;

public abstract class Summon {

	protected Board board;
	protected Eater user;
	protected double x,y;
	protected double rotation;
	
	public Summon(Board frame, Eater summoner) {
		board = frame;
		user = summoner;
	}
	//set all vars before other items change them
	public void prepare() {
		
	}
	//run on special start
	public void initialize() {
		
	}
	//run while special is active
	public void execute() {
		for(int i=0; i<board.cookies.size(); i++) {
			Cookie c = board.cookies.get(i);
			if(hitsCircle(c.getX(),c.getY(),c.getRadius())) {
				collisionCookie(c);
			}
		}
		for(int i=0; i<board.walls.size(); i++) {
			Wall w = board.walls.get(i);
			if(hitsRect(w.getX(),w.getY(),w.getW(),w.getH())) {
				collisionWall(w,user.getGhosted(),user.getShielded());
				if(!user.getShielded())System.out.println("ahh");
			}
		}
		
	}
	//run when special ends
	public void end(boolean interrupted) {
		
	}
	public void paint(Graphics2D g2) {
		
	}
	//if circle intersects
	public boolean hitsCircle(double cX, double cY, double cR) {
		return false;
	}
	//if rectangle intersects
	public boolean hitsRect(double rX, double rY, double rW, double rH) {
		return false;
	}
	
	public void collisionCookie(Cookie c) {
		
	}
	public void collisionWall(Wall w, boolean ghost, boolean shield) {
		
	}
	
	public double playerVelAngle() {
		return Math.atan2(user.getYVel(),user.getXVel());
	}
	public double playerDirAngle() {
		switch(user.getDir()) {
		case(Eater.UP):
			return 3*Math.PI/2;
		case(Eater.RIGHT):
			return 0;
		case(Eater.DOWN):
			return Math.PI/2;
		case(Eater.LEFT):
			return Math.PI;
		default:
			return 0;
		}
	}
	
}
