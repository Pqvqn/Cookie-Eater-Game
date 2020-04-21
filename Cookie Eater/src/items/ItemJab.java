package items;
import ce3.*;

public class ItemJab extends Item{
	
	private double range;
	private SummonJab jab;
	
	public ItemJab(Board frame) {
		super(frame);
		range = 250;
		jab = new SummonJab(board,board.player,range);
		name = "Jab";
	}
	public void prepare() {
		player.addSummon(jab);
		jab.prepare();
	}
	public void initialize() {
		jab.setRange(range);
		jab.initialize();
	}
	public void execute() {
		jab.execute();
	}
	public void end(boolean interrupted) {
		jab.end(false);
		player.removeSummon(jab);
	}
	public void amplify() {
		super.amplify();
		range+=200;
		jab.setRange(range);
	}
	public void deamplify() {
		super.deamplify();
		range-=200;
		jab.setRange(range);
	}
}
