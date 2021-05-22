package mechanisms;

import ce3.*;

public class WallDoor extends Wall{

	private int thresh; //amount of cookies needed to unlock
	
	public WallDoor(Game frame, Board gameboard, int xPos, int yPos, int width, int height, int requirement) {
		super(frame, gameboard, xPos, yPos, width, height);
		thresh = requirement;
	}
	
	public WallDoor(Game frame, Board gameboard, int xPos, int yPos, int radius, int requirement) {
		super(frame, gameboard, xPos, yPos, radius); 
		thresh = requirement;
	}
	
	public WallDoor(Game frame, Board gameboard, SaveData sd) {
		super(frame, gameboard, sd);
		thresh = sd.getInteger("threshold",0);
	}

	public SaveData getSaveData() {
		SaveData data = super.getSaveData();
		data.addData("threshold",thresh);
		return data;
	}
	
	public void runUpdate() {
		//check if players have collected enough cookies to remove door
		if(board.totalScore()>=thresh)
			remove();
	}
	
}