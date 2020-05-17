package enemies;

import java.awt.*;
import java.io.IOException;

import ce3.*;
import cookies.*;
import sprites.*;
import levels.*;

public class EnemySlob extends Enemy{

	private SegmentCircle blob;
	private SpriteEnemy sprite;
	private SegmentCircle blob2;
	private SpriteEnemy sprite2;
	private Cookie target;
	private double[] chargeCoords; //coordinates to charge to
	private int prevCookies; //number of cookies after last charge
	private double angle;
	private final int NEUTRAL=0,HIT=1;

	
	public EnemySlob(Board frame, double x, double y) {
		super(frame,x,y);
		mass = 60;
		shields=5;
		steals = true;
		friction = .97;
		terminalVelocity = 6;
		normalVelocity = .2;
		acceleration = 1;
		angle = 0;
		chargeCoords = null;
		prevCookies = 0;
	}
	public void buildBody() {
		setImgs(new String[] {"blob","blobMad"});
		parts.add(blob = new SegmentCircle(board,this,xPos,yPos,30,0,Color.ORANGE));
		parts.add(blob2 = new SegmentCircle(board,this,xPos,yPos,30,0,Color.ORANGE));
		try {
			sprite = new SpriteEnemy(board,blob,imgs);
			sprite2 = new SpriteEnemy(board,blob2,imgs);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void orientParts() {
		blob.setLocation(xPos+15*Math.cos(angle),yPos+15*Math.sin(angle));
		blob2.setLocation(xPos-15*Math.cos(angle),yPos-15*Math.sin(angle));
	}
	public void runUpdate() {
		if(player.getDir()==Eater.NONE)return;
		if(chargeCoords!=null) {
			accelerateToTarget(chargeCoords[0],chargeCoords[1]);
			prevCookies = stash.size();
			if(Math.sqrt(Math.pow(Math.abs(chargeCoords[0]-xPos), 2)+Math.pow(Math.abs(chargeCoords[1]-yPos), 2))<100) {
				chargeCoords=null;
			}
		}else {
			target = board.nearestCookie(xPos,yPos);
			if(stash.size()-prevCookies>=10 && Level.lineOfSight((int)(.5+xPos),(int)(.5+yPos),player.getX(),player.getY(), board.walls)) {
				normalVelocity = 2;
				chargeCoords = new double[2];
				chargeCoords[0]=player.getX();
				chargeCoords[1]=player.getY();
				angle = Math.atan2(player.getY()-yPos, player.getX()-xPos);
			}else {
				if(target!=null && !Level.lineOfSight((int)(.5+xPos),(int)(.5+yPos),target.getX(),target.getY(), board.walls))target = null;
				if(target!=null) {
					normalVelocity = .1;
					accelerateToTarget(target.getX(),target.getY());
					angle = Math.atan2(target.getY()-yPos, target.getX()-xPos);
					
				}
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
		}else {
			sprite.setImage(HIT);
			sprite2.setImage(HIT);
		}
		super.paint(g);
		sprite.paint(g);
		sprite2.paint(g);
	}
}
