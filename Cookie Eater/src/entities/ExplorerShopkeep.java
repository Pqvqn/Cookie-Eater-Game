package entities;

import java.awt.*;
import java.util.*;

import ce3.*;
import cookies.*;
import levels.*;

public class ExplorerShopkeep extends Explorer{
	
	private SegmentCircle part;
	
	public ExplorerShopkeep(Board frame) {
		super(frame);
		name = "Unknown";
		chooseResidence();
		radius = 40;
		min_cat = 3;
		max_cat = 8;
		mass = 400;
		tester = new SegmentCircle(board,this,x,y,radius*2,0);
		input_speed = 5;
	}

	public void runEnds() {
		for(int i=0; i<Math.random()*4-1; i++) {
			removeRandomly();
		}
		for(int i=0; i<Math.random()*4-1 || to_sell.size()<min_cat; i++) {
			double choose = Math.random()*5;
			if(choose<=2) {
				addRandomly(new CookieShield(board,0,0,15));
			}else if(choose<=3) {
				addRandomly(new CookieItem(board,0,0,Level.generateItem(board,"Circle"),30));
			}else if(choose<=3.5) {
				addRandomly(new CookieItem(board,0,0,Level.generateItem(board,"Shield"),30));
			}else {
				addRandomly(new CookieItem(board,0,0,Level.generateItem(board,"Field"),30));
			}

		}
		while(to_sell.size()>max_cat)removeRandomly();
	}
	public void runUpdate() {
		super.runUpdate();
	}
	public void chooseDir() {
		tester.setSize(radius*2);
		ArrayList<Double> xs = new ArrayList<Double>();
		ArrayList<Double> ys = new ArrayList<Double>();
		ArrayList<Integer> dos = new ArrayList<Integer>();dos.add(0);dos.add(1);dos.add(2);dos.add(3);
		for(int i=0; i<4; i++) {
			double xv = x_velocity, yv = y_velocity; //used to find average x/y velocities over time period
			switch(i) { //change velocity based on direction accelerating in
				case UP:
					xv-=Math.signum(xv)*(((input_speed-Math.max(input_speed-Math.abs(xv/fric), 0))*fric)/2);
					yv+=-1*(((input_speed-Math.max(input_speed-(Math.abs(-1*maxvel-yv)/accel), 0))*accel)/2);
					break;
				case DOWN:
					xv-=Math.signum(xv)*(((input_speed-Math.max(input_speed-Math.abs(xv/fric), 0))*fric)/2);
					yv+=1*(((input_speed-Math.max(input_speed-(Math.abs(1*maxvel-yv)/accel), 0))*accel)/2);
					break;
				case LEFT:
					yv-=Math.signum(yv)*(((input_speed-Math.max(input_speed-Math.abs(yv/fric), 0))*fric)/2);
					xv+=-1*(((input_speed-Math.max(input_speed-(Math.abs(-1*maxvel-xv)/accel), 0))*accel)/2);
					break;
				case RIGHT:
					yv-=Math.signum(yv)*(((input_speed-Math.max(input_speed-Math.abs(yv/fric), 0))*fric)/2);
					xv+=1*(((input_speed-Math.max(input_speed-(Math.abs(1*maxvel-xv)/accel), 0))*accel)/2);
					break;
			}
			xs.add(x+xv*input_speed);
			ys.add(y+yv*input_speed);
			tester.setLocation(xs.get(i),ys.get(i)); //move tester to predicted location
			for(Wall w:board.walls) { //if tester hits a wall, rule this direction out
				if(tester.collidesWithRect(false, w.getX(), w.getY(), w.getW(), w.getH()) && dos.contains(i)) {
					dos.remove(dos.indexOf(i));
				}
			}
		}
		if(dos.isEmpty()) {
			
		}else {
			direction = dos.get((int)(Math.random()*dos.size()));
		}
		tester.setLocation(xs.get(direction),ys.get(direction));
	}
	public int doSpecial() {
		return -1;
	}
	public void chooseResidence() {
		residence = findFloor("Descending Labyrinths",false,0,2);
	}

	public void createStash() {
		super.createStash();
		for(int i=0; i<4; i++) {
			double choose = Math.random()*5;
			if(choose<=2) {
				addRandomly(new CookieShield(board,0,0,15));
			}else if(choose<=3) {
				addRandomly(new CookieItem(board,0,0,Level.generateItem(board,"Circle"),30));
			}else if(choose<=3.5) {
				addRandomly(new CookieItem(board,0,0,Level.generateItem(board,"Shield"),30));
			}else {
				addRandomly(new CookieItem(board,0,0,Level.generateItem(board,"Field"),30));
			}

		}
		shield_stash.add(new CookieShield(board,0,0,15));
		shield_stash.add(new CookieShield(board,0,0,15));
		shield_stash.add(new CookieShield(board,0,0,15));
		shield_stash.add(new CookieShield(board,0,0,15));
		shield_stash.add(new CookieShield(board,0,0,15));
		shield_stash.add(new CookieShield(board,0,0,15));
		shield_stash.add(new CookieShield(board,0,0,15));
		shield_stash.add(new CookieShield(board,0,0,15));
		shield_stash.add(new CookieShield(board,0,0,15));
		shield_stash.add(new CookieShield(board,0,0,15));
		shield_stash.add(new CookieShield(board,0,0,15));
		shield_stash.add(new CookieShield(board,0,0,15));
		shield_stash.add(new CookieShield(board,0,0,15));
		shield_stash.add(new CookieShield(board,0,0,15));
		shield_stash.add(new CookieShield(board,0,0,15));
	}
	public void buildBody() {
		parts.add(part = new SegmentCircle(board,this,x,y,radius,0));
	}
	public void orientParts() {
		part.setLocation(x,y);
		part.setSize(radius);
	}

	public void paint(Graphics g) {
		super.paint(g);
		if(part!=null)part.paint(g);
		if(tester!=null)part.paint(g);
		g.setColor(coloration);
		g.fillOval((int)(.5+x-getRadius()), (int)(.5+y-getRadius()), (int)(.5+getRadius()*2), (int)(.5+getRadius()*2));
		g.setColor(Color.WHITE);
		g.drawOval((int)(.5+tester.getCenterX()-getRadius()), (int)(.5+tester.getCenterY()-getRadius()), (int)(.5+getRadius()*2), (int)(.5+getRadius()*2));
	}
}
