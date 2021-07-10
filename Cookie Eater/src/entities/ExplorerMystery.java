package entities;

import java.awt.*;
import java.util.HashMap;

import ce3.*;
import cookies.*;
import levels.*;
import menus.*;
import items.*;

public class ExplorerMystery extends Explorer{
	
	private SegmentCircle part;
	
	/*STATES:
	 * Pass: Yes, No
	 * 
	 * 
	 * 
	 */
	
	/*FUNCTIONS:
	 * Give: Give $
	 * Take: Take $
	 * Gib: gibs
	 */
	
	public ExplorerMystery(Game frame, Board gameboard, int cycletime) {
		super(frame,gameboard,cycletime);
		radius = 40;
		min_cat = 4;
		max_cat = 8;
		mass = 400;
		tester = new SegmentCircle(board,this,x,y,radius*2,0,"test");
		input_speed = 30;
		startShields = 1;
		setShields(startShields);
		state = VENDOR;
	}
	public ExplorerMystery(Game frame, Board gameboard, SaveData sd, int cycle) {
		super(frame,gameboard,sd,cycle);
		for(Segment testPart : parts){
			if(testPart.name.equals("body")) {
				part = (SegmentCircle)testPart;
			}
		}
		tester = new SegmentCircle(board,this,x,y,radius*2,0,"test");
	}
	public String getName() {return "Mystery";}
	public void runEnds() {
		super.runEnds();
		for(int i=0; i<Math.random()*6-1; i++) {
			removeRandomly();
		}
		for(int i=0; i<Math.random()*6-1 || to_sell.size()<min_cat; i++) {
			double choose = Math.random()*10;
			if(choose<=.5) {
				addRandomly(new CookieShield(game,board,0,0,10));
			}else {
				addRandomly(new CookieItem(game,board,0,0,Item.generateItem(game,board,findItem()),(int)(.5+Math.random()*3)*5+35));
			}

		}
		addCookies(50);
		while(to_sell.size()>max_cat)removeRandomly();
	}
	public void runUpdate() {
		super.runUpdate();
		if(convo!=null && speaking<=0 && Level.lineLength(board.player().getX(), board.player().getY(), x, y)<150) {
			speak(convo);
			speaking++;
		}
		if(speaking>0 && speaking++>1000/game.getAdjustedCycle() && Level.lineLength(board.player().getX(), board.player().getY(), x, y)>=150) {
			speak(null);
			speaking = 0;
		}
		if(convo!=null)convo.test();
	}
	public void setConvo() {
		convo = new Conversation(board,this,"Odd1","greet");
		convo.setDisplayed(false);
	}
	public void chooseDir() {
		direction = NONE;
	}
	public int doSpecial() {
		return -1;
	}
	public void chooseResidence() {
		String[] levels = {};
		int[] weights = {};
		residence = chooseFloor(convertToMap(levels,weights), 100, true, 8);
	}

	public void createStash() {
		super.createStash();
		for(int i=0; i<5; i++) {
			double choose = Math.random()*10;
			if(choose<=.5) {
				addRandomly(new CookieShield(game,board,0,0,10));
			}else {
				addRandomly(new CookieItem(game,board,0,0,Item.generateItem(game,board,findItem()),(int)(.5+Math.random()*3)*5+35));
			}

		}
	}
	public void doFunction(String f, String[] args) {
		super.doFunction(f,args);
		switch(f) {
		case "Test": //tests if player bought all cookies
			setState("Pass","Yes");
			for(Cookie c:on_display) {
				if(board.cookies.contains(c)) {
					setState("Pass","No");
				}
			}
			break;
			
		case "Gib": //give reward (unimplemented)
			for(int i=0; i<5; i++) {
				addRandomly(new CookieShield(game,board,0,0,0));
			}
			sellWares(shop_spots);
		default:
			break;
		}
	}
	//chooses Item cookie to add to wares
	public String findItem() {
		String ret = "";
		switch((int)(Math.random()*21)) {
		case 0:
			ret = "Autopilot";
			break;
		case 1:
			ret = "Boost";
			break;
		case 2:
			ret = "Circle";
			break;
		case 3:
			ret = "Clone";
			break;
		case 4:
			ret = "Chain";
			break;
		case 5:
			ret = "Field";
			break;
		case 6:
			ret = "Flow";
			break;
		case 7:
			ret = "Ghost";
			break;
		case 8:
			ret = "Hold";
			break;
		case 9:
			ret = "Rebound";
			break;
		case 10:
			ret = "Recharge";
			break;
		case 11:
			ret = "Recycle";
			break;
		case 12:
			ret = "Repeat";
			break;
		case 13:
			ret = "Return";
			break;
		case 14:
			ret = "Ricochet";
			break;
		case 15:
			ret = "Shield";
			break;
		case 16:
			ret = "Shrink";
			break;
		case 17:
			ret = "Slowmo";
			break;
		case 18:
			ret = "Melee";
			break;
		case 19:
			ret = "Projectile";
			break;
		case 20:
			ret = "Teleport";
			break;
		default:
			ret = "Boost";
			break;
		}
		return ret;
	}
	
	public void setUpStates(){
		super.setUpStates();
		setState("Pass","No");
	}
	
	public void buildBody() {
		parts.add(part = new SegmentCircle(board,this,x,y,radius,0,"body"));
	}
	public void orientParts() {
		part.setLocation(x,y);
		part.setSize(radius);
		tester.setSize(radius*2);
		tester.setExtraSize(radius*2);
		super.orientParts();
	}

	public void paint(Graphics g) {
		super.paint(g);
	}
}
