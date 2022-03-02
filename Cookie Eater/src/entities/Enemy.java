package entities;

import java.awt.*;
import java.awt.geom.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import ce3.*;
import cookies.*;
import levels.*;

public abstract class Enemy extends Entity{

	protected Entity explorerTarget; //entity to bother
	protected boolean steals;
	protected ArrayList<String> imgs;
	protected double targetx, targety;
	protected int shieldBounces; //number of bounces before this shield expires
	public static Class[] enemytypes = {EnemyBlob.class, EnemyBloc.class, EnemyCrawler.class, EnemyGlob.class, EnemyParasite.class, EnemySlob.class, EnemySpawner.class, EnemySpawnerArena.class};
	
	public Enemy(Game frame, Board gameboard, int cycletime, double xp, double yp) {
		super(frame,gameboard,cycletime);
		ded=false;
		x = xp;
		y = yp;
		radius = 30;
		x_velocity=0;
		y_velocity=0;
		mass = 100;
		imgs = new ArrayList<String>();
		shieldBounces = 0;
		buildBody();
		orientParts();
		createStash();
	}
	public Enemy(Game frame, Board gameboard, SaveData sd, int cycle) {
		super(frame,gameboard,sd,cycle);
		steals = sd.getBoolean("steals",0);
		targetx = sd.getDouble("target",0);
		targety = sd.getDouble("target",1);
		imgs = new ArrayList<String>();
	}
	public SaveData getSaveData() {
		SaveData data = super.getSaveData();
		data.addData("steals",steals);
		data.addData("target",targetx,0);
		data.addData("target",targety,1);
		data.addData("type",this.getClass().getName());
		return data;
	}
	//return Enemy created by SaveData, testing for correct type of Enemy
	public static Enemy loadFromData(Game frame, Board gameboard, SaveData sd, int cycle) {
		//enemy subclasses
		
		String thistype = sd.getString("type",0);
		for(int i=0; i<enemytypes.length; i++) {
			//if class type matches type from file, instantiate and return it
			if(thistype.equals(enemytypes[i].getName())){
				try {
					return (Enemy) (enemytypes[i].getDeclaredConstructor(Game.class, Board.class, SaveData.class, int.class).newInstance(frame, gameboard, sd, cycle));
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | NoSuchMethodException | SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
		//default to blob
		return new EnemyBlob(frame, gameboard, sd, cycle);
	}
	//return Enemy newly created, testing for correct type of Enemy
	public static Enemy loadNewInstance(Game frame, Board gameboard, int cycle, int xp, int yp, String thistype) {
		//enemy subclasses
		for(int i=0; i<enemytypes.length; i++) {
			//if class type matches type from file, instantiate and return it
			if(thistype.equals(enemytypes[i].getName())){
				try {
					return (Enemy) (enemytypes[i].getDeclaredConstructor(Game.class, Board.class, int.class, double.class, double.class).newInstance(frame, gameboard, cycle, xp, yp));
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | NoSuchMethodException | SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
		//default to blob
		return new EnemyBlob(frame, gameboard, cycle, xp, yp);
	}
	//transfer array into arraylist
	protected void setImgs(String[] imgList) {
		for(int i=0; i<imgList.length; i++)imgs.add(imgList[i]);
	}
	//runs each cycle
	public void runUpdate() {
		super.runUpdate();
		if(ded)return;
		if(Math.random()>.999 && currSpecial>=0 && !getPowerups().isEmpty()) {
			special(0); 
		}
		orientParts();
	}
	public void doMovement() {
		if(explorerTarget==null || (!board.presentnpcs.contains(explorerTarget) && !board.players.contains(explorerTarget)))
			explorerTarget = targetExplorer(); //find target if none targeted or target died
		super.doMovement();
	}
	//accelerates towards target coordinate
	public void accelerateToTarget(double tarX, double tarY) {
		targetx = tarX;
		targety = tarY;
		if(lock)return;
		double rat = accel / Level.lineLength(x, y, tarX, tarY);
		if(Level.lineLength(x, y, tarX, tarY)==0) rat = 0;
		if(Math.abs(x_velocity)<maxvel)x_velocity+=rat*(tarX-x);
		if(Math.abs(y_velocity)<maxvel)y_velocity+=rat*(tarY-y);
	}
	//deletes this enemy
	public void kill() {
		ArrayList<Cookie> stash = getStash();
		while(!stash.isEmpty()) {
			double ang = Math.random()*Math.PI*2;
			double r=80*board.currLevel.getScale();
			if(stash.get(0) instanceof CookieStore)r*=2;
			double addx = r*Math.cos(ang), addy = r*Math.sin(ang);
			boolean hit = false;
			/*for(int i=0; i<board.walls.size(); i++) {
				Wall w = board.walls.get(i);
				if(Level.collidesCircleAndRect((int)(.5+x+addx),(int)(.5+y+addy),stash.get(0).getRadius(),w.getX(),w.getY(),w.getW(),w.getH())) {
					hit = true;
				}
			}*/
			double rr = stash.get(0).getRadius();
			Ellipse2D.Double c = new Ellipse2D.Double((int)(.5+x+addx-rr),(int)(.5+y+addy-rr),(int)(.5+rr*2),(int)(.5+rr*2));
			Area b = new Area(c);
			b.intersect(board.wallSpace);
			hit = !b.isEmpty();
			if((int)(.5+x+addx)<0||(int)(.5+x+addx)>board.currLevel.x_resol||(int)(.5+y+addy)<0||(int)(.5+y+addy)>board.currLevel.y_resol)hit=true;
			if(!hit) {
				Cookie remove = stash.remove(0);
				remove.setPos((int)(.5+x+addx),(int)(.5+y+addy));
				board.currLevel.cookies.add(remove);
			}
		}
		board.currLevel.enemies.remove(this);
		ded=true;
		wipeStash();
	}
	
	public void triggerShield() {
		//increment bounces while still shielded
		if(shielded) {
			shieldBounces++;
		}else {
			shieldBounces = 0;
		}
		super.triggerShield();
		//kill if bounced too many times on one shield
		if(shieldBounces >= getShields()*3) {
			kill();
		}
	}
	
	//picks an npc or player from the board to target
	public Entity targetExplorer() {
		int e = (int)(Math.random()*(board.players.size()+board.presentnpcs.size()));
		return e>=board.players.size() ? board.presentnpcs.get(e-board.players.size()) : board.players.get(e);
	}
	
	//puts cookies in stash on spawn
	public void createStash() {
		
	}
	//draws
	public void paint(Graphics g) {
		for(int i=0; i<parts.size(); i++) {
			parts.get(i).update();
		}
		if(currSpecial!=-1) {
			Color meh = special_colors.get(currSpecial);
			g.setColor(new Color(meh.getRed(),meh.getGreen(),meh.getBlue(),100));
			g.fillOval((int)(.5+x-1.5*radius), (int)(.5+y-1.5*radius), (int)(.5+3*radius), (int)(.5+3*radius));
		}
		Graphics2D g2 = (Graphics2D)g;
		AffineTransform origt = g2.getTransform();
		for(int i=0; i<summons.size(); i++) { //draw summons
			summons.get(i).paint(g2);
			g2.setTransform(origt);
		}
	}
	
	//overriding to not delete spoiled cookies and to not install pickups
	public void giveCookie(Cookie c) {
		if(c instanceof CookieItem) {
			addItem(getCurrentSpecial(), (CookieItem)c);
		}else if(c instanceof CookieShield) {
			shield_stash.add(((CookieShield)c));
		}else if(c instanceof CookieStat) {
			stat_stash.add(((CookieStat)c));
		}else{
			cash_stash.add(c);
		}
		if(c.getValue()>0 || !c.getDecayed())
			activateSpecials();
	}
	
	public double totalVel() {
		return Math.sqrt(x_velocity*x_velocity+y_velocity*y_velocity);
	}
	public double getAim() {
		return Math.atan2((targety-y),(targetx-x));
	}
}
