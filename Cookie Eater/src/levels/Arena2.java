package levels;

import java.util.*;

import ce3.*;
import entities.*;

public class Arena2 extends Arena{

	public Arena2(Game frame, Board gameboard, String id) {
		super(frame,gameboard,id);
		name = "Dungeon Foyer";
		nameAbbrev = "dun";
		minDecay = 90;
		maxDecay = 3000;
	}
	public Arena2(Game frame, Board gameboard, ArrayList<Level> prev, ArrayList<Level> next, SaveData sd) {
		super(frame, gameboard, prev, next, sd);
	}
	public void spawnEnemies() { 
		int cycle = game.getCycle();
		for(int i=0;i<1;i++) {
			Enemy e;
			e = new EnemySpawnerArena(game,board,cycle,board.x_resol/2,board.y_resol/2);
			board.enemies.add(e);

		}
	}
}
