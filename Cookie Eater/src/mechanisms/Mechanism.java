package mechanisms;

import java.awt.*;
import java.awt.geom.Area;

import ce3.*;

public class Mechanism {

	Game game;
	Board board;
	double x,y;
	double mass;
	
	public Mechanism(Game frame, Board gameboard, int xPos, int yPos) {
		game = frame;
		board = gameboard;
		x = xPos;
		y = yPos;
		mass = 100;
	}
	
	public Mechanism(Game frame, Board gameboard, SaveData sd) {
		game = frame;
		board = gameboard;
		x = sd.getDouble("position",0);
		y = sd.getDouble("position",1);
		mass = sd.getDouble("mass",0);
	}
	public SaveData getSaveData() {
		SaveData data = new SaveData();
		data.addData("position",x,0);
		data.addData("position",y,1);
		data.addData("mass",mass);
		return data;
	}
	
	public void runUpdate() {
		
	}
	public double getX() {return x;}
	public double getY() {return y;}
	public double getXVel() {return 0;}
	public double getYVel() {return 0;}
	public double getMass() {return mass;}
	public Area getArea() {
		return new Area();
	}
	
	public void paint(Graphics g) {
		
	}
	
	public void remove() {
		board.mechanisms.remove(this);
	}
}
