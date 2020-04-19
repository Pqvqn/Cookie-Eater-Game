package items;
import ce3.*;

public class ItemBoost extends Item{
	
	double speedy;
	int dir;
	double initx, inity;
	
	public ItemBoost(Board frame) {
		super(frame);
		speedy = 30;
		name = "Boost";
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
	}
	
	public void end(boolean interrupted) {
		player.lockControl(false);
	}
	public void bounce(boolean x, boolean y) {
		if(x)initx*=-1;
		if(y)inity*=-1;
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
