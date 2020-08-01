package oldsummonstorage;

import ce3.*;

public class ItemHook extends Item{
	
	private double speed;
	private SummonHook hook;
	
	public ItemHook(Board frame) {
		super(frame);
		speed = 1.5;
		name = "Hook";
		desc="Hooks into object and pulls.`Amplify: Stronger pull";
	}
	public void prepare() {
		hook = new SummonHook(board,user,speed*board.getAdjustedCycle());
		user.addSummon(hook);
		hook.prepare();
	}
	public void initialize() {
		hook.initialize();
	}
	public void execute() {
		if(checkCanceled())return;
		hook.execute();
	}
	public void end(boolean interrupted) {
		hook.end(false);
		user.removeSummon(hook);
	}

	public void amplify() {
		super.amplify();
		speed+=1.5;
		/*speed+=4;
		for(int i=0; i<proj.size(); i++) {
			proj.get(i).setSpeed(speed);
		}*/
	}
	public void deamplify() {
		super.deamplify();
		speed-=1.5;
		/*speed-=4;
		for(int i=0; i<proj.size(); i++) {
			proj.get(i).setSpeed(speed);
		}*/
	}
}
