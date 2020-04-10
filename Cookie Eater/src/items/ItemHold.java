package items;
import ce3.*;

public class ItemHold extends Item{
	
	int dir;
	
	public ItemHold(Board frame) {
		super(frame);
	}
	//run on special start
	public void initialize() {
		player.setXVel(0);
		player.setYVel(0);
		dir = player.getDir();
	}
	//run while special is active
	public void execute() {
		player.setDir(Eater.NONE);
	}
	//run when special ends
	public void end(boolean interrupted) {
		player.setDir(dir);
	}
	public String name() {
		return "Hold";
	}
}
