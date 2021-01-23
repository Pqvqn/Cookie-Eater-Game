package items;
import ce3.*;

public class ItemHold extends Item{
	
	private int times;
	
	public ItemHold(Game frame) {
		super(frame);
		name = "Hold";
		desc="Holds user in place.`Amplify: Pulls down speed more";
		times=1;
	}
	public void initialize() {

		user.lockControl(true);
	}
	public void execute() {
		if(checkCanceled())return;
		for(int i=0; i<times; i++)
			user.averageVels(0, 0, false);
	}
	public void end(boolean interrupted) {
		user.lockControl(false);
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
