package items;
import ce3.*;

public class ItemBoost extends Item{
	
	private double speedy;
	private int dir;
	private double initx, inity;
	
	public ItemBoost(Board frame) {
		super(frame);
		speedy = 30;
		name = "Boost";
		desc="Zooms forward`Amplify: Speed increases";
	}
	public void prepare() {
		dir=player.getDir(); //store direction for later
		
	}
	public void initialize() {
		player.lockControl(true);
		initx = player.getXVel();
		inity = player.getYVel();
	}
	
	public void execute() {	
		if(checkCanceled())return;
		if (player.getXVel()==0 && player.getYVel()==0 && dir!=Eater.NONE) {
			switch(dir) {
			case(Eater.UP):
				player.setYVel(-1);
				break;
			case(Eater.RIGHT):
				player.setXVel(1);
				break;
			case(Eater.DOWN):
				player.setYVel(1);
				break;
			case(Eater.LEFT):
				player.setXVel(-1);
				break;
			}
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
		//player.setXVel(x*r); //make fast
		//player.setYVel(y*r);
		player.averageVels(x*r, y*r);
		initx = player.getXVel();
		inity = player.getYVel();
	}
	
	public void end(boolean interrupted) {
		player.lockControl(false);
	}
	public void bounce(double x, double y) {
		initx = player.getXVel();
		inity = player.getYVel();
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
