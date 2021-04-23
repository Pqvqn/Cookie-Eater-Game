package items;
import ce3.*;

public class ItemShrink extends Item{
	
	private double radius_fraction;
	private double start_radius;
	
	public ItemShrink(Game frame, Board gameboard) {
		super(frame,gameboard);
		name = "Shrink";
		desc="Decreases user's size.`Amplify- More shrinkage";
		radius_fraction = .5;
	}
	public void prepare() {
		
	}
	public void initialize() {
		start_radius = user.getRadius();
		user.setRadius(radius_fraction*start_radius);
	}
	public void execute() {
		if(checkCanceled())return;
	}
	public void end(boolean interrupted) {
		user.setRadius(start_radius);
	}
	public void amplify() {
		super.amplify();
		radius_fraction/=2;
	}
	public void deamplify() {
		super.deamplify();
		radius_fraction*=2;
	}
}
