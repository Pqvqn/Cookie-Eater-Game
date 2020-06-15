package items;
import ce3.*;

public class ItemSlowmo extends Item{
	
	private int initTime;
	private double prop;
	private boolean initcc;
	
	public ItemSlowmo(Board frame) {
		super(frame);
		prop = .5;
		name = "Slowmo";
		desc="Slows down time.`Amplify: Slowing factor increases";
	}
	public void prepare() {
		initTime=board.getCycle();
		initcc=user.getCalibCheck();
	}
	public void initialize() {
		board.setCycle((int)(.5+initTime*(1/prop)));
		user.setCalibCheck(false);
	}
	public void execute() {
		if(checkCanceled())return;
	}
	public void end(boolean interrupted) {
		board.setCycle(initTime);
		user.setCalibCheck(initcc);
	}
	public void amplify() {
		super.amplify();
		prop/=2;
	}
	public void deamplify() {
		super.deamplify();
		prop*=2;
	}
}
