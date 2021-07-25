package entities;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

import ce3.*;
import cookies.*;
import menus.*;
import levels.*;
import mechanisms.*;

public abstract class Entity {

	//parent class of moving, colliding objects on the stage
	
	protected Game game;
	protected Board board; //main board
	protected double scale; //zoom in/out of screen
	protected double x, y; //position
	protected double x_velocity, y_velocity; //speed
	protected double mass; //weight
	protected double radius; //represents size
	protected double countVels; //number of averaged velocities in cycle
	protected boolean lock; //can entity control its movement
	protected boolean ghost; //if the entity is in ghost mode
	protected double extra_radius; //area outside of the model, interacts with everything other than walls
	protected ArrayList<Summon> summons; //constructed objects owned by entity
	protected ArrayList<Object> bumped; //all things bumped into during this cycle
	protected double calibration_ratio; //framerate ratio
	protected String connectedEntity; //toString of entity that this is connected to
	protected String connectionCode; //toString of this entity before being saved to file
	
	//shields
	protected boolean shielded; //in stun after shield use
	protected boolean shield_tick; //countdown shield
	protected int shield_length, shieldlen; //stun length
	protected int shield_frames; //counting how deep into shield
	
	//specials
	protected boolean special; //whether a special is active
	protected int special_length, speciallen; //how long special lasts
	//protected ArrayList<ArrayList<Item>> powerups;
	protected int currSpecial;
	protected ArrayList<Double> special_frames; //counting how deep into special
	protected int special_cooldown, specialcool; //frames between uses of special
	protected double special_use_speed; //"frames" passed per frame of special use
	protected int special_recharges; //how many recharges have been set off in a row
	protected ArrayList<Boolean> special_activated; //if special is triggerable
	protected ArrayList<Color> special_colors; //color associated with each special
	
	protected ArrayList<Cookie> cash_stash; //only plain cookies
	protected ArrayList<ArrayList<CookieItem>> item_stash; //item cookies (separated based on slot)
	protected ArrayList<CookieShield> shield_stash; //shields
	protected ArrayList<CookieStat> stat_stash; //stat changers
	protected double decayed_value; //value of spoiled cookies
	protected ArrayList<Segment> parts; //segments that make up this entity
	protected boolean ded; //am i deceased
	protected Rectangle bounds; //rectangle bounding box of all parts
	protected int offstage; //how far entity can go past the screen's edge before getting hit
	
	//movement stats
	protected double min_recoil, minrec; //how fast entity bounces off wall (min and max)
	protected double max_recoil, maxrec;
	protected double acceleration, accel; //added to dimensional speed depending on direction
	protected double max_velocity, maxvel; //cap on accelerated-to dimensional speed
	protected double terminal_velocity, termvel; //maximum possible dimensional speed
	protected double friction, fric; //removed from dimensional speed
	
	protected String name;
	protected Map<String,String> variableStates; //behavior-determining states
	protected double[] relativeFrame = {0,0}; //x,y on main coordinates of relative frame's 0,0
	protected double[] relativeVel = {0,0}; //velocity of the frame relative to the board
	protected boolean averageVelOverride; //whether the averageVels should be disregarded this cycle
	
	public Entity(Game frame, Board gameboard, int cycletime) {
		calibration_ratio = cycletime/15.0;
		game = frame;
		board = gameboard;
		scale = 1;
		summons = new ArrayList<Summon>();
		bumped = new ArrayList<Object>();
		cash_stash = new ArrayList<Cookie>();
		shield_stash = new ArrayList<CookieShield>();
		item_stash = new ArrayList<ArrayList<CookieItem>>();
		stat_stash = new ArrayList<CookieStat>();
		parts = new ArrayList<Segment>();
		special = false;
		special_length = 60;
		special_frames = new ArrayList<Double>();
		special_cooldown = 180;
		special_use_speed = 1;
		special_recharges = 0;
		special_colors = new ArrayList<Color>();
		special_colors.add(new Color(255,0,255));special_colors.add(new Color(0,255,255));special_colors.add(new Color(255,255,0));
		special_activated = new ArrayList<Boolean>();
		for(int i=0; i<(gameboard.mode==Board.PVP?1:3); i++) {
			item_stash.add(new ArrayList<CookieItem>());
			special_frames.add(0.0);
			special_activated.add(false);
		}
		currSpecial = -1;
		decayed_value = 0;
		offstage = 30;
		shield_length = 60;
		shield_frames = 0;
		shield_tick = true;
		
		//default movement stats
		acceleration = .5;
		max_velocity = 10;
		terminal_velocity = 50;
		friction = -.95;
		min_recoil = 7;
		max_recoil = 30;
		
		
		bounds = null;
		averageVelOverride = false;
		setUpStates();
		calibrateStats();
	}
	
	public Entity(Game frame, Board gameboard, SaveData sd, int cycle) {
		calibration_ratio = cycle/15.0;
		game = frame;
		board = gameboard;
		
		bumped = new ArrayList<Object>();
		bounds = null;
		averageVelOverride = false;
		special = false;
		special_frames = new ArrayList<Double>();
		special_recharges = 0;
		special_activated = new ArrayList<Boolean>();
		currSpecial = -1;
		shield_frames = 0;
		
		scale = sd.getDouble("scale",0);
		name = sd.getString("name",0);
		x = sd.getDouble("position",0);
		y = sd.getDouble("position",1);
		relativeFrame[0] = sd.getDouble("position",2);
		relativeFrame[1] = sd.getDouble("position",3);
		x_velocity = sd.getDouble("velocity",0);
		y_velocity = sd.getDouble("velocity",1);
		relativeVel[0] = sd.getDouble("velocity",2);
		relativeVel[1] = sd.getDouble("velocity",3);
		radius = sd.getDouble("size",0);
		extra_radius = sd.getDouble("size",1);
		shielded = sd.getBoolean("shielded",0);
		ghost = sd.getBoolean("ghosted",0);
		lock = sd.getBoolean("locked",0);
		ded = sd.getBoolean("dead",0);
		mass = sd.getDouble("mass",0);
		
		ArrayList<SaveData> summon_data = sd.getSaveDataList("summons");
		summons = new ArrayList<Summon>();
		if(summon_data!=null) {
			for(int i=0; i<summon_data.size(); i++) {
				summons.add(new Summon(game, board, this, summon_data.get(i), cycle));
			}
		}

		ArrayList<SaveData> cash_data = sd.getSaveDataList("cashstash");
		cash_stash = new ArrayList<Cookie>();
		if(cash_data!=null) {
			for(int i=0; i<cash_data.size(); i++) {
				cash_stash.add(new Cookie(game, board, cash_data.get(i), true));
			}
		}
		ArrayList<SaveData> shield_data = sd.getSaveDataList("shieldstash");
		shield_stash = new ArrayList<CookieShield>();
		if(shield_data!=null) {
			for(int i=0; i<shield_data.size(); i++) {
				shield_stash.add(new CookieShield(game, board, shield_data.get(i)));
			}
		}
		ArrayList<SaveData> stat_data = sd.getSaveDataList("statstash");
		stat_stash = new ArrayList<CookieStat>();
		if(stat_data!=null) {
			for(int i=0; i<stat_data.size(); i++) {
				stat_stash.add(new CookieStat(game, board, stat_data.get(i)));
			}
		}
		SaveData allItemData = sd.getSaveDataList("itemstash").get(0);
		item_stash = new ArrayList<ArrayList<CookieItem>>();
		if(allItemData!=null) {
			for(int i=0; i<allItemData.numTags(); i++) {
				ArrayList<SaveData> itemData = allItemData.getSaveDataList("slot"+i);
				if(itemData!=null) {
					item_stash.add(i, new ArrayList<CookieItem>());
					for(int j=0; j<itemData.size(); j++) {
						CookieItem ci = new CookieItem(game, board, itemData.get(j));
						item_stash.get(i).add(ci);
						ci.getItem().setUser(this);
					}
				}
			}
		}
		ArrayList<SaveData> part_data = sd.getSaveDataList("segments");
		parts = new ArrayList<Segment>();
		if(part_data!=null) {
			for(int i=0; i<part_data.size(); i++) {
				parts.add(Segment.loadFromData(board, this, part_data.get(i)));
			}
		}

		special_length = sd.getInteger("specialframes",0);
		special_cooldown = sd.getInteger("specialframes",1);
		special_use_speed = sd.getDouble("specialframes",2);
		special_colors = new ArrayList<Color>();
		for(int i=0; i<sd.getData("specialcolor").size(); i++) {
			special_colors.add(new Color(sd.getInteger("specialcolor",i)));
		}
		for(int i=0; i<(gameboard.mode==Board.PVP?1:3); i++) {
			special_frames.add(0.0);
			special_activated.add(false);
		}
		decayed_value = sd.getDouble("decayedvalue",0);
		offstage = sd.getInteger("offstage",0);
		shield_length = sd.getInteger("shieldframes",0);
		shield_tick = sd.getBoolean("shieldframes",1);
		
		acceleration = sd.getDouble("movement",0);
		max_velocity = sd.getDouble("movement",1);
		terminal_velocity = sd.getDouble("movement",2);
		friction = sd.getDouble("movement",3);
		min_recoil = sd.getDouble("movement",4);
		max_recoil = sd.getDouble("movement",5);
		
		variableStates = new HashMap<String,String>();
		HashMap<String,ArrayList<Object>> stateData = sd.getSaveDataList("customstates").get(0).dataMap();
		Iterator<String> stateit = stateData.keySet().iterator();
		while(stateit.hasNext()) {
			String key = stateit.next();
			variableStates.put(key, stateData.get(key).get(0).toString());
		}
		
		if(sd.getData("connected")!=null) {
			board.connections.get(sd.getString("connected",0)).add(this);
		}
		
		calibrateStats();
	}
	public SaveData getSaveData() {
		SaveData data = new SaveData();
		data.addData("scale",scale);
		
		data.addData("name",name);
		data.addData("position",x,0);
		data.addData("position",y,1);
		data.addData("position",relativeFrame[0],2);
		data.addData("position",relativeFrame[1],3);
		data.addData("velocity",x_velocity,0);
		data.addData("velocity",y_velocity,1);
		data.addData("velocity",relativeVel[0],2);
		data.addData("velocity",relativeVel[1],3);
		data.addData("size",radius,0);
		data.addData("size",extra_radius,1);
		data.addData("shielded",shielded);
		data.addData("ghosted",ghost);
		data.addData("locked",lock);
		data.addData("dead",ded);
		data.addData("mass",mass);
		
		for(int i=0; i<parts.size(); i++) {
			data.addData("segments",parts.get(i).getSaveData(),i);
		}
		
		
		data.addData("specialframes",special_length,0);
		data.addData("specialframes",special_cooldown,1);
		data.addData("specialframes",special_use_speed,2);
		for(int i=0; i<special_colors.size(); i++) {
			data.addData("specialcolor",special_colors.get(i).getRGB(),i);
		}
		data.addData("decayedvalue",decayed_value);
		data.addData("offstage",offstage);
		data.addData("shieldframes",shield_length,0);
		data.addData("shieldframes",shield_tick,1);
		
		data.addData("movement",acceleration,0);
		data.addData("movement",max_velocity,1);
		data.addData("movement",terminal_velocity,2);
		data.addData("movement",friction,3);
		data.addData("movement",min_recoil,4);
		data.addData("movement",max_recoil,5);
		
		for(int i=0; i<summons.size(); i++) {
			data.addData("summons",summons.get(i).getSaveData(),i);
		}
		for(int i=0; i<cash_stash.size(); i++) {
			data.addData("cashstash",cash_stash.get(i).getSaveData(),i);
		}
		for(int i=0; i<shield_stash.size(); i++) {
			data.addData("shieldstash",shield_stash.get(i).getSaveData(),i);
		}		
		for(int i=0; i<stat_stash.size(); i++) {
			data.addData("statstash",stat_stash.get(i).getSaveData(),i);
		}
		SaveData itemData = new SaveData();
		for(int i=0; i<item_stash.size(); i++) {
			int j;
			for(j=0; j<item_stash.get(i).size(); j++) {
				itemData.addData("slot"+i,item_stash.get(i).get(j).getSaveData(),j);
			}
			itemData.addData("slot"+i,"null",j);
		}
		data.addData("itemstash",itemData);
		
		SaveData stateData = new SaveData();
		Iterator<String> stateit = variableStates.keySet().iterator();
		while(stateit.hasNext()) {
			String key = stateit.next();
			stateData.addData(key, variableStates.get(key),0);
		}
		data.addData("customstates",stateData);
		
		if(connectedEntity!=null) 
			data.addData("connected",connectedEntity);
		data.addData("connectcode",connectionCode);
		
		return data;
	}
	
	public void runUpdate() {
		if(ded)return;
		countVels = 0;
		scale = board.currFloor.getScale();
		
		//shields
		if(shielded && shield_tick) {
			if(shield_frames++>shieldlen) {
				shielded=false;
				shield_frames = 0;
			}
		}
		
		//specials
		if(special) {
			special_frames.set(currSpecial,special_frames.get(currSpecial)+special_use_speed); //increase special timer
			for(int j=0; j<summons.size(); j++) {
				summons.get(j).runUpdate();
			}
			ArrayList<CookieItem> powerups = getPowerups();
			for(int i=0; i<powerups.size(); i++) {
				powerups.get(i).getItem().execute();
			}

			if(special_frames.get(currSpecial)>speciallen) {
				special = false;
				int sz = powerups.size();
				for(int i=0; i<sz; i++) {
					powerups.get(i).getItem().end(false);
				}
				currSpecial = -1;
			}
			
		}
		for(int i=0; i<special_frames.size(); i++) {
			if(special_frames.get(i)>speciallen) {
				special_frames.set(i,special_frames.get(i)+1);
				special_recharges=0;
				if(special_frames.get(i)>speciallen+specialcool) {
					special_frames.set(i,0.0);
				}
			}
		}
		if(this instanceof Enemy) {
			for(int i=0; i<cash_stash.size(); i++) {
				cash_stash.get(i).runUpdate();
			}
			for(int i=0; i<item_stash.size(); i++) {
				for(int j=0; j<item_stash.get(i).size(); j++) {
					item_stash.get(i).get(j).runUpdate();
				}
			}
			for(int i=0; i<shield_stash.size(); i++) {
				shield_stash.get(i).runUpdate();
			}
			for(int i=0; i<stat_stash.size(); i++) {
				stat_stash.get(i).runUpdate();
			}
		}
		
		//collisions
		testCollisions();
		if(outOfBounds()) {
			killBounceEdge(!shielded);
		}
		
		doMovement();
	}
	//runs movement physics
	public void doMovement() {
		if(Math.abs(x_velocity)>termvel)x_velocity = termvel * Math.signum(x_velocity); //make sure it's not too fast
		if(Math.abs(y_velocity)>termvel)y_velocity = termvel * Math.signum(y_velocity);
		x+=x_velocity; //move
		y+=y_velocity;
		x_velocity*=fric; //multiply by friction to remove some vel
		y_velocity*=fric;
	}
	//resets after cycle end
	public void endCycle() {
		bumped = new ArrayList<Object>();
		bounds = null;
		for(int i=0; i<summons.size(); i++) {
			summons.get(i).endCycle();
		}
	}
	//tests all collisions
	public void testCollisions() {
	//	for(int j=0; j<parts.size(); j++) {
			if(ded)return;
			
			for(int i=0; i<board.cookies.size(); i++) { //for every cookie, test if any parts impact
				if(i<board.cookies.size()) {
					Cookie c = board.cookies.get(i);
					if(c!=null && collidesWithBounds(true,c.getBounds()) && collidesWithArea(true,c.getArea())) {
						hitCookie(c);
					}
				}
			}
			
			ArrayList<Entity> entities = new ArrayList<Entity>();
			for(Entity e : board.players)entities.add(e);
			for(Entity e : board.enemies)entities.add(e);
			for(Entity e : board.present_npcs)entities.add(e);
			for(Entity e : board.effects)entities.add(e);
			for(int i=0; i<entities.size(); i++) { //for every entity and its summons, test if any parts impact
				Entity e = entities.get(i);
				for(Summon s : e.getSummons())entities.add(s);
				if(allowedToCollide(this,e)) {
					if(collidesWithBounds(true,true,e) && collidesWithArea(true,true,e)) {
						double bmass = mass;
						double bxv = x_velocity;
						double byv = y_velocity;
						double[] point = Level.areasHitPoint(getArea(true),e.getArea(true));
						collideAt(e,point[0],point[1],e.getXVel(),e.getYVel(),e.getMass());
						e.collideAt(this,point[0],point[1],bxv,byv,bmass);
						while(collidesWithArea(true,true,e)) {
							double rat = 1/Math.sqrt(Math.pow(x-point[0],2)+Math.pow(y-point[1],2));
							setX((getX()-point[0])*rat+getX());
							setY((getY()-point[1])*rat+getY());
							orientParts();
							rat = 1/Math.sqrt(Math.pow(e.x-point[0],2)+Math.pow(e.y-point[1],2));
							e.setX((e.getX()-point[0])*rat+e.getX());
							e.setY((e.getY()-point[1])*rat+e.getY());
							e.orientParts();
						}
					}
				}
			}
			//test collision with level mechamisms
			if(!ghost) {
				for(int i=0; i<board.mechanisms.size(); i++) {
					Mechanism m = board.mechanisms.get(i);
					Area ma = m.getArea();
					if(collidesWithArea(false,ma)) {
						double[] point = Level.areasHitPoint(ma,getArea(false));
						collideAt(ma,point[0],point[1],m.getXVel(),m.getYVel(),m.getMass());
						triggerShield();
						while(collidesWithArea(false,ma)) {
							double rat = 1/Math.sqrt(Math.pow(x-point[0],2)+Math.pow(y-point[1],2));
							setX((getX()-point[0])*rat+getX());
							setY((getY()-point[1])*rat+getY());
							orientParts();
						}
					}
				}
			}
				
			//test collision with static walls
			if(!ghost && collidesWithArea(false,board.wallSpace)) {
				double[] point = Level.areasHitPoint(board.wallSpace,getArea(false));
				collideAt(board.wallSpace,point[0],point[1],0,0,999999999);
				triggerShield();
				while(collidesWithArea(false,board.wallSpace)) {
					double rat = 1/Math.sqrt(Math.pow(x-point[0],2)+Math.pow(y-point[1],2));
					setX((getX()-point[0])*rat+getX());
					setY((getY()-point[1])*rat+getY());
					orientParts();
				}

			}
			
	}

	//sets off shield
	public void triggerShield() {
		if(!shielded && board.currFloor.takeDamage()) { //if out of shield and menat to take damage
			shielded=true;
			shield_frames++;
			if(shield_stash.size()<=0) {
				kill(); //kill if out of shields
				return;
			}else {
				removeShields(1); //use shield if can
			}
		}
		scale = board.currFloor.getScale();
		if(Math.sqrt(x_velocity*x_velocity+y_velocity*y_velocity)<minrec){
			double rat = (minrec)/Math.sqrt(x_velocity*x_velocity+y_velocity*y_velocity);
			x_velocity *= rat;
			y_velocity *= rat;
		}
		if(Math.sqrt(x_velocity*x_velocity+y_velocity*y_velocity)>maxrec){
			double rat = (maxrec)/Math.sqrt(x_velocity*x_velocity+y_velocity*y_velocity);
			x_velocity *= rat;
			y_velocity *= rat;
		}
	}
	//tests if two entities can collide
	public static boolean allowedToCollide(Entity e1, Entity e2) {
		if(!e1.canCollideWith(e2) || !e2.canCollideWith(e1))return false; //if either entity specifically cannot collide
		if(e1.equals(e2))return false; //if entity is colliding with itself
		if((e1.getGhosted() && !e1.getShielded()) || (e2.getGhosted() && !e2.getShielded()))return false; //if either entity is ghosted but not shielded
		if(e1 instanceof Summon && ((Summon)e1).getUser().equals(e2) && ((Summon)e1).getAnchored())return false; //if one entity is the other's summon
		if(e2 instanceof Summon && ((Summon)e2).getUser().equals(e1) && ((Summon)e2).getAnchored())return false;
		if(e1 instanceof Effect && e2 instanceof Effect)return false; //if both entities are effects
		if(e1 instanceof Effect && !((Effect)e1).doesCollision())return false; //if either entity is an effect that can't collide
		if(e2 instanceof Effect && !((Effect)e2).doesCollision())return false;
		return true;
	}
	//if this entity class has specifications preventing collisions
	public boolean canCollideWith(Entity e) {
		return true;
	}
	//collides with anything other than cookies
	public boolean collidesWithAnything() {
		for(int j=0; j<parts.size(); j++) {
			ArrayList<Entity> entities = new ArrayList<Entity>();
			for(Entity e : board.players)entities.add(e);
			for(Entity e : board.enemies)entities.add(e);
			for(Entity e : board.present_npcs)entities.add(e);
			for(Entity e : board.effects)entities.add(e);
			for(int i=0; i<entities.size(); i++) { //for every entity and its summons, test if any parts impact
				Entity e = entities.get(i);
				if(!e.equals(this)) {
					if(!e.getGhosted() && !ghost) {
						return collidesWithBounds(true,true,e) && collidesWithArea(true,true,e);
						/*for(int k=0; k<e.getParts().size(); k++) {
							Segment s = e.getParts().get(k);
							if(s instanceof SegmentCircle) {
								SegmentCircle s2 = (SegmentCircle)s;
								if(parts.get(j).collidesWithCircle(true,s2.getCenterX(),s2.getCenterY(),s2.getTotalRadius())) {
									return true;
								}
							}
						}*/
					}
				}
			}
			if(collidesWithArea(false,board.wallSpace))return true;
			for(Mechanism m : board.mechanisms) {
				if(collidesWithArea(false,m.getArea()))return true;
			}
			/*for(int i=0; i<board.walls.size(); i++) { //for every wall, test if any parts impact
				Wall w = board.walls.get(i);
				if(parts.get(j).collidesWithRect(false,w.getX(),w.getY(),w.getW(),w.getH())){
					return true;
				}
			}*/
		}
		return false;
	}
	//create parts for the entity
	protected void buildBody() {
		
	}
	//sets positions of all segments relative to location
	public void orientParts() {
		bounds = null;
		bounds = getBounding(true);
	}
	public ArrayList<Segment> getParts(){return parts;}
	//returns relative frame offsets
	public double[] getRelativeFrame() {
		return relativeFrame;
	}
	//sets offset of relative frame
	public void setRelativeFrame(double xP, double yP) {
		relativeFrame[0] = xP;
		relativeFrame[1] = yP;
	}
	
	//sets offset of relative frame velocity
	public void setRelativeVel(double xV, double yV) {
		relativeVel[0] = xV;
		relativeVel[1] = yV;
	}
	
	//position methods, rel determines if relative frame is used
	public double getX() {return getX(false);}
	public double getY() {return getY(false);}
	public double getX(boolean rel) {return x+ (rel?-relativeFrame[0]:0);}
	public double getY(boolean rel) {return y+ (rel?-relativeFrame[1]:0);}
	public void setX(double xp) {setX(xp,false);}
	public void setY(double yp) {setY(yp,false);}
	public void setX(double xp, boolean rel) {x=xp+ (rel?relativeFrame[0]:0);}
	public void setY(double yp, boolean rel) {y=yp+ (rel?relativeFrame[1]:0);}
	
	public double getXVel() {return getXVel(false);}
	public double getYVel() {return getYVel(false);}
	public double getXVel(boolean rel) {return x_velocity+ (rel?-relativeVel[0]:0);}
	public double getYVel(boolean rel) {return y_velocity+ (rel?-relativeVel[1]:0);}
	public void setXVel(double a) {setXVel(a,false);}
	public void setYVel(double a) {setYVel(a,false);}
	public void setXVel(double a, boolean rel) {x_velocity=a+ (rel?relativeVel[0]:0);}
	public void setYVel(double a, boolean rel) {y_velocity=a+ (rel?relativeVel[1]:0);}
	
	public double getMass() {return mass;}
	
	//direction accelerating towards
	public double getAim() {return 0.0;}
	
	public double getMaxVel() {return maxvel;}
	public double getFriction() {return fric;}
	
	//calibrates all movement and timing stats for scale and framerate
	public void calibrateStats() {
		accel = acceleration*scale*calibration_ratio*calibration_ratio;
		maxvel = max_velocity*scale*calibration_ratio;
		termvel = terminal_velocity*scale*calibration_ratio;
		fric = Math.pow(friction, calibration_ratio);
		
		minrec = min_recoil*scale*calibration_ratio;
		maxrec = max_recoil*scale*calibration_ratio;
		
		shieldlen = (int)(.5+shield_length*(1/calibration_ratio));
		speciallen = (int)(.5+special_length*(1/calibration_ratio));
		specialcool = (int)(.5+special_cooldown*(1/calibration_ratio));
	}
	
	
	public void lockControl(boolean l) {lock = l;}
	
	//takes velocity changes from items and averages them
	public void averageVels(double xVel, double yVel, boolean rel) {
		if(averageVelOverride)return;
		if(countVels==0) { //if first, set velocity to given
			setXVel(0,rel);
			setYVel(0,rel);
		}
		countVels++;
		setXVel((getXVel(rel)*(countVels-1)+xVel)/countVels,rel); //average given velocity into current
		setYVel((getYVel(rel)*(countVels-1)+yVel)/countVels,rel);
	}
	public void setAverageVelOverride(boolean b) {averageVelOverride = b;}
	
	public void setShielded(boolean s) {shielded=s;shield_tick=!s;}
	public boolean getShielded() {return shielded;}
	public void setGhost(boolean g) {ghost = g;}
	public boolean getGhosted() {return ghost;}
	
	public void setExtraRadius(double er) {
		for(int i=0; i<parts.size(); i++) {
			parts.get(i).setExtraSize(er);
		}
		extra_radius=er/scale;
	}
	public double getExtraRadius() {return extra_radius*scale;}
	public void setRadius(double r) {
		for(int i=0; i<parts.size(); i++) {
			parts.get(i).setSize(parts.get(i).getSize()*(r/radius));
		}
		radius=r/scale;
	}
	public double getRadius() {return radius*scale;}
	public double getTotalRadius() {return (radius+extra_radius)*scale;}
	
	public void addSummon(Summon s) {summons.add(s);}
	public void removeSummon(Summon s) {summons.remove(s);}
	public ArrayList<Summon> getSummons() {return summons;}
	
	public void addBump(Object b) {bumped.add(b);}
	
	//bounces accoridng to collision with moving mass at point 
	public void collideAt(Object b, double xp, double yp, double oxv, double oyv, double om) {
		if(b!=null&&bumped.contains(b)) {
			bumped.add(b);
			return; //if already hit, don't hit again
		}
		bumped.add(b);
		
		if(x_velocity==0 && y_velocity==0) { //if no velocity, set vel slightly towards wall
			double rat = 1/Math.sqrt(Math.pow(x-xp,2)+Math.pow(y-yp,2));
			x_velocity = (xp-getX())*rat;
			y_velocity = (yp-getY())*rat;
		}
		double actual_mass = mass;
		//for(Summon2 s: summons)actual_mass+=s.getMass(); //adjust mass for summons
		double pvx = (xp-x), pvy = (yp-y); //change in position from entity to collision point
		if(Math.abs(pvx)<1)pvx=0;if(Math.abs(pvy)<1)pvy=0; //make slight differences into differences of 0
		double oxm = oxv*om, oym = oyv*om; //momentum of other entity
		double txm = x_velocity*actual_mass, tym = y_velocity*actual_mass; //momentum of this entity
		double oProj = Math.abs((oxm*pvx+oym*pvy)/(pvx*pvx+pvy*pvy));
		double tProj = Math.abs((txm*pvx+tym*pvy)/(pvx*pvx+pvy*pvy));
		double projdx = (oProj+tProj)*pvx,projdy = (oProj+tProj)*pvy; //lost velocity conversion
	
		double proejjjg = (x_velocity*pvy+y_velocity*-pvx)/(pvx*pvx+pvy*pvy); //normalize
		
		x_velocity=pvy*proejjjg-projdx/actual_mass; //calculate final velocities
		y_velocity=-pvx*proejjjg-projdy/actual_mass;
		
		//handle sound volume based on intensity
		double impact = Math.abs(
				actual_mass*Math.sqrt(Math.pow(x_velocity,2)+Math.pow(y_velocity,2)) -
				om*Math.sqrt(Math.pow(oxv,2)+Math.pow(oyv,2)));
		double maxImpact = board.player().termvel*board.player().mass/2;
		if(impact > maxImpact)impact = maxImpact;
		float impactVal = (float)(impact/maxImpact) * (Audio.VOLUME_RANGE) + Audio.VOLUME_LOW;
		game.audio.playSound("bonk2",impactVal);
		
		if(x_velocity==0 && y_velocity==0) { //if no velocity, set vel slightly away from wall
			double rat = 1/Math.sqrt(Math.pow(x-xp,2)+Math.pow(y-yp,2));
			x_velocity = (getX()-xp)*rat;
			y_velocity = (getY()-yp)*rat;
		}
		
		if(special) { //bounce any items
			ArrayList<CookieItem> powerups = getPowerups();
			for(int i=0; i<powerups.size(); i++) {
				powerups.get(i).getItem().bounce(b,xp,yp);
			}
		}
	}
	public void setCalibration(double calrat) { //recalibrate everything that used cycle to better match current fps
		if(!game.check_calibration || calrat==calibration_ratio || game.getAdjustedCycle()/(double)game.getCycle()>2 || game.getAdjustedCycle()/(double)game.getCycle()<.5)return;
		
		calibration_ratio = calrat/15.0;
		calibrateStats();
		
		//calibrate summons
		for(int i=0; i<summons.size(); i++) {
			summons.get(i).setCalibration(calrat);
		}
	}
	
	//activates special A (all powerups tied to A)
	public void special(int index) {
		if(this instanceof Eater && (((Eater)this).getState() == Eater.WIN || ((Eater)this).getState() == Eater.DEAD))return; //when game paused, don't special
		if(board.currFloor.specialsEnabled()) {
			if(special || special_frames.get(index)!=0 || !special_activated.get(index))return;
			currSpecial = index;
			ArrayList<CookieItem> powerups = item_stash.get(index);
			for(int i=0; i<powerups.size(); i++) {
				powerups.get(i).getItem().prepare();
			}
			//cut off
			for(int i=0; i<powerups.size(); i++) {
				powerups.get(i).getItem().initialize();
			}
			special=true;
			special_activated.set(index, false);
			
		}else {
			currSpecial = index;

		}
		}
	public boolean getSpecialActivated(int s) {return special_activated.get(s);}
	public boolean getSpecialActivated() {return special;}
	public void activateSpecials() {
		for(int i=0; i<special_activated.size(); i++) {
			if(special_frames.get(i)==0)
				special_activated.set(i, true);
		}
	}
	public void rechargeSpecial(int s) {
		special_activated.set(s, true);
		special_frames.set(currSpecial,0.0);
		special_recharges++;
	}
	public int getSpecialRecharges() { return special_recharges;}
	
	public void giveCookie(Cookie c) {
		if(c instanceof CookieItem) {
			addItem(getCurrentSpecial(), (CookieItem)c);
		}else if(c instanceof CookieShield) {
			shield_stash.add(((CookieShield)c));
		}else if(c instanceof CookieStat) {
			stat_stash.add(((CookieStat)c));
		}else if(c.getValue()!=0){
			cash_stash.add(c);
		}
		if(c.getValue()>0 || !c.getDecayed())
			activateSpecials();
	}
	public void pickupItem(CookieItem i) {
		giveCookie(i);
	}
	public void hitCookie(Cookie c) {
		if(ded)return;
		if(!(c instanceof CookieStore) || ((CookieStore)c).purchase(this)) {
			c.kill(this);
			if(c instanceof CookieItem && !board.currFloor.installPickups()) {
				pickupItem((CookieItem)c);
			}else {
				giveCookie(c);
			}
		}
	}
	public ArrayList<Cookie> getStash() {
		ArrayList<Cookie> stash = new ArrayList<Cookie>();
		for(Cookie c : cash_stash)stash.add(c);
		for(ArrayList<CookieItem> c : item_stash)for(CookieItem c2 : c)stash.add(c2);
		for(Cookie c : shield_stash)stash.add(c);
		for(Cookie c : stat_stash)stash.add(c);
		return stash;}
	public ArrayList<Cookie> getCashStash() {return cash_stash;}
	public ArrayList<CookieShield> getShieldStash() {return shield_stash;}
	public ArrayList<ArrayList<CookieItem>> getItemStash() {return item_stash;}
	public ArrayList<CookieStat> getStatStash() {return stat_stash;}
	public void wipeStash() {
		cash_stash = new ArrayList<Cookie>();
		shield_stash = new ArrayList<CookieShield>();
		item_stash = new ArrayList<ArrayList<CookieItem>>();
		for(int i=0; i<(board.mode==Board.PVP?1:3); i++) {
			item_stash.add(new ArrayList<CookieItem>());
		}
		stat_stash = new ArrayList<CookieStat>();
	}
	public double getDecayedValue() {return decayed_value;}
	public void setDecayedValue(double dv) {decayed_value=dv;}
	public int getShields() {return shield_stash.size();}
	public void removeShields(int num) {
		for(int i=0; i<num && !shield_stash.isEmpty(); i++) {
			shield_stash.remove(0);
		}
	}
	public void addShields(int num) {
		for(int i=0; i<num; i++) {
			CookieShield s = new CookieShield(game,board,0,0,0);
			shield_stash.add(s);
		}
	}
	public void setShields(int num) {
		if(shield_stash.size()>num) {
			removeShields(shield_stash.size()-num);
		}else if(shield_stash.size()<num) {
			addShields(num-shield_stash.size());
		}
	}
	public void removeCookies(double num) {
		while(num>0 && !cash_stash.isEmpty()) {
			Cookie chosen = cash_stash.get(0);
			if(chosen.getValue()>num) {
				chosen.setValue(chosen.getValue()-num);
				num = 0;
			}else {
				num -= chosen.getValue();
				cash_stash.remove(chosen);
			}

		}
	}
	public void addCookies(double num) {
		while((int)num>0) {
			cash_stash.add(new Cookie(game,board,(int)getX(),(int)getY(),true));
			num--;
		}
		if(num>0) {
			Cookie c = new Cookie(game,board,(int)getX(),(int)getY(),true);
			c.setValue(num);
			cash_stash.add(c);
			
		}
	}
	public void spend(double amount) {
		removeCookies(amount);
	}
	public void pay(double amount) {
		addCookies(amount);
	}
	public void payCookies(Entity recipient, double num) {
		while(num>0 && !cash_stash.isEmpty()) {
			Cookie chosen = cash_stash.get(0);
			if(chosen.getValue()>num) {
				chosen.setValue(chosen.getValue()-num);
				num = 0;
				if(recipient instanceof Eater) {
					((Eater)recipient).addCash(chosen.getValue());

				}
			}else {
				num -= chosen.getValue();
				cash_stash.remove(chosen);
				recipient.giveCookie(chosen);
				
				if(recipient instanceof Eater) {
					((Eater)recipient).addCash(chosen.getValue());

				}
			}

		}
	}
	public double getCash() {
		double total = 0;
		for(Cookie c : cash_stash) {
			total+=c.getValue();
		}
		return total;
	}
	public void addItem(int index, CookieItem i) {
		if(index<0)index=0;
		boolean add = true;
		ArrayList<CookieItem> powerups = item_stash.get(index);
		for(int j=0; j<powerups.size(); j++) { //see if item already in list
			CookieItem test = powerups.get(j);
			if(i.getName().equals(test.getName())) { //if duplicate, amplify instead of adding
				add = false;
				test.getItem().amplify();
			}
		}
		if(add && i!=null) {
			item_stash.get(index).add(i);
			i.getItem().setUser(this);
		}
	}
	public void removeItem(int index, CookieItem i) {
		if(item_stash.get(index).contains(i)) {
			item_stash.get(index).remove(i);
		}
	}

	public void extendSpecial(double time) {
		for(int i=0; i<special_frames.size(); i++) {
			if(special_frames.get(i)<speciallen && special_frames.get(i)!=0) {
				if(special_frames.get(i)>time) {
					special_frames.set(i,special_frames.get(i)-time);
				}else {
					special_frames.set(i, 1.0);
				}
			}
		}
	}
	public int getSpecialLength() {return speciallen;}
	public int getSpecialCooldown() {return specialcool;}
	public ArrayList<Double> getSpecialFrames() {return special_frames;}
	public void setSpecialFrames(ArrayList<Double> sf) {special_frames = sf;}
	public ArrayList<Color> getSpecialColors() {return special_colors;}
	public int getCurrentSpecial() {return currSpecial;}
	public double getSpecialUseSpeed() {return special_use_speed;}
	public void setSpecialUseSpeed(double sus) {special_use_speed = sus;}
	public ArrayList<CookieItem> getPowerups() {return item_stash.get(currSpecial);}
	public ArrayList<ArrayList<CookieItem>> getItems() {return item_stash;}
	
	public int getOffstage() {return offstage;}
	public void setOffstage(int d) {offstage=d;}
	//tests if off screen
	public boolean outOfBounds() {
		return x<0-offstage || x>board.x_resol+offstage || y<0-offstage || y>board.y_resol+offstage;
	}
	//kill, but only if no bounce (on edge)
	public void killBounceEdge(boolean breakShield) {
		if(!shielded && getShields()<=0) { //kill if no shields, otherwise bounce
			kill();
			return;
		}else if(!shielded && breakShield){//only remove shields if not in stun and shield to be broken
			removeShields(1);
		}
		if(x<0) {
			//bounce(null,-100-offstage,-100,100-(int)(.5+radius),board.Y_RESOL+100);
			collideAt(board.wallSpace,x-radius,y,0,0,999999999);
		}else if(x>board.x_resol) {
			//bounce(null,board.X_RESOL+(int)(.5+radius*scale)+offstage,-100,100-(int)(.5+radius*scale),board.Y_RESOL+1000);
			collideAt(board.wallSpace,x+radius,y,0,0,999999999);
		}else if(y<0) {
			//bounce(null,-100,-100-offstage,board.X_RESOL+100,100-(int)(.5+radius*scale));
			collideAt(board.wallSpace,x,y-radius,0,0,999999999);
		}else if(y>board.y_resol) {
			//bounce(null,-100,board.Y_RESOL+(int)(.5+radius*scale)+offstage,board.X_RESOL+100,100-(int)(.5+radius*scale));
			collideAt(board.wallSpace,x,y+radius,0,0,999999999);
		}
		
	}
	public void kill() {}
	public boolean isDed() {return ded;}
	public String getName() {return name;}
	public Color getColor() {return null;}
	public Rectangle getBounding(boolean extra) {
		if(bounds==null) {
			Rectangle r = new Rectangle();
			for(Segment s : parts) {
				if(r.isEmpty())r=s.getBounding(extra);
				r = r.union(s.getBounding(extra));
			}
			bounds = r;
			return bounds;
		}else {
			return bounds;
		}
		
	}
	public Area getArea(boolean extra) {
		Area a = new Area();
		for(Segment s : parts) {
			a.add(s.getArea(extra));
		}
		return a;
	}
	public boolean collidesWithBounds(boolean extra, boolean oextra, Entity other) {
		return collidesWithBounds(extra,other.getBounding(oextra));
	}
	public boolean collidesWithArea(boolean extra, boolean oextra, Entity other) {
		return collidesWithArea(extra,other.getArea(oextra));
	}
	public boolean collidesWithBounds(boolean extra, Rectangle r) {
		return getBounding(extra).intersects(r);
	}
	public boolean collidesWithArea(boolean extra, Area a) {
		Area b = getArea(extra);
		b.intersect(a);
		return !b.isEmpty();
	}
	
	public void speak(Conversation convo) {
		
	}
	//calls custom functions using string identifier
	public void doFunction(String f, String[] args) {
		switch(f) {
		
		}
	}
	
	//sets up state variables and possible values
	public void setUpStates(){
		variableStates = new HashMap<String,String>();
	}
	//gets state from variable
	public String getState(String var) {
		return variableStates.get(var);
	}
	//sets variable's state
	public void setState(String var, String state) {
		variableStates.put(var,state);
	}
	//tests if varaible's current state is equal to a value
	public boolean stateIs(String var, String state) {
		return variableStates.get(var).equals(state);
	}
	public double getMinRecoil() {return minrec;}
	public void setMinRecoil(double r) {min_recoil = r;}
	public double getMaxRecoil() {return maxrec;}
	public void setMaxRecoil(double r) {max_recoil = r;}
	
	public void connect(Entity e) {connectedEntity = e.toString();}
	public String connectedTo() {return connectedEntity;}
	public String connectionCode() {return connectionCode;}
	
}
