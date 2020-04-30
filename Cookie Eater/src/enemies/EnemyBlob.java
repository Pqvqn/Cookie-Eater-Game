package enemies;

import java.awt.*;

import ce3.*;

public class EnemyBlob extends Enemy{

	public EnemyBlob(Board frame, double x, double y) {
		super(frame,x,y);
	}
	public void buildBody() {
		parts.add(new SegmentCircle(board,this,xPos,yPos,30,0,Color.ORANGE));
	}
}
