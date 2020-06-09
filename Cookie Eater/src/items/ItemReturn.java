package items;
import ce3.*;

public class ItemReturn extends Item{
	
	private double x,y;
	
	public ItemReturn(Board frame) {
		super(frame);
		name = "Return";
		desc="Sends user back to starting point of special.`Amplify: None";
	}
	public void prepare() {
		x=user.getX();
		y=user.getY();
	}
	public void initialize() {

	}
	public void execute() {
		if(checkCanceled())return;
	}
	public void end(boolean interrupted) {
		user.setX(x);
		user.setY(y);
	}
	public void amplify() {
		super.amplify();
		// no idea :/
	}
	public void deamplify() {
		super.deamplify();
		// no idea :/
	}
}
