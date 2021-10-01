package entities;

import java.awt.*;
import java.io.IOException;

import ce3.*;
import cookies.*;
import sprites.*;
import levels.*;
import mechanisms.*;

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
	
	public EnemySlob(Game frame, Board gameboard, int cycletime, double xp, double yp) {
		super(frame,gameboard,cycletime,xp,yp);
		averageStats();
		mass = 60;
		setShields(5);
		steals = true;
		angle = 0;
		chargeCoords = null;
		prevCookies = 0;
		name = "Charger";
	}
	public EnemySlob(Game frame, Board gameboard, SaveData sd, int cycle) {
		super(frame,gameboard,sd,cycle);
		setImgs(new String[] {"blob","blobMad","blobEmpty","blobMadEmpty"});
		for(Segment testPart : parts){
			if(testPart.name.equals("front")) {
				blob = (SegmentCircle)testPart;
			}else if(testPart.name.equals("back")) {
				blob2 = (SegmentCircle)testPart;
			}
		}
		if(sd.getString("chargepoint",0)!=null) {
			chargeCoords[0] = sd.getDouble("chargepoint",0);
			chargeCoords[1] = sd.getDouble("chargepoint",1);
		}else {
			chargeCoords = null;
		}

		prevCookies = sd.getInteger("prevcookies",0);
		angle = sd.getDouble("angle",0);
		try {
			sprite = new SpriteEnemy(board,blob,imgs);
			sprite2 = new SpriteEnemy(board,blob2,imgs);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public SaveData getSaveData() {
		SaveData data = super.getSaveData();
		if(chargeCoords!=null) {
			data.addData("chargepoint",chargeCoords[0],0);
			data.addData("chargepoint",chargeCoords[1],1);
		}
		data.addData("prevcookies",prevCookies);
		data.addData("angle",angle);
		return data;
	}
	public void averageStats() {
		acceleration=1;
		max_velocity=5;
		friction=.95;
		terminal_velocity=80;
		calibrateStats();
	}
	public void buildBody() {
		setImgs(new String[] {"blob","blobMad","blobEmpty","blobMadEmpty"});
		parts.add(blob = new SegmentCircle(board,this,x,y,30,0,"front"));
		parts.add(blob2 = new SegmentCircle(board,this,x,y,30,0,"back"));
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
		super.orientParts();
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
			if(cash_stash.size()-prevCookies>=10 && Level.lineOfSight((int)(.5+x),(int)(.5+y),(int)(.5+explorerTarget.getX()),(int)(.5+explorerTarget.getY()), (int)(radius*scale*1.5), board.wallSpace)) {
				max_velocity = 10;
				chargeCoords = new double[2];
				chargeCoords[0]=explorerTarget.getX();
				chargeCoords[1]=explorerTarget.getY();
				angle = Math.atan2(explorerTarget.getY()-y, explorerTarget.getX()-x);
			}else {
				if(target!=null && !Level.lineOfSight((int)(.5+x),(int)(.5+y),target.getX(),target.getY(), (int)(radius*scale*1.5), board.wallSpace))target = null;
				if(target!=null) {
					max_velocity = 5;
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
