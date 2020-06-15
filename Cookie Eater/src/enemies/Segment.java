package enemies;

import java.awt.*;

import ce3.*;
import sprites.*;
import items.*;

public abstract class Segment {

	protected Sprite sprite;
	protected double xPos,yPos;
	protected Board board;
	protected Entity owner;
	protected double angle;
	protected double scale;
	protected double extra_size; //extra area
	protected double size;
	
	public Segment(Board frame, Entity host, double x, double y, double a) {
		board = frame;
		xPos = x;
		yPos = y;
		angle = a;
		owner = host;
	}
	public boolean collidesWithRect(boolean extra, double x, double y, double w, double h) {return false;}
	public boolean collidesWithCircle(boolean extra, double x, double y, double r) {return false;}
	public boolean collidesWithSummon(boolean extra, Summon s) {return false;}
	public double[] rectHitPoint(boolean extra, double rx, double ry, double rw, double rh) {return null;}
	public double[] circHitPoint(boolean extra, double cx, double cy, double cr) {return null;}
	public double[] summonHitPoint(boolean extra, Summon s) {return null;}
	public void setLocation(double x, double y) {
		xPos = x;
		yPos = y;
	}
	public double getCenterX() {return xPos;}
	public double getCenterY() {return yPos;}
	public void setSize(double s) {size=s;}
	public double getSize() {return size;}
	public void setExtraSize(double es) {extra_size=es;}
	public double getExtraSize() {return extra_size;}
	public void paint(Graphics g) {
		scale = board.currFloor.getScale();
	}
}
