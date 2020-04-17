package items;
import ce3.*;

public class ItemGhost extends Item{
	
	
	public ItemGhost(Board frame) {
		super(frame);
		name = "Ghost";
	}
	public void initialize() {
		player.setGhost(true);
	}
	public void execute() {
	}
	public void end(boolean interrupted) {
		player.setGhost(false);
	}
	public void amplify() {
		super.amplify();
		// no idea :/
	}
	public void deamplify() {
		super.deamplify();
		// no idea :/
	}
}
