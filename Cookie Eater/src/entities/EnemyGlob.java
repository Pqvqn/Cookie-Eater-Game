package entities;

import java.awt.*;
import java.io.IOException;

import ce3.*;
import cookies.*;
import sprites.*;
import levels.*;

public class EnemyGlob extends Enemy{

	private SegmentCircle blob;
	private SpriteEnemy sprite;
	private SegmentCircle blob2;
	private SpriteEnemy sprite2;
	private SegmentCircle blob3;
	private SpriteEnemy sprite3;
	private Cookie target;
	private final int NEUTRAL=0,HIT=1;

	
	public EnemyGlob(Board frame, double xp, double yp) {
		super(frame,xp,yp);
		mass = 90;
		setShields(3);
		steals = true;
		friction = .999;
		terminalVelocity = 2;
		normalVelocity = .2;
		acceleration = .005;
		name = "Tres Blob";
	}
	public void buildBody() {
		setImgs(new String[] {"blob","blobMad","blobEmpty","blobMadEmpty"});
		parts.add(blob = new SegmentCircle(board,this,x,y,30,0));
		parts.add(blob2 = new SegmentCircle(board,this,x,y,30,0));
		parts.add(blob3 = new SegmentCircle(board,this,x,y,30,0));
		try {
			sprite = new SpriteEnemy(board,blob,imgs);
			sprite2 = new SpriteEnemy(board,blob2,imgs);
			sprite3 = new SpriteEnemy(board,blob3,imgs);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void orientParts() {
		blob.setLocation(x+15,y);
		blob2.setLocation(x-15,y);
		blob3.setLocation(x,y-15);
	}
	public void runUpdate() {
		if(Level.lineOfSight((int)(.5+x),(int)(.5+y),(int)(.5+player.getX()),(int)(.5+player.getY()), board.walls)){
			normalVelocity = .6;
			accelerateToTarget(player.getX(),player.getY());
		}else {
			target = board.nearestCookie(x,y);
			if(target!=null && !Level.lineOfSight((int)(.5+x),(int)(.5+y),target.getX(),target.getY(), board.walls))target = null;
			if(target!=null) {
				normalVelocity = .2;
				accelerateToTarget(target.getX(),target.getY());
			}
		}
		super.runUpdate();
	}
	public void collideWall(Wall w) {
		//kill();
	}
	public double getRadius() {return blob.getRadius();}
	public void paint(Graphics g) {
		if(!getShielded()) {
			sprite.setImage(NEUTRAL+2);
			sprite2.setImage(NEUTRAL+2);
			sprite3.setImage(NEUTRAL);
		}else {
			sprite.setImage(HIT+2);
			sprite2.setImage(HIT+2);
			sprite3.setImage(HIT);
		}
		super.paint(g);
		sprite.paint(g);
		sprite2.paint(g);
		sprite3.paint(g);
	}
}
