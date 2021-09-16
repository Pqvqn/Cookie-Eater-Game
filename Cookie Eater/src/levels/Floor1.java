package levels;

import java.util.*;

import ce3.*;

public class Floor1 extends Floor{

	public Floor1(Game frame, Board gameboard, int wid, int hei, String floorid) {
		super(frame,gameboard,wid,hei,floorid);
		roomWeights = new HashMap<Class,Integer>();
		roomWeights.put(Room1.class,1);
		numRooms = 49;
	}
	
	public Store generateStore() {
		return new Store1(game, board, id);
	}
}
