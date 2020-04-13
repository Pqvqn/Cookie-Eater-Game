package items;
import ce3.*;

public class ItemBounce extends Item{

	private double storex, storey;
	private double consta;
	private int bounces;
	
	public ItemBounce(Board frame) {
		super(frame);
		consta = 4;
		name = "Bounce";
	}
	public void initialize() {
		storex=0;
		storey=0;
		bounces=0;
	}
	public void execute() {
		if(storex==0)storex=player.getXVel();
		if(storey==0)storey=player.getYVel();
		if(bounces>0)
			player.averageVels(storex,storey);
	}
	public void end(boolean interrupted) {
	
	}
	public void bounce(boolean x, boolean y) {
		bounces++;
		if(x)storex=player.getXVel()*(Math.log(bounces+3)/Math.log(consta));
		if(y)storey=player.getYVel()*(Math.log(bounces+3)/Math.log(consta));
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
