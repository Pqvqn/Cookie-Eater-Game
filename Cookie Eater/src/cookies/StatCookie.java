package cookies;

import java.awt.Color;
import java.awt.Graphics;

import ce3.*;
//import levels.*;

public class StatCookie extends StoreCookie{

	private double[][] mr;
	private double accelChange;
	private double maxvelChange;
	private double fricChange;
	
	public StatCookie(Board frame, int startx, int starty) {
		super(frame,startx,starty);
		mr = player.getMovementRand();
		accelChange = 0;
		maxvelChange = 0;
		fricChange = 0;
		switch((int)(Math.random()*4)) {
		case 0:
			break;
		case 1:
			accelChange = .2*Math.signum(Math.random()-.5)*(mr[0][1]-mr[0][0])*(int)(Math.random()+1.2);
			break;
		case 2:
			maxvelChange = .2*Math.signum(Math.random()-.5)*(mr[1][1]-mr[1][0])*(int)(Math.random()+1.2);
			break;
		case 3:
			fricChange = .2*Math.signum(Math.random()-.5)*(mr[2][1]-mr[2][0])*(int)(Math.random()+1.2);
			break;
		}
		
	}
	public void kill() {
		player.addToMovement(accelChange,maxvelChange,fricChange);
		super.kill();
		//player.win();
		board.score++;
	}
	public void paint(Graphics g) {
		Color c;
		if(accelChange>.2*(mr[0][1]-mr[0][0])) {
			c = new Color(180,180,255);
		}else if(accelChange>0) {
			c = new Color(60,60,255);
		}else if(accelChange<-.2*(mr[0][1]-mr[0][0])) {
			c = new Color(0,0,25);
		}else if(accelChange<0) {
			c = new Color(0,0,75);
		}else if(maxvelChange>.2*(mr[1][1]-mr[1][0])) {
			c = new Color(180,255,180);
		}else if(maxvelChange>0) {
			c = new Color(60,255,60);
		}else if(maxvelChange<-.2*(mr[1][1]-mr[1][0])) {
			c = new Color(0,25,0);
		}else if(maxvelChange<0) {
			c = new Color(0,75,0);
		}else if(fricChange>.2*(mr[2][1]-mr[2][0])) {
			c = new Color(255,180,180);
		}else if(fricChange>0) {
			c = new Color(255,60,60);
		}else if(fricChange<-.2*(mr[2][1]-mr[2][0])) {
			c = new Color(25,0,0);
		}else if(fricChange<0) {
			c = new Color(75,0,0);
		}else {
			c = new Color(120,120,120);
		}
		g.setColor(c);
		g.fillOval((int)(.5+x-radius*board.currFloor.getScale()), (int)(.5+y-radius*board.currFloor.getScale()), (int)(.5+radius*board.currFloor.getScale()*2), (int)(.5+radius*board.currFloor.getScale()*2));
	}
}
