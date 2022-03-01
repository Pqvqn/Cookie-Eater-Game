package items;
import java.util.*;

import ce3.*;
import entities.*;

public class ItemClone extends Item{
	
	private int num;
	private ArrayList<EffectClone> clones;
	private final boolean[][] states = {{false,false},{true,true},{true,false},{false,true}};
	
	public ItemClone(Game frame, Board gameboard) {
		super(frame,gameboard);
		clones = new ArrayList<EffectClone>();
		num = 1;
		name = "Clone";
		desc="Creates mirrored clone of user movement.`Amplify- More clones";
	}
	public void prepare() {
	}
	public void initialize() {
		for(int i=1; i<=num; i++) {
			EffectClone clone;
			clones.add(clone = new EffectClone(game,board,game.getCycle(),user,states[i%4][0],states[i%4][1],(i-1)%7>=3));
			board.currLevel.effects.add(clone);
		}
	}
	public void execute() {
		if(checkCanceled())return;
	}
	public void end(boolean interrupted) {
		for(int i=0; i<clones.size(); i++) {
			clones.get(i).kill();
			clones.remove(i);
			i--;
		}
	}
	
	public void amplify() {
		super.amplify();
		num++;
	}
	public void deamplify() {
		super.deamplify();
		num--;
	}
}
