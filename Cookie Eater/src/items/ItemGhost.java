package items;
import ce3.*;

public class ItemGhost extends Item{
	
	private int offstage_dist;
	private int initod;
	private double lastx, lasty; //last position of user where they weren't inside something
	
	public ItemGhost(Game frame, Board gameboard) {
		super(frame,gameboard);
		name = "Ghost";
		desc="Player goes through walls.`Amplify- Accessible area offscreen increases";
		offstage_dist = 0;
	}
	public void initialize() {
		user.setGhost(true);
		user.setOffstage(offstage_dist);
		initod=user.getOffstage();
		lastx = user.getX(true);
		lasty = user.getY(true);
	}
	public void execute() {
		if(checkCanceled())return;
		if(!user.collidesWithAnything()) {
			lastx = user.getX(true);
			lasty = user.getY(true);
		}
	}
	public void end(boolean interrupted) {
		user.setGhost(false);
		user.setOffstage(initod);
		if(user.collidesWithAnything()) { //if user ends ghost while inside something, send them to last position that wasn't like that
			user.setX(lastx,true);
			user.setY(lasty,true);
			//user.setXVel(0);
			//user.setYVel(0);
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
