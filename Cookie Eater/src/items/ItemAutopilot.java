package items;
import ce3.*;
import levels.*;
import cookies.*;

public class ItemAutopilot extends Item{
	
	private double speedy;
	private double initx, inity;
	private Cookie nearest;
	private double searchRadius;
	private boolean velCheck; //whether this cycle should be skipped to account for directional changes
	
	public ItemAutopilot(Game frame, Board gameboard) {
		super(frame,gameboard);
		speedy = 5;
		searchRadius = 100;
		name = "Autopilot";
		desc="Automatically aims towards near cookies.`Amplify- Allowed radius and base speed increase";
	}
	public void prepare() {
		velCheck = true;
		nearest = null;
	}
	public void initialize() {
		//adjustSpeeds();
	}
	
	public void execute() {	
		if(checkCanceled())return;
		if(nearest == null) {
			if(velCheck) {
				velCheck = false;
			}else {
				nearest = user.nearestCookie((int)user.getXVel(),(int)user.getYVel());
				if(nearest!=null && Level.lineLength(nearest.getX(),nearest.getY(),user.getX(),user.getY())<searchRadius * board.currLevel.getScale()) {
					adjustSpeeds();
				}else {
					nearest = null;
					velCheck = true;
					user.setAverageVelOverride(false);
				}
			}
		}else {
			adjustSpeeds();
			if(!board.currLevel.chunker.containsCookie(nearest)) {
				nearest = null;
				velCheck = true;
				user.setAverageVelOverride(false);
			}
			if(!board.currLevel.cookies().isEmpty())
				//user.averageVels(initx,inity,true);
				user.setXVel(initx,true);
				user.setYVel(inity,true);
		}
	}
	
	public void adjustSpeeds() { //aims to nearest cookie
		if(nearest==null)return;
		user.setAverageVelOverride(true); //prevent averageVels from interfering
		//double rat = ((board.getAdjustedCycle()/15.0)*speedy*board.currFloor.getScale())/Level.lineLength(user.getX(), user.getY(), nearest.getX(), nearest.getY());
		//use current velocity, with base velocity if current is too low
		double speed = Math.max(Math.sqrt(Math.pow(user.getXVel(true),2)+Math.pow(user.getYVel(true),2)),(game.getAdjustedCycle()/15.0)*speedy*board.currLevel.getScale());
		double rat = speed/Level.lineLength(user.getX(), user.getY(), nearest.getX(), nearest.getY());
		initx = rat * (nearest.getX()-user.getX()); //update velocities towards target
		inity = rat * (nearest.getY()-user.getY());
	}
	public void end(boolean interrupted) {
		user.setAverageVelOverride(false);
	}
	public void bounce(Object bouncedOff, double x, double y) {
		adjustSpeeds();
	}
	public void amplify() {
		super.amplify();
		speedy+=5;
		searchRadius+=100;
	}
	public void deamplify() {
		super.deamplify();
		speedy-=5;
		searchRadius-=100;
	}
}
