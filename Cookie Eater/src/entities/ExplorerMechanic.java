package entities;

import java.awt.*;

import ce3.*;
import cookies.*;
import levels.*;
import menus.*;

public class ExplorerMechanic extends Explorer{
	
	private SegmentCircle part;
	
	/*STATES:
	 *
	 */
	
	/*FUNCTIONS:
	 * Give: Give $
	 * Take: Take $
	 */
	
	public ExplorerMechanic(Board frame, int cycletime) {
		super(frame,cycletime);
		chooseResidence();
		radius = 40;
		min_cat = 3;
		max_cat = 3;
		mass = 400;
		tester = new SegmentCircle(board,this,x,y,radius*2,0);
		input_speed = 30;
		start_shields = 1;
		setShields(start_shields);
		state = VENDOR;
	}
	public String getName() {return "Mechanic";}
	public void runEnds() {
		super.runEnds();
		//when player dies, reset to beginning
		chooseResidence();
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
		//convo = new Conversation(board,this,"TestSpeech5","here");
		//convo.setDisplayed(false);
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

	public void levelComplete() {
		super.levelComplete();
		//if player completes store, move to next store
		if(board.currFloor instanceof Store) {
			do{
				residence = residence.getNext();
			}while(!(residence instanceof Store) && residence.getNext()!=null);
			createStash(); //reset stat cookies for each store
		}
	}
	
	public void createStash() {
		super.createStash();
		//stash contains 3 random stat changing cookies each time
		for(int i=0; i<3; i++) {
			addRandomly(new CookieStat(board,0,0));
		}
	}
	
	public void setUpStates(){
		super.setUpStates();
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
