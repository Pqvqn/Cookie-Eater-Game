package mechanisms;

import ce3.Board;
import ce3.Game;

public class WallCase extends Wall{

	public WallCase(Game frame, Board gameboard, int xPos, int yPos, int width, int height) {
		super(frame, gameboard, xPos, yPos, width, height);
	}
	
	public WallCase(Game frame, Board gameboard, int xPos, int yPos, int radius) {
		super(frame, gameboard, xPos, yPos, radius);
	}

}