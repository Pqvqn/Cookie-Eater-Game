package levels;


import java.awt.Color;

import ce3.*;

public class Floor1 extends Level{

	//public double scale;
	//private Level next;
	//private Board board;
	//public double startx;
	//public double starty;
	public Floor1(Game frame) {
		this(frame,null);
	}
	public Floor1(Game frame, Level nextFloor) {
		super(frame, nextFloor);
		name = "Forest Entrance";
		nameAbbrev = "for";
		next = nextFloor;
		scale = 1;
		minDecay = 60;
		maxDecay = 1800;
		bgColor = new Color(40,70,40);
		wallColor = new Color(50,30,10);
	}
	
	public void build() {
		super.build();
	}
	
	public void placeCookies() {
		super.placeCookies(90,100);
		/*int cooks = 0;
		board.cookies.add(new Cookie(board,1400,400));
		cooks++;
		board.scoreToWin = cooks;*/
	}


}
