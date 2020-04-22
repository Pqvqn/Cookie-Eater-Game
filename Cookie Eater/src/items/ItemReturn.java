package items;
import ce3.*;

public class ItemReturn extends Item{
	
	private double x,y;
	
	public ItemReturn(Board frame) {
		super(frame);
		name = "Return";
	}
	public void prepare() {
		x=player.getX();
		y=player.getY();
	}
	public void initialize() {

	}
	public void execute() {
		if(checkCanceled())return;
	}
	public void end(boolean interrupted) {
		player.setX(x);
		player.setY(y);
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
