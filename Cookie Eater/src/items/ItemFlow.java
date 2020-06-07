package items;
import ce3.*;

public class ItemFlow extends Item{
	
	private double inituse;
	private double rate;
	
	public ItemFlow(Board frame) {
		super(frame);
		name = "Flow";
		desc="High speed decreases special bar usage.`Amplify: Speed to time conversion becomes more severe";
		rate = 1.2;
	}
	public void prepare() {
		inituse = player.getSpecialUseSpeed();
	}
	public void initialize() {
	}
	public void execute() {
		if(checkCanceled())return;
		double velocity = Math.sqrt(Math.pow(player.getXVel(),2)+Math.pow(player.getYVel(), 2)); //get player speed
		velocity/=(board.getAdjustedCycle()/15.0);
		velocity/=board.currFloor.getScale();
		if(velocity<1)velocity=1;
		player.setSpecialUseSpeed(velocity/Math.pow(velocity,rate));
	}
	public void end(boolean interrupted) {
		player.setSpecialUseSpeed(inituse);
	}
	public void amplify() {
		super.amplify();
		rate*=1.2;
	}
	public void deamplify() {
		super.deamplify();
		rate/=1.2;
	}
}
