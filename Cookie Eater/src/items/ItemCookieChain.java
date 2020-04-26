package items;
import ce3.*;

public class ItemCookieChain extends Item{
	
	private double cooks;
	private int time;
	private int count;
	
	public ItemCookieChain(Board frame) {
		super(frame);
		time = player.getSpecialLength()/8;
		name = "Chain";
	}
	public void initialize() {
		count = 0;
	}
	public void execute() {
		if(checkCanceled())return;
		if(cooks!=board.player.getCash()) {
			cooks=board.player.getCash();
			if(count<=0) {
				player.extendSpecial(time);
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
