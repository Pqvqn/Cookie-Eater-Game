package enemies;

import java.awt.*;
import java.io.IOException;

import ce3.*;
import sprites.*;

public class EnemyBlob extends Enemy{

	private SegmentCircle blob;
	private SpriteEnemyBlob sprite;
	
	public EnemyBlob(Board frame, double x, double y) {
		super(frame,x,y);
		mass = 100;
		x_vel = 1;
		constfric=.05*board.currFloor.getScale()/board.getAdjustedCycle();
		shields=3;
		steals = true;
		try {
			sprite = new SpriteEnemyBlob(board,this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void buildBody() {
		parts.add(blob = new SegmentCircle(board,this,xPos,yPos,30,0,Color.ORANGE));
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
			blob.setColor(Color.ORANGE);
		}else {
			blob.setColor(Color.RED);
		}
		super.paint(g);
		sprite.paint(g);
	}
}
