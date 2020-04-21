package items;

import java.awt.*;

import ce3.*;

public class SummonJab extends Summon{
	
	private int range;
	private double angle;
	private int distforward;
	private double amountforward;
	private double holdfor;
	private final int EXTEND = 1, RETRACT = -1, SHEATHED = 0, HOLD = 2;
	private int state;
	private int framecount;
	
	public SummonJab(Board frame, Eater summoner, int r) {
		super(frame, summoner);
		range = r;
		amountforward = 1.5*board.getAdjustedCycle();
		holdfor = 200/board.getAdjustedCycle();
	}
	public void setRange(int r) {
		range = r;
	}
	public void prepare() {
		x=player.getX();
		y=player.getY();
		angle = playerAngle();
		state = EXTEND;
	}
	public void initialize() {
		distforward = 0;
		framecount = 0;
	}
	public void execute() {
		x=player.getX();
		y=player.getY();
		switch(state) {
		case SHEATHED:
			distforward=0;
			break;
		case EXTEND:
			if(distforward>=range)
				state=HOLD;
			distforward+=amountforward;
			break;
		case HOLD:
			if(framecount++>holdfor)
				state = RETRACT;
			break;
		case RETRACT:
			if(distforward<0)
				state=SHEATHED;
			distforward-=amountforward;
			break;
		}
	}
	public void end(boolean interrupted) {
			
	}
	public void paint(Graphics2D g2) {
		g2.setColor(Color.WHITE);
		g2.rotate(angle,x,y);
		g2.fillRect(x-5,y-5,10+distforward,10);
	}
}
