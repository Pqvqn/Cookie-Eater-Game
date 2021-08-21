package levels;

import java.util.*;

import ce3.*;

public class Floor1 extends Floor{

	public Floor1(Game frame, Board gameboard, int wid, int hei) {
		super(frame,gameboard,wid,hei);
		roomWeights = new HashMap<Class,Integer>();
		roomWeights.put(Room2.class,1);
		numRooms = 10;
	}
}
