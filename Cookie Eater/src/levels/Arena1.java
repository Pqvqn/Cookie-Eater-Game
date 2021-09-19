package levels;

import java.util.*;

import ce3.*;

public class Arena1 extends Arena{

	public Arena1(Game frame, Board gameboard, Floor floor, String id) {
		super(frame,gameboard,floor,id);
		name = "Forest Entrance";
		nameAbbrev = "for";
	}
	public Arena1(Game frame, Board gameboard, Floor floor, SaveData sd) {
		super(frame, gameboard, floor, sd);
	}
}
