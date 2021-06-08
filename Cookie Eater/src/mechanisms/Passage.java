package mechanisms;

import ce3.*;
import levels.*;
import entities.*;

public class Passage extends Mechanism{

	private Game game;
	private Board board;
	private Level entranceFloor; //where passage opens from
	private Level exitFloor; //where player goes into
	public static final int TOP=0, BOTTOM=1, RIGHT=2, LEFT=3; //the location of the wall that has the entrance
	private int width; //how wide the opening is
	private int inx,iny,outx,outy; //positions
	private int direction; //direction of passage entrance
	private boolean mode; //whether the passage is an entrance
	private final int gap = 30; //gap between screen edge and passage point
	
	public Passage(Game frame, Board gameboard, Level entrance, Level exit, int dir, int offset, int wid) {
		super(frame,gameboard,0,0);
		mass = 0;
		game = frame;
		board = gameboard;
		entranceFloor = entrance;
		exitFloor = exit;
		width = wid;
		direction = dir;
		build(dir,offset);
		setMode(true);
	}
	
	public Passage(Game frame, Board gameboard, Level entrance, Level exit, SaveData sd) {
		super(frame,gameboard,sd);
		entranceFloor = entrance;
		exitFloor = exit;
		direction = sd.getInteger("direction",0);
		mode = sd.getBoolean("mode",0);
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
	public boolean isHorizontal() {return direction==TOP || direction==BOTTOM;}
	public int getDirection() {
		if(mode) {
			return direction;
		}else {
			return opposite(direction);
		}
	}
	
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
		}
		return dir;
	}
	public Level getExit() {return exitFloor;}
	
	//whether a certain point has passed this passage
	public boolean passed(double xp, double yp) {
		boolean horiz = isHorizontal();
		int dir = getDirection();
		boolean less = dir == LEFT || dir == TOP;
		double myf = (horiz)?y:x;
		double theirf = (horiz)?yp:xp;
		double myb = (horiz)?x:y;
		double theirb = (horiz)?xp:yp;
		boolean pass = (less)?theirf<=myf:theirf>=myf;
		boolean within = Math.abs(myb - theirb) < width/2;
		return pass && within;
	}
	
	public void runUpdate() {
		for(int i=0; i<board.players.size(); i++) {
			Eater p = board.players.get(i);
			if(mode && passed(p.getX(),p.getY())) {
				board.setNext(exitFloor);
				p.win();
			}
		}
	}
}
