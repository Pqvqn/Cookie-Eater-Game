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
			user.averageVels(initx,inity);
	}
	
	public void adjustSpeeds() { //aims to nearest cookie
		nearest = board.nearestCookie(user.getX(),user.getY());
		if(nearest==null)return;
		double rat = ((board.getAdjustedCycle()/15.0)*speedy*board.currFloor.getScale())/Level.lineLength(user.getX(), user.getY(), nearest.getX(), nearest.getY());
		initx = rat * (nearest.getX()-user.getX());
		inity = rat * (nearest.getY()-user.getY());
	}
	public void end(boolean interrupted) {
	}
	public void bounce(double x, double y) {
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
