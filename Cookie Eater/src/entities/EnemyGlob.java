package entities;

import java.awt.*;
import java.io.IOException;

import ce3.*;
import cookies.*;
import sprites.*;
import levels.*;
import mechanisms.*;

public class EnemyGlob extends Enemy{

	private SegmentCircle blob;
	private SpriteEnemy sprite;
	private SegmentCircle blob2;
	private SpriteEnemy sprite2;
	private SegmentCircle blob3;
	private SpriteEnemy sprite3;
	private Cookie target;
	private final int NEUTRAL=0,HIT=1;

	
	public EnemyGlob(Game frame, Board gameboard, int cycletime, double xp, double yp) {
		super(frame,gameboard,cycletime,xp,yp);
		averageStats();
		mass = 90;
		setShields(3);
		steals = true;
		name = "Tres Blob";
	}
	public EnemyGlob(Game frame, Board gameboard, SaveData sd, int cycle) {
		super(frame,gameboard,sd,cycle);
		setImgs(new String[] {"blob","blobMad","blobEmpty","blobMadEmpty"});
		for(Segment testPart : parts){
			if(testPart.name.equals("top")) {
				blob3 = (SegmentCircle)testPart;
			}else if(testPart.name.equals("right")) {
				blob = (SegmentCircle)testPart;
			}else if(testPart.name.equals("left")) {
				blob2 = (SegmentCircle)testPart;
			}
		}
		try {
			sprite = new SpriteEnemy(board,blob,imgs);
			sprite2 = new SpriteEnemy(board,blob2,imgs);
			sprite3 = new SpriteEnemy(board,blob3,imgs);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void averageStats() {
		acceleration=.25;
		max_velocity=5;
		friction=.15;
		terminal_velocity=50;
		calibrateStats();
	}
	public void buildBody() {
		setImgs(new String[] {"blob","blobMad","blobEmpty","blobMadEmpty"});
		parts.add(blob = new SegmentCircle(board,this,x,y,30,0,"right"));
		parts.add(blob2 = new SegmentCircle(board,this,x,y,30,0,"left"));
		parts.add(blob3 = new SegmentCircle(board,this,x,y,30,0,"top"));
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
		super.orientParts();
	}
	public void runUpdate() {
		if(Level.lineOfSight((int)(.5+x),(int)(.5+y),(int)(.5+explorerTarget.getX()),(int)(.5+explorerTarget.getY()), (int)(radius*scale*1.5), board.wallSpace)){
			max_velocity = 25;
			calibrateStats();
			accelerateToTarget(explorerTarget.getX(),explorerTarget.getY());
		}else {
			target = board.nearestCookie(x,y);
			if(target!=null && !Level.lineOfSight((int)(.5+x),(int)(.5+y),target.getX(),target.getY(), (int)(radius*scale*1.5), board.wallSpace))target = null;
			if(target!=null) {
				max_velocity = 10;
				calibrateStats();
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
