package items;
import ce3.*;

public class ItemCircle extends Item{

	private double count;
	private double radius;
	private double radiusunadjust;
	private double radians;
	private double initx,inity;
	
	public ItemCircle(Board frame) {
		super(frame);
		count = 0;
		radians = 0;
		name = "Circle";
		radiusunadjust = 100;
	}
	public void prepare() {
		radius=radiusunadjust*board.currFloor.getScale();
		count = 0;
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
	}
	public void initialize() {
		player.lockControl(true);
		
		//player.setXVel(0);
		//player.setYVel(0);
	}
	
	public void execute() {
		if(checkCanceled())return;
		if(count<=Math.PI*2) {
			count+=.1*(board.getAdjustedCycle()/15.0);
			radians+=.1*(board.getAdjustedCycle()/15.0);
		
					
			player.averageVels(radius*Math.cos(radians+.1*(board.getAdjustedCycle()/15.0))-radius*Math.cos(radians), -(radius*Math.sin(radians)-radius*Math.sin(radians-.1*(board.getAdjustedCycle()/15.0))));

		}else {count=0;}	
	}
	
	public void end(boolean interrupted) {
		player.lockControl(false);
		count = 0;
		player.setXVel(0);
		player.setYVel(0);
	}
	public void bounce(boolean x, boolean y) {
		if(x)radians*=-1;
		if(y)radians=Math.PI-radians;
	}
	public void amplify() {
		super.amplify();
		radiusunadjust+=100;
	}
	public void deamplify() {
		super.deamplify();
		radiusunadjust-=100;
	}
}
