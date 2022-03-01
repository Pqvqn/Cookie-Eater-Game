package mechanisms;

import ce3.*;
import levels.*;

public class WallMove extends Wall{
	
	Path path; //path of points to move from
	boolean moving; //whether moving or still
	double xV,yV; //last x and y velocity of movement

	public WallMove(Game frame, Board gameboard, Level lvl, int xPos, int yPos, int width, int height, double angle, int originX, int originY, Path path) {
		super(frame,gameboard,lvl,xPos,yPos,width,height,angle,originX,originY);
		initPaths(path);
	}
	
	public WallMove(Game frame, Board gameboard, Level lvl, int xPos, int yPos, int width, int height, double angle, Path path) {
		super(frame,gameboard,lvl,xPos,yPos,width,height,angle);
		initPaths(path);
	}
	
	public WallMove(Game frame, Board gameboard, Level lvl, int xPos, int yPos, int width, int height, Path path) {
		super(frame,gameboard,lvl,xPos,yPos,width,height);
		initPaths(path);
	}
	
	public WallMove(Game frame, Board gameboard, Level lvl, int xPos, int yPos, int radius, Path path) {
		super(frame,gameboard,lvl,xPos,yPos,radius);
		initPaths(path);
	}
	
	/*public WallMove(Game frame, Board gameboard, int xPos, int yPos, int radius, SaveData randomization, Path path) {
		super(frame,gameboard,xPos,yPos,radius);
		randomize(randomization);
		initPaths(path);
	}*/
	
	public WallMove(Game frame, Board gameboard, Level lvl, SaveData sd) {
		super(frame, gameboard, lvl, sd);
		moving = sd.getBoolean("moving",0);
		xV = sd.getDouble("velocity",0);
		yV = sd.getDouble("velocity",1);
		path = new Path(sd.getSaveDataList("path").get(0));
	}
	
	public void randomize(Level lvl, SaveData rnd) {
		super.randomize(lvl, rnd);
		path.randomize(rnd.getString("pathrand",0),this,null);
		initPaths(path);
	}

	public SaveData getSaveData() {
		SaveData data = super.getSaveData();
		data.addData("moving",moving);
		data.addData("velocity",xV,0);
		data.addData("velocity",yV,1);
		data.addData("path",path.getSaveData());
		return data;
	}
	
	private void initPaths(Path paths) {
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
		path.reset();
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
			
			if(Math.sqrt(Math.pow(prevX-nextX,2)+Math.pow(prevY-nextY,2))!=0){
				//conversion for x/y distances into x/y speeds
				double rat = speed/Math.sqrt(Math.pow(prevX-nextX,2)+Math.pow(prevY-nextY,2));
				//move wall
				xV = rat * (nextX-prevX);
				yV = rat * (nextY-prevY);
				move(xV,yV);
			}
			
			if(shape==CIRCLE) {
				r+=path.getExpansion()[0];
			}else if(shape==RECTANGLE) {
				w+=path.getExpansion()[0];
				h+=path.getExpansion()[1];
			}
			
			a+=path.getRotation();
			
			//update path
			int check = path.checkpoint();
			path.update();

			//target next checkpoint if correct time passed
			if(check!=path.checkpoint()){
				w = path.size()[0];
				h = path.size()[1];
				x = path.position()[0];
				y = path.position()[1];
				a = path.angle();
			}
			
		}
	}
}


