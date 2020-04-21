package items;
import ce3.*;

public class ItemJab extends Item{
	
	private int range;
	private Summon jab;
	
	public ItemJab(Board frame) {
		super(frame);
		range = 40;
		jab = new SummonJab(board,board.player,range);
		name = "Jab";
	}
	public void prepare() {
		player.addSummon(jab);
		jab.prepare();
	}
	public void initialize() {
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
		range+=20;
	}
	public void deamplify() {
		super.deamplify();
		range-=20;
	}
}
