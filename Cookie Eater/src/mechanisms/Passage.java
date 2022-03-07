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
	private static final int[] OPPOSITES = {1,0,3,2,5,4};
	private static final int[] ROTATE_ORDER = {2,0,1,3,-1,-1};
	protected int width; //how wide the opening is
	protected int inx,iny,outx,outy; //positions
	protected int direction; //direction of passage entrance
	protected boolean mode; //whether the passage is an entrance
	protected final int gap = 30; //gap between screen edge and passage point
	protected boolean triggered; //tracking if player has stayed inside
	
	public Passage(Game frame, Board gameboard, Level entrance, Level exit, int dir, int offset, int wid) {
		super(frame,gameboard,null,0,0);
		mass = 0;
		game = frame;
		board = gameboard;
		entranceRoom = entrance;
		exitRoom = exit;
		width = wid;
		direction = dir;
		triggered = false;
		setMode(true);
		build(dir,offset);
	}
	
	public Passage(Game frame, Board gameboard, ArrayList<Level> options, SaveData sd) {
		super(frame,gameboard,null,sd);
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
		
		direction = sd.getInteger("direction",0);
		inx = sd.getInteger("position",0);
		iny = sd.getInteger("position",1);
		outx = sd.getInteger("position",2);
		outy = sd.getInteger("position",3);
		width = sd.getInteger("width",0);
		triggered = sd.getBoolean("triggered",0);
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
		data.addData("rooms",entranceRoom!=null?entranceRoom.getID():Floor.blankCode,0);
		data.addData("rooms",exitRoom!=null?exitRoom.getID():Floor.blankCode,1);
		data.addData("triggered",triggered);
		return data;
	}
	
	//set positions based on orientation
	private void build(int dir, int offset) {
		if(dir==TOP) {
			iny = -gap;
			outy = level.y_resol+gap;
			inx = offset;
			outx = offset;
		}else if(dir==BOTTOM) {
			outy = -gap;
			iny = level.y_resol+gap;
			inx = offset;
			outx = offset;
		}else if(dir==LEFT) {
			inx = -gap;
			outx = level.x_resol+gap;
			iny = offset;
			outy = offset;
		}else if(dir==RIGHT) {
			outx = -gap;
			inx = level.x_resol+gap;
			iny = offset;
			outy = offset;
		}else if(dir==CEILING) {
			inx = level.x_resol/5;
			outx = 4*level.x_resol/5;
			iny = level.y_resol/2;
			outy = level.y_resol/2;
		}else if(dir==FLOOR) {
			inx = 4*level.x_resol/5;
			outx = level.x_resol/5;
			iny = level.y_resol/2;
			outy = level.y_resol/2;
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
		level = mode?entranceRoom:exitRoom;
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
	
	//order passage angles from pointing up around counterclockwise
	public int rotationOrder(int dir) {return ROTATE_ORDER[dir];}
	//the opposite direction from the given one
	public int opposite(int dir) {return OPPOSITES[dir];}
	
	//coordinates for the other side of the passage
	public int[] oppositeCoordinates() {
		return new int[] {(!mode)?inx:outx, (!mode)?iny:outy};
	}
	//min and max angles that can be moved to from this passage
	public double[] angleRange() {
		double[] range = {0,Math.PI};
		int add = rotationOrder(getDirection());
		if(add < 0) {
			range[1] = 2 * Math.PI;
		}else {
			range[0] = (range[0] + add * (Math.PI/2));
			range[1] = (range[1] + add * (Math.PI/2));
		}
		return range;
	}
	public Level getExit() {return exitRoom;}
	public Level getEntrance() {return entranceRoom;}
	public Level getOtherSide() {return mode?exitRoom:entranceRoom;}
	
	//whether a certain point has passed this passage
	public boolean passed(double xp, double yp) {
		return Math.sqrt(Math.pow(xp-x,2)+Math.pow(yp-y,2))<width/2;
	}
	
	//proportion of cookies needed to open gate
	public double cookieProportion() {
		return (entranceRoom.getExitProportion() + exitRoom.getExitProportion())/2;
	}
	
	//passes player through passage
	public void trigger(Eater e) {
		triggered=true;
		board.setNext(mode?exitRoom:entranceRoom);
		if(board.nextLevel.loaded()) {
			e.backtrack(this);
		}else{
			e.win(this);
		}
	}
	
	public void runUpdate() {
		for(int i=0; i<board.players.size(); i++) {
			Eater p = board.players.get(i);
			boolean pass = passed(p.getX(),p.getY());
			if(pass && !triggered && getDirection()!=CEILING && board.currLevel.getPassages().contains(this)) {
				trigger(p);
			}
			triggered = pass;
		}
		
	}
	public void paint(Graphics g) {
		g.setColor(Color.WHITE);
		g.drawOval((int)(x-width/2),(int)(y-width/2),width,width);
	}
}
