package items;
import ce3.*;

public class ItemGhost extends Item{
	
	private int offstage_dist;
	private int initod;
	
	public ItemGhost(Board frame) {
		super(frame);
		name = "Ghost";
		offstage_dist = 0;
	}
	public void initialize() {
		player.setGhost(true);
		player.setOffstage(offstage_dist);
		initod=player.getOffstage();
	}
	public void execute() {
	}
	public void end(boolean interrupted) {
		player.setGhost(false);
		player.setOffstage(initod);
	}
	public void amplify() {
		super.amplify();
		offstage_dist+=100;
	}
	public void deamplify() {
		super.deamplify();
		offstage_dist-=100;
	}
}
