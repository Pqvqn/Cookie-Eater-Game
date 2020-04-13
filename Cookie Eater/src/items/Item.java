package items;
import ce3.*;

public abstract class Item {

	protected Board board;
	protected Eater player;
	protected int amps;
	protected String name;
	
	public Item(Board frame) {
		board = frame;
		player = board.player;
		amps=0;
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
		String ret = name;
		for(int i=0; i<amps; i++)ret+="+";
		return ret;
	}
	//returns name without modifiers
	public String getName() {
		return name;
	}
	//what to do when bounced off wall (mainly movement items)
	public void bounce(boolean x, boolean y) {
		
	}
	public void amplify() {
		amps++;
	}
	public void deamplify() {
		amps--;
	}
}
