package levels;

import java.util.*;

import ce3.*;
//import entities.*;

public abstract class Arena extends Level{
	
	public Arena(Game frame, Board gameboard) {
		super(frame,gameboard);
	}
	public Arena(Game frame, Board gameboard, ArrayList<Level> nextFloor, SaveData sd) {
		super(frame, gameboard, nextFloor, sd);
	}
	public boolean installPickups() {return true;}
}
