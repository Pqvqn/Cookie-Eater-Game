package items;
import ce3.*;

public class ItemReturn extends Item{
	
	private double x,y;
	private int amps;
	
	public ItemReturn(Game frame) {
		super(frame);
		amps = 1;
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
		game.freeze(amps*100-100);
	}
	public void amplify() {
		super.amplify();
		amps++;
	}
	public void deamplify() {
		super.deamplify();
		amps--;
	}
}
