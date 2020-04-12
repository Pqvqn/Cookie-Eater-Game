package items;
import ce3.*;

public abstract class Item {

	protected Board board;
	protected Eater player;
	
	public Item(Board frame) {
		board = frame;
		player = board.player;
	}
	//run on special start
	public void initialize() {
		
	}
	//run while special is active
	public void execute() {
		
	}
	//run when special ends
	public void end(boolean interrupted) {
		
	}
	//string that names this item
	public String name() {
		return null;
	}
	//what to do when bounced off wall (mainly movement items)
	public void bounce(boolean x, boolean y) {
		
	}
}
