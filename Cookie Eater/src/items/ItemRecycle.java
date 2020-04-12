package items;
import ce3.*;

public class ItemRecycle extends Item{

	
	public ItemRecycle(Board frame) {
		super(frame);
	}
	public void initialize() {
		player.setGrabDecay(true);
		
	}
	public void execute() {
		
	}
	public void end(boolean interrupted) {
		player.setGrabDecay(false);
	}
	public String name() {
		return "Recycle";
	}
}
