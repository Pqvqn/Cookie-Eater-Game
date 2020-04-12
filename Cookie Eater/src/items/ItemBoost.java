package items;
import ce3.*;

public class ItemBoost extends Item{
	
	double speedy;
	int dir;
	double initx, inity;
	
	public ItemBoost(Board frame) {
		super(frame);
	}

	public void initialize() {
		dir=player.getDir(); //store direction for later
		speedy = (board.getAdjustedCycle()/15.0)*30*board.currFloor.getScale(); //speed of boost
		player.lockControl(true);
		initx = player.getXVel();
		inity = player.getYVel();
	}
	
	public void execute() {	
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
		double h = speedy;
		double r; 
		if(x*x+y*y==0) { //ratio of normal dimensional velocity to new velocity
			r = 0;
		}else {
			r = Math.sqrt((h*h)/(x*x+y*y));
		}
		//player.setXVel(x*r); //make fast
		//player.setYVel(y*r);
		player.averageVels(x*r, y*r);
	}
	
	public void end(boolean interrupted) {
		player.lockControl(false);
	}
	public void bounce(boolean x, boolean y) {
		if(x)initx*=-1;
		if(y)inity*=-1;
	}
	public String name() {
		return "Boost";
	}
}
