package levels;

import java.util.*;

import ce3.*;

public class Floor3 extends Floor{

	public Floor3(Game frame, Board gameboard, int wid, int hei, String floorid) {
		super(frame,gameboard,wid,hei,floorid);
		roomWeights = new HashMap<Class,Integer>();
		roomWeights.put(Room3.class,1);
		numRooms = 10;
	}
	
	public Floor3(Game frame, Board gameboard, SaveData sd) {
		super(frame, gameboard, sd);
	}
	
	public Store generateStore() {
		return new Store3(game, board, id);
	}
}
