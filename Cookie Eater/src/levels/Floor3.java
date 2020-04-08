package levels;


import ce3.*;

public class Floor3 extends Level{

	
	public Floor3(Board frame, Level nextFloor) {
		super(frame, nextFloor);
		next = nextFloor;
		scale = .9;
		board = frame;
		minDecay = 60;
		maxDecay = 3600;
	}
	
	public void build() {
		super.build();
		genPaths(6);
		
	}
	//creates nodes and connections
	public void genPaths(int num) {
		
	}
	public void placeCookies() {
		super.placeCookies(5,(int)(100*scale));
	}

}
