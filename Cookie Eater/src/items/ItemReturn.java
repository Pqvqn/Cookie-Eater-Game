package items;
import ce3.*;

public class ItemReturn extends Item{
	
	private double x,y;
	private int amp;
	
	public ItemReturn(Game frame) {
		super(frame);
		amp = 1;
		name = "Return";
		desc="Sends user back to starting point of special.`Amplify: Longer game freeze on return";
	}
	public void prepare() {
		x=user.getX(true);
		y=user.getY(true);
	}
	public void initialize() {

	}
	public void execute() {
		if(checkCanceled())return;
	}
	public void end(boolean interrupted) {
		user.setX(x,true);
		user.setY(y,true);
		game.freeze(amp*100-100);
	}
	public void amplify() {
		super.amplify();
		amp++;
	}
	public void deamplify() {
		super.deamplify();
		amps--;
	}
}
