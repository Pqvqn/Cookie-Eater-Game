package entities;

import java.awt.*;
import java.util.*;

import ce3.*;
import cookies.*;
import levels.*;
import menus.*;
import items.*;

public class ExplorerTutor extends Explorer{
	
	private SegmentCircle part;
	/*STATES:
	 * Name: None, Bart, Boy, etc.
	 * Relationship: Stranger, Helper, Dislike, Partners, Friend
	 * Pay: None, Split, Keep, Ready, Jest
	 * MyCash: *double*
	 * PlayerCash: *double*
	 * ToSell: *CookieItem*
	 * AskPrice: *double*
	 * CanSell: Yes, No
	 */
	
	/*FUNCTIONS:
	 * Give: dollars
	 * Take: dollars
	 * SellItem: Item, Price
	 * CheckAfford: AskPrice
	 */
	
	public ExplorerTutor(Game frame, Board gameboard, int cycletime) {
		super(frame,gameboard,cycletime);
		radius = 40;
		min_cat = 3;
		max_cat = 8;
		mass = 200;
		tester = new SegmentCircle(board,this,x,y,radius*2,0,"test");
		input_speed = 30;
		startShields = 3;
		setShields(startShields);
		state = VENTURE;
	}
	public ExplorerTutor(Game frame, Board gameboard, SaveData sd, int cycle) {
		super(frame,gameboard,sd,cycle);
		for(Segment testPart : parts){
			if(testPart.name.equals("body")) {
				part = (SegmentCircle)testPart;
			}
		}
		tester = new SegmentCircle(board,this,x,y,radius*2,0,"test");
	}
	public String getName() {return "Tutor";}
	public void runEnds() {
		super.runEnds();
		state = VENTURE;
		setState("Pay","None");
		setState("MyCash","0");
		setState("PlayerCash","0");
		setState("ToSell","");
		setState("AskPrice","0");
		setState("CanSell","No");
		//if partners, become friends on run end
		if(stateIs("Relationship","Partners")) {
			setState("Relationship","Friends");
		}

		/*if(Math.random()>1) { //.2
			for(int i=0; i<Math.random()*2+1; i++) {
				residence = residence.getNext();
			}
		}else {
			kill();
		}*/
		chooseResidence();
	}
	public void runUpdate() {
		super.runUpdate();
		if(state != VENTURE) {
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
		if(( state == STOP || state == STAND ) && stateIs("Relationship","Partners")) { //if partners
			state = STAND; //ready self to venture
			if(stateIs("Pay","Split")){ //if splitting, track baseline cash
				setState("MyCash",""+getCash());
				setState("PlayerCash",""+board.player().cash);
			}
		}
	}
	
	public void levelComplete() {
		super.levelComplete();
		//run when entering store
		if(residence instanceof Store) {
			//if partnering and reach store, stop venturing
			if(stateIs("Relationship","Partners")) {
				if(stateIs("Pay","Split")) { //if splitting, ready self for split and calculate how much
					setState("Pay","Ready");
					setState("MyCash",""+(getCash()-Double.parseDouble(getState("MyCash")))/2);
					setState("PlayerCash",""+(board.player().cash-Double.parseDouble(getState("PlayerCash")))/2);
				}
				setState("Relationship","Friends");
				state = STOP;
			}
			
			if(!item_stash.isEmpty() && (stateIs("Relationship","Partners") || stateIs("Relationship","Friends"))) {
				for(int i=0; i<item_stash.size(); i++) {
					if(!item_stash.get(i).isEmpty()) {
						int randomIndex = (int)(Math.random()*item_stash.get(i).size());
						CookieItem c = item_stash.get(i).get(randomIndex);
						setState("ToSell",c.getItem().getName());
						setState("AskPrice",""+((int)(Math.random()*20)*.5+30));
						setState("Selling","Not");
						break;
					}
				}
			}
		}
	}
	
	public void chooseDir() {
		tester.setSize(radius*2);
		double[] xs = new double[4];
		double[] ys = new double[4];
		//ArrayList<Integer> dos = new ArrayList<Integer>();dos.add(0);dos.add(1);dos.add(2);dos.add(3);
		int[] dos = {0,0,0,0}; //weight of quality for each dir choice
		for(int i=0; i<4; i++) {
			testDirection(i);
			xs[i] = tester.getCenterX();
			ys[i] = tester.getCenterY();
			//bees[i][0] = xs[i];
			//bees[i][1] = ys[i];
			yeehaw = false;
			//for(Wall w:board.walls) { //if tester hits a wall, rule this direction out
				if(tester.collidesWithArea(false,board.wallSpace)
						|| xs[i]<0 || xs[i]>board.currLevel.x_resol || ys[i]<0 || ys[i]>board.currLevel.y_resol) {
					//[i][0] = tester.rectHitPoint(false, w.getX(), w.getY(), w.getW(), w.getH(),0)[0];
					//yees[i][1] = tester.rectHitPoint(false, w.getX(), w.getY(), w.getW(), w.getH(),0)[1];
					dos[i] += -500;
					yeehaw = true;
				}
			//}
				ArrayList<Cookie> testCookies = board.currLevel.chunker.surroundingChunk(tester.getCenterX(),tester.getCenterY()).getCookies();
				for(int j=0; j<testCookies.size(); j++) { //if tester hits a cookie, prioritize this direction
					Cookie c = testCookies.get(j);
					if(tester.collidesWithBounds(false,c.getBounds()) && tester.collidesWithArea(false,c.getArea())){
						dos[i] += 1;
					}
				}
		}
		int bigind = direction; //most preferred direction (largest weight)
		int nearind = direction; //nearest cookie direction
		if(bigind<0 || bigind>=dos.length) {bigind = 0;nearind=0;}
		dos[nearind]+=10;
		for(int i=0; i<dos.length; i++) {
			Cookie near = nearestCookie((int)(xs[i]-x), (int)(ys[i]-y));
			Cookie nearb = nearestCookie((int)(xs[nearind]-x), (int)(ys[nearind]-y));
			if(near!=null && Level.lineLength(near.getX(),near.getY(),xs[i],ys[i]) < Level.lineLength(nearb.getX(),nearb.getY(),xs[nearind],ys[nearind])) {
				dos[nearind]-=10;
				dos[i]+=10;
				nearind = i;
			}
			if(dos[i]>dos[bigind]) {
				bigind = i;
			}
		}
		direction = bigind;
		/*if(dos.isEmpty()) {
			
		}else {
			direction = dos.get((int)(Math.random()*dos.size()));
		}*/
		tester.setLocation(xs[direction],ys[direction]);
	}
	boolean yeehaw;
	//double[][] bees = {{0,0},{0,0},{0,0},{0,0}};
	//double[][] yees = {{0,0},{0,0},{0,0},{0,0}};
	public int doSpecial() {
		return 0;
	}
	public void doFunction(String f, String[] args) {
		super.doFunction(f,args);
		switch(f) {

		case "SellItem": //sell cookie item to player {item, price}
			//find correct item cookie
			for(int i=0; i<item_stash.size(); i++) {
				for(int j=0; j<item_stash.get(i).size(); j++) {
					CookieItem c = item_stash.get(i).get(j);
					if(c.getItem().getName().equals(args[0])){
						//sell the cookie
						c.setVendor(this);
						c.setPrice(Double.parseDouble(args[1]));
						c.purchase(board.player());
						board.player().giveCookie(c);
						removeItem(i,c); //this could be problematic
						i=item_stash.size();
						break;
					}
				}
			}
			break;
		
		case "CheckAfford": //test if player can afford price {price}
			setState("CanSell",(Double.parseDouble(args[0])<=board.player().getCash())?"Yes":"No");
			break;
		}
	}
	public void chooseResidence() {
		if(!board.stores.isEmpty() && board.currDungeon==2)residence = board.stores.get(board.floors.get(0).getID());
	}
	public void setConvo() {
		convo = new Conversation(board,this,"Recruit1","begin");
		convo.setDisplayed(false);
	}
	public void setUpStates(){
		super.setUpStates();
		setState("Relationship","Stranger");
		setState("Name","None");
		setState("Pay","None");
		setState("PlayerCash","0");
		setState("MyCash","0");
		setState("ToSell","");
		setState("AskPrice","0");
		setState("Selling","Not");
	}
	
	public void createStash() {
		super.createStash();
		giveCookie(new CookieItem(game,board,null,0,0,Item.generateItem(game,board,"Ghost"),0));
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
	public Cookie choosePurchase(ArrayList<Cookie> options) {
		if(stateIs("Pay","Ready") || (Double.parseDouble(getState("MyCash"))>0))return null;
		int highestweight = 4;
		int highestindex = -1;
		for(int i=0; i<options.size(); i++) {
			Cookie c = options.get(i);
			int weight = 0;
			if(c instanceof CookieShield)weight+=7-shield_stash.size();
			if(c instanceof CookieItem)weight+=6;
			if(c instanceof CookieStore && ((CookieStore)c).price()/getCash() > .7)weight-=2;
			if(c instanceof CookieStore && ((CookieStore)c).price() > getCash())weight=0;
			if(weight>highestweight) {
				highestweight = weight;
				highestindex = i;
			}
		}
		return (highestindex>=0)?options.get(highestindex):null;
	}
	public void paint(Graphics g) {
		super.paint(g);
		//debug tracker display stuff
		/*g.setColor(Color.WHITE);
		if(yeehaw) {
			g.setColor(Color.MAGENTA);
			
		}
		g.drawOval((int)(.5+tester.getCenterX()-((SegmentCircle)tester).getRadius()), (int)(.5+tester.getCenterY()-((SegmentCircle)tester).getRadius()), (int)(.5+((SegmentCircle)tester).getRadius()*2), (int)(.5+((SegmentCircle)tester).getRadius()*2));
		for(int i=0; i<4; i++) {
			if(i==direction) {
				g.setColor(Color.MAGENTA);
			}else {
				g.setColor(Color.WHITE);
			}
			g.drawOval((int)(.5+yees[i][0])-4, (int)(.5+yees[i][1])-4, 8, 8);
			g.drawOval((int)(.5+bees[i][0]-((SegmentCircle)tester).getRadius()), (int)(.5+bees[i][1]-((SegmentCircle)tester).getRadius()), (int)(.5+((SegmentCircle)tester).getRadius()*2), (int)(.5+((SegmentCircle)tester).getRadius()*2));
		}*/
	}
}
