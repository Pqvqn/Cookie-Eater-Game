package levels;


import ce3.*;
import java.util.*;

public class Floor3 extends Level{
	
	private ArrayList<int[]> nodes;
	private ArrayList<int[]> lines;
	private int nodeRad; //radius around nodes where no walls exist
	private int lineRad; //radius around line nodes where no walls exist
	private int lineDiv; //number of nodes on lines between nodes
	private int wallCutoff; //min wall size
	private final int[][] AREAS = {{0,board.X_RESOL/2,0,board.Y_RESOL/2}, //regions of board - one node per until all filled
			{0,board.X_RESOL/2,board.Y_RESOL/2,board.Y_RESOL},
			{board.X_RESOL/2,board.X_RESOL,0,board.Y_RESOL/2},
			{board.X_RESOL/2,board.X_RESOL,board.Y_RESOL/2,board.Y_RESOL}};
	
	public Floor3(Board frame, Level nextFloor) {
		super(frame, nextFloor);
		next = nextFloor;
		scale = .9;
		board = frame;
		minDecay = 60;
		maxDecay = 3600;
		nodes = new ArrayList<int[]>();
		lines = new ArrayList<int[]>();
		nodeRad = 150;
		lineRad = 100;
		lineDiv = 10;
		wallCutoff = 50;
	}
	
	public void build() {
		super.build();
		genPaths(4);
		genWalls(200);
		nodes = new ArrayList<int[]>();
		lines = new ArrayList<int[]>();
	}
	//creates nodes and connections
	public void genPaths(int num) {
		nodes.add(new int[] {(int)startx,(int)starty,nodeRad}); //add start area to nodes
		ArrayList<int[]> ranges = new ArrayList<int[]>(); //put ranges into list
		for(int i=0; i<AREAS.length; i++) {
			ranges.add(AREAS[i]);
		}
		for(int i=1; i<=num; i++) { //make num of extra nodes
			if(i<=AREAS.length) { //if some region empty
				int[] ra = ranges.remove((int)(Math.random()*ranges.size())); //choose region
				nodes.add(new int[] {(int)(Math.random()*(ra[1]-ra[0])+ra[0]),(int)(Math.random()*(ra[3]-ra[2])+ra[2]),nodeRad}); //add randomly in region
			}else{
				nodes.add(new int[] {(int)(Math.random()*board.X_RESOL),(int)(Math.random()*board.Y_RESOL),nodeRad}); //add random node
			}
			int c = (int)(Math.random()*(nodes.size()-1)); //choose random existing node
			lines.add(new int[] {nodes.get(i)[0],nodes.get(i)[1],nodes.get(c)[0],nodes.get(c)[1]});
			//splitLine(lineRad,nodes.get(c)[0],nodes.get(c)[1],nodes.get(i)[0],nodes.get(i)[1]);//make two lines for path
		}
		for(int[] b : lines) { //add lineDiv number of nodes along lines
			double diffX = b[0]-b[2], diffY = b[1]-b[3];
			double currX = b[2], currY=b[3];
			for(int i=0; i<lineDiv; i++) {
				currX+=diffX/lineDiv;
				currY+=diffY/lineDiv;
				nodes.add(new int[] {(int)(currX+.5),(int)(currY+.5),lineRad});
			}
		}
	}
	
	
	//places walls that don't touch paths or nodes
	public void genWalls(int num) {
		for(int i=0; i<num; i++) { //make num of walls
			int cX = (int)(Math.random()*board.X_RESOL), cY = (int)(Math.random()*board.Y_RESOL); //choose wall center
			int x=cX,y=cY,w=1,h=1;
			if(rectOK(x,y,w,h)) { //if center is valid
				
				while(rectOK(x,y,w,h)) { //move corner until it cant be moved
					x--;h++;
				}
				x+=10;h-=10;
				while(rectOK(x,y,w,h)) {
					x--;y--;
				}
				x+=10;y+=10;
				while(rectOK(x,y,w,h)) {
					w++;y--;
				}
				w-=10;y+=10;
				while(rectOK(x,y,w,h)) {
					w++;h++;
				}
				w-=10;h-=10;
				if(h>=wallCutoff && w>=wallCutoff) //remove small walls
					board.walls.add(new Wall(board,x,y,w,h));
			}
		}
	}  
	
	//are any rectangle corners colliding with nodes
	public boolean rectOK(int x, int y, int w, int h) {
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
		return true;
	}
	
	
	public void placeCookies() {
		super.placeCookies(5,(int)(100*scale));
	}

}
