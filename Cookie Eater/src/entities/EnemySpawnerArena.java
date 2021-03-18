package entities;

import java.awt.*;
import java.io.IOException;
import java.util.*;

import ce3.*;
import cookies.*;
import mechanisms.*;
import sprites.*;
import items.*;

public class EnemySpawnerArena extends Enemy{

	private SegmentCircle blob;
	private SpriteEnemy sprite;
	private ArrayList<Enemy> spawns;
	private int spawnCap; //how many enemies to have at the same time
	private double spawn_rate, spawn, spawn_counter; //time between spawn attempts	
	private final int NEUTRAL=0,HIT=1;
	
	public EnemySpawnerArena(Game frame, Board gameboard, int cycletime, double xp, double yp) {
		super(frame,gameboard,cycletime,xp,yp);
		averageStats();
		mass = 1000000;
		setShields(0);
		steals = true;
		spawns = new ArrayList<Enemy>();
		name = "Arena Spawner";
		spawnCap = 4;
		spawn_rate = 200;
		setGhost(true);
	}
	public EnemySpawnerArena(Game frame, Board gameboard, SaveData sd, int cycle) {
		super(frame,gameboard,sd,cycle);
		setImgs(new String[] {"blobEmpty","blobMadEmpty"});
		for(Segment testPart : parts){
			if(testPart.name.equals("body")) {
				blob = (SegmentCircle)testPart;
			}
		}
		spawnCap = sd.getInteger("spawn",1);
		spawn_rate = sd.getInteger("spawn",0);
		try {
			sprite = new SpriteEnemy(board,blob,imgs);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public SaveData getSaveData() {
		SaveData data = super.getSaveData();
		data.addData("spawn",spawn_rate,0);
		data.addData("spawn",spawnCap,1);
		// TODO load enemy list into spawner
		return data;
	}
	public void averageStats() {
		acceleration=1;
		max_velocity=5;
		friction=.15;
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
		//run spawn tests after counter is ready
		if(spawn_counter++ >= spawn && spawn>0) {
			spawn_counter = 0;
			
			for(int i=0; i<spawns.size(); i++) { //remove dead spawns
				Enemy e = spawns.get(i);
				if(!board.enemies.contains(e))spawns.remove(e);
			}
			if(spawns.size()<spawnCap) { //if not enough spawns, spawn
				spawnEnemy();
			}
		}
		
		super.runUpdate();
	}
	
	//create random enemy randomly near, with random items
	public void spawnEnemy() {
		double angle = Math.random()*2*Math.PI; //choose random angle, turn into position
		double sx = x+board.currFloor.getScale()*150*Math.cos(angle);
		double sy = y+board.currFloor.getScale()*150*Math.sin(angle);
		
		int reward = 1;
		Enemy newE = null;
		int choice = (int)(Math.random()*10);
		if(choice <= 3) {
			newE = new EnemyBlob(game,board,game.getCycle(),sx,sy);
			reward = 1;
		}else if(choice <= 5) {
			newE = new EnemyBloc(game,board,game.getCycle(),sx,sy);
			reward = 2;
		}else if(choice <= 7) {
			newE = new EnemyCrawler(game,board,game.getCycle(),sx,sy);
			reward = 2;
		}else if(choice <= 8) {
			newE = new EnemySlob(game,board,game.getCycle(),sx,sy);
			reward = 8;
		}else if(choice <= 9) {
			newE = new EnemyGlob(game,board,game.getCycle(),sx,sy);
			reward = 10;
		}else if(choice <= 10) {
			newE = new EnemyParasite(game,board,game.getCycle(),sx,sy);
			reward = 5;
		}
		
		if(newE!=null && !newE.collidesWithAnything()) { //if won't hit something, add spawn to lists and reset cookie collection count
			spawns.add(newE);
			board.enemies.add(newE);
			newE.addCookies(reward);
			
			if(Math.random()<.1) {
				ArrayList<String> possible = new ArrayList<String>();
				possible.add("Autopilot");
				possible.add("Boost");
				possible.add("Circle");
				possible.add("Clone");
				possible.add("Chain");
				possible.add("Field");
				possible.add("Flow");
				possible.add("Ghost");
				possible.add("Hold");
				possible.add("Rebound");
				possible.add("Recharge");
				possible.add("Recycle");
				possible.add("Repeat");
				possible.add("Return");
				possible.add("Ricochet");
				possible.add("Shield");
				possible.add("Shrink");
				possible.add("Slowmo");
				possible.add("Melee");
				possible.add("Projectile");
				possible.add("Teleport");
				newE.giveCookie(new CookieItem(game,board,0,0,
						Item.generateItem(game,possible.get((int)(Math.random()*possible.size()))),(int)(Math.random()*50)+10));
			}
				
		}
	}
	
	public void calibrateStats() {
		super.calibrateStats();
		spawn = spawn_rate / calibration_ratio;
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
