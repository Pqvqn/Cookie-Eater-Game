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
	private boolean horiz; //whether orientation is horizontal
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
		build(dir,offset);
	}
	
	public Passage(Game frame, Board gameboard, Level entrance, Level exit, SaveData sd) {
		super(frame,gameboard,sd);
		entranceFloor = entrance;
		exitFloor = exit;
		horiz = sd.getBoolean("horiz",0);
		mode = sd.getBoolean("mode",0);
		inx = sd.getInteger("position",0);
		iny = sd.getInteger("position",1);
		outx = sd.getInteger("position",2);
		outy = sd.getInteger("position",3);
		width = sd.getInteger("width",0);
	}
	
	public SaveData getSaveData() {
		SaveData data = super.getSaveData();
		data.addData("horiz",horiz);
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
		if(horiz) {
			return (in)?inx-width/2:outx-width/2;
		}else {
			return (in)?inx:outx;
		}
	}
	public int getRight(boolean in) {
		if(horiz) {
			return (in)?inx+width/2:outx+width/2;
		}else {
			return (in)?inx:outx;
		}
	}
	public int getUp(boolean in) {
		if(horiz) {
			return (in)?iny:outy;
		}else {
			return (in)?iny-width/2:outy-width/2;
		}
	}
	public int getDown(boolean in) {
		if(horiz) {
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
	public boolean isHorizontal() {return horiz;}
	public Level getExit() {return exitFloor;}
	
	public void runUpdate() {
		for(int i=0; i<board.players.size(); i++) {
			Eater p = board.players.get(i);
			if(mode && Level.lineLength(p.getX(),p.getY(),x,y)<p.getRadius()) {
				board.setNext(exitFloor);
				p.win();
			}
		}
	}
}
