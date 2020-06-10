package enemies;

import java.awt.*;
import java.io.IOException;
import java.util.*;

import ce3.*;
import cookies.*;
import sprites.*;
import levels.*;

public class EnemyBlob extends Enemy{

	private SegmentCircle blob;
	private SpriteEnemy sprite;
	private Cookie target;
	private final int NEUTRAL=0,HIT=1;
	
	public EnemyBlob(Board frame, double xp, double yp) {
		super(frame,xp,yp);
		mass = 30;
		shields=1;
		steals = true;
		friction = .97;
		terminalVelocity = 2;
		normalVelocity = .2;
		acceleration = .2;
	}
	public void buildBody() {
		setImgs(new String[] {"blob","blobMad"});
		parts.add(blob = new SegmentCircle(board,this,x,y,30,0,Color.ORANGE));
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
	public void createStash() {
		ArrayList<String> possible = new ArrayList<String>();
		possible.add("Boost");
		possible.add("Projectile");
		stash.add(new CookieItem(board,0,0,board.currFloor.generateItem(possible.get((int)(Math.random()*possible.size()))),0));
		
	}
	public void runUpdate() {
		target = board.nearestCookie(x,y);
		if(target!=null && !Level.lineOfSight((int)(.5+x),(int)(.5+y),target.getX(),target.getY(), board.walls))target = null;
		if(target!=null) {
			accelerateToTarget(target.getX(),target.getY());
		}
		super.runUpdate();
	}
	public void collideWall(Wall w) {
		//kill();
	}
	public double getRadius() {return blob.getRadius();}
	public Color getColor() {return blob.getColor();}
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
