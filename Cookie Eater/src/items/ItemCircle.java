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
		radius = 200*board.currFloor.getScale();
		player.lockControl(true);
		initx=player.getXVel();
		inity=-1*player.getYVel();
		if(initx==0 && inity==0) { //if not moving, base on direction
			switch(player.getDir()) {
			case Eater.DOWN:
				radians = Math.PI/2;
				break;
			case Eater.LEFT:
				radians = 0;
				break;
			case Eater.UP:
				radians = 3*Math.PI/2;
				break;
			case Eater.RIGHT:
				radians = Math.PI;
				break;
			}
		}else {
			radians = Math.PI + ((initx>=0) ? Math.atan(inity/initx) : Math.atan(inity/initx) + Math.PI); //find angle to start at
		}
		//player.setXVel(0);
		//player.setYVel(0);
	}
	
	public void execute() {
		if(count<=Math.PI*2) {
			count+=.1;
			radians+=.1;
			/*if(player.getXVel()!=0 && (radius*Math.cos(radians)-radius*Math.cos(radians-.1))/player.getXVel()<0)//if direction has changed (hit wall) change accordingly
				radians += Math.PI;
			if(player.getYVel()!=0 && (-(radius*Math.sin(radians)-radius*Math.sin(radians-.1)))/player.getYVel()<0)
				radians += Math.PI;*/
			/*double[] spd = relativeVel((radius*Math.cos(radians+.1)-radius*Math.cos(radians)),
					(radius*Math.cos(radians+.1)-radius*Math.cos(radians)),
					player.getXVel()+player.getFriction()*Math.signum(player.getXVel()),
					player.getYVel()+player.getFriction()*Math.signum(player.getYVel()));*/
					
			player.averageVels(radius*Math.cos(radians+.1)-radius*Math.cos(radians), -(radius*Math.sin(radians)-radius*Math.sin(radians-.1)));

		}
	}
	/*public double[] relativeVel(double x, double y, double hX, double hY) {
		double h = Math.sqrt(hX*hX+hY*hY);
		double r; 
		if(x*x+y*y==0) { //ratio of normal dimensional velocity to new velocity
			r = 0;
		}else {
			r = Math.sqrt((h*h)/(x*x+y*y));
		}
		double[] ret = {x*r, y*r};
		return ret;
		
	}*/
	
	public void end(boolean interrupted) {
		player.lockControl(false);
		count = 0;
		player.setXVel(initx);
		player.setYVel(inity*-1);
	}
	public void bounce(boolean x, boolean y) {
		if(x)radians*=-1;
		if(y)radians=Math.PI-radians;
	}
	public String name() {
		return "Circle";
	}
}
