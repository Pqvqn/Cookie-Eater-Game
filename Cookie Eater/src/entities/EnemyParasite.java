package entities;

import java.awt.*;
import java.io.IOException;

import ce3.*;
import cookies.*;
import sprites.*;
import levels.*;

public class EnemyParasite extends Enemy{

	private SegmentCircle blob;
	private SpriteEnemy sprite;
	private Entity target; //entity to attach to
	private double[] stickPoint; //offset from entity to attach to
	private final int NEUTRAL=0,HIT=1;
	
	public EnemyParasite(Board frame, int cycletime, double xp, double yp) {
		super(frame,cycletime,xp,yp);
		mass = 30;
		setShields(3);
		steals = true;
		friction = .9;
		terminalVelocity = 10;
		normalVelocity = 10;
		acceleration = .1;
		stickPoint = new double[2];
		name = "Parasite";
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
		if(target==null)target=board.player;
		if(Math.sqrt(Math.pow(stickPoint[0],2)+Math.pow(stickPoint[1],2))<=target.getRadius()+getRadius()) { //if too close, choose new offset point
			double r = (target.getRadius()*1.5 + getRadius()*1.5) / Level.lineLength(target.getX(), target.getY(), getX(), getY()); //ratio for point near target in same direction
			stickPoint[0] = r*(getX()-target.getX());
			stickPoint[1] = r*(getY()-target.getY());
		}
		accelerateToTarget(target.getX() + stickPoint[0],target.getY() + stickPoint[1]); //move to stick point
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
