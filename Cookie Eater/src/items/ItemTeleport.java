package items;
import ce3.*;
import cookies.*;

public class ItemTeleport extends Item{
	
	private Cookie target;
	private int amps;
	
	public ItemTeleport(Board frame) {
		super(frame);
		amps = 1;
		name = "Teleport";
		desc="Teleports user to farthest cookie.`Amplify: Longer game freeze on teleport";
	}
	public void prepare() {
		double maxDist=0;
		for(Cookie c : board.cookies) { //find farthest cookie
			double compare = Math.sqrt(Math.pow(c.getX() - user.getX(), 2)+(Math.pow(c.getY() - user.getY(), 2)));
			if(compare > maxDist) {
				maxDist = compare;
				target = c;
			}
		}
	}
	public void initialize() {
		if(target!=null) { //only if target found, teleport to it
			user.setX(target.getX());
			user.setY(target.getY());
			board.freeze(amps*100-100);
		}
	}
	public void execute() {
		if(checkCanceled())return;
	}
	public void end(boolean interrupted) {
	}
	public void amplify() {
		super.amplify();
		amps++;
	}
	public void deamplify() {
		super.deamplify();
		amps--;
	}
}
