package items;
import ce3.*;
import levels.*;
import cookies.*;

public class ItemAutopilot extends Item{
	
	private double speedy;
	private double initx, inity;
	private Cookie nearest;
	
	public ItemAutopilot(Board frame) {
		super(frame);
		speedy = 5;
		name = "Autopilot";
		desc="Automatically aims towards nearest cookie.`Amplify: Speed increases";
	}
	public void prepare() {	
	}
	public void initialize() {
		adjustSpeeds();
	}
	
	public void execute() {	
		if(checkCanceled())return;
		adjustSpeeds();
		if(!board.cookies.isEmpty())
			//user.averageVels(initx,inity,true);
			user.setXVel(initx,true);
			user.setYVel(inity,true);
	}
	
	public void adjustSpeeds() { //aims to nearest cookie
		nearest = board.nearestCookie(user.getX()+user.getXVel(),user.getY()+user.getYVel());
		if(nearest==null)return;
		//double rat = ((board.getAdjustedCycle()/15.0)*speedy*board.currFloor.getScale())/Level.lineLength(user.getX(), user.getY(), nearest.getX(), nearest.getY());
		double rat = ((board.getAdjustedCycle()/15.0)*(Math.sqrt(Math.pow(user.getXVel(true),2)+Math.pow(user.getYVel(true),2))/Level.lineLength(user.getX(), user.getY(), nearest.getX(), nearest.getY())));
		initx = rat * (nearest.getX()-user.getX());
		inity = rat * (nearest.getY()-user.getY());
		
		//AVERAGE VEL OVERRIDE
	}
	public void end(boolean interrupted) {
	}
	public void bounce(Object bouncedOff, double x, double y) {
		adjustSpeeds();
	}
	public void amplify() {
		super.amplify();
		speedy+=5;
	}
	public void deamplify() {
		super.deamplify();
		speedy-=5;
	}
}
