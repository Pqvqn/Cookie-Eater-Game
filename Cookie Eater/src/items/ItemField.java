package items;
import ce3.*;

public class ItemField extends Item{
	
	private int added_radius;
	
	public ItemField(Board frame) {
		super(frame);
		name = "Field";
		added_radius = 40;
	}
	public void prepare() {
	}
	public void initialize() {
		player.addRadius(added_radius);
	}
	public void execute() {
		if(checkCanceled())return;
	}
	public void end(boolean interrupted) {
		player.addRadius(-added_radius);
	}
	public void amplify() {
		super.amplify();
		added_radius+=40;
	}
	public void deamplify() {
		super.deamplify();
		added_radius-=40;
	}
}
