package items;
import ce3.*;

public class ItemFlow extends Item{
	
	private double inituse;
	private double rate;
	
	public ItemFlow(Game frame) {
		super(frame);
		name = "Flow";
		desc="High speed decreases special bar usage.`Amplify- Speed to time conversion becomes more severe";
		rate = 1.2;
	}
	public void prepare() {
		inituse = user.getSpecialUseSpeed();
	}
	public void initialize() {
	}
	public void execute() {
		if(checkCanceled())return;
		double velocity = Math.sqrt(Math.pow(user.getXVel(true),2)+Math.pow(user.getYVel(true), 2)); //get user speed
		velocity/=(game.getAdjustedCycle()/15.0);
		velocity/=board.currFloor.getScale();
		if(velocity<1)velocity=1;
		user.setSpecialUseSpeed(velocity/Math.pow(velocity,rate));
	}
	public void end(boolean interrupted) {
		user.setSpecialUseSpeed(inituse);
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
