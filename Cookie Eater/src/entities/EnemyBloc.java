package entities;

import java.awt.*;
import java.awt.geom.*;
import java.io.IOException;

import ce3.*;
import cookies.*;
import sprites.*;
import levels.*;

public class EnemyBloc extends Enemy{

	private SegmentRectangle bloc;
	//private SpriteEnemy sprite;
	private Cookie target;
	private final int NEUTRAL=0,HIT=1;
	
	public EnemyBloc(Board frame, double xp, double yp) {
		super(frame,xp,yp);
		mass = 30;
		setShields(10);
		steals = true;
		friction = .999;
		terminalVelocity = 2;
		normalVelocity = .2;
		acceleration = .01;
		name = "Bloc";
	}
	public void buildBody() {
		setImgs(new String[] {"blob","blobMad"});
		parts.add(bloc = new SegmentRectangle(board,this,x,y,60,60,0));
		/*try {
			sprite = new SpriteEnemy(board,bloc,imgs);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
	public void orientParts() {
		bloc.setLocation(x,y);
		bloc.setAngle(0);
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
	public double getRadius() {return bloc.getSize()/2;}
	public void paint(Graphics g) {
		if(getShielded()) {
			g.setColor(Color.RED);
			//sprite.setImage(NEUTRAL);
		}else {
			g.setColor(Color.WHITE);
			//sprite.setImage(HIT);
		}
		super.paint(g);
		Graphics2D g2 = (Graphics2D)g;
		AffineTransform origt = g2.getTransform(); //transformation to reset to
		g2.rotate(0,x,y);
		g2.fillRect((int)(.5+bloc.getCenterX()-bloc.getWidth()/2),(int)(.5+bloc.getCenterY()-bloc.getLength()/2),(int)(.5+bloc.getWidth()),(int)(.5+bloc.getLength()));
		g2.setTransform(origt);
		//sprite.paint(g);
	}
}
