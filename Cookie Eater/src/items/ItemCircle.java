package items;
import ce3.*;

public class ItemCircle extends Item{

	private double count;
	private double radius;
	private double radians;
	private double initx,inity;
	
	public ItemCircle(Board frame) {
		super(frame);
		count = 0;
		radians = 0;
	}

	public void initialize() {
		radius = 100*board.currFloor.getScale();
		player.lockControl(true);
		initx=player.getXVel();
		inity=-1*player.getYVel();
		if(initx==0 && inity==0) { //if not moving, base on direction
			switch(player.getDir()) {
			case Eater.UP:
				radians = Math.PI/2;
				break;
			case Eater.RIGHT:
				radians = 0;
				break;
			case Eater.DOWN:
				radians = 3*Math.PI/2;
				break;
			case Eater.LEFT:
				radians = Math.PI;
				break;
			}
		}else {
			radians = Math.PI + ((initx>=0) ? Math.atan(inity/initx) : Math.atan(inity/initx) + Math.PI); //find angle to start at
		}
		
	}
	
	public void execute() {
		if(count<=Math.PI*2) {
			count+=.1;
			radians+=.1;
			if(player.getXVel()!=0 && (radius*Math.cos(radians)-radius*Math.cos(radians-.1))/player.getXVel()<0)//if direction has changed (hit wall) change accordingly
				radians += Math.PI;
			if(player.getYVel()!=0 && (-(radius*Math.sin(radians)-radius*Math.sin(radians-.1)))/player.getYVel()<0)
				radians += Math.PI;
			double x = (radius*Math.cos(radians+.1)-radius*Math.cos(radians));
			double y = -(radius*Math.sin(radians+.1)-radius*Math.sin(radians));
			double h = Math.sqrt(Math.pow(player.getXVel()+player.getFriction()*sign(player.getXVel()),2)+Math.pow(player.getYVel()+player.getFriction()*sign(player.getXVel()),2));
			double r; 
			if(x*x+y*y==0) { //ratio of normal dimensional velocity to new velocity
				r = 0;
			}else {
				r = Math.sqrt((h*h)/(x*x+y*y));
			}
			player.setXVel(x*r); //make fast
			player.setYVel(y*r);

		}
	}
	public int sign(double t) {
		if (t<0) {
			return -1;
		}else if (t<0){
			return 1;
		}else {
			return 0;
		}
	}
	
	public void end(boolean interrupted) {
		player.lockControl(false);
		count = 0;
		player.setXVel(initx*-1);
		player.setYVel(inity);
	}
	public String name() {
		return "Circle";
	}
}
