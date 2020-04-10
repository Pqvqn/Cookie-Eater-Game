package items;
import ce3.*;

public class ItemShield extends Item{
	

	
	public ItemShield(Board frame) {
		super(frame);
	}

	public void initialize() {
	
	}
	
	public void execute() {	
		player.setShielded(true);
	}
	
	public void end(boolean interrupted) {

	}
	public String name() {
		return "Shield";
	}
}
