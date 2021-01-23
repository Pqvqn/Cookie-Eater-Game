package items;
import ce3.*;

public class ItemSlowmo extends Item{
	
	private int initTime;
	private double prop;
	private boolean initcc; //starting boolean for checking calibration
	private double initact; //starting adjusted cycle time
	
	public ItemSlowmo(Game frame) {
		super(frame);
		prop = .5;
		name = "Slowmo";
		desc="Slows down time.`Amplify: Slowing factor increases";
	}
	public void prepare() {
		initTime=game.getCycle();
		initcc=game.check_calibration;
		initact=game.getAdjustedCycle();
	}
	public void initialize() {
		game.setCycle((int)(.5+initTime*(1/prop)));
		game.check_calibration = false;
	}
	public void execute() {
		if(checkCanceled())return;
	}
	public void end(boolean interrupted) {
		game.setCycle(initTime);
		game.check_calibration = initcc;
		board.setCalibrations(initact);
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
