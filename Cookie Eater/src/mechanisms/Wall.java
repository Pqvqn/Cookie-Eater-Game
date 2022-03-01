package mechanisms;

import java.awt.*;
import java.awt.geom.*;

import ce3.Board;
import ce3.Game;
import ce3.SaveData;
import levels.Level;

public class Wall extends Mechanism{
	
	double w,h;
	double a;
	double ox,oy; //origin to rotate around
	double r;
	int shape;
	int[][] ppoints;
	static final int CIRCLE = 0, RECTANGLE = 1, POLYGON = 2;
	
	public Wall(Game frame, Board gameboard, Level lvl, int xPos, int yPos, int width, int height, double angle, int originX, int originY) {
		super(frame, gameboard, lvl, xPos, yPos);
		shape = RECTANGLE;
		w = width;
		h = height;
		a = angle;
		r=-1;
		ox=originX;
		oy=originY;
	}
	
	public Wall(Game frame, Board gameboard, Level lvl, int xPos, int yPos, int width, int height, double angle) {
		super(frame, gameboard, lvl, xPos, yPos);
		shape = RECTANGLE; 
		w = width;
		h = height;
		a = angle;
		r=-1;
		ox=x+w/2;
		oy=y+h/2;
	}
	
	public Wall(Game frame, Board gameboard, Level lvl, int xPos, int yPos, int width, int height) {
		super(frame, gameboard, lvl, xPos, yPos);
		shape = RECTANGLE;
		w = width;
		h = height;
		a = 0;
		r=-1;
		ox=x+w/2;
		oy=y+h/2;
	}
	
	public Wall(Game frame, Board gameboard, Level lvl, int xPos, int yPos, int radius) {
		super(frame, gameboard, lvl, xPos, yPos);
		shape = CIRCLE;
		r = radius;
		w=-1;h=-1;
		a = 0;
		ox=x;
		oy=y;
	}
	
	public Wall(Game frame, Board gameboard, Level lvl, int xPos, int yPos, int[][] points) {
		super(frame, gameboard, lvl, xPos, yPos);
		ppoints = new int[2][points.length];
		shape = POLYGON;
		ox = 0;
		oy = 0;
		for(int i=0; i<points.length; i++) {
			ppoints[0][i] = points[i][0];
			ppoints[1][i] = points[i][1];
			ox += points[i][0] / points.length;
			oy += points[i][1] / points.length;
		}
	}

	public Wall(Game frame, Board gameboard, Level lvl, SaveData sd) {
		super(frame, gameboard, lvl, sd);
		w = sd.getDouble("dimensions",0);
		h = sd.getDouble("dimensions",1);
		r = sd.getDouble("radius",0);
		a = sd.getDouble("angle",0);
		shape = sd.getInteger("shape",0);
		ox = sd.getDouble("origin",0);
		oy = sd.getDouble("origin",1);
		if(sd.dataMap().containsKey("points")) {
			ppoints = new int[2][sd.getData("points").size()/2];
			for(int i=0; i<sd.getData("points").size(); i++) {
				ppoints[i%2][(int)(i/2)] = sd.getInteger("points",i);
			}
		}
	}
	public SaveData getSaveData() {
		SaveData data = super.getSaveData();
		data.addData("dimensions",w,0);
		data.addData("dimensions",h,1);
		data.addData("radius",r);
		data.addData("angle",a);
		data.addData("shape",shape);
		data.addData("origin",ox,0);
		data.addData("origin",oy,1);
		if(ppoints != null) {
			for(int i=0; i<ppoints[0].length; i++) {
				data.addData("points",ppoints[0][i],i*2);
				data.addData("points",ppoints[1][i],i*2+1);
			}
		}
		return data;
	}
	
	public double getW() {return w;}
	public double getH() {return h;}
	public double getA() {return a;}
	public double getR() {return r;}
	//x and y adjusted for rotation
	public double getAdjX() {
		return ox - Level.lineLength(x,y,ox,oy) * Math.cos(a+Math.atan2(y-oy,x-ox));
	}
	public double getAdjY() {
		return oy - Level.lineLength(x,y,ox,oy) * Math.sin(a+Math.atan2(y-oy,x-ox));
	}
	public double getOX() {return ox;}
	public double getOY() {return oy;}
	public void setW(int width) {w=width;}
	public void setH(int height) {h=height;}
	public void resetOrigin() {
		ox=x+w/2;
		oy=y+h/2;
	}
	
	public void move(double dx, double dy) {
		x+=dx;
		y+=dy;
		ox+=dx;
		oy+=dy;
	}

	public Area getArea() {
		if(shape == RECTANGLE) {
			Rectangle2D.Double c = new Rectangle2D.Double(x,y,w,h);
			AffineTransform at = AffineTransform.getRotateInstance(a+Math.PI,ox,oy);
			Shape cc = at.createTransformedShape(c);
			return new Area(cc);
		}else if(shape == CIRCLE) {
			Ellipse2D.Double c = new Ellipse2D.Double(x-r,y-r,r*2,r*2);
			return new Area(c);
		}else if (shape == POLYGON){
			Polygon c = new Polygon(ppoints[0],ppoints[1],ppoints[0].length);
			return new Area(c);
		}else {
			return new Area();
		}
	}
	
	public void runUpdate() {
		
	}
}
