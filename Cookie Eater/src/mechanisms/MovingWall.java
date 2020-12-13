package mechanisms;

import ce3.Board;

public class MovingWall extends Wall{
	
	WallPath path; //path of points to move from
	int checkpoint; //current checkpoint index on path
	boolean moving; //whether moving or still
	double xV,yV; //last x and y velocity of movement

	public MovingWall(Board frame, int xPos, int yPos, int width, int height, double angle, int originX, int originY, WallPath path) {
		super(frame,xPos,yPos,width,height,angle,originX,originY);
		initPaths(path);
	}
	
	public MovingWall(Board frame, int xPos, int yPos, int width, int height, double angle, WallPath path) {
		super(frame,xPos,yPos,width,height,angle);
		initPaths(path);
	}
	
	public MovingWall(Board frame, int xPos, int yPos, int width, int height, WallPath path) {
		super(frame,xPos,yPos,width,height);
		initPaths(path);
	}
	
	public MovingWall(Board frame, int xPos, int yPos, int radius, WallPath path) {
		super(frame,xPos,yPos,radius);
		initPaths(path);
	}
	
	private void initPaths(WallPath paths) {
		double area; //area of wall geometry to determine mass
		double density = 2; //mass per unit area
		if(shape == CIRCLE) {
			area = Math.PI*r*r;
			mass = area*density;
		}else if(shape == RECTANGLE) {
			area = w*h;
			mass = area*density;
		}
		path = paths;
		checkpoint = 0;
		moving = true;
	}
	
	public double getXVel() {
		return moving?xV:0;
	}
	public double getYVel() {
		return moving?yV:0;
	}
	
	public void runUpdate() {
		if(moving) {
			//x and y to move from and to
			double[][] pos = path.getSegment();
			double nextX=pos[1][0], nextY=pos[1][1], prevX=pos[0][0], prevY=pos[0][1];
			double speed = path.getSpeed();
			
			//conversion for x/y distances into x/y speeds
			double rat = speed/Math.sqrt(Math.pow(prevX-nextX,2)+Math.pow(prevY-nextY,2));
			//move wall
			xV = rat * (nextX-prevX);
			yV = rat * (nextY-prevY);
			move(xV,yV);
			//target next checkpoint if close enough to current
			if(Math.sqrt(Math.pow(nextX-x,2) + Math.pow(nextY-y,2))<=speed*2){
				checkpoint++;
			}
			
		}
	}
}


