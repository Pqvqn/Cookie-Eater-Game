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
	
	public EnemyBlob(Board frame, double x, double y) {
		super(frame,x,y);
		mass = 30;
		shields=1;
		steals = true;
		friction = .97;
		terminalVelocity = 6;
		normalVelocity = .2;
		acceleration = 1;
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
	public void orientParts() {
		blob.setLocation(xPos,yPos);
	}
	public void runUpdate() {
		if(player.getDir()==Eater.NONE)return;
		target = board.nearestCookie(xPos,yPos);
		if(target!=null && !Level.lineOfSight((int)(.5+xPos),(int)(.5+yPos),target.getX(),target.getY(), board.walls))target = null;
		if(target!=null) {
			accelerateToTarget(target.getX(),target.getY());
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
		}else {
			sprite.setImage(HIT);
		}
		super.paint(g);
		sprite.paint(g);
	}
}
