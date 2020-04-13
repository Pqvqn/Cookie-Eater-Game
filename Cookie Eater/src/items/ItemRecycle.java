package items;
import ce3.*;

public class ItemRecycle extends Item{
	
	private double value;
	private double initvalue;
	
	public ItemRecycle(Board frame) {
		super(frame);
		name = "Recycle";
		value = 0.4;
	}
	public void initialize() {
		initvalue = player.getDecayedValue();
		player.setDecayedValue(value);
		
	}
	public void execute() {
		
	}
	public void end(boolean interrupted) {
		player.setDecayedValue(initvalue);
	}
	public void amplify() {
		super.amplify();
		value+=.3;
	}
	public void deamplify() {
		super.deamplify();
		value-=.3;
	}
}
