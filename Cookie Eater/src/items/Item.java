package items;

import ce3.*;
import entities.Eater;
import entities.Entity;

public abstract class Item {

	protected Game game;
	protected Board board;
	protected Entity user;
	protected int amps;
	protected String name;
	protected boolean cancel;
	protected int waiting;
	protected String desc;
	protected boolean isplayer;
	
	public Item(Game frame, Board gameboard) {
		game = frame;
		board = gameboard;
		user = null;
		amps=0;
		cancel = false;
		desc = "";
		isplayer = false;
	}
	//resets variables from constructor based on SaveData values
	public void loadFromData(SaveData sd) {
		System.out.println(board);
		name = sd.getString("name",0);
		desc = sd.getString("description",0);
		int neededAmps = sd.getInteger("amplifies",0);
		while(neededAmps != amps) {
			if(neededAmps>amps) {
				amplify();
			}else {
				deamplify();
			}
		}
	}
	//puts all saved data into SaveData instance
	public SaveData getSaveData() {
		SaveData data = new SaveData();
		data.addData("amplifies",amps);
		data.addData("name",name);
		data.addData("description",desc);
		return data;
	}
	//give the item something to use it
	public void setUser(Entity u) {
		user = u;
		isplayer = user instanceof Eater;
	}
	//set all vars before other items change them
	public void prepare() {
		
	}
	//run on special start
	public void initialize() {
		
	}
	//run while special is active
	public void execute() {
	}
	//run when special ends
	public void end(boolean interrupted) {
		
	}
	//string that names this item
	public String name() {
		String ret = name;
		for(int i=0; i<amps; i++)ret+="+";
		return ret;
	}
	//returns name without modifiers
	public String getName() {
		return name;
	}
	//what to do when bounced off wall (mainly movement items) with x/y pos of bounce point
	public void bounce(Object bouncedOff, double x, double y) {
		
	}
	public void amplify() {
		amps++;
	}
	public void deamplify() {
		amps--;
	}
	public void cancelCycles(int c) {
		cancel = true;
		waiting = c;
	}
	public boolean checkCanceled() {
		if(cancel) {
			if(waiting--<=0) {
				cancel = false;
			}//if execute must be skipped
			return true;
		}
		return false;
	}
	public String getDesc() {return desc;}
	
	//creates an item from its name
	public static Item generateItem(Game game, Board board, String i) {
			Item b;
			switch(i) {
			case "Boost":
				b = new ItemBoost(game,board);
				break;
			/*case "Bounce":
				b = new ItemBounce(game);
				break;*/
			case "Circle":
				b = new ItemCircle(game,board);
				break;
			case "Chain":
				b = new ItemCookieChain(game,board);
				break;
			case "Field":
				b = new ItemField(game,board);
				break;
			case "Hold":
				b = new ItemHold(game,board);
				break;
			case "Recycle":
				b = new ItemRecycle(game,board);
				break;
			case "Shield":
				b = new ItemShield(game,board);
				break;
			case "Slowmo":
				b = new ItemSlowmo(game,board);
				break;
			case "Ghost":
				b = new ItemGhost(game,board);
				break;
			case "Return":
				b = new ItemReturn(game,board);
				break;
			case "Teleport":
				b = new ItemTeleport(game,board);
				break;
			/*case "Jab":
				b = new ItemJab(game);
				break;*/
			case "Repeat":
				b = new ItemRepeat(game,board);
				break;
			/*case "Projectile":
				b = new ItemProjectile(game);
				break;*/
			case "Rebound":
				b = new ItemRebound(game,board);
				break;
			case "Clone":
				b = new ItemClone(game,board);
				break;
			case "Ricochet":
				b = new ItemRicochet(game,board);
				break;
			/*case "Slash":
				b = new ItemSlash(game);
				break;
			case "Wall":
				b = new ItemWall(game);
				break;*/
			case "Shrink":
				b = new ItemShrink(game,board);
				break;
			/*case "Hook":
				b = new ItemHook(game);
				break;*/
			case "Autopilot":
				b = new ItemAutopilot(game,board);
				break;
			case "Flow":
				b = new ItemFlow(game,board);
				break;
			case "Recharge":
				b = new ItemRecharge(game,board);
				break;
			case "Melee":
				b = new ItemSummonMelee(game,board);
				break;
			case "Projectile":
				b = new ItemSummonProjectile(game,board);
				break;
			default:
				b = null;
			}
			return b;
		}
}
