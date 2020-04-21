package items;

import java.awt.*;

import ce3.*;

public abstract class Summon {

	protected Board board;
	protected Eater player;
	protected int x,y;
	protected double rotation;
	
	public Summon(Board frame, Eater summoner) {
		board = frame;
		player = summoner;
	}
	//set all vars before other items change them
	public void prepare() {
		
	}
	//run on special start
	public void initialize() {
		
	}
	//run while special is active
	public void execute() {
		
	}
	//run when special ends
	public void end(boolean interrupted) {
		
	}
	public void paint(Graphics2D g2) {
		
	}
	
	
	public double playerVelAngle() {
		return Math.atan2(player.getYVel(),player.getXVel());
	}
	public double playerDirAngle() {
		switch(player.getDir()) {
		case(Eater.UP):
			return 3*Math.PI/2;
		case(Eater.RIGHT):
			return 0;
		case(Eater.DOWN):
			return Math.PI/2;
		case(Eater.LEFT):
			return Math.PI;
		default:
			return 0;
		}
	}
	
}
