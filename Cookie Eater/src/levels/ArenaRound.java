package levels;

import java.util.*;

import ce3.*;

public class ArenaRound extends Arena{

	public ArenaRound(Game frame, Board gameboard, Floor floor, String id) {
		super(frame,gameboard,floor,id);
		name = "Hostile Tunnels";
		nameAbbrev = "enm";
	}
	public ArenaRound(Game frame, Board gameboard, Floor floor, SaveData sd) {
		super(frame, gameboard, floor, sd);
	}
}
