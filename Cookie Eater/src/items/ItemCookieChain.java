package items;
import ce3.*;

public class ItemCookieChain extends Item{
	
	private double cooks;
	private int time;
	private int count;
	
	public ItemCookieChain(Game frame) {
		super(frame);
		name = "Chain";
		desc="Grabbing cookies increases special time length.`Amplify- More time per cookie";
	}
	public void prepare() {
		time = user.getSpecialLength()/8;
	}
	public void initialize() {
		count = 0;
	}
	public void execute() {
		if(checkCanceled())return;
		if(cooks!=user.getStash().size()) {
			cooks=user.getStash().size();
			if(count<=0) {
				user.extendSpecial(time);
				count = time;
			}

		}
		if(count>0)
			count--;
	}
	public void end(boolean interrupted) {
		
	}
	public void amplify() {
		super.amplify();
		time*=2;
	}
	public void deamplify() {
		super.deamplify();
		time/=2;
	}
}
