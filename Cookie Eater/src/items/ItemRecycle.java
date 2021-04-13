package items;
import ce3.*;

public class ItemRecycle extends Item{
	
	private double value;
	private double initvalue;
	
	public ItemRecycle(Game frame) {
		super(frame);
		name = "Recycle";
		desc="Spoiled cookies give some cash.`Amplify- Cash per spoiled cookie increases";
		value = 0.4;
	}
	public void prepare() {
		initvalue = user.getDecayedValue();
	}
	public void initialize() {
		user.setDecayedValue(value);
	}
	public void execute() {
		if(checkCanceled())return;
	}
	public void end(boolean interrupted) {
		user.setDecayedValue(initvalue);
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
