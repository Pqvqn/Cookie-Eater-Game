package items;
import ce3.*;

public class ItemWall extends Item{
	
	private double range;
	private SummonWall wall;
	
	public ItemWall(Board frame) {
		super(frame);
		range = 200;
		name = "Wall";
		desc="Makes wall that can be hit without shield usage.`Amplify: Range increases";
	}
	public void prepare() {
		wall = new SummonWall(board,board.user,range);
		user.addSummon(wall);
		wall.prepare();
	}
	public void initialize() {
		wall.setRange(range);
		wall.initialize();
	}
	public void execute() {
		if(checkCanceled())return;
		if(wall==null)return;
		wall.execute();
	}
	public void end(boolean interrupted) {
		wall.end(false);
		user.removeSummon(wall);
	}
	public void amplify() {
		super.amplify();
		range+=200;
	}
	public void deamplify() {
		super.deamplify();
		range-=200;
	}
}
