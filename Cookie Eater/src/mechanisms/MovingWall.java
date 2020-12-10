package mechanisms;

import ce3.Board;

public class MovingWall extends Wall{
	
	double[][] path;
	double speed;
	int checkpoint;

	public MovingWall(Board frame, int xPos, int yPos, int width, int height, double angle, int originX, int originY, double[][] paths, double initspeed) {
		super(frame,xPos,yPos,width,height,angle,originX,originY);
		initPaths(paths,initspeed);
	}
	
	public MovingWall(Board frame, int xPos, int yPos, int width, int height, double angle, double[][] paths, double initspeed) {
		super(frame,xPos,yPos,width,height,angle);
		initPaths(paths,initspeed);
	}
	
	public MovingWall(Board frame, int xPos, int yPos, int width, int height, double[][] paths, double initspeed) {
		super(frame,xPos,yPos,width,height);
		initPaths(paths,initspeed);
	}
	
	public MovingWall(Board frame, int xPos, int yPos, int radius, double[][] paths, double initspeed) {
		super(frame,xPos,yPos,radius);
		initPaths(paths,initspeed);
	}
	
	private void initPaths(double[][] paths, double initSpeed) {
		path = paths;
		speed = initSpeed;
		checkpoint = 0;
	}
	
	public void runUpdate() {
		//x and y to move from and to
		double prevX = path[checkpoint%path.length][0], prevY = path[checkpoint%path.length][1];
		double nextX = path[(checkpoint+1)%path.length][0], nextY = path[(checkpoint+1)%path.length][1];
		//conversion for x/y distances into x/y speeds
		double rat = speed/Math.sqrt(Math.pow(prevX-nextX,2)+Math.pow(prevY-nextY,2));
		//move wall
		move(rat * (nextX-prevX),rat * (nextY-prevY));
		//target next checkpoint if close enough to current
		if(Math.sqrt(Math.pow(path[(checkpoint+1)%path.length][0]-x,2) + Math.pow(path[checkpoint%path.length][1]-y,2))<=50){
			checkpoint++;
		}
	}
	
}
