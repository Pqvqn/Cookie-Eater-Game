package ce3;

import java.awt.Color;
import java.awt.Graphics;

public class Cookie {

	private int x,y;
	public static final int DEFAULT_RADIUS=30;
	private int radius;
	private Board board;
	
	public Cookie(Board frame, int startx, int starty) {
		board = frame;
		
		x=startx;
		y=starty;
		radius=DEFAULT_RADIUS;
	}
	
	public void runUpdate() {
		//delete self on collision with player
		if(collidesWithCircle(board.player.getX(),board.player.getY(),board.player.getRadius())) { 
			kill();
		}
	}
	
	//test if collides with a circle
	public boolean collidesWithCircle(int oX, int oY, int oRad) {
		int xDiff = Math.abs(oX - x);
		int yDiff = Math.abs(oY - y);
		return (Math.sqrt(xDiff*xDiff + yDiff*yDiff)) < oRad + radius*board.currFloor.scale;
	}
	
	//delete self and increase score
	public void kill() {
		board.score++;
		board.cookies.remove(board.cookies.indexOf(this));
	}
	
	public void paint(Graphics g) {
		g.setColor(Color.ORANGE.darker());
		g.fillOval((int)(.5+x-radius*board.currFloor.scale), (int)(.5+y-radius*board.currFloor.scale), (int)(.5+radius*board.currFloor.scale*2), (int)(.5+radius*board.currFloor.scale*2));
	}
}
