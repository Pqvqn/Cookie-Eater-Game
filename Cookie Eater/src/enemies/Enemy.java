package enemies;

import java.awt.*;
import java.util.*;

import ce3.*;

public abstract class Enemy {

	protected ArrayList<Segment> parts;
	protected double xPos,yPos;
	protected Board board;
	protected Eater player;
	
	public Enemy(Board frame, double x, double y) {
		board = frame;
		xPos = x;
		yPos = y;
		player = board.player;
		parts = new ArrayList<Segment>();
		buildBody();
	}
	protected void buildBody() {
		
	}
	public void runUpdate() {
		testCollisions();
	}
	public void collideAt(double x, double y, double recoil) {
		
	}
	public void testCollisions() {
		for(int i=0; i<board.walls.size(); i++) { //for every wall, test if any parts impact
			Wall w = board.walls.get(i);
			for(int j=0; j<parts.size(); j++) {
				if(parts.get(j).collidesWithRect(w.getX(),w.getY(),w.getW(),w.getH())){
					collideAt(0,0,0); //temp
				}
			}
		}
	}
	public void kill() {
		board.enemies.remove(this);
	}
	public void paint(Graphics g) {
		for(int i=0; i<parts.size(); i++) {
			parts.get(i).paint(g);
		}
	}
}
