package items;
import ce3.*;

public class ItemCircle extends Item{

	private double count;
	private double radius;
	private double radiusunadjust;
	private double radians;
	private double initx,inity;
	
	public ItemCircle(Game frame) {
		super(frame);
		count = 0;
		radians = 0;
		name = "Circle";
		desc="Completes a circular path.`Amplify: Larger radius";
		radiusunadjust = 100;
	}
	public void prepare() {

	}
	public void initialize() {
		user.lockControl(true);
		
		//user.setXVel(0);
		//user.setYVel(0);
		radius=radiusunadjust*board.currFloor.getScale();
		count = 0;
		initx=user.getXVel(true);
		inity=-1*user.getYVel(true);
		
		if(initx==0 && inity==0) { //if not moving, base on direction
			radians = user.getAim()+Math.PI;
		}else {
			radians = Math.PI + ((initx>=0) ? Math.atan(inity/initx) : Math.atan(inity/initx) + Math.PI); //find angle to start at
		}
	}
	
	public void execute() {
		if(checkCanceled())return;
		if(count<=Math.PI*2) {
			count+=.1*(game.getAdjustedCycle()/15.0);
			radians+=.1*(game.getAdjustedCycle()/15.0);
		
					
			user.averageVels(radius*Math.cos(radians+.1*(game.getAdjustedCycle()/15.0))-radius*Math.cos(radians), -(radius*Math.sin(radians)-radius*Math.sin(radians-.1*(game.getAdjustedCycle()/15.0))),true);

		}else {count=0;}	
	}
	
	public void end(boolean interrupted) {
		user.lockControl(false);
		count = 0;
		user.averageVels(initx,-inity,true);
	}
	public void bounce(Object bouncedOff, double x, double y) {
		radians = Math.PI + ((user.getXVel(true)>=0) ? Math.atan(user.getYVel(true)/user.getXVel(true)) : Math.atan(user.getYVel(true)/user.getXVel(true)) + Math.PI);
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
