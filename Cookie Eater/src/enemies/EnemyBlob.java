package enemies;

import java.awt.*;
import java.io.IOException;

import ce3.*;
import sprites.*;

public class EnemyBlob extends Enemy{

	private SegmentCircle blob;
	private SpriteEnemy sprite;
	private final int NEUTRAL=0,HIT=1;
	
	public EnemyBlob(Board frame, double x, double y) {
		super(frame,x,y);
		mass = 100;
		constfric=.05*board.currFloor.getScale()/board.getAdjustedCycle();
		shields=3;
		steals = true;
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
	public void runUpdate() {
		super.runUpdate();
		constfric=.05*board.currFloor.getScale()/board.getAdjustedCycle();
		blob.setLocation(xPos,yPos);
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
