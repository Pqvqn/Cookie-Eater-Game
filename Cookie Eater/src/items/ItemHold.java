package items;
import ce3.*;

public class ItemHold extends Item{
	
	int dir;
	
	public ItemHold(Board frame) {
		super(frame);
	}
	//run on special start
	public void initialize() {

		player.lockControl(true);
		player.setXVel(Math.signum(player.getXVel()));
		player.setYVel(Math.signum(player.getYVel()));
	}
	//run while special is active
	public void execute() {

	}
	//run when special ends
	public void end(boolean interrupted) {
		player.lockControl(false);
	}
	public String name() {
		return "Hold";
	}
}
