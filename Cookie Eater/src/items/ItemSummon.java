package items;
import ce3.*;

public class ItemSummon extends Item{
	
	public ItemSummon(Board frame) {
		super(frame);
		name = "Summon";
		desc="Items affect a separate summoned entity. `Amplify: Summon gains health";
	}
	public void prepare() {
		//user's items given to summon
	}
	public void initialize() {
	
	}
	public void execute() {
		if(checkCanceled())return;
		//summon's item progress given to user
	}
	public void end(boolean interrupted) {
		//undo user summon thing
	}
	public void amplify() {
		super.amplify();
		//hp up
	}
	public void deamplify() {
		super.deamplify();
		//hp down
	}
}
