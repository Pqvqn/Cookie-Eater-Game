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
		initx = user.getXVel();
		inity = user.getYVel();
	}
	
	public void execute() {	
		if(checkCanceled())return;
		if (user.getXVel()==0 && user.getYVel()==0 && board.player.getDir()!=Eater.NONE) {
			user.setXVel(Math.cos(dir));
			user.setYVel(Math.sin(dir));
		}
		double x = initx;
		double y = inity;
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
		initx = user.getXVel();
		inity = user.getYVel();
	}
	
	public void end(boolean interrupted) {
		user.lockControl(false);
	}
	public void bounce(double x, double y) {
		initx = user.getXVel();
		inity = user.getYVel();
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
