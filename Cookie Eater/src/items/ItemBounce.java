package items;
import ce3.*;

public class ItemBounce extends Item{ //discontinued

	private double storex, storey;
	private double consta;
	private int bounces;
	
	public ItemBounce(Board frame) {
		super(frame);
		consta = 4;
		name = "Bounce";
		desc="An item you should not own for it has been removed.`Amplify: More bouncy";
	}
	public void initialize() {
		storex=0;
		storey=0;
		bounces=0;
	}
	public void execute() {
		if(checkCanceled())return;
		if(storex==0)storex=player.getXVel();
		if(storey==0)storey=player.getYVel();
		if(bounces>0)
			player.averageVels(storex,storey);
	}
	public void end(boolean interrupted) {
	
	}
	public void bounce(double x, double y) {
		bounces++;
		if(x-player.getX()!=0)storex=player.getXVel()*(Math.log(bounces+3)/Math.log(consta));
		if(y-player.getY()!=0)storey=player.getYVel()*(Math.log(bounces+3)/Math.log(consta));
	}
	public void amplify() {
		super.amplify();
		consta-=1;
	}
	public void deamplify() {
		super.deamplify();
		consta+=1;
	}
}
