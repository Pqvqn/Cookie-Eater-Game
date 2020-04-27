package items;
import ce3.*;

public class ItemField extends Item{
	
	private int added_radius;
	private int start_radius;
	
	public ItemField(Board frame) {
		super(frame);
		name = "Field";
		added_radius = 40;
	}
	public void prepare() {
		
	}
	public void initialize() {
		start_radius = player.getTotalRadius();
		player.addExtraRadius(added_radius);
	}
	public void execute() {
		if(checkCanceled())return;
	}
	public void end(boolean interrupted) {
		player.setExtraRadius(start_radius-player.getRadius());
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
