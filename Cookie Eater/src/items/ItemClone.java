package items;
import java.util.*;

import ce3.*;

public class ItemClone extends Item{
	
	private int num;
	private ArrayList<SummonClone> clones;
	private final boolean[][] states = {{false,false},{true,true},{true,false},{false,true}};
	
	public ItemClone(Board frame) {
		super(frame);
		clones = new ArrayList<SummonClone>();
		num = 1;
		name = "Clone";
		desc="Creates mirrored clone of user movement.`Amplify: More clones";
	}
	public void prepare() {
		for(int i=0; i<num; i++) {
			clones.add(new SummonClone(board,board.user,user.getX(),user.getY(),states[i%3+1][0],states[i%3+1][1],i>2));
			user.addSummon(clones.get(i));
			clones.get(i).prepare();
		}
	}
	public void initialize() {
		for(int i=0; i<clones.size(); i++) {
			//proj.get(i).setSpeed(speed);
			clones.get(i).initialize();
		}
	}
	public void execute() {
		if(checkCanceled())return;
		for(int i=0; i<clones.size(); i++) {
			clones.get(i).execute();
		}
	}
	public void end(boolean interrupted) {
		for(int i=0; i<clones.size(); i++) {
			clones.get(i).end(false);
			user.removeSummon(clones.get(i));
			clones.remove(i);
			i--;
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
