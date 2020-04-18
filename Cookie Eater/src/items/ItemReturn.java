package items;
import ce3.*;

public class ItemReturn extends Item{
	
	private double x,y;
	
	public ItemReturn(Board frame) {
		super(frame);
		name = "Return";
	}
	public void initialize() {
		x=player.getX();
		System.out.println(x);
		y=player.getY();
	}
	public void execute() {
	}
	public void end(boolean interrupted) {
		player.setX(x);
		System.out.println(x);
		System.out.println(player.getX());
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
