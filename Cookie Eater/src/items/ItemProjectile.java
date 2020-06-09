package items;
import java.util.*;

import ce3.*;

public class ItemProjectile extends Item{
	
	private double speed;
	private int num;
	private double offset_const;
	private ArrayList<SummonProjectile> proj;
	
	public ItemProjectile(Board frame) {
		super(frame);
		speed = 1.5*board.getAdjustedCycle();
		proj = new ArrayList<SummonProjectile>();
		num = 1;
		offset_const = Math.PI/12;
		name = "Projectile";
		desc="Shoots a projectile.`Amplify: More shots";
	}
	public void prepare() {
		for(int i=0; i<num; i++) {
			proj.add(new SummonProjectile(board,board.user,speed,chooseOffset(i,num)));
			user.addSummon(proj.get(i));
			proj.get(i).prepare();
		}
	}
	public void initialize() {
		for(int i=0; i<proj.size(); i++) {
			//proj.get(i).setSpeed(speed);
			proj.get(i).initialize();
		}
	}
	public void execute() {
		if(checkCanceled())return;
		for(int i=0; i<proj.size(); i++) {
			proj.get(i).execute();
		}
	}
	public void end(boolean interrupted) {
		for(int i=0; i<proj.size(); i++) {
			proj.get(i).end(false);
			user.removeSummon(proj.get(i));
			proj.remove(i);
			i--;
		}
	}
	private double chooseOffset(int thisNum, int total) {
		if(total%2==0) {
			return -(offset_const * total/2 - offset_const/2) + thisNum*offset_const;
		}else {
			return -(offset_const * (total-1)/2) + thisNum*offset_const;
		}
	}
	public void amplify() {
		super.amplify();
		num++;
		/*speed+=4;
		for(int i=0; i<proj.size(); i++) {
			proj.get(i).setSpeed(speed);
		}*/
	}
	public void deamplify() {
		super.deamplify();
		num--;
		/*speed-=4;
		for(int i=0; i<proj.size(); i++) {
			proj.get(i).setSpeed(speed);
		}*/
	}
}
