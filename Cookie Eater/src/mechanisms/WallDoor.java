package mechanisms;

import ce3.*;

public class WallDoor extends Wall{

	private int thresh; //amount of cookies needed to unlock
	private double prop; //proportion of cookies on board to use
	
	
	public WallDoor(Game frame, Board gameboard, int xPos, int yPos, int width, int height, double requirement, boolean isReqProp) {
		super(frame, gameboard, xPos, yPos, width, height);
		setReq(requirement,isReqProp);
	}
	
	public WallDoor(Game frame, Board gameboard, int xPos, int yPos, int radius, double requirement, boolean isReqProp) {
		super(frame, gameboard, xPos, yPos, radius); 
		setReq(requirement,isReqProp);
	}
	
	public WallDoor(Game frame, Board gameboard, SaveData sd) {
		super(frame, gameboard, sd);
		int requirement = sd.getInteger("requirement",0);
		boolean isReqProp = sd.getBoolean("requirement",1);
		setReq(requirement,isReqProp);
	}
	
	public void setReq(double requirement, boolean isReqProp) {
		if(!isReqProp) {
			thresh = (int)requirement;
		}else {
			prop = requirement;
			thresh = -1;
		}
	}

	public void updateCookieTotal(int total) {
		//set amount of cookies needed to open based on total amount of cookies on the board
		thresh = (int)(total * prop);
	}
	
	public SaveData getSaveData() {
		SaveData data = super.getSaveData();
		boolean threshValid = thresh >= 0;
		data.addData("requirement",threshValid?thresh:prop,0);
		data.addData("requirement",!threshValid,1);
		return data;
	}
	
	public void runUpdate() {
		//check if players have collected enough cookies to remove door
		if(thresh>=0 && board.totalScore()>=thresh)
			remove();
	}
	
}