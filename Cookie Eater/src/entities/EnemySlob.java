package entities;

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

	
	public EnemySlob(Board frame, double xp, double yp) {
		super(frame,xp,yp);
		mass = 60;
		setShields(5);
		steals = true;
		friction = .999;
		terminalVelocity = 3;
		normalVelocity = .2;
		acceleration = .05;
		angle = 0;
		chargeCoords = null;
		prevCookies = 0;
	}
	public void buildBody() {
		setImgs(new String[] {"blob","blobMad","blobEmpty","blobMadEmpty"});
		parts.add(blob = new SegmentCircle(board,this,x,y,30,0));
		parts.add(blob2 = new SegmentCircle(board,this,x,y,30,0));
		try {
			sprite = new SpriteEnemy(board,blob,imgs);
			sprite2 = new SpriteEnemy(board,blob2,imgs);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void orientParts() {
		blob.setLocation(x+15*Math.cos(angle),y+15*Math.sin(angle));
		blob2.setLocation(x-15*Math.cos(angle),y-15*Math.sin(angle));
	}
	public void runUpdate() {
		if(chargeCoords!=null) {
			accelerateToTarget(chargeCoords[0],chargeCoords[1]);
			prevCookies = cash_stash.size();
			if(Math.sqrt(Math.pow(Math.abs(chargeCoords[0]-x), 2)+Math.pow(Math.abs(chargeCoords[1]-y), 2))<100) {
				chargeCoords=null;
			}
		}else {
			target = board.nearestCookie(x,y);
			if(cash_stash.size()-prevCookies>=10 && Level.lineOfSight((int)(.5+x),(int)(.5+y),(int)(.5+player.getX()),(int)(.5+player.getY()), board.walls)) {
				normalVelocity = 2;
				chargeCoords = new double[2];
				chargeCoords[0]=player.getX();
				chargeCoords[1]=player.getY();
				angle = Math.atan2(player.getY()-y, player.getX()-x);
			}else {
				if(target!=null && !Level.lineOfSight((int)(.5+x),(int)(.5+y),target.getX(),target.getY(), board.walls))target = null;
				if(target!=null) {
					normalVelocity = .1;
					accelerateToTarget(target.getX(),target.getY());
					angle = Math.atan2(target.getY()-y, target.getX()-x);
					
				}
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
			sprite.setImage(NEUTRAL);
			sprite2.setImage(NEUTRAL+2);
		}else {
			sprite.setImage(HIT);
			sprite2.setImage(HIT+2);
		}
		super.paint(g);
		sprite2.paint(g);
		sprite.paint(g);
	}
}
