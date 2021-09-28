package mechanisms;

import java.awt.*;
import java.util.*;

import ce3.*;
import levels.*;
import entities.*;

public class Passage extends Mechanism{

	protected Level entranceRoom; //where passage opens from
	protected Level exitRoom; //where player goes into
	public static final int TOP=0, BOTTOM=1, RIGHT=2, LEFT=3, FLOOR=4, CEILING=5; //the location of the wall that has the entrance
	protected int width; //how wide the opening is
	protected int inx,iny,outx,outy; //positions
	protected int direction; //direction of passage entrance
	protected boolean mode; //whether the passage is an entrance
	protected final int gap = 30; //gap between screen edge and passage point
	
	public Passage(Game frame, Board gameboard, Level entrance, Level exit, int dir, int offset, int wid) {
		super(frame,gameboard,0,0);
		mass = 0;
		game = frame;
		board = gameboard;
		entranceRoom = entrance;
		exitRoom = exit;
		width = wid;
		direction = dir;
		build(dir,offset);
		setMode(true);
	}
	
	public Passage(Game frame, Board gameboard, ArrayList<Level> options, SaveData sd) {
		super(frame,gameboard,sd);
		mode = sd.getBoolean("mode",0);
		
		for(int i=0; i<options.size(); i++) {
			String id = options.get(i).getID();
			if(sd.getString("rooms",0).equals(id)) {
				entranceRoom = options.get(i);
			}
			if(sd.getString("rooms",1).equals(id)) {
				exitRoom = options.get(i);
			}
		}
		/*if(current.getID().equals(sd.getString("floors",0))) {
			entranceFloor = current;
			for(int i=0; i<nextOptions.size(); i++) {
				if(nextOptions.get(i).getID().equals(sd.getString("floors",1))) {
					exitFloor = nextOptions.get(i);
				}
			}
		}else if(current.getID().equals(sd.getString("floors",1))) {
			exitFloor = current;
			for(int i=0; i<prevOptions.size(); i++) {
				if(prevOptions.get(i).getID().equals(sd.getString("floors",0))) {
					entranceFloor = prevOptions.get(i);
				}
			}
		}*/
		
		direction = sd.getInteger("direction",0);
		inx = sd.getInteger("position",0);
		iny = sd.getInteger("position",1);
		outx = sd.getInteger("position",2);
		outy = sd.getInteger("position",3);
		width = sd.getInteger("width",0);
		setMode(mode);
	}
	
	public SaveData getSaveData() {
		SaveData data = super.getSaveData();
		data.addData("direction",direction);
		data.addData("mode",mode);
		data.addData("position",inx,0);
		data.addData("position",iny,1);
		data.addData("position",outx,2);
		data.addData("position",outy,3);
		data.addData("width",width);
		data.addData("rooms",entranceRoom.getID(),0);
		data.addData("rooms",exitRoom.getID(),1);
		return data;
	}
	
	//set positions based on orientation
	private void build(int dir, int offset) {
		if(dir==TOP) {
			iny = -gap;
			outy = board.y_resol+gap;
			inx = offset;
			outx = offset;
		}else if(dir==BOTTOM) {
			outy = -gap;
			iny = board.y_resol+gap;
			inx = offset;
			outx = offset;
		}else if(dir==LEFT) {
			inx = -gap;
			outx = board.x_resol+gap;
			iny = offset;
			outy = offset;
		}else if(dir==RIGHT) {
			outx = -gap;
			inx = board.x_resol+gap;
			iny = offset;
			outy = offset;
		}else if(dir==CEILING) {
			inx = board.x_resol/4;
			outx = 3*board.x_resol/4;
			iny = board.y_resol/2;
			outy = board.y_resol/2;
		}else if(dir==FLOOR) {
			inx = 3*board.x_resol/4;
			outx = board.x_resol/4;
			iny = board.y_resol/2;
			outy = board.y_resol/2;
		}
	}
	
	public int getLeft(boolean in) {
		if(isHorizontal()) {
			return (in)?inx-width/2:outx-width/2;
		}else {
			return (in)?inx:outx;
		}
	}
	public int getRight(boolean in) {
		if(isHorizontal()) {
			return (in)?inx+width/2:outx+width/2;
		}else {
			return (in)?inx:outx;
		}
	}
	public int getUp(boolean in) {
		if(isHorizontal()) {
			return (in)?iny:outy;
		}else {
			return (in)?iny-width/2:outy-width/2;
		}
	}
	public int getDown(boolean in) {
		if(isHorizontal()) {
			return (in)?iny:outy;
		}else {
			return (in)?iny+width/2:outy+width/2;
		}
	}
	public boolean isEntrance() {return mode;}
	public void setMode(boolean isEntrance) {
		mode = isEntrance;
		x = (mode)?inx:outx;
		y = (mode)?iny:outy;
	}
	public void setMode(Level level) {
		if(level.equals(entranceRoom)) {
			setMode(true);
		}
		if(level.equals(exitRoom)) {
			setMode(false);
		}
	}
	public boolean entranceAt(Level l) {
		if(l==entranceRoom)return true;
		if(l==exitRoom)return false;
		return false;
	}
	
	public boolean isHorizontal() {return direction==TOP || direction==BOTTOM;}
	public boolean isVertical() {return direction==LEFT || direction==RIGHT;}
	public int getDirection() {
		if(mode) {
			return direction;
		}else {
			return opposite(direction);
		}
	}
	public int getEntranceDirection() {
		return direction;
	}
	public int getWidth() {return width;}
	
	//the opposite direction from the given one
	public int opposite(int dir) {
		switch(dir) {
		case TOP:
			return BOTTOM;
		case BOTTOM:
			return TOP;
		case LEFT:
			return RIGHT;
		case RIGHT:
			return LEFT;
		case CEILING:
			return FLOOR;
		case FLOOR:
			return CEILING;
		}
		return dir;
	}
	//coordinates for the other side of the passage
	public int[] oppositeCoordinates() {
		return new int[] {(!mode)?inx:outx, (!mode)?iny:outy};
	}
	public Level getExit() {return exitRoom;}
	public Level getEntrance() {return entranceRoom;}
	
	//whether a certain point has passed this passage
	public boolean passed(double xp, double yp) {
		
		return Math.sqrt(Math.pow(xp-x,2)+Math.pow(yp-y,2))<width/2;
		
		/*if(!isHorizontal() && !isVertical()) {
			return Math.sqrt(Math.pow(xp-x,2)+Math.pow(yp-y,2))<width/2;
		}
		
		boolean horiz = isHorizontal();
		int dir = getDirection();
		boolean less = dir == LEFT || dir == TOP;
		double myf = (horiz)?y:x;
		double theirf = (horiz)?yp:xp;
		double myb = (horiz)?x:y;
		double theirb = (horiz)?xp:yp;
		boolean pass = (less)?theirf<=myf:theirf>=myf;
		boolean within = Math.abs(myb - theirb) < width/2;
		return pass && within;*/
	}
	
	//proportion of cookies needed to open gate
	public double cookieProportion() {
		return (entranceRoom.getExitProportion() + exitRoom.getExitProportion())/2;
	}
	
	public void runUpdate() {
		for(int i=0; i<board.players.size(); i++) {
			Eater p = board.players.get(i);
			if(mode && passed(p.getX(),p.getY())) {
				board.setNext(exitRoom);
				p.win(this);
			}
		}
	}
	public void paint(Graphics g) {
		g.setColor(Color.WHITE);
		g.drawOval((int)(x-width/2),(int)(y-width/2),width,width);
	}
}
