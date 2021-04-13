package items;
import ce3.*;

public class ItemField extends Item{
	
	private double added_radius;
	private double start_radius;
	
	public ItemField(Game frame) {
		super(frame);
		name = "Field";
		desc="Larger collection area.`Amplify- Area increases";
		added_radius = 40;
	}
	public void prepare() {
		
	}
	public void initialize() {
		start_radius = user.getExtraRadius();
		user.setExtraRadius(start_radius+added_radius);
	}
	public void execute() {
		if(checkCanceled())return;
	}
	public void end(boolean interrupted) {
		user.setExtraRadius(start_radius);
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
