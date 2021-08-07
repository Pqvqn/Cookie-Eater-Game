package levels;

import java.util.*;

import ce3.*;

public class Floor {
	
	protected Game game;
	protected Board board;
	protected Level[][] roomGrid; //grid of room locations
	protected ArrayList<Store> entrances; //entrances into the floor, located in stores
	protected ArrayList<Store> exits; //exits to other floors, located in stores

	public Floor(Game frame, Board gameboard, int wid, int hei) {
		game = frame;
		board = gameboard;
		roomGrid = new Level[wid][hei];
	}
	
	//creates and connects levels in paths
	public void generateFloor(HashMap<Level,Integer> roomWeights, int numRooms) {
		
	}
	
}
