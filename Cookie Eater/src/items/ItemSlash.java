package items;
import ce3.*;

public class ItemSlash extends Item{
	
	private double region;
	private SummonSlash slash;
	
	public ItemSlash(Board frame) {
		super(frame);
		region = Math.PI/3;
		name = "Slash";
		desc="Swings a melee hit.`Amplify: Region of swing increases";
	}
	public void prepare() {
		slash = new SummonSlash(board,user,region);
		user.addSummon(slash);
		slash.prepare();
	}
	public void initialize() {
		slash.setRegion(region);
		slash.initialize();
	}
	public void execute() {
		if(checkCanceled())return;
		if(slash==null)return;
		slash.execute();
	}
	public void end(boolean interrupted) {
		slash.end(false);
		user.removeSummon(slash);
	}
	public void amplify() {
		super.amplify();
		region+=2*Math.PI/3;
	}
	public void deamplify() {
		super.deamplify();
		region-=2*Math.PI/3;
	}
}
