package entities;

import java.awt.*;
import java.awt.geom.*;
import java.io.IOException;

import ce3.*;
import cookies.*;
import sprites.*;
import levels.*;
import mechanisms.*;

public class EnemyBloc extends Enemy{

	private SegmentRectangle bloc;
	private SpriteEnemy sprite;
	private Cookie target;
	private final int NEUTRAL=0,HIT=1;
	
	public EnemyBloc(Game frame, int cycletime, double xp, double yp) {
		super(frame,cycletime,xp,yp);
		averageStats();
		mass = 60;
		setShields(2);
		steals = true;
		name = "Bloc";
	}
	public void averageStats() {
		acceleration=.5;
		max_velocity=5;
		friction=.15;
		terminal_velocity=50;
		calibrateStats();
	}
	public void buildBody() {
		setImgs(new String[] {"bloc","blocMad"});
		parts.add(bloc = new SegmentRectangle(board,this,x,y,60,60,Math.random()*Math.PI));
		try {
			sprite = new SpriteEnemy(board,bloc,imgs);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void orientParts() {
		bloc.setLocation(x,y);
		if(target!=null)bloc.setAngle(Math.atan2(target.getY()-y,target.getX()-x));
		super.orientParts();
	}
	public void runUpdate() {
		target = board.nearestCookie(x,y);
		if(target!=null && !Level.lineOfSight((int)(.5+x),(int)(.5+y),target.getX(),target.getY(), (int)(radius*scale*1.5), board.wallSpace))target = null;
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
			sprite.setImage(HIT);
		}else {
			g.setColor(Color.WHITE);
			sprite.setImage(NEUTRAL);
		}
		super.paint(g);
		Graphics2D g2 = (Graphics2D)g;
		AffineTransform origt = g2.getTransform(); //transformation to reset to
		//bloc.paint(g2);
		g2.rotate(bloc.getAngle(),x,y);
		sprite.paint(g);
		//g2.fillRect((int)(.5+bloc.getEdgeX()),(int)(.5+bloc.getEdgeY()),(int)(.5+bloc.getWidth()),(int)(.5+bloc.getLength()));
		g2.setTransform(origt);

	}
}
