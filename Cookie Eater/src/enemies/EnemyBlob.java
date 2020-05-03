package enemies;

import java.awt.*;

import ce3.*;

public class EnemyBlob extends Enemy{

	public EnemyBlob(Board frame, double x, double y) {
		super(frame,x,y);
		mass = 100;
		x_vel = 1;
		constfric=.0;
	}
	public void buildBody() {
		parts.add(new SegmentCircle(board,this,xPos,yPos,30,0,Color.ORANGE));
	}
	public void runUpdate() {
		super.runUpdate();
		parts.get(0).setLocation(xPos,yPos);
	}
	public void collideWall(Wall w) {
		//kill();
	}
}
