package ce3;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import java.util.*;

public class Eater{
	
	private Queue<Double> x_positions;
	private Queue<Double> y_positions;
	private final int TRAIL_LENGTH = 10;
	private int radius;
	public static final int DEFAULT_RADIUS = 40;
	private double x, y;
	private final double START_X=300, START_Y=200;
	public static final int UP=0, RIGHT=1, DOWN=2, LEFT=3;
	private int direction;
	private double x_velocity, y_velocity;
	private final double ACCELERATION = .5;
	private final double MAX_VELOCITY = 10;
	private final double FRICTION = .1;
	private Color coloration;
	private boolean dO;
	
	private Board board;
	
	public Eater(Board frame) {
		dO= true;
		board = frame;
		x=START_X;
		y=START_Y;
		direction = RIGHT;
		x_velocity = 0;
		y_velocity = 0;
		radius=DEFAULT_RADIUS;
		coloration = Color.blue.brighter();
		/*x_positions = new LinkedList<Double>();
		y_positions = new LinkedList<Double>();
		for(int i=0; i<=TRAIL_LENGTH; i++) {
			x_positions.add(START_X);
			y_positions.add(START_Y);
		}*/
	}
	
	public int getX() {
		return (int)(x+.5);
	}
	public int getY() {
		return (int)(y+.5);
	}
	public int getTrailX() {
		if(x_positions.peek()==null) {
			return -1;
		}
		double curr = x_positions.remove();
		x_positions.add(curr);
		return (int)(curr+.5);
	}
	public int getTrailY() {
		if(y_positions.peek()==null) {
			return -1;
		}
		double curr = y_positions.remove();
		y_positions.add(curr);
		return (int)(curr+.5);
	}
	public int getTrailLength() {
		return TRAIL_LENGTH;
	}
	public void setDir(int dir) {
		direction = dir;
	}
	public int getRadius() {
		return radius;
	}
	/*public String getTrailLists() {
		String ret = "";
		for(int i=0; i<=TRAIL_LENGTH; i++) {
			double yes = x_positions.remove();
			ret+=yes+" ";
			x_positions.add(yes);
		}
		ret+=" - - ";
		for(int i=0; i<=TRAIL_LENGTH; i++) {
			double yes = y_positions.remove();
			ret+=yes+" ";
			y_positions.add(yes);
		}
		return ret;
	}*/
	public boolean collidesWithRect(int oX, int oY, int oW, int oH) {
		/*return (x + radius > oX && x - radius < oX + oW) &&
				(y + radius > oY && y - radius < oY + oH);*/
		return (Math.abs(x - oX) <= radius && y>=oY && y<=oY+oH) ||
				(Math.abs(x - (oX+oW)) <= radius && y>=oY && y<=oY+oH)||
				(Math.abs(y - oY) <= radius && x>=oX && x<=oX+oW) ||
				(Math.abs(y - (oY+oH)) <= radius && x>=oX && x<=oX+oW) ||
				(Math.sqrt((x-oX)*(x-oX) + (y-oY)*(y-oY))<=radius) ||
				(Math.sqrt((x-(oX+oW))*(x-(oX+oW)) + (y-oY)*(y-oY))<=radius) ||
				(Math.sqrt((x-oX)*(x-oX) + (y-(oY+oH))*(y-(oY+oH)))<=radius) ||
				(Math.sqrt((x-(oX+oW))*(x-(oX+oW)) + (y-(oY+oH))*(y-(oY+oH)))<=radius);
						
	}
	public void kill() {
		coloration = Color.black;
		x_velocity = 0;
		y_velocity = 0;
		dO = false;
		try {
			Thread.sleep(200);
		}catch(InterruptedException e){};
		board.resetLevel();
		reset();
	}
	public void win() {
		coloration = Color.green;
		x_velocity = 0;
		y_velocity = 0;
		dO = false;
		try {
			Thread.sleep(200);
		}catch(InterruptedException e){};
		board.nextLevel();
		reset();
	}
	public void reset() {
		coloration = Color.blue.brighter();
		x_velocity=0;
		y_velocity=0;
		x = START_X;
		y = START_Y;
		dO = true;
		direction = RIGHT;
	}
	
	public void runUpdate() {
		if(!dO)return;
		switch(direction) {
			case UP:
				if(y_velocity>-MAX_VELOCITY)
					y_velocity-=ACCELERATION;
				break;
			case RIGHT:
				if(x_velocity<MAX_VELOCITY)
					x_velocity+=ACCELERATION;
				break;
			case DOWN:
				if(y_velocity<MAX_VELOCITY)
					y_velocity+=ACCELERATION;
				break;
			case LEFT:
				if(x_velocity>-MAX_VELOCITY)
					x_velocity-=ACCELERATION;
				break;
		}
		x+=x_velocity;
		y+=y_velocity;
		if(Math.abs(x_velocity)<FRICTION){
			x_velocity=0;
		}else if(x_velocity>0) {
			x_velocity-=FRICTION;
		}else if(x_velocity<0) {
			x_velocity+=FRICTION;
		}
		if(Math.abs(y_velocity)<FRICTION){
			y_velocity=0;
		}else if(y_velocity>0) {
			y_velocity-=FRICTION;
		}else if(x_velocity<0) {
			y_velocity+=FRICTION;
		}
		/*x_positions.add(x);
		y_positions.add(y);
		x_positions.remove();
		y_positions.remove();*/
		if(board.score>=board.scoreToWin)
			win();
		
		for(int i=0; i<board.walls.size(); i++) {
			Wall rect = board.walls.get(i);
			if(collidesWithRect(rect.getX(), rect.getY(), rect.getW(), rect.getH())) {
				i=board.walls.size();
				kill();}
		}
		
	}
	
	public void paint(Graphics g) {
		g.setColor(coloration);
		g.fillOval((int)(x-radius), (int)(y-radius), radius*2, radius*2);
		/*int rate = 5;
		int x=0, y=0;
		int diam = player.getRadius()*2-player.getTrailLength()*rate;
		double alphaChange = 255/(player.getTrailLength());
		for(int i=0; i<=player.getTrailLength(); i++) {
			Color color = new Color(200,100,0,255);//(int)(i*alphaChange)
			g.setColor(color);
			g.fillOval(player.getTrailX()-(diam+i*rate)/2, player.getTrailY()-(diam+i*rate)/2, diam+i*rate, diam+i*rate);}*/
		
	}
;}
