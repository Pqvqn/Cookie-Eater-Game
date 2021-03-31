package levels;

import ce3.*;

public class ArenaRound extends Arena{

	public ArenaRound(Game frame, Board gameboard) {
		super(frame,gameboard);
		name = "Hostile Tunnels";
		nameAbbrev = "enm";
	}
	public ArenaRound(Game frame, Board gameboard, Level nextFloor, SaveData sd) {
		super(frame, gameboard, nextFloor, sd);
	}
}
