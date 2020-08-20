package entities;

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
	
	public EnemyBlob(Board frame, int cycletime, double xp, double yp) {
		super(frame,cycletime,xp,yp);
		mass = 30;
		setShields(1);
		steals = true;
		friction = .999;
		terminalVelocity = 2;
		normalVelocity = .2;
		acceleration = .01;
		name = "Blob";
	}
	public void buildBody() {
		setImgs(new String[] {"blob","blobMad"});
		parts.add(blob = new SegmentCircle(board,this,x,y,30,0));
		try {
			sprite = new SpriteEnemy(board,blob,imgs);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void orientParts() {
		blob.setLocation(x,y);
		super.orientParts();
	}
	public void runUpdate() {
		target = board.nearestCookie(x,y);
		if(target!=null && !Level.lineOfSight((int)(.5+x),(int)(.5+y),target.getX(),target.getY(), board.walls))target = null;
		if(target!=null) {
			accelerateToTarget(target.getX(),target.getY());
		}
		super.runUpdate();
	}
	public void collideWall(Wall w) {
		//kill();
	}
	public double getRadius() {return blob.getRadius();}
	public void paint(Graphics g) {
		if(!getShielded()) {
			sprite.setImage(NEUTRAL);
		}else {
			sprite.setImage(HIT);
		}
		super.paint(g);
		sprite.paint(g);
	}
}
