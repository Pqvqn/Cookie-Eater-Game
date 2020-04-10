package items;
import ce3.*;

public class ItemBoost extends Item{
	
	double speedy;
	int dir;
	double x_vel,y_vel;
	
	public ItemBoost(Board frame) {
		super(frame);
	}

	public void initialize() {
		dir=player.getDir(); //store direction for later
		speedy = 30*board.currFloor.getScale(); //speed of boost
		x_vel=player.getXVel();
		y_vel=player.getYVel();
	}
	
	public void execute() {	
		if(x_vel!=0 && player.getXVel()/x_vel<0) //if direction has changed (hit wall) change accordingly
			x_vel=player.getXVel();
		if(y_vel!=0 && player.getYVel()/y_vel<0)
			y_vel=player.getYVel();
		player.setDir(Eater.NONE); //reset direction
		double x = x_vel;
		double y = y_vel;
		double h = speedy;
		double r; 
		if(x*x+y*y==0) { //ratio of normal dimensional velocity to new velocity
			r = 0;
		}else {
			r = Math.sqrt((h*h)/(x*x+y*y));
		}
		player.setXVel(x*r); //make fast
		player.setYVel(y*r);
	}
	
	public void end(boolean interrupted) {
		player.setDir(dir); //set dir back
	}
}
