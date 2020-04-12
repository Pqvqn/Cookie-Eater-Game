package items;
import ce3.*;

public class ItemHold extends Item{
	
	int dir;
	
	public ItemHold(Board frame) {
		super(frame);
	}
	public void initialize() {

		player.lockControl(true);
	}
	public void execute() {
		player.averageVels(0, 0);
	}
	public void end(boolean interrupted) {
		player.lockControl(false);
	}
	public String name() {
		return "Hold";
	}
}
