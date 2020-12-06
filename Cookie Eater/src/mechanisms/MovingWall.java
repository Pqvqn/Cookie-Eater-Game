package mechanisms;

import ce3.Board;

public class MovingWall extends Wall{

	public MovingWall(Board frame, int xPos, int yPos, int width, int height, double angle, int originX, int originY) {
		super(frame,xPos,yPos,width,height,angle,originX,originY);
	}
	
	public MovingWall(Board frame, int xPos, int yPos, int width, int height, double angle) {
		super(frame,xPos,yPos,width,height,angle);
	}
	
	public MovingWall(Board frame, int xPos, int yPos, int width, int height) {
		super(frame,xPos,yPos,width,height);
	}
	
	public MovingWall(Board frame, int xPos, int yPos, int radius) {
		super(frame,xPos,yPos,radius);
	}
	
}
