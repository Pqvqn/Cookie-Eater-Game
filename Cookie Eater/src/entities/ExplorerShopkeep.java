package entities;

import java.awt.*;

import ce3.*;
import cookies.*;
import levels.*;
import menus.*;

public class ExplorerShopkeep extends Explorer{
	
	private SegmentCircle part;
	
	/*STATES:
	 * Relationship: Stranger, Enemy, Lover, Friend 
	 * 
	 * 
	 * 
	 */
	
	public ExplorerShopkeep(Board frame, int cycletime) {
		super(frame,cycletime);
		chooseResidence();
		radius = 40;
		min_cat = 3;
		max_cat = 8;
		mass = 400;
		tester = new SegmentCircle(board,this,x,y,radius*2,0);
		input_speed = 30;
		start_shields = 1;
		setShields(start_shields);
		state = VENDOR;
	}
	public String getName() {return "Shopkeeper";}
	public void runEnds() {
		super.runEnds();
		for(int i=0; i<Math.random()*4-1; i++) {
			removeRandomly();
		}
		for(int i=0; i<Math.random()*4-1 || to_sell.size()<min_cat; i++) {
			double choose = Math.random()*5;
			if(choose<=2) {
				addRandomly(new CookieShield(board,0,0,15));
			}else if(choose<=3) {
				addRandomly(new CookieItem(board,0,0,Level.generateItem(board,"Circle"),30));
			}else if(choose<=3.5) {
				addRandomly(new CookieItem(board,0,0,Level.generateItem(board,"Shield"),30));
			}else {
				addRandomly(new CookieItem(board,0,0,Level.generateItem(board,"Field"),30));
			}

		}
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
			double choose = Math.random()*5;
			if(choose<=2) {
				addRandomly(new CookieShield(board,0,0,15));
			}else if(choose<=3) {
				addRandomly(new CookieItem(board,0,0,Level.generateItem(board,"Circle"),30));
			}else if(choose<=3.5) {
				addRandomly(new CookieItem(board,0,0,Level.generateItem(board,"Shield"),30));
			}else {
				addRandomly(new CookieItem(board,0,0,Level.generateItem(board,"Field"),30));
			}

		}
	}
	
	public void setUpStates(){
		super.setUpStates();
		setState("Relationship","Stranger");
	}
	
	public void buildBody() {
		parts.add(part = new SegmentCircle(board,this,x,y,radius,0));
	}
	public void orientParts() {
		part.setLocation(x,y);
		part.setSize(radius);
		tester.setSize(radius*2);
		tester.setExtraSize(radius*2);
	}

	public void paint(Graphics g) {
		super.paint(g);
	}
}
