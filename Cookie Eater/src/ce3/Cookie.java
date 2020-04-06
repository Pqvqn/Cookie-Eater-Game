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
		if(collidesWithCircle(board.player.getX(),board.player.getY(),board.player.getRadius())) {
			kill();
		}
	}
	
	public boolean collidesWithCircle(int oX, int oY, int oRad) {
		int xDiff = Math.abs(oX - x);
		int yDiff = Math.abs(oY - y);
		return (Math.sqrt(xDiff*xDiff + yDiff*yDiff)) < oRad + radius;
	}
	
	
	public void kill() {
		board.score++;
		board.cookies.remove(board.cookies.indexOf(this));
	}
	
	public void paint(Graphics g) {
		g.setColor(Color.ORANGE.darker());
		g.fillOval((int)(x-radius), (int)(y-radius), radius*2, radius*2);
	}
}
