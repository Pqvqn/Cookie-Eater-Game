package levels;

import ce3.*;

public class Arena1 extends Arena{

	public Arena1(Game frame, Board gameboard) {
		super(frame,gameboard);
		name = "Forest Entrance";
		nameAbbrev = "for";
	}
	public Arena1(Game frame, Board gameboard, Level nextFloor, SaveData sd) {
		super(frame, gameboard, nextFloor, sd);
	}
}
