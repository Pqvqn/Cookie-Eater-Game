package levels;


import java.awt.*;
import java.util.*;

import ce3.*;

public class Room1 extends Level{

	//public double scale;
	//private Level next;
	//private Board board;
	//public double startx;
	//public double starty;
	public Room1(Game frame, Board gameboard, Floor floor, String id) {
		super(frame,gameboard,floor,id);
		name = "Forest Entrance";
		nameAbbrev = "for";
		scale = 1;
		minDecay = 60;
		maxDecay = 1800;
		exitProportion = .5;
		bgColor = new Color(40,70,40);
		wallColor = new Color(50,30,10);
	}
	public Room1(Game frame, Board gameboard, ArrayList<Level> prev, ArrayList<Level> next, SaveData sd) {
		super(frame, gameboard, prev, next, sd);
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
