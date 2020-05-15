package enemies;

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

	
	public EnemyGlob(Board frame, double x, double y) {
		super(frame,x,y);
		mass = 100;
		shields=3;
		steals = true;
		friction = .97;
		terminalVelocity = 6;
		normalVelocity = .2;
		acceleration = 1;
	}
	public void buildBody() {
		setImgs(new String[] {"blob","blobMad"});
		parts.add(blob = new SegmentCircle(board,this,xPos,yPos,30,0,Color.ORANGE));
		parts.add(blob2 = new SegmentCircle(board,this,xPos,yPos,30,0,Color.ORANGE));
		parts.add(blob3 = new SegmentCircle(board,this,xPos,yPos,30,0,Color.ORANGE));
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
		blob.setLocation(xPos-15,yPos);
		blob2.setLocation(xPos+15,yPos);
		blob3.setLocation(xPos,yPos-15);
	}
	public void runUpdate() {
		if(player.getDir()==Eater.NONE)return;
		if(Level.lineOfSight((int)(.5+xPos),(int)(.5+yPos),player.getX(),player.getY(), board.walls)){
			normalVelocity = .6;
			accelerateToTarget(player.getX(),player.getY());
		}else {
			target = board.nearestCookie(xPos,yPos);
			if(target!=null && !Level.lineOfSight((int)(.5+xPos),(int)(.5+yPos),target.getX(),target.getY(), board.walls))target = null;
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
	public Color getColor() {return blob.getColor();}
	public void paint(Graphics g) {
		if(!isShielded()) {
			sprite.setImage(NEUTRAL);
			sprite2.setImage(NEUTRAL);
			sprite3.setImage(NEUTRAL);
		}else {
			sprite.setImage(HIT);
			sprite2.setImage(HIT);
			sprite3.setImage(HIT);
		}
		super.paint(g);
		sprite.paint(g);
		sprite2.paint(g);
		sprite3.paint(g);
	}
}
