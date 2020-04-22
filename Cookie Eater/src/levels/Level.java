package levels;


import java.awt.*;
import java.util.ArrayList;

import ce3.*;
import cookies.*;
import items.*;

public abstract class Level{
	
	protected double scale; //zoom in/out of level
	protected Level next; //level to move to once completed
	protected Board board;
	protected double startx; //player start pos.
	protected double starty;
	protected int minDecay; //frames for cookie at edge corner to decay fully
	protected int maxDecay; //frames for cookie at center to decay fully
	protected Color bgColor;
	protected Color wallColor;
	protected int shieldCost;

	protected ArrayList<int[]> nodes;
	protected ArrayList<int[]> lines;
	
	public Level(Board frame) {
		this(frame,null);
	}
	
	public Level(Board frame, Level nextFloor) {
		next = nextFloor;
		scale = 1;
		board = frame;
		bgColor = Color.GRAY;
		wallColor = Color.red.darker();
	}
	
	//put walls in floor
	public void build() {
		startx = board.player.getX(); //start floor where last floor ended
		starty = board.player.getY();
		board.walls.add(new Wall(board,0,0,board.X_RESOL,board.BORDER_THICKNESS)); //add border walls
		board.walls.add(new Wall(board,0,0,board.BORDER_THICKNESS,board.Y_RESOL));
		board.walls.add(new Wall(board,0,board.Y_RESOL-board.BORDER_THICKNESS,board.X_RESOL,board.BORDER_THICKNESS));
		board.walls.add(new Wall(board,board.X_RESOL-board.BORDER_THICKNESS,0,board.BORDER_THICKNESS,board.Y_RESOL));
		
	}
	public void placeCookies() {
		placeCookies(100,100);
	}
	
	//put cookies in floor
	public void placeCookies(int clearance, int separation) { //clearance between cookies and walls, separation between cookies
		//place cookies so that none touch walls
		int cooks = 0; //count of cookies placed
		//vars for first/last cookie in line
		int xOrig = board.BORDER_THICKNESS+clearance+(int)(Cookie.DEFAULT_RADIUS*scale)+1, yOrig = board.BORDER_THICKNESS+clearance+(int)(Cookie.DEFAULT_RADIUS*scale)+1;
		int tY = 0, tX = 0;
		//adjust cookie grid to be centered
		for(tY = yOrig; tY<board.Y_RESOL-board.BORDER_THICKNESS-clearance; tY+=separation);
		for(tX = xOrig; tX<board.X_RESOL-board.BORDER_THICKNESS-clearance; tX+=separation);
		xOrig+=(board.X_RESOL-tX-xOrig)/2;
		yOrig+=(board.Y_RESOL-tY-yOrig)/2;
		for(int pY = yOrig; pY<board.Y_RESOL-board.BORDER_THICKNESS-clearance; pY+=separation) { //make grid of cookies
			for(int pX = xOrig; pX<board.X_RESOL-board.BORDER_THICKNESS-clearance; pX+=separation) {
				boolean place = true;
				for(Wall w : board.walls) { //only place if not too close to any walls
					if(collidesCircleAndRect(pX,pY,(int)(Cookie.DEFAULT_RADIUS*scale+clearance+.5),w.getX(),w.getY(),w.getW(),w.getH())) 
						place = false;
					
				}
				if(place) { //place cookies, increment count
					board.cookies.add(new Cookie(board,pX,pY));
					cooks++;
				}
			}
		}
		//remove cookies that player can't access
		for(int i=0; i<board.cookies.size(); i++) {
			Cookie currCookie = board.cookies.get(i);
			if(splitSight((int)(board.player.getRadius()*scale*1.5),currCookie.getX(),currCookie.getY(),board.player.getX(),board.player.getY())) {
				currCookie.setAccess(true);
				
			}
			
		}
		boolean did = false;
		do{
			did = false;
			for(int i=0; i<board.cookies.size(); i++) {
				Cookie currCookie = board.cookies.get(i);
				if(!currCookie.getAccess()) {
					for(int j=0; j<board.cookies.size(); j++) {
						Cookie testCookie = board.cookies.get(j);
						if(testCookie.getAccess() && splitSight((int)(board.player.getRadius()*scale*1.5),currCookie.getX(),currCookie.getY(),testCookie.getX(),testCookie.getY())) {
							currCookie.setAccess(true);
							did=true;
							j=board.cookies.size();
						}	
					}
				}
			}
		}while(did);
		for(int i=0; i<board.cookies.size(); i++) {
			Cookie currCookie = board.cookies.get(i);
			if(!currCookie.getAccess()) {
				currCookie.kill(false);
				cooks--;
				i--;
			}
		}
		
		board.player.setScoreToWin(cooks);
	}
	//puts cookie item on board
	protected void placeItem(int x, int y, String i, double p, Color c) {
		Item b;
		switch(i) {
		case "Boost":
			b = new ItemBoost(board);
			break;
		case "Bounce":
			b = new ItemBounce(board);
			break;
		case "Circle":
			b = new ItemCircle(board);
			break;
		case "Chain":
			b = new ItemCookieChain(board);
			break;
		case "Field":
			b = new ItemField(board);
			break;
		case "Hold":
			b = new ItemHold(board);
			break;
		case "Recycle":
			b = new ItemRecycle(board);
			break;
		case "Shield":
			b = new ItemShield(board);
			break;
		case "Slowmo":
			b = new ItemSlowmo(board);
			break;
		case "Ghost":
			b = new ItemGhost(board);
			break;
		case "Return":
			b = new ItemReturn(board);
			break;
		case "Teleport":
			b = new ItemTeleport(board);
			break;
		case "Jab":
			b = new ItemJab(board);
			break;
		case "Repeat":
			b = new ItemRepeat(board);
			break;
		default:
			b = null;
		}
		board.cookies.add(new CookieItem(board, x, y, b, p, c));
	}
	protected void configureCatalogue(double def, ArrayList<String> I,ArrayList<Double> P,ArrayList<Color> C) {	
		addToCatalogue(I,"Boost",P,def*1,C,new Color(200,200,30));
		addToCatalogue(I,"Bounce",P,def*.9,C,new Color(0,150,200));
		addToCatalogue(I,"Circle",P,def*1,C,new Color(220, 170, 70));
		addToCatalogue(I,"Chain",P,def*1.1,C,new Color(150,80,20));
		addToCatalogue(I,"Field",P,def*1.1,C,new Color(90,30,170));
		addToCatalogue(I,"Hold",P,def*1.1,C,new Color(200,50,50));
		addToCatalogue(I,"Recycle",P,def*1.3,C,new Color(30,30,230));
		addToCatalogue(I,"Shield",P,def*1.2,C,new Color(20,170,180));
		addToCatalogue(I,"Slowmo",P,def*1.3,C,new Color(100,120,100));
		addToCatalogue(I,"Ghost",P,def*1.3,C,new Color(30,200,150));
		addToCatalogue(I,"Return",P,def*1.1,C,new Color(200,80,20));
		addToCatalogue(I,"Teleport",P,def*1.2,C,new Color(180,20,30));
		addToCatalogue(I,"Jab",P,def*1.1,C,new Color(180,180,190));
		addToCatalogue(I,"Repeat",P,def*1.2,C,new Color(50,30,30));
	}
	protected void addToCatalogue(ArrayList<String> I, String i, ArrayList<Double> P, double p, ArrayList<Color> C,Color c) {
		I.add(i);
		P.add(p);
		C.add(c);
	}
	public Level getNext() {
		return next;
	}
	public void setNext(Level newNext) {
		next = newNext;
	}
	public int getStartX() {return (int)(.5+startx);}
	public int getStartY() {return (int)(.5+starty);}
	public double getScale() {return scale;}
	public int getMinDecay() {return minDecay;}
	public int getMaxDecay() {return maxDecay;}
	public Color getBGColor() {return bgColor;}
	public Color getWallColor() {return wallColor;}
	public int getShieldCost() {return shieldCost;}
	public boolean haltEnabled() {return false;}
	public boolean specialsEnabled() {return true;} //if specials are allows
	
	//gives length of line rom start/end points
	public static double lineLength(double x1, double y1, double x2, double y2) {
		return Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));
	}
	
	//tests if a circle and a rectangle overlap
	public static boolean collidesCircleAndRect(int cX, int cY, int cR, int rX, int rY, int rW, int rH) {
		return (Math.abs(cX - rX) <= cR && cY>=rY && cY<=rY+rH) ||
				(Math.abs(cX - (rX+rW)) <= cR && cY>=rY && cY<=rY+rH)||
				(Math.abs(cY - rY) <= cR && cX>=rX && cX<=rX+rW) ||
				(Math.abs(cY - (rY+rH)) <= cR && cX>=rX && cX<=rX+rW) ||
				(Math.sqrt((cX-rX)*(cX-rX) + (cY-rY)*(cY-rY))<=cR) ||
				(Math.sqrt((cX-(rX+rW))*(cX-(rX+rW)) + (cY-rY)*(cY-rY))<=cR) ||
				(Math.sqrt((cX-rX)*(cX-rX) + (cY-(rY+rH))*(cY-(rY+rH)))<=cR) ||
				(Math.sqrt((cX-(rX+rW))*(cX-(rX+rW)) + (cY-(rY+rH))*(cY-(rY+rH)))<=cR) ||
				(cX >= rX && cX <= rX+rW && cY >= rY && cY <= rY+rH);
	}
	
	//tests if there is an unbroken line between two points
	public boolean lineOfSight(int x1, int y1, int x2, int y2) {
		boolean hit = false;
		for(Wall w : board.walls) {
			if(collidesLineAndRect(x1, y1, x2, y2, w.getX(), w.getY(), w.getW(), w.getH())) 
				hit = true;
		}
		return !hit;
	}
	
	//turns one line into two, one on each side of the circle instead of center
	public boolean splitSight(int rad, int x1, int y1, int x2, int y2) {
		double x = Math.abs(y1-y2);
		double y = Math.abs(x1-x2);
		double h = rad;
		double r = Math.sqrt((h*h)/(x*x+y*y));
		if(x1==x2 || ((double)y1-y2)/(x1-x2)<0) {
			return lineOfSight((int)(.5+x1-x*r), (int)(.5+y1-y*r), (int)(.5+x2-x*r), (int)(.5+y2-y*r)) && lineOfSight((int)(.5+x1+x*r), (int)(.5+y1+y*r), (int)(.5+x2+x*r), (int)(.5+y2+y*r)); 
		}else {
			return lineOfSight((int)(.5+x1+x*r), (int)(.5+y1-y*r), (int)(.5+x2+x*r), (int)(.5+y2-y*r)) && lineOfSight((int)(.5+x1-x*r), (int)(.5+y1+y*r), (int)(.5+x2-x*r), (int)(.5+y2+y*r)); 
		}
	}
	//tests if a line and a rectangle overlap
	public static boolean collidesLineAndRect(double x1, double y1, double x2, double y2, double rX, double rY, double rW, double rH) {
		return collidesLineAndLine(x1,y1,x2,y2,rX,rY,rX+rW,rY) || //top
				collidesLineAndLine(x1,y1,x2,y2,rX,rY,rX,rY+rH) || //left
				collidesLineAndLine(x1,y1,x2,y2,rX,rY+rH,rX+rW,rY+rH) || //bottom
				collidesLineAndLine(x1,y1,x2,y2,rX+rW,rY,rX+rW,rY+rH) || //right
				(x1>=rX && x1<=rX+rW && y1>=rY && y1<=rY+rH); //both points inside rectangle
	}
	//tests if a line and a circle overlap
	public static boolean collidesLineAndCircle(double x1, double y1, double x2, double y2, double cX, double cY, double cR) {
		double lM = (y2-y1)/(x2-x1);
		double dM = -1/lM;
		double dL = Math.sqrt(1+dM*dM);
		double ratio = cR/dL;
		double fX = ratio;
		double fY = ratio*dM;
		return collidesLineAndLine(x1,y1,x2,y2,cX+fX,cY+fY,cX-fX,cY-fY) || lineLength(x1,y1,cX,cY)<=cR || lineLength(x2,y2,cX,cY)<=cR;
	}
	//tests if two lines intersect
	public static boolean collidesLineAndLine(double x1a, double y1a, double x2a, double y2a, double x1b, double y1b, double x2b, double y2b) {
		
		if(x1a==x2a) { //vertical line
			if(x1a>=Math.min(x1b, x2b) && x1a<=Math.max(x1b, x2b)) { //if this line is in middle of other
				if(x1a==x1b && x1a==x2b) { //if both are vertical
					return (y1a>=Math.min(y1b, y2b) && y1a<=Math.max(y1b, y2b)) || //return if at least one point is in other line
							(y2a>=Math.min(y1b, y2b) && y2a<=Math.max(y1b, y2b)) ||
							(y1b>=Math.min(y1a, y2a) && y1b<=Math.max(y1a, y2a)) ||
							(y2b>=Math.min(y1a, y2a) && y2b<=Math.max(y1a, y2a));
				}else { //one vertical, one not
					double mb = (y1b-y2b)/(x1b-x2b); //slope
					double bb = y1b-mb*x1b; //yint
					double y = mb * x1a + bb; //intersection
					return (y>=Math.min(y1a, y2a) && y<=Math.max(y1a, y2a)); //if intersection is within line ends
				}
			}else {
				return false; //line outside of range
			}
		}
		if(x1b==x2b){
			if(x1b>=Math.min(x1a, x2a) && x1b<=Math.max(x1a, x2a)) {
				double ma = (y1a-y2a)/(x1a-x2a);
				double ba = y1a-ma*x1a;
				double y = ma * x1b + ba;
				return (y>=Math.min(y1b, y2b) && y<=Math.max(y1b, y2b));
			}else {
				return false;
			}
		}
		if(y1a==y2a){
			if(y1a>=Math.min(y1b, y2b) && y1a<=Math.max(y1b, y2b)) {
				if(y1a==y1b && y1a==y2b) {
					return (x1a>=Math.min(x1b, x2b) && x1a<=Math.max(x1b, x2b)) ||
							(x2a>=Math.min(x1b, x2b) && x2a<=Math.max(x1b, x2b)) ||
							(x1b>=Math.min(x1a, x2a) && x1b<=Math.max(x1a, x2a)) ||
							(x2b>=Math.min(x1a, x2a) && x2b<=Math.max(x1a, x2a));
				}else {
					double nb = (x1b-x2b)/(y1b-y2b);
					double db = x1b-nb*y1b;
					double x = nb * y1a + db;
					return (x>=Math.min(x1a, x2a) && x<=Math.max(x1a, x2a));
				}
			}else {
				return false;
			}
		}
		if(y1b==y2b){
			if(y1b>=Math.min(y1a, y2a) && y1b<=Math.max(y1a, y2a)) {
				double na = (x1a-x2a)/(y1a-y2a);
				double da = x1a-na*y1a;
				double x = na * y1b + da;
				return (x>=Math.min(x1b, x2b) && x<=Math.max(x1b, x2b));
			}else {
				return false;
			}
		}
		
		double ma = (y1a-y2a)/(x1a-x2a); //slope
		double na = (x1a-x2a)/(y1a-y2a); //inv slope
		double mb = (y1b-y2b)/(x1b-x2b);
		double nb = (x1b-x2b)/(y1b-y2b);
		double ba = y1a-ma*x1a; //yint
		double da = x1a-na*y1a; //xint
		double bb = y1b-mb*x1b;
		double db = x1b-nb*y1b;
		
		double x = (ba-bb)/(mb-ma); //intersection coords
		double y = (da-db)/(nb-na);
		
		return (x>=Math.min(x1a, x2a) && x<=Math.max(x1a, x2a)) &&
				(x>=Math.min(x1b, x2b) && x<=Math.max(x1b, x2b)) &&
				(y>=Math.min(y1a, y2a) && y<=Math.max(y1a, y2a)) &&
				(y>=Math.min(y1b, y2b) && y<=Math.max(y1b, y2b)); //intersections within bounds
		
	}
	//creates nodes and connections
		public void genPaths(int num, int nradmin, int nradmax, int lrad, int ldiv, int[][] areas) {
			nodes.add(new int[] {(int)startx,(int)starty,(int)(Math.random()*(nradmax-nradmin)+nradmin)}); //add start area to nodes
			ArrayList<int[]> ranges = new ArrayList<int[]>(); //put ranges into list
			for(int i=0; i<areas.length; i++) {
				ranges.add(areas[i]);
			}
			for(int i=1; i<=num; i++) { //make num of extra nodes
				if(i<=areas.length) { //if some region empty
					int[] ra = ranges.remove((int)(Math.random()*ranges.size())); //choose region
					nodes.add(new int[] {(int)(Math.random()*(ra[1]-ra[0])+ra[0]),(int)(Math.random()*(ra[3]-ra[2])+ra[2]),(int)(Math.random()*(nradmax-nradmin)+nradmin)}); //add randomly in region
				}else{
					nodes.add(new int[] {(int)(Math.random()*board.X_RESOL),(int)(Math.random()*board.Y_RESOL),(int)(Math.random()*(nradmax-nradmin)+nradmin)}); //add random node
				}
				int c = (int)(Math.random()*(nodes.size()-1)); //choose random existing node
				lines.add(new int[] {nodes.get(i)[0],nodes.get(i)[1],nodes.get(c)[0],nodes.get(c)[1]});
				//splitLine(lineRad,nodes.get(c)[0],nodes.get(c)[1],nodes.get(i)[0],nodes.get(i)[1]);//make two lines for path
			}
			for(int[] b : lines) { //add lineDiv number of nodes along lines
				double diffX = b[0]-b[2], diffY = b[1]-b[3];
				double currX = b[2], currY=b[3];
				for(int i=0; i<ldiv; i++) {
					currX+=diffX/ldiv;
					currY+=diffY/ldiv;
					nodes.add(new int[] {(int)(currX+.5),(int)(currY+.5),lrad});
				}
			}
		}
		
		
		//places walls that don't touch paths or nodes
		public void genWalls(int sep, int min, int max) {
			//for(int i=0; i<num; i++) { //make num of walls
			for(int i=board.BORDER_THICKNESS; i<board.Y_RESOL; i+=sep) {
				for(int j=board.BORDER_THICKNESS; j<board.X_RESOL; j+=sep) {
					//int cX = (int)(Math.random()*board.X_RESOL), cY = (int)(Math.random()*board.Y_RESOL); //choose wall center
					int x=j,y=i,w=1,h=1;
					if(rectOK(x,y,w,h,max)) { //if center is valid
					
						while(rectOK(x,y,w,h,max)) { //move corner until it cant be moved
							x--;h++;
						}
						x+=10;h-=10;
						while(rectOK(x,y,w,h,max)) {
							x--;y--;
						}
						x+=10;y+=10;
						while(rectOK(x,y,w,h,max)) {
							w++;y--;
						}
						w-=10;y+=10;
						while(rectOK(x,y,w,h,max)) {
							w++;h++;
						}
						w-=10;h-=10;
						while(rectOK(x,y,w,h,max)) { //move side until it cant be moved
							x--;
						}
						x++;
						while(rectOK(x,y,w,h,max)) {
							y--;
						}
						y++;
						while(rectOK(x,y,w,h,max)) {
							w++;
						}
						w--;
						while(rectOK(x,y,w,h,max)) {
							h++;
						}
						h--;
						if(h>=min && w>=min) //remove small walls
							board.walls.add(new Wall(board,x,y,w,h));
					}
				}
			}
		}  
		
		//are any rectangle corners colliding with nodes
		public boolean rectOK(int x, int y, int w, int h, int max) {
			if(w>max || h>max) {
				return false; //false if wall too big
			}
			if(x<=0 || x+w>=board.X_RESOL || y<=0 || y+h>=board.Y_RESOL) {
				return false; //false if some point is outside board
			}
			for(int[] node : nodes) {
				if(collidesCircleAndRect((int)(node[0]+.5),(int)(node[1]+.5),node[2],x,y,w,h)) {
					return false; //false if wall hits node with side
				}
				if((lineLength(node[0],node[1],x,y)<node[2] || lineLength(node[0],node[1],x+w,y)<node[2]) ||
				lineLength(node[0],node[1],x,y+h)<node[2] || lineLength(node[0],node[1],x+w,y+h)<node[2]) {
					return false; //false if any edge is in a node radius
				}
			}
			for(Wall wl : board.walls) {
				int num = 0;
				if( (x>wl.getX()+10 && x<wl.getX()+wl.getW()-10) && (y>wl.getY()+10 && y<wl.getY()+wl.getH()-10))num++;
				if( (y>wl.getY()+10 && y<wl.getY()+wl.getH()-10) && (x+w>wl.getX()+10 && x+w<wl.getX()+wl.getW()-10))num++;
				if( (x+w>wl.getX()+10 && x+w<wl.getX()+wl.getW()-10) && (y+h>wl.getY()+10 && y+h<wl.getY()+wl.getH()-10))num++;
				if( (y+h>wl.getY()+10 && y+h<wl.getY()+wl.getH()-10) && (x>wl.getX()+10 && x<wl.getX()+wl.getW()-10))num++;
				if(num>=1) 
					return false; //false if at least 3 corners are within another wall
			}
			return true;
		}
}
