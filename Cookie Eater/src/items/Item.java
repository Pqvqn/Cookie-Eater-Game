package items;

import java.lang.reflect.InvocationTargetException;

import ce3.*;
import entities.*;

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
	public static final String[] item_names = {"Autopilot","Boost","Chain","Circle","Clone","Field","Flow","Ghost","Hold",
												"Melee","Projectile","Rebound","Recharge","Recycle","Repeat","Return",
												"Ricochet","Shield","Shrink","Slowmo","Teleport"};
	public static final Class[] item_classes = {
			ItemAutopilot.class,ItemBoost.class,ItemCookieChain.class,ItemCircle.class,ItemClone.class,ItemField.class,ItemFlow.class,ItemGhost.class,ItemHold.class,
			ItemSummonMelee.class,ItemSummonProjectile.class,ItemRebound.class,ItemRecharge.class,ItemRecycle.class,ItemRepeat.class,ItemReturn.class,
			ItemRicochet.class,ItemShield.class,ItemShrink.class,ItemSlowmo.class,ItemTeleport.class};
	
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
	
	public static int itemIndex(String i) {
		for(int j=0; j<item_names.length; j++) {
			if(item_names[j].equals(i)) {
				return j;
			}
		}
		return -1;
	}
	
	//creates an item from its name
	public static Item generateItem(Game game, Board board, String i) {
			Class c;
			int index = itemIndex(i);
			if(index<0)return null;
			c = item_classes[index];
			Item b = null;
			try {
				b = (Item) (c.getDeclaredConstructor(Game.class, Board.class).newInstance(game,board));
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return b;
		}
}
