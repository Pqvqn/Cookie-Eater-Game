package items;
import ce3.*;

public class ItemSlowmo extends Item{
	
	int initTime;
	double prop;
	
	public ItemSlowmo(Board frame) {
		super(frame);
		prop = .5;
		initTime = board.getCycle();
		name = "Slowmo";
	}
	public void initialize() {
		initTime=board.getCycle();
		board.setCycle((int)(.5+initTime*(1/prop)));
	}
	public void execute() {
		if(checkCanceled())return;
	}
	public void end(boolean interrupted) {
		board.setCycle(initTime);
	}
	public void amplify() {
		super.amplify();
		prop-=.2;
	}
	public void deamplify() {
		super.deamplify();
		prop-=.2;
	}
}
