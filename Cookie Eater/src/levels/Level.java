package levels;


import ce3.*;

public abstract class Level{
	
	protected double scale; //zoom in/out of level
	protected Level next; //level to move to once completed
	protected Board board;
	protected double startx; //player start pos.
	protected double starty;
	protected int minDecay; //frames for cookie at edge corner to decay fully
	protected int maxDecay; //frames for cookie at center to decay fully
	
	public Level(Board frame, Level nextFloor) {
		next = nextFloor;
		scale = 1;
		board = frame;
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
				currCookie.kill();
				board.score--;
				cooks--;
				i--;
			}
		}
		
		board.scoreToWin = cooks;
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
	
	
	//gives length of line rom start/end points
	public static double lineLength(int x1, int y1, int x2, int y2) {
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
	public static boolean collidesLineAndRect(int x1, int y1, int x2, int y2, int rX, int rY, int rW, int rH) {
		return collidesLineAndLine(x1,y1,x2,y2,rX,rY,rX+rW,rY) || //top
				collidesLineAndLine(x1,y1,x2,y2,rX,rY,rX,rY+rH) || //left
				collidesLineAndLine(x1,y1,x2,y2,rX,rY+rH,rX+rW,rY+rH) || //bottom
				collidesLineAndLine(x1,y1,x2,y2,rX+rW,rY,rX+rW,rY+rH) || //right
				(x1>=rX && x1<=rX+rW && y1>=rY && y1<=rY+rH); //both points inside rectangle
	}
	
	//tests if two lines intersect
	public static boolean collidesLineAndLine(int x1a, int y1a, int x2a, int y2a, int x1b, int y1b, int x2b, int y2b) {
		
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
}
