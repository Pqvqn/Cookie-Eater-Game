package levels;


import ce3.*;

public class Floor2 extends Level{

	//public double scale;
	private Level next;
	private Board board;
	//public double startx;
	//public double starty;
	
	
	public Floor2(Board frame, Level nextFloor) {
		super(frame, nextFloor);
		next = nextFloor;
		scale = .95;
		board = frame;
	}
	
	public void build() {
		super.build();
		genWalls(2);
		
	}
	public void genWalls(int num) {
		for(int i=0; i<num; i++) {
			int x=-1,y=-1,w=-1,h=-1;
			while(x<0 || collidesCircleAndRect((int)(startx+.5),(int)(starty+.5),board.player.getRadius()*3,(int)(x-.5*w+.5),(int)(y-.5*h+.5),w,h)) {
				x=(int)(Math.random()*board.X_RESOL);
				y=(int)(Math.random()*board.Y_RESOL);
				w=(int)(Math.random()*300)+200;
				h=(int)(Math.random()*300)+200;
			}
			board.walls.add(new Wall(board,(int)(x-.5*w+.5),(int)(y-.5*h+.5),w,h));
		}
	}
	public void placeCookies() {
		super.placeCookies(50,120);
	}

}
