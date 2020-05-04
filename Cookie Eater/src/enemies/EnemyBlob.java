package enemies;

import java.awt.*;

import ce3.*;

public class EnemyBlob extends Enemy{

	public EnemyBlob(Board frame, double x, double y) {
		super(frame,x,y);
		mass = 100;
		x_vel = 1;
		constfric=.05*board.currFloor.getScale()/board.getAdjustedCycle();
		shields=3;
		steals = true;
	}
	public void buildBody() {
		parts.add(new SegmentCircle(board,this,xPos,yPos,30,0,Color.ORANGE));
	}
	public void runUpdate() {
		super.runUpdate();
		constfric=.05*board.currFloor.getScale()/board.getAdjustedCycle();
		parts.get(0).setLocation(xPos,yPos);
	}
	public void collideWall(Wall w) {
		//kill();
	}
	public void paint(Graphics g) {
		if(shield_frames==0) {
			parts.get(0).setColor(Color.ORANGE);
		}else {
			parts.get(0).setColor(Color.RED);
		}
		super.paint(g);
	}
}
