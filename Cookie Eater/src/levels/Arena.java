package levels;

import java.util.*;

import ce3.*;
//import entities.*;

public abstract class Arena extends Level{
	
	public Arena(Game frame, Board gameboard, Floor floor, String id) {
		super(frame,gameboard,floor,id);
	}
	public Arena(Game frame, Board gameboard, Floor floor, SaveData sd) {
		super(frame, gameboard, floor, sd);
	}
	public boolean installPickups() {return true;}
}
