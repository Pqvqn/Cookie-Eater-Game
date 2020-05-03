package enemies;

import java.awt.*;
import java.util.*;

import ce3.*;
import levels.Level;

public abstract class Enemy {

	protected ArrayList<Segment> parts;
	protected double xPos,yPos;
	protected Board board;
	protected Eater player;
	protected double mass;
	protected double x_vel, y_vel;
	protected double fric;
	protected double constfric;
	
	public Enemy(Board frame, double x, double y) {
		board = frame;
		xPos = x;
		yPos = y;
		player = board.player;
		parts = new ArrayList<Segment>();
		x_vel=0;
		y_vel=0;
		mass = 100;
		buildBody();
	}
	//create parts for the enemy
	protected void buildBody() {
		
	}
	//runs each cycle
	public void runUpdate() {
		fric = constfric*board.currFloor.getScale();
		xPos+=x_vel;
		yPos+=y_vel;
		if(Math.abs(x_vel)>fric) {
			x_vel-=Math.signum(x_vel)*fric;
		}else {
			x_vel=0;
		}
		if(Math.abs(y_vel)>fric) {
			y_vel-=Math.signum(y_vel)*fric;
		}else {
			y_vel=0;
		}
		testCollisions();
	}
	//given point, adjusts velocity for bouncing off from that point
	public void collideAt(double x, double y, double oxv, double oyv, double om) {
		
		double v1x = x-xPos,v1y = y-yPos;
		double proj1 = (x_vel*v1x+y_vel*v1y)/(v1x*v1x+v1y*v1y);
		double proj2 = (x_vel*v1y+y_vel*-v1x)/(v1x*v1x+v1y*v1y);
		double ewXvel = proj1*v1x+proj2*v1y;
		double ewYvel = proj1*v1y+proj2*-v1x;
		
		double oVel =Math.sqrt(Math.pow(oxv,2)+Math.pow(oyv,2));
		
		double newVel = (om*oVel+mass*totalVel()) / 2    /   mass;
		//double rat = totalVel()/Level.lineLength(x, y, xPos, yPos);
		
		double rat = newVel/oVel;
		double newX = oxv * rat;
		double newY = oyv * rat;
		
		double fX = newX + ewXvel;
		double fY = newY + ewYvel;
		
		double rat2 = newVel/Math.sqrt(Math.pow(fX,2)+Math.pow(fY,2));
		x_vel = fX * rat2;
		y_vel = fY * rat2;
		System.out.println(newVel);
		/*
		//double massProp = 2*mass/(om+mass);
		double rat = Math.sqrt(Math.pow(oxv,2)+Math.pow(oyv,2))/Level.lineLength(x, y, xPos, yPos);
		//rat*=massProp;
		double oxadd = rat*(xPos-x);
		double oyadd = rat*(yPos-y);
		double rat2 = totalVel()/Level.lineLength(x, y, xPos, yPos);
		//rat2*=1-massProp;
		double txadd = rat2*(xPos-x;
		double tyadd = rat2*(yPos-y);
		x_vel=oxadd+txadd;
		y_vel=oyadd+tyadd;*/
	}
	//when hit wall
	public void collideWall() {
		//kill();
	}
	//tests all collisions
	public void testCollisions() {
		for(int j=0; j<parts.size(); j++) {
			for(int i=0; i<board.walls.size(); i++) { //for every wall, test if any parts impact
				Wall w = board.walls.get(i);
				if(parts.get(j).collidesWithRect(w.getX(),w.getY(),w.getW(),w.getH())){
					collideAt(parts.get(j).rectHitPoint(w.getX(),w.getY(),w.getW(),w.getH())[0],
							parts.get(j).rectHitPoint(w.getX(),w.getY(),w.getW(),w.getH())[1],
							0,0,999999999);
					collideWall();
				}
			}
			if(parts.get(j).collidesWithCircle(player.getX(),player.getY(),player.getTotalRadius())) { //test if hits player
				collideAt(parts.get(j).circHitPoint(player.getX(),player.getY(),player.getTotalRadius())[0],
						parts.get(j).circHitPoint(player.getX(),player.getY(),player.getTotalRadius())[1],
						player.getXVel(),player.getYVel(),player.getMass());
			}
		}
	}
	//deletes this enemy
	public void kill() {
		board.enemies.remove(this);
	}
	//draws
	public void paint(Graphics g) {
		for(int i=0; i<parts.size(); i++) {
			parts.get(i).paint(g);
		}
	}
	public double totalVel() {
		return Math.sqrt(x_vel*x_vel+y_vel*y_vel);
	}
}
