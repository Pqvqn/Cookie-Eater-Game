package levels;

import java.util.*;

import ce3.*;

public class ArenaRound extends Arena{

	public ArenaRound(Game frame, Board gameboard) {
		super(frame,gameboard);
		name = "Hostile Tunnels";
		nameAbbrev = "enm";
	}
	public ArenaRound(Game frame, Board gameboard, ArrayList<Level> nextFloor, SaveData sd) {
		super(frame, gameboard, nextFloor, sd);
	}
}
