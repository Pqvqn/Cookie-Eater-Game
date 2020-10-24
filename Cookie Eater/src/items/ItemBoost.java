package items;
import ce3.*;

public class ItemBoost extends Item{
	
	private double speedy;
	private double dir;
	private double initx, inity;
	
	public ItemBoost(Board frame) {
		super(frame);
		speedy = 30;
		name = "Boost";
		desc="Zooms forward`Amplify: Speed increases";
	}
	public void prepare() {
		dir=user.getAim(); //store direction for later
		
	}
	public void initialize() {
		user.lockControl(true);
		initx = user.getXVel(true);
		inity = user.getYVel(true);
	}
	
	public void execute() {	
		if(checkCanceled())return;
		if (user.getXVel(true)==0 && user.getYVel(true)==0) {
			user.setXVel(Math.cos(dir),true);
			user.setYVel(Math.sin(dir),true);
		}
		double x = initx==0 ? user.getXVel(true) : initx;
		double y = inity==0 ? user.getYVel(true) : inity;
		double h = (board.getAdjustedCycle()/15.0)*speedy*board.currFloor.getScale(); //speed of boost
		double r; 
		if(x*x+y*y==0) { //ratio of normal dimensional velocity to new velocity
			r = 0;
		}else {
			r = Math.sqrt((h*h)/(x*x+y*y));
		}
		//user.setXVel(x*r); //make fast
		//user.setYVel(y*r);
		user.averageVels(x*r, y*r);
		initx = user.getXVel(true);
		inity = user.getYVel(true);
	}
	
	public void end(boolean interrupted) {
		user.lockControl(false);
	}
	public void bounce(Object bouncedOff, double x, double y) {
		initx = user.getXVel(true);
		inity = user.getYVel(true);
	}
	public void amplify() {
		super.amplify();
		speedy+=30;
	}
	public void deamplify() {
		super.deamplify();
		speedy-=30;
	}
}
