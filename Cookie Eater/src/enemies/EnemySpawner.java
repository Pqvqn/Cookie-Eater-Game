package enemies;

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
	
	public EnemySpawner(Board frame, double x, double y) {
		super(frame,x,y);
		mass = 1000;
		shields=0;
		steals = true;
		friction = .97;
		terminalVelocity = 6;
		normalVelocity = .2;
		acceleration = 1;
		spawns = new ArrayList<Enemy>();
	}
	public void buildBody() {
		setImgs(new String[] {"blobEmpty","blobMadEmpty"});
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
		int currCookies=0;
		for(int i=0; i<spawns.size(); i++) { //find amount of collected cookies by spawns
			Enemy e = spawns.get(i);
			currCookies+=e.stash.size();
			if(!board.enemies.contains(e))spawns.remove(e);
		}
		if(currCookies-prevCookies>=30||spawns.size()==0) { //if enough collected, or no spawns, attempt spawn
			double angle = Math.random()*2*Math.PI; //choose random angle, turn into position
			Enemy newE = new EnemyBlob(board,xPos+board.currFloor.getScale()*150*Math.cos(angle),yPos+board.currFloor.getScale()*150*Math.sin(angle));
			boolean add = true;
			for(int j=0; newE != null && j<newE.getParts().size(); j++) { //abort spawn if it will hit wall
				for(int i=0; newE != null && i<board.walls.size(); i++) {
					Wall w = board.walls.get(i);
					if(newE.getParts().get(j).collidesWithRect(w.getX(),w.getY(),w.getW(),w.getH())){
						add=false;
						newE = null;
					}
				}
			}
			if(add) { //if go ahead, add spawn to lists and reset cookie collection count
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
