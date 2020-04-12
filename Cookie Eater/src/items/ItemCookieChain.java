package items;
import ce3.*;

public class ItemCookieChain extends Item{
	
	private int cooks;
	private int time;
	private int count;
	
	public ItemCookieChain(Board frame) {
		super(frame);
		time = player.getSpecialLength()/4;
	}
	public void initialize() {
		count = 0;
	}
	public void execute() {
		if(cooks!=board.score) {
			cooks=board.score;
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
	public String name() {
		return "Chain";
	}
}
