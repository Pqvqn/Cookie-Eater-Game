package levels;

import java.util.*;

import ce3.*;

public class Floor2 extends Floor{

	public Floor2(Game frame, Board gameboard, int wid, int hei, String floorid) {
		super(frame,gameboard,wid,hei,floorid);
		roomWeights = new HashMap<Class,Integer>();
		roomWeights.put(Room2.class,1);
		numRooms = 10;
	}
	
	public Store generateStore() {
		return new Store2(game, board, id);
	}
}
