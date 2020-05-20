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
		mass = 300;
		shields=0;
		steals = true;
		friction = .97;
		terminalVelocity = 6;
		normalVelocity = .2;
		acceleration = 1;
		spawns = new ArrayList<Enemy>();
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
		int currCookies=0;
		for(int i=0; i<spawns.size(); i++) {
			Enemy e = spawns.get(i);
			currCookies+=e.stash.size();
			if(!board.enemies.contains(e))spawns.remove(e);
		}
		if(currCookies-prevCookies>=30||spawns.size()==0) {
			double angle = Math.random()*2*Math.PI;
			Enemy newE;
			spawns.add(newE = new EnemyBlob(board,xPos+board.currFloor.getScale()*75*Math.cos(angle),yPos+board.currFloor.getScale()*75*Math.sin(angle)));
			board.enemies.add(newE);
			prevCookies = currCookies;
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
