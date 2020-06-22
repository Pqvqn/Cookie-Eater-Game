package entities;

import java.awt.*;
import java.io.IOException;
import java.util.*;

import ce3.*;
import sprites.*;

public class EnemySpawner extends Enemy{

	private SegmentCircle blob;
	private SpriteEnemy sprite;
	private int prevCookies; //number of cookies after last spawn
	private ArrayList<Enemy> spawns;
	private final int NEUTRAL=0,HIT=1;
	
	public EnemySpawner(Board frame, double xp, double yp) {
		super(frame,xp,yp);
		mass = 1000;
		setShields(0);
		steals = true;
		friction = .999;
		terminalVelocity = 6;
		normalVelocity = .2;
		acceleration = .05;
		spawns = new ArrayList<Enemy>();
	}
	public void buildBody() {
		setImgs(new String[] {"blobEmpty","blobMadEmpty"});
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
	}
	public void runUpdate() {
		int currCookies=0;
		for(int i=0; i<spawns.size(); i++) { //find amount of collected cookies by spawns
			Enemy e = spawns.get(i);
			currCookies+=e.getStash().size();
			if(!board.enemies.contains(e))spawns.remove(e);
		}
		if(currCookies-prevCookies>=30||spawns.size()==0) { //if enough collected, or no spawns, attempt spawn
			double angle = Math.random()*2*Math.PI; //choose random angle, turn into position
			Enemy newE = new EnemyBlob(board,x+board.currFloor.getScale()*150*Math.cos(angle),y+board.currFloor.getScale()*150*Math.sin(angle));
			if(!collidesWithAnything()) { //if won't hit something, add spawn to lists and reset cookie collection count
				spawns.add(newE);
				board.enemies.add(newE);
				prevCookies = currCookies;
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
		}else {
			sprite.setImage(HIT);
		}
		super.paint(g);
		sprite.paint(g);
	}
}
