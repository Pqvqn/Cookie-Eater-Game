package entities;

import java.awt.*;

import ce3.*;
import cookies.*;
import levels.*;

public class ExplorerSidekick extends Explorer{
	
	private SegmentCircle part;
	
	public ExplorerSidekick(Board frame, int cycletime) {
		super(frame,cycletime);
		name = "Sidekick";
		chooseResidence();
		radius = 40;
		min_cat = 3;
		max_cat = 8;
		mass = 200;
		tester = new SegmentCircle(board,this,x,y,radius*2,0);
		input_speed = 30;
		start_shields = 10;
		setShields(start_shields);
		state = VENTURE;
	}

	public void runEnds() {
		super.runEnds();
		state = VENTURE;
		if(Math.random()>.2) {
			for(int i=0; i<Math.random()*2+1; i++) {
				residence = residence.getNext();
			}
		}else {
			kill();
		}
	}
	public void runUpdate() {
		super.runUpdate();
	}
	public void chooseDir() {
		tester.setSize(radius*2);
		double[] xs = new double[4];
		double[] ys = new double[4];
		//ArrayList<Integer> dos = new ArrayList<Integer>();dos.add(0);dos.add(1);dos.add(2);dos.add(3);
		int[] dos = {0,0,0,0}; //weight of quality for each dir choice
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
			xs[i]=(x+xv*input_speed);
			ys[i]=(y+yv*input_speed);
			tester.setLocation(xs[i],ys[i]); //move tester to predicted location
			bees[i][0] = xs[i];
			bees[i][1] = ys[i];
			yeehaw = false;
			for(Wall w:board.walls) { //if tester hits a wall, rule this direction out
				if(tester.collidesWithRect(false, w.getX(), w.getY(), w.getW(), w.getH())
						|| xs[i]<0 || xs[i]>board.X_RESOL || ys[i]<0 || ys[i]>board.Y_RESOL) {
					yees[i][0] = tester.rectHitPoint(false, w.getX(), w.getY(), w.getW(), w.getH())[0];
					yees[i][1] = tester.rectHitPoint(false, w.getX(), w.getY(), w.getW(), w.getH())[1];
					dos[i] += -100;
					yeehaw = true;
				}
			}
			for(Cookie c:board.cookies) { //if tester hits a wall, rule this direction out
				if(tester.collidesWithCircle(true, c.getX(), c.getY(), c.getRadius())){
					dos[i] += 1;
				}
			}
		}
		int bigind = direction; //most preferred direction (largest weight)
		int nearind = direction; //nearest cookie direction
		if(bigind<0 || bigind>=dos.length) {bigind = 0;nearind=0;}
		dos[nearind]+=10;
		for(int i=0; i<dos.length; i++) {
			Cookie near = board.nearestCookie(xs[i], ys[i]);
			Cookie nearb = board.nearestCookie(xs[nearind], ys[nearind]);
			if(near!=null && Level.lineLength(near.getX(),near.getY(),xs[i],ys[i]) < Level.lineLength(nearb.getX(),nearb.getY(),xs[nearind],ys[nearind])) {
				dos[nearind]-=10;
				dos[i]+=10;
				nearind = i;
			}
			if(dos[i]>dos[bigind]) {
				bigind = i;
			}
		}
		direction = bigind;
		/*if(dos.isEmpty()) {
			
		}else {
			direction = dos.get((int)(Math.random()*dos.size()));
		}*/
		tester.setLocation(xs[direction],ys[direction]);
	}
	boolean yeehaw;
	double[][] bees = {{0,0},{0,0},{0,0},{0,0}};
	double[][] yees = {{0,0},{0,0},{0,0},{0,0}};
	public int doSpecial() {
		return -1;
	}
	public void chooseResidence() {
		residence = findFloor("Descending Labyrinths",false,0,2);
	}

	public void createStash() {
		super.createStash();
	}
	public void buildBody() {
		parts.add(part = new SegmentCircle(board,this,x,y,radius,0));
	}
	public void orientParts() {
		part.setLocation(x,y);
		part.setSize(radius);
		tester.setSize(radius*2);
		tester.setExtraSize(radius*2);
	}

	public void paint(Graphics g) {
		super.paint(g);
		if(part!=null)part.paint(g);
		if(tester!=null)tester.paint(g);
		g.setColor(coloration);
		g.fillOval((int)(.5+x-getRadius()), (int)(.5+y-getRadius()), (int)(.5+getRadius()*2), (int)(.5+getRadius()*2));
		
		//debug tracker display stuff
		g.setColor(Color.WHITE);
		if(yeehaw) {
			g.setColor(Color.MAGENTA);
			
		}
		g.drawOval((int)(.5+tester.getCenterX()-((SegmentCircle)tester).getRadius()), (int)(.5+tester.getCenterY()-((SegmentCircle)tester).getRadius()), (int)(.5+((SegmentCircle)tester).getRadius()*2), (int)(.5+((SegmentCircle)tester).getRadius()*2));
		for(int i=0; i<4; i++) {
			if(i==direction) {
				g.setColor(Color.MAGENTA);
			}else {
				g.setColor(Color.WHITE);
			}
			g.drawOval((int)(.5+yees[i][0])-4, (int)(.5+yees[i][1])-4, 8, 8);
			g.drawOval((int)(.5+bees[i][0]-((SegmentCircle)tester).getRadius()), (int)(.5+bees[i][1]-((SegmentCircle)tester).getRadius()), (int)(.5+((SegmentCircle)tester).getRadius()*2), (int)(.5+((SegmentCircle)tester).getRadius()*2));
		}
	}
}
