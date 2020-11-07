package items;
import ce3.*;
import levels.*;
import cookies.*;

public class ItemAutopilot extends Item{
	
	private double speedy;
	private double initx, inity;
	private Cookie nearest;
	private double searchRadius;
	
	public ItemAutopilot(Board frame) {
		super(frame);
		speedy = 5;
		searchRadius = 150;
		name = "Autopilot";
		desc="Automatically aims towards near cookies.`Amplify: Allowed radius increases ";
	}
	public void prepare() {	
	}
	public void initialize() {
		adjustSpeeds();
	}
	
	public void execute() {	
		if(checkCanceled())return;
		if(nearest == null) {
			nearest = board.nearestCookie(user.getX()+user.getXVel(),user.getY()+user.getYVel());
			if(Level.lineLength(nearest.getX(),nearest.getY(),user.getX(),user.getY())<searchRadius * board.currFloor.getScale()) {
				adjustSpeeds();
			}else {
				nearest = null;
			}
		}else {
			adjustSpeeds();
			if(!board.cookies.isEmpty())
				//user.averageVels(initx,inity,true);
				user.setXVel(initx,true);
				user.setYVel(inity,true);
		}
	}
	
	public void adjustSpeeds() { //aims to nearest cookie
		
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
		//speedy+=5;
		searchRadius+=100;
	}
	public void deamplify() {
		super.deamplify();
		//speedy-=5;
		searchRadius-=100;
	}
}
