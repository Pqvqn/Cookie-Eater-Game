package entities;

import java.awt.*;
import java.io.IOException;
import java.util.*;

import ce3.*;
import mechanisms.*;
import sprites.*;

public class EnemySpawner extends Enemy{

	private SegmentCircle blob;
	private SpriteEnemy sprite;
	private int prevCookies; //number of cookies after last spawn
	private ArrayList<Enemy> spawns;
	private final int NEUTRAL=0,HIT=1;
	
	public EnemySpawner(Game frame, Board gameboard, int cycletime, double xp, double yp) {
		super(frame,gameboard,cycletime,xp,yp);
		averageStats();
		mass = 1000;
		setShields(0);
		steals = true;
		spawns = new ArrayList<Enemy>();
		name = "Spawner";
	}
	public EnemySpawner(Game frame, Board gameboard, SaveData sd, int cycle) {
		super(frame,gameboard,sd,cycle);
		setImgs(new String[] {"blobEmpty","blobMadEmpty"});
		for(Segment testPart : parts){
			if(testPart.name.equals("body")) {
				blob = (SegmentCircle)testPart;
			}
		}
		prevCookies = sd.getInteger("prevcookies",0);
		spawns = new ArrayList<Enemy>();
		for(Entity e : board.connections.get(sd.getString("connectcode",0))) {
			if(e instanceof Enemy) {
				spawns.add((Enemy)e);
			}
		}
		try {
			sprite = new SpriteEnemy(board,blob,imgs);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public SaveData getSaveData() {
		SaveData data = super.getSaveData();
		data.addData("prevcookies",prevCookies);
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
		setImgs(new String[] {"blobEmpty","blobMadEmpty"});
		parts.add(blob = new SegmentCircle(board,this,x,y,30,0,"body"));
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
		int currCookies=0;
		for(int i=0; i<spawns.size(); i++) { //find amount of collected cookies by spawns
			Enemy e = spawns.get(i);
			currCookies+=e.getStash().size();
			if(!board.currLevel.enemies.contains(e))spawns.remove(e);
		}
		if(currCookies-prevCookies>=30||spawns.size()==0) { //if enough collected, or no spawns, attempt spawn
			double angle = Math.random()*2*Math.PI; //choose random angle, turn into position
			Enemy newE = new EnemyBlob(game,board,game.getCycle(),x+board.currLevel.getScale()*150*Math.cos(angle),y+board.currLevel.getScale()*150*Math.sin(angle));
			if(!newE.collidesWithAnything()) { //if won't hit something, add spawn to lists and reset cookie collection count
				spawns.add(newE);
				board.currLevel.enemies.add(newE);
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
