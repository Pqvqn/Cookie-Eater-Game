package items;
import ce3.*;

public class ItemHold extends Item{
	
	int times;
	
	public ItemHold(Board frame) {
		super(frame);
		name = "Hold";
		times=1;
	}
	public void initialize() {

		player.lockControl(true);
	}
	public void execute() {
		for(int i=0; i<times; i++)
			player.averageVels(0, 0);
	}
	public void end(boolean interrupted) {
		player.lockControl(false);
	}
	public void amplify() {
		super.amplify();
		times++;
	}
	public void deamplify() {
		super.deamplify();
		times--;
	}
}
