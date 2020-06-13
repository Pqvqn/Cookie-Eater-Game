package items;
import ce3.*;

public class ItemGhost extends Item{
	
	private int offstage_dist;
	private int initod;
	
	public ItemGhost(Board frame) {
		super(frame);
		name = "Ghost";
		desc="Player goes through walls.`Amplify: Accessible area offscreen increases";
		offstage_dist = 0;
	}
	public void initialize() {
		user.setGhost(true);
		if(isplayer) {
			((Eater)user).setOffstage(offstage_dist);
			initod=((Eater)user).getOffstage();
		}
	}
	public void execute() {
		if(checkCanceled())return;
	}
	public void end(boolean interrupted) {
		user.setGhost(false);
		if(isplayer) {
			((Eater)user).setOffstage(initod);
		}

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
