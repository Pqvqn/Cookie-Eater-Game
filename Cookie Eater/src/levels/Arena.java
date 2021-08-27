package levels;

import java.util.*;

import ce3.*;
//import entities.*;

public abstract class Arena extends Level{
	
	public Arena(Game frame, Board gameboard, Floor floor, String id) {
		super(frame,gameboard,floor,id);
	}
	public Arena(Game frame, Board gameboard, ArrayList<Level> prev, ArrayList<Level> next, SaveData sd) {
		super(frame, gameboard, prev, next, sd);
	}
	public boolean installPickups() {return true;}
}
