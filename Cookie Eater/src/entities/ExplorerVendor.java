package entities;

import java.awt.*;

import ce3.*;
import cookies.*;
import levels.*;
import menus.*;

public class ExplorerVendor extends Explorer{
	
	private SegmentCircle part;
	
	/*STATES:
	 * Relationship: Stranger, Enemy, Lover, Friend 
	 * Grunts: did, didn't
	 * Affiliation: Enemy
	 * Gruntcount: 0...
	 */
	
	/*FUNCTIONS:
	 * Give: Give $
	 * Take: Take $
	 */
	
	public ExplorerVendor(Board frame, int cycletime) {
		super(frame,cycletime);
		chooseResidence();
		radius = 40;
		min_cat = 2;
		max_cat = 3;
		mass = 400;
		tester = new SegmentCircle(board,this,x,y,radius*2,0);
		input_speed = 30;
		start_shields = 1;
		setShields(start_shields);
		state = VENDOR;
	}
	public String getName() {return "Vendor";}
	public void runEnds() {
		super.runEnds();
		for(int i=0; i<Math.random()*4-1; i++) {
			removeRandomly();
		}
		for(int i=0; i<Math.random()*4-1 || to_sell.size()<min_cat; i++) {
			double choose = Math.random()*10;
			if(choose<=1) {
				addRandomly(new CookieShield(board,0,0,10));
			}else {
				addRandomly(new CookieItem(board,0,0,Level.generateItem(board,findItem()),(int)(.5+Math.random()*3)*5+35));
			}

		}
		addCookies(50);
		while(to_sell.size()>max_cat)removeRandomly();
	}
	public void runUpdate() {
		super.runUpdate();
		if(convo!=null && speaking<=0 && Level.lineLength(board.player.getX(), board.player.getY(), x, y)<150) {
			speak(convo);
			speaking++;
		}
		if(speaking>0 && speaking++>1000/board.getAdjustedCycle() && Level.lineLength(board.player.getX(), board.player.getY(), x, y)>=150) {
			speak(null);
			speaking = 0;
		}
		if(convo!=null)convo.test();
	}
	public void setConvo() {
		convo = new Conversation(board,this,"TestSpeech5","here");
		convo.setDisplayed(false);
	}
	public void chooseDir() {
		direction = NONE;
	}
	public int doSpecial() {
		return -1;
	}
	public void chooseResidence() {
		residence = findFloor("Dungeon Foyer",true,0,2);
	}

	public void createStash() {
		super.createStash();
		for(int i=0; i<4; i++) {
			double choose = Math.random()*10;
			if(choose<=1) {
				addRandomly(new CookieShield(board,0,0,10));
			}else {
				addRandomly(new CookieItem(board,0,0,Level.generateItem(board,findItem()),(int)(.5+Math.random()*3)*5+35));
			}

		}
	}
	
	//chooses Item cookie to add to wares
	public String findItem() {
		String ret = "";
		switch((int)(Math.random()*15)) {
		case 0:
			ret = "Boost";
			break;
		case 1:
			ret = "Circle";
			break;
		case 2:
			ret = "Melee";
			break;
		case 3:
			ret = "Projectile";
			break;
		case 4:
			ret = "Rebound";
			break;
		case 5:
			ret = "Recycle";
			break;
		case 6:
			ret = "Autopilot";
			break;
		case 7:
			ret = "Ricochet";
			break;
		case 8:
			ret = "Flow";
			break;
		case 9:
			ret = "Repeat";
			break;
		case 10:
			ret = "Recharge";
			break;
		case 11:
			ret = "Repeat";
			break;
		case 12:
			ret = "Flow";
			break;
		case 13:
			ret = "Chain";
			break;
		case 14:
			ret = "Chain";
		default:
			ret = "Boost";
			break;
		}
		return ret;
	}
	
	public void setUpStates(){
		super.setUpStates();
		setState("Relationship","Stranger");
		setState("Grunts","didn't");
		setState("Affiliation","Enemy");
		setState("Gruntcount","0");
	}
	
	public void buildBody() {
		parts.add(part = new SegmentCircle(board,this,x,y,radius,0));
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
