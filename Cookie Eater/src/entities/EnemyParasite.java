package entities;

import java.awt.*;
import java.io.IOException;

import ce3.*;
import sprites.*;
import levels.*;
import mechanisms.*;

public class EnemyParasite extends Enemy{

	private SegmentCircle blob;
	private SpriteEnemy sprite;
	private double[] stickPoint; //offset from entity to attach to
	private final int NEUTRAL=0,HIT=1;
	
	public EnemyParasite(Board frame, int cycletime, double xp, double yp) {
		super(frame,cycletime,xp,yp);
		averageStats();
		mass = 30;
		setShields(3);
		steals = true;
		stickPoint = new double[2];
		name = "Parasite";
	}
	public void averageStats() {
		acceleration=.5;
		max_velocity=50;
		friction=.2;
		terminal_velocity=50;
		calibrateStats();
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
		if(Math.sqrt(Math.pow(stickPoint[0],2)+Math.pow(stickPoint[1],2))<=explorerTarget.getRadius()+getRadius()) { //if too close, choose new offset point
			double r = (explorerTarget.getRadius()*1.5 + getRadius()*1.5) / Level.lineLength(explorerTarget.getX(), explorerTarget.getY(), getX(), getY()); //ratio for point near target in same direction
			stickPoint[0] = r*(getX()-explorerTarget.getX());
			stickPoint[1] = r*(getY()-explorerTarget.getY());
		}
		accelerateToTarget(explorerTarget.getX() + stickPoint[0],explorerTarget.getY() + stickPoint[1]); //move to stick point
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
		g.setColor(Color.white);
		//if(target!=null)g.drawOval(target.getX()-25,target.getY()-25,50,50); //highlights chosen target
	}
}
