package entities;

import java.awt.*;
import java.awt.geom.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import ce3.*;
import levels.*;
import mechanisms.*;
import cookies.*;
import sprites.*;
import menus.*;

public class Explorer extends Entity{

	protected Level residence; //which room this explorer is on
	protected int residenceDungeon; //which dungeon this explorer is on
	protected int state;
	public static final int VENDOR = 0, VENTURE = 1, STAND = 2, STOP = 3, MECHANIC = 4;
	protected int[][] shop_spots;
	protected ArrayList<CookieStore> to_sell;
	protected ArrayList<CookieStore> on_display;
	protected int min_cat, max_cat;
	protected SpriteExplorer sprite;
	
	protected double[][] mr; //accel min,max; maxvel min,max; fric min,max
	public static final int CORPSE=-2, NONE=-1, UP=0, RIGHT=1, DOWN=2, LEFT=3;
	protected int direction;
	protected Color coloration;
	protected Segment tester; //segment used to test possible movement paths to choose optimal one
	protected double input_speed; //how many frames must pass between inputs
	protected int input_counter; //counts the above
	protected ArrayList<CookieItem> pickups; //items picked up but not activated
	protected int startShields;
	protected int speaking; //how long been speaking for
	protected Conversation convo;
	protected Cookie target;
	
	public Explorer(Game frame, Board gameboard, int cycletime) {
		super(frame,gameboard,cycletime);
		to_sell = new ArrayList<CookieStore>();
		on_display = new ArrayList<CookieStore>();
		pickups = new ArrayList<CookieItem>();
		name = getName();
		//chooseResidence();
		state = VENDOR;
		coloration = Color.gray;
		
		mr = CookieStat.MR;
		
		averageStats();
		
		direction = NONE;

		input_counter = 0;
		speaking = 0;
		
		residenceDungeon = board.currDungeon;
		
		setShields(startShields);
		buildBody();
		try {
			sprite = new SpriteExplorer(board,this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public Explorer(Game frame, Board gameboard, SaveData sd, int cycle) {
		super(frame,gameboard,sd,cycle);
		direction = sd.getInteger("direction",0);
		startShields = sd.getInteger("startshields",0);
		addShields(startShields);
		state = sd.getInteger("state",0);
		coloration = new Color(sd.getInteger("color",0));
		
		min_cat = sd.getInteger("catalogsize",0);
		max_cat = sd.getInteger("catalogsize",1);
		input_speed = sd.getDouble("inputspeed",0);
		

		ArrayList<SaveData> pickup_data = sd.getSaveDataList("pickupstash");
		pickups = new ArrayList<CookieItem>();
		if(pickup_data!=null) {
			for(int i=0; i<pickup_data.size(); i++) {
				pickups.add(new CookieItem(game, board, pickup_data.get(i)));
			}
		}
	
		mr = new double[3][2];
		ArrayList<Object> stats = sd.getData("statranges");
		for(int i=0; i<6; i++) {
			mr[i/2][i%2] = Double.parseDouble(stats.get(i).toString());
		}
		
		residence = board.currFloor.findRoom(sd.getString("residence",0));
		
		ArrayList<Object> spots = sd.getData("shopspots");
		if(spots!=null) {
			shop_spots = new int[spots.size()/2][2];
			for(int i=0; i<spots.size(); i++) {
				shop_spots[i/2][i%2] = Integer.parseInt(spots.get(i).toString());
			}
		}

		ArrayList<SaveData> sell_data = sd.getSaveDataList("sellstash");
		to_sell = new ArrayList<CookieStore>();
		if(sell_data!=null) {
			for(int i=0; i<sell_data.size(); i++) {
				CookieStore disp;
				to_sell.add(disp = CookieStore.loadFromData(game, board, sell_data.get(i)));
				disp.setVendor(this);
			}
		}
		
		ArrayList<SaveData> display_data = sd.getSaveDataList("displaystash");
		on_display = new ArrayList<CookieStore>();
		if(display_data!=null) {
			for(int i=0; i<display_data.size(); i++) {
				CookieStore disp;
				on_display.add(disp = CookieStore.loadFromData(game, board, display_data.get(i)));
				board.cookies.add(disp);
				disp.setVendor(this);
			}
		}
		
		
		try {
			sprite = new SpriteExplorer(board,this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public SaveData getSaveData() {
		SaveData data = super.getSaveData();
		data.addData("direction",direction);
		data.addData("startshields",startShields);
		data.addData("state",state);
		data.addData("color",coloration.getRGB());
		
		data.addData("catalogsize",min_cat,0);
		data.addData("catalogsize",max_cat,1);
		data.addData("inputspeed",input_speed);
		
		data.addData("residence",residence.getID());
		
		for(int i=0; i<6; i++) {
			data.addData("statranges",mr[i/2][i%2],i);
		}
		
		if(shop_spots != null) {
			for(int i=0; i<shop_spots.length*2; i++) {
				data.addData("shopspots",shop_spots[i/2][i%2],i);
			}
		}
		
		for(int i=0; i<pickups.size(); i++) {
			data.addData("pickupstash",pickups.get(i).getSaveData(),i);
		}	
		for(int i=0; i<to_sell.size(); i++) {
			data.addData("sellstash",to_sell.get(i).getSaveData(),i);
		}	
		for(int i=0; i<on_display.size(); i++) {
			data.addData("displaystash",on_display.get(i).getSaveData(),i);
		}
		data.addData("type",this.getClass().getName());
		return data;
	}
	//return Explorer created by SaveData, testing for correct type of Explorer
	public static Explorer loadFromData(Game frame, Board gameboard, SaveData sd, int cycle) {
		//explorer subclasses
		Class[] explorertypes = {ExplorerMechanic.class, ExplorerMystery.class, ExplorerShopkeep.class, ExplorerSidekick.class,ExplorerVendor.class};
		String thistype = sd.getString("type",0);
		for(int i=0; i<explorertypes.length; i++) {
			//if class type matches type from file, instantiate and return it
			if(thistype.equals(explorertypes[i].getName())){
				try {
					return (Explorer) (explorertypes[i].getDeclaredConstructor(Game.class, Board.class, SaveData.class, int.class).newInstance(frame, gameboard, sd, cycle));
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | NoSuchMethodException | SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
		//default to mechanic
		return new ExplorerMechanic(frame, gameboard, sd, cycle);
	}
	public Level getResidence() {return residence;}
	public String getName() {return "Unknown";}
	//make changes after player ends a run
	public void runEnds() {
		
	}
	//updates every cycle
	public void runUpdate() {
		super.runUpdate();
		if(direction == CORPSE) {
			orientParts();
			return;
		}
		if(parts.isEmpty())buildBody();
		if(state == VENDOR || state == MECHANIC) { //if selling
			x_velocity = 0; //reset speeds
			y_velocity = 0;
			if(shop_spots!=null) {
				x = shop_spots[0][0];
				y = shop_spots[0][1];
			}
			lock = true;
		}else if(state == STOP) { //if staying in shop
			x_velocity = 0; //reset speeds
			y_velocity = 0;
			lock = true;
		}else if(state == STAND){
			//traverse to cookie on board to purchase it
			if(target==null || !board.cookies.contains(target)) {
				target = choosePurchase(board.cookies);
			}
			if(target!=null) {
				traverseShop(target);
			}else {
				if(shop_spots!=null)traverseShop(shop_spots[0][0],shop_spots[0][1],getRadius());
			}
			lock = false;
		}else if (state == VENTURE) {
			if(input_counter++>=input_speed) {
				input_counter = 0;
				chooseDir();
			}
			lock = false;
		}
		scale = board.currLevel.getScale();
		
		int spec = (special)?-1:doSpecial();
		if(spec!=-1 && special_activated.get(spec)) {
			special(spec);
		}
		orientParts();
	}
	public void doMovement() {
		if(!lock) {
			switch(direction) {
				case UP: //if up
					if(y_velocity>-maxvel) //if below speed cap
						y_velocity-=accel; //increase speed upward
						y_velocity/=fric;
					break;
				case RIGHT:
					if(x_velocity<maxvel)
						x_velocity+=accel;
						x_velocity/=fric;
					break;
				case DOWN:
					if(y_velocity<maxvel)
						y_velocity+=accel;
						y_velocity/=fric;
					break;
				case LEFT:
					if(x_velocity>-maxvel)
						x_velocity-=accel;
						x_velocity/=fric;
					break;
			}
		}
		super.doMovement();
	}

	//dies on floor
	public void kill() {
		ded = true;
		board.present_npcs.remove(this);
		if(special) {
			ArrayList<CookieItem> powerups = getPowerups();
			for(int i=0; i<powerups.size(); i++) //stop special
				powerups.get(i).getItem().end(true);
		}

		pickups = new ArrayList<CookieItem>();
		special = false;
		direction = CORPSE;
		x_velocity = 0;
		y_velocity = 0;
		wipeStash();
		to_sell = new ArrayList<CookieStore>();
		setShields(startShields);
		//randomizeStats();
			
		decayed_value = 0;
		extra_radius = 0;
		ghost = false;
		offstage = 60;
		averageStats();
		reset();
	}
	//reset npc for new FLOOR
	public void reset() {
		special = false;
		currSpecial = -1;
		shielded = false;
		shield_frames = 0;
		for(int i=0; i<special_frames.size(); i++)special_frames.set(i,0.0);
		for(int i=0; i<special_activated.size(); i++)special_activated.set(i,false);
		x_velocity=0;
		y_velocity=0;
		direction = NONE;
		scale = board.currLevel.getScale();
	}
	//chooses which level to go to on game start
	public void chooseResidence() {
		if(!board.stores.isEmpty())residence = board.stores.get(board.floors.get(0).getID());
	}
	/*//given a level's name, finds the "num+1"th level of that type from the board's level progression - uses backup as index if this doesn't exist
	public Level findFloor(String type, boolean store, int num, int backup) {
		int count = 0;
		Level point = board.floors.getLast();
		while(point.getPassages().size()>0) {
			if(point.getName().equals(type) && store==point instanceof Store) {
				count++;
				if(count>num)return point;
			}
			point = point.getPassages().get(0).getExit();
		}
		if(board.floors.size()>backup) {
			return board.floors.get(backup);
		}else {
			return null;
		}
	}*/
	//
	public Level chooseRoom(HashMap<String,Integer> priorities, int storebonus, boolean storemode, int steps, Level start) {
		Level point = start;
		boolean tryagain = false;
		for(int i=0; i<steps || (storemode && !(point instanceof Store)) || tryagain; i++) {
			tryagain = false;
			//gather weights for options
			ArrayList<Level> nexts = new ArrayList<Level>();
			ArrayList<Integer> chances = new ArrayList<Integer>();
			int sum = 0;
			for(Passage p : point.getPassages()) {
				if(p.entranceAt(point)) {
					Level next = p.getExit();
					nexts.add(next);
					if(priorities.containsKey(next.getName())) {
						chances.add(priorities.get(next.getName()));
					}else {
						chances.add(1);
					}
					if(next instanceof Store) {
						if(!storemode) {
							chances.set(chances.size()-1, chances.get(chances.size()-1)+storebonus);
						}else {
							chances.set(chances.size()-1, 999999);
						}
					}
					sum += chances.get(chances.size()-1);
					chances.set(chances.size()-1, sum);
				}
			}
			//randomly choose a path
			int chosen = (int)(Math.random() * sum);
			Level chosenLevel = null;
			for(int j=0; j<nexts.size() && chosenLevel == null; j++) {
				if(chances.get(j) >= chosen) {
					chosenLevel = nexts.get(j);
				}
			}
			if(chosenLevel==null) {
				if(nexts.size()>0) {
					chosenLevel = nexts.get(0);
				}else {
					chosenLevel = point;
				}
			}
			//if store is full, force continue
			if(chosenLevel instanceof Store){
				boolean vendor = state==VENDOR, passerby = state==STAND||state==STOP||state==VENTURE, mechanic = state==MECHANIC;
				if(((Store)chosenLevel).isFull(this,vendor,passerby,mechanic)) {
					tryagain = true;
				}
			}
			//choose best option based on priorities
			point = chosenLevel;
		}
		return point;
	}
	//converts name and weight arrays into a HashMap
	public HashMap<String,Integer> convertToMap(String[] labels, int[] weights) {
		HashMap<String,Integer> ret = new HashMap<String,Integer>();
		for(int i=0; i<labels.length; i++) {
			if(i<weights.length) {
				ret.put(labels[i],weights[i]);
			}else {
				ret.put(labels[i],1);
			}
		}
		return ret;
	}
	//creates a completely new stash of items
	public void createStash() {
		to_sell = new ArrayList<CookieStore>();
	}
	//adds item to a random point in to_sell list
	public void addRandomly(CookieStore c) {
		to_sell.add((int)(Math.random()*(to_sell.size()+1)),c);
	}
	//removes random item from to_sell list
	public void removeRandomly() {
		if(!to_sell.isEmpty())
			to_sell.remove((int)(Math.random()*(to_sell.size())));
	}
	//puts all items to sell out on display
	public void sellWares(int[][] positions) {
		shop_spots = positions;
		setX(shop_spots[0][0]); //put explorer in place
		setY(shop_spots[0][1]);
		for(int i=1; !to_sell.isEmpty() && i<shop_spots.length; i++) { //put all cookies in place
			CookieStore c = to_sell.remove(0);
			c.setPos(shop_spots[i][0],shop_spots[i][1]);
			c.setVendor(this);
			board.cookies.add(c);
			on_display.add(c);
		}
	}
	//puts self in shop
	public void standBy(int[] position) {
		int[][] b = {position};
		shop_spots = b;
		setX(position[0]); //put explorer in place
		setY(position[1]);
		state = STOP;
	}
	//removes items from display and re-stashes them
	public void packUp() {
		for(int i=on_display.size()-1; i>=0; i--) {
			if(board.cookies.contains(on_display.get(i))){
				on_display.get(i).kill(this);
				to_sell.add(on_display.remove(i));
			}else {
				on_display.remove(i);
			}
		}
	}
	
	public void doFunction(String f, String[] args) {
		switch(f) {
		case "Give": //pays player $ {dollars}
			double dollars = Double.parseDouble(args[0]);
			payCookies(board.player(),dollars);
			break;
		case "Take": //takes $ from player {dollars}
			double dollars2 = Double.parseDouble(args[0]);
			board.player().payCookies(this,dollars2);
			break;
		}
	}
	
	public int getDir() {return direction;}
	public double getAim() {
		switch(direction) {
		case UP:
			return 3*Math.PI/2;
		case RIGHT:
			return 0;
		case DOWN:
			return Math.PI/2;
		case LEFT:
			return Math.PI;
		default:
			return Math.PI/2;
		}}
	//chooses a direction to accelerate to
	public void chooseDir() {
		
	}
	//moves tester to where this explorer would be if they press dir right now
	public void testDirection(int dir) {
		double xv = x_velocity, yv = y_velocity; //used to find average x/y velocities over time period
		switch(dir) { //change velocity based on direction accelerating in
			case UP:
				//xv-=Math.signum(xv)*(((input_speed-Math.max(input_speed-Math.abs(xv/fric), 0))*fric)/2);
				xv *= Math.pow(fric,input_speed);
				yv+=-1*(((input_speed-Math.max(input_speed-(Math.abs(-1*maxvel-yv)/accel), 0))*accel)/2);
				break;
			case DOWN:
				//xv-=Math.signum(xv)*(((input_speed-Math.max(input_speed-Math.abs(xv/fric), 0))*fric)/2);
				xv *= Math.pow(fric,input_speed);
				yv+=1*(((input_speed-Math.max(input_speed-(Math.abs(1*maxvel-yv)/accel), 0))*accel)/2);
				break;
			case LEFT:
				//yv-=Math.signum(yv)*(((input_speed-Math.max(input_speed-Math.abs(yv/fric), 0))*fric)/2);
				yv *= Math.pow(fric,input_speed);
				xv+=-1*(((input_speed-Math.max(input_speed-(Math.abs(-1*maxvel-xv)/accel), 0))*accel)/2);
				break;
			case RIGHT:
				//yv-=Math.signum(yv)*(((input_speed-Math.max(input_speed-Math.abs(yv/fric), 0))*fric)/2);
				yv *= Math.pow(fric,input_speed);
				xv+=1*(((input_speed-Math.max(input_speed-(Math.abs(1*maxvel-xv)/accel), 0))*accel)/2);
				break;
		}
		tester.setLocation(x+xv*input_speed,y+yv*input_speed); //move tester to predicted location
	}
	//returns which special to do, -1 if none
	public int doSpecial() {
		return -1;
	}
	
	public void traverseShop(Cookie targ) {
		traverseShop(targ.getX(),targ.getY(),targ.getRadius());
	}
	//set direction to get to target cookie in shop
	public void traverseShop(double xt, double yt, double rt) {
		int dir = NONE;
		if(Math.sqrt(Math.pow(x-xt,2)+Math.pow(y-yt,2))<getRadius()+rt){

		}else if(Math.abs(x-xt)<getRadius()) {
			dir = y<yt ? DOWN : UP; 
		}else if(Math.abs(y-board.y_resol/2)<getRadius()) {
			dir = x>xt ? LEFT : RIGHT; 
		}else {
			dir = y<board.y_resol/2 ? DOWN : UP; 
		}
		if(dir!=direction && direction!=NONE) {
			direction = NONE;
			averageVels(0,0,false);
		}else {
			direction = dir;
		}
	}
	//returns chosen cookie to try to buy from options
	public Cookie choosePurchase(ArrayList<Cookie> options) {
		return null;
	}
	
	public void spend(double amount) {
		removeCookies(amount);
	}
	public void pay(double amount) {
		addCookies(amount);
	}
	//gives a random set of movement stats and colors accordingly
	public void randomizeStats() {
		acceleration = Math.random()*(mr[0][1]-mr[0][0])+mr[0][0];
		max_velocity = Math.random()*(mr[1][1]-mr[1][0])+mr[1][0];
		friction = Math.random()*(mr[2][1]-mr[2][0])+mr[2][0];
		colorize();
		calibrateStats();
	}
	//gives average of each stat
	public void averageStats() {
		acceleration=(mr[0][1]+mr[0][0])/2;
		max_velocity=(mr[1][1]+mr[1][0])/2;
		friction=(mr[2][1]+mr[2][0])/2;
		colorize();
		calibrateStats();
	}
	//creates color based on stats
	public void colorize() {
		coloration = new Color((int)((friction-mr[2][0])/(mr[2][1]-mr[2][0])*255),(int)((max_velocity-mr[1][0])/(mr[1][1]-mr[1][0])*255),(int)((acceleration-mr[0][0])/(mr[0][1]-mr[0][0])*255));
	}
	//prepares explorer at start of new level
	public void spawn() {
		ded = false;
		reset();
		setConvo();
	}
	//choose conversation for this level
	public void setConvo() {
		 convo = null;
	}
	public void levelComplete() {
		if(direction == CORPSE) { 
			chooseResidence();
			return;
		}
		if(state == VENTURE || state == STAND) { //move on if in correct state
			residence = board.currLevel.getPassages().get(0).getExit();
			state = VENTURE;
		}
		//reset special
		if(special) {
			ArrayList<CookieItem> powerups = getPowerups();
			for(int i=0; i<powerups.size(); i++) //stop special
				powerups.get(i).getItem().end(true);
			special_frames.set(currSpecial,0.0);
		}
		
		special = false;
		currSpecial = -1;
	}
	public int getState() {return state;}
	//sets words in text box at bottom of screen - null for removing box
	public void speak(Conversation conversation) {
		if(conversation!=null) {conversation.setDisplayed(true);
		}else {convo.setDisplayed(false);}
		board.setDialogue(this, conversation);
		if(conversation!=null)conversation.test();
	}
	public Color getColor() {return coloration;}
	public SpriteExplorer getSprite() {return sprite;}
	public void paint(Graphics g) {
		
		for(int i=0; i<parts.size(); i++) {
			if(parts.get(i)!=null)parts.get(i).update();
		}
		if(tester!=null)tester.update();
		g.setColor(coloration);
		g.fillOval((int)(.5+x-getRadius()), (int)(.5+y-getRadius()), (int)(.5+getRadius()*2), (int)(.5+getRadius()*2));
		
		Graphics2D g2 = (Graphics2D)g;
		AffineTransform origt = g2.getTransform();
		for(int i=0; i<summons.size(); i++) { //draw summons
			summons.get(i).paint(g2);
			g2.setTransform(origt);
		}
		//sprite
		if(sprite==null)return;
		sprite.setColor(coloration);
		sprite.paint(g);
	}
}
