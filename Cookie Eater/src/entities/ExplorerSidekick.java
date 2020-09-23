package entities;

import java.awt.*;
import java.util.ArrayList;

import ce3.*;
import cookies.*;
import levels.*;
import menus.Conversation;

public class ExplorerSidekick extends Explorer{
	
	private SegmentCircle part;
	/*STATES:
	 * Name: None, Bart, Boy, etc.
	 * Relationship: Stranger, Helper, Dislike, Partners
	 * Pay: None, Split, Keep, Ready, Jest
	 * MyCash: *double*
	 * PlayerCash: *double*
	 */
	
	public ExplorerSidekick(Board frame, int cycletime) {
		super(frame,cycletime);
		chooseResidence();
		radius = 40;
		min_cat = 3;
		max_cat = 8;
		mass = 200;
		tester = new SegmentCircle(board,this,x,y,radius*2,0);
		input_speed = 30;
		start_shields = 50;
		setShields(start_shields);
		state = VENTURE;
	}
	public String getName() {return "Sidekick";}
	public void runEnds() {
		super.runEnds();
		state = VENTURE;
		setState("Pay","None");
		setState("MyCash","0");
		setState("PlayerCash","0");
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
		if(state == STOP && stateIs("Relationship","Partners")) {
			state = STAND;
		}
		if(state != VENTURE) {
			if(stateIs("Pay","Split") && (Double.parseDouble(getState("MyCash"))>0 || Double.parseDouble(getState("PlayerCash"))>0)) { //if splitting money; ready self for swap and calculate how much must be swapped
				setState("Pay","Ready");
				setState("MyCash",""+(getCash()-Double.parseDouble(getState("MyCash"))/2));
				setState("PlayerCash",""+(board.player.cash-Double.parseDouble(getState("PlayerCash"))/2));
			}
			if(convo!=null && speaking<=0 && Level.lineLength(board.player.getX(), board.player.getY(), x, y)<150) {
				speak(convo);
				speaking++;
			}
			if(speaking>0 && speaking++>1000/board.getAdjustedCycle() && Level.lineLength(board.player.getX(), board.player.getY(), x, y)>=150) {
				speak(null);
				speaking = 0;
			}
			if(convo!=null)convo.test();
			if(stateIs("Pay","Split") && (Double.parseDouble(getState("MyCash"))==0 && Double.parseDouble(getState("PlayerCash"))==0)) {
				setState("MyCash",""+getCash());
				setState("PlayerCash",""+board.player.cash);
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
						|| xs[i]<0 || xs[i]>board.X_RESOL || ys[i]<0 || ys[i]>board.Y_RESOL) {
					//[i][0] = tester.rectHitPoint(false, w.getX(), w.getY(), w.getW(), w.getH(),0)[0];
					//yees[i][1] = tester.rectHitPoint(false, w.getX(), w.getY(), w.getW(), w.getH(),0)[1];
					dos[i] += -100;
					yeehaw = true;
				}
			//}
			for(int j=0; j<board.cookies.size(); j++) { //if tester hits a cookie, prioritize this direction
				Cookie c = board.cookies.get(j);
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
			Cookie near = board.nearestCookie(xs[i], ys[i]);
			Cookie nearb = board.nearestCookie(xs[nearind], ys[nearind]);
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
	public void chooseResidence() {
		residence = findFloor("Descending Labyrinths",true,0,2);
		//residence = findFloor("Forest Entrance",false,0,0);
	}
	public void setConvo() {
		convo = new Conversation(board,this,"Recruit1","greet");
		convo.setDisplayed(false);
	}
	public void setUpStates(){
		super.setUpStates();
		setState("Relationship","Stranger");
		setState("Name","None");
		setState("Pay","None");
		setState("PlayerCash","0");
		setState("MyCash","0");
	}
	public void createStash() {
		super.createStash();
		giveCookie(new CookieItem(board,0,0,Level.generateItem(board,"Ghost"),0));
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
	public Cookie choosePurchase(ArrayList<Cookie> options) {
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
