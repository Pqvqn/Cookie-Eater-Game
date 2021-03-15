package entities;

import java.awt.*;
import java.io.IOException;

import ce3.*;
import cookies.*;
import sprites.*;
import levels.*;
import mechanisms.*;

public class EnemyCrawler extends Enemy{

	private SegmentCircle blob;
	private SpriteEnemy sprite;
	private Cookie target;
	private final int NEUTRAL=0,HIT=1;
	
	public EnemyCrawler(Game frame, Board gameboard, int cycletime, double xp, double yp) {
		super(frame,gameboard,cycletime,xp,yp);
		averageStats();
		mass = 30;
		setShields(3);
		steals = true;
		name = "Crawler";
	}
	public EnemyCrawler(Game frame, Board gameboard, SaveData sd, int cycle) {
		super(frame,gameboard,sd,cycle);
		setImgs(new String[] {"blob","blobMad"});
		for(Segment testPart : parts){
			if(testPart.name.equals("body")) {
				blob = (SegmentCircle)testPart;
			}
		}
		try {
			sprite = new SpriteEnemy(board,blob,imgs);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void averageStats() {
		acceleration=.5;
		max_velocity=5;
		friction=.15;
		terminal_velocity=50;
		calibrateStats();
	}
	public void buildBody() {
		setImgs(new String[] {"blob","blobMad"});
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
		double xp = (x<board.x_resol/2) ? 0 : board.x_resol; //nearest walls
		double yp = (y<board.y_resol/2) ? 0 : board.y_resol;
		Cookie tar1 = board.nearestCookie(x,y); //nearest cookies to nearest walls
		Cookie tar2 = board.nearestCookie(x, y);
		if(tar1!=null && tar2!=null) {
			double dist1 = Level.lineLength(tar1.getX(),tar1.getY(),x,yp);
			double dist2 = Level.lineLength(tar2.getX(),tar2.getY(),xp,y);
			boolean see1 = Level.lineOfSight((int)(.5+x),(int)(.5+y),tar1.getX(),tar1.getY(), (int)(radius*scale*1.5), board.wallSpace);
			boolean see2 = Level.lineOfSight((int)(.5+x),(int)(.5+y),tar2.getX(),tar2.getY(), (int)(radius*scale*1.5), board.wallSpace);
			target = ((dist1<dist2 && see1) || !see2) ? tar1 : tar2; //target is the closest cookie to a border wall close to the player
		}
		
		if(target!=null && !Level.lineOfSight((int)(.5+x),(int)(.5+y),target.getX(),target.getY(), (int)(radius*scale*1.5), board.wallSpace))target = null;
		if(target!=null) {
			accelerateToTarget(target.getX(),target.getY());
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
		g.setColor(Color.white);
		//if(target!=null)g.drawOval(target.getX()-25,target.getY()-25,50,50); //highlights chosen target
	}
}
