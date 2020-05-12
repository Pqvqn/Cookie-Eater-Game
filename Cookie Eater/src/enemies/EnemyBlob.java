package enemies;

import java.awt.*;
import java.io.IOException;

import ce3.*;
import cookies.*;
import sprites.*;
import levels.*;

public class EnemyBlob extends Enemy{

	private SegmentCircle blob;
	private SpriteEnemy sprite;
	private Cookie target;
	private final int NEUTRAL=0,HIT=1;
	private double accel;
	private double normMaxSpeed;
	
	public EnemyBlob(Board frame, double x, double y) {
		super(frame,x,y);
		mass = 100;
		shields=3;
		steals = true;
	}
	public void buildBody() {
		setImgs(new String[] {"blob","blobMad"});
		parts.add(blob = new SegmentCircle(board,this,xPos,yPos,30,0,Color.ORANGE));
		try {
			sprite = new SpriteEnemy(board,blob,imgs);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void runUpdate() {
		if(player.getDir()==Eater.NONE)return;
		target = board.nearestCookie(xPos,yPos);
		if(target!=null && !Level.lineOfSight((int)(.5+xPos),(int)(.5+yPos),target.getX(),target.getY(), board.walls))target = null;
		constfric = Math.pow(0.97, 1/(double)board.getAdjustedCycle());
		maxSpeed = 6*board.currFloor.getScale()*board.getAdjustedCycle();
		normMaxSpeed = .2*board.currFloor.getScale()*board.getAdjustedCycle();
		accel = 1*board.currFloor.getScale()/board.getAdjustedCycle();
		if(target!=null) {
			double rat = accel / Level.lineLength(xPos, yPos, target.getX(), target.getY());
			if(Level.lineLength(xPos, yPos, target.getX(), target.getY())==0) rat = 0;
			if(Math.abs(x_vel)<normMaxSpeed)x_vel+=rat*(target.getX()-xPos);
			if(Math.abs(y_vel)<normMaxSpeed)y_vel+=rat*(target.getY()-yPos);
		}
		super.runUpdate();
		blob.setLocation(xPos,yPos);
	}
	public void collideWall(Wall w) {
		//kill();
	}
	public double getRadius() {return blob.getRadius();}
	public Color getColor() {return blob.getColor();}
	public void paint(Graphics g) {
		if(!isShielded()) {
			sprite.setImage(NEUTRAL);
		}else {
			sprite.setImage(HIT);
		}
		super.paint(g);
		sprite.paint(g);
	}
}
