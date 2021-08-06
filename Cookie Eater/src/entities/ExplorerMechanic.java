package entities;

import java.awt.*;
import java.util.*;

import ce3.*;
import cookies.*;
import levels.*;
import menus.*;

public class ExplorerMechanic extends Explorer{
	
	private SegmentCircle part;
	
	/*STATES:
	 * Pickups: none, *list, separated by comma*
	 * HasPUP: yes, no
	 * InstallPrice: *double*
	 */
	
	/*FUNCTIONS:
	 * Give: dollars
	 * Take: dollars
	 * Display: type = [Pups, Stats]
	 * Save
	 */
	
	public ExplorerMechanic(Game frame, Board gameboard, int cycletime) {
		super(frame,gameboard,cycletime);
		radius = 40;
		min_cat = 3;
		max_cat = 3;
		mass = 400;
		tester = new SegmentCircle(board,this,x,y,radius*2,0,"test");
		input_speed = 30;
		startShields = 1;
		setShields(startShields);
		state = MECHANIC;
	}
	public ExplorerMechanic(Game frame, Board gameboard, SaveData sd, int cycle) {
		super(frame,gameboard,sd,cycle);
		for(Segment testPart : parts){
			if(testPart.name.equals("body")) {
				part = (SegmentCircle)testPart;
			}
		}
		tester = new SegmentCircle(board,this,x,y,radius*2,0,"test");
	}
	public String getName() {return "Mechanic";}
	public void runEnds() {
		super.runEnds();
		//when player dies, reset to beginning
		chooseResidence();
		createStash(); //reset stat cookies for new run
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
		
		//nab all player pickups
		ArrayList<CookieItem> pickups = board.player().getPickups();
		while(!(pickups.isEmpty())) {
			CookieItem c = pickups.remove(0);
			to_sell.add(c);
			c.setVendor(this);
			c.setPrice(Double.parseDouble(getState("InstallPrice"))); //set new price for install
			if(getState("Pickups").equals("none")){
				setState("Pickups",c.getItem().getName());
			}else {
				setState("Pickups",getState("Pickups")+", "+c.getItem().getName());
			}
			setState("HasPUP","yes");
		}
		
	}
 	public void setConvo() {
		convo = new Conversation(board,this,"Mechanic1","pup");
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
		residence = chooseFloor(convertToMap(levels,weights), 100, true, 1);
	}

	public void levelComplete() {
		super.levelComplete();
		//if player completes store, move to next store
		if(board.currLevel instanceof Store) {
			/*do{
				residence = residence.getPassages().get(0).getExit();
			}while(!(residence instanceof Store) && residence.getPassages().size()>0);*/
			createStash(); //reset stat cookies for each store
			setState("Pickups","none");
			setState("HasPUP","no");
		}
	}
	
	public void createStash() {
		super.createStash();
		//stash contains 3 random stat changing cookies each time
		on_display = new ArrayList<CookieStore>();
		for(int i=0; i<4; i++) {
			to_sell.add(0,new CookieStat(game,board,0,0));
		}
	}
	
	public void setUpStates(){
		super.setUpStates();
		setState("Pickups","none");
		setState("HasPUP","no");
		setState("InstallPrice","10.0");
	}
	
	public void doFunction(String f, String[] args) {
		super.doFunction(f,args);
		switch(f) {

		case "Display": //put part of stash out to sell {type}
			packUp();
			ArrayList<CookieStore> storage = new ArrayList<CookieStore>(); //not displayed cookies
			switch(args[0]) {
			case "Stats": //switch non-stats to back
				if(!to_sell.isEmpty()) {
					for(int i=to_sell.size()-1; i>=0; i--) {
						if(!(to_sell.get(i) instanceof CookieStat)) {
							storage.add(to_sell.remove(i));
						}
					}
				}
				break;
			case "Pups": //switch stats to back
				if(!to_sell.isEmpty()) {
					for(int i=to_sell.size()-1; i>=0; i--) {
						if(to_sell.get(i) instanceof CookieStat) {
							storage.add(to_sell.remove(i));
						}
					}
				}
				break;
			}
			
			sellWares(shop_spots);
			
			while(!storage.isEmpty()) {
				to_sell.add(storage.remove(0));
			}
			break;
		case "Save": //save game file
			game.board.createSave();
			break;
		case "Exit": //exit game window
			System.exit(0);
			break;
		}
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
