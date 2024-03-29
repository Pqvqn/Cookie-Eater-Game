package mechanisms;

import java.awt.Color;
import java.awt.Font;

import ce3.*;
import ui.*;
import levels.*;

public class WallDoor extends Wall{

	private int thresh; //amount of cookies needed to unlock
	private double prop; //proportion of cookies on board to use
	private UIText pointDisplay; //displays number of cookies left to be collected
	
	/*public WallDoor(Game frame, Board gameboard, int xPos, int yPos, int width, int height, double requirement, boolean isReqProp) {
		super(frame, gameboard, xPos, yPos, width, height);
		setReq(requirement,isReqProp);
	}*/
	
	public WallDoor(Game frame, Board gameboard, Level lvl, int xPos, int yPos, int radius, double requirement, boolean isReqProp) {
		super(frame, gameboard, lvl, xPos, yPos, radius); 
		setReq(requirement,isReqProp);
	}
	
	public WallDoor(Game frame, Board gameboard, Level lvl, SaveData sd) {
		super(frame, gameboard, lvl, sd);
		double requirement = sd.getDouble("requirement",0);
		boolean isReqProp = sd.getBoolean("requirement",1);
		setReq(requirement,isReqProp);
	}
	
	public void setReq(double requirement, boolean isReqProp) {
		pointDisplay = new UIText(game, (int)(x+w/2+(Board.FRAME_X_RESOL/2-x)/15), (int)(y+h/2+(Board.FRAME_Y_RESOL/2-y)/10), "", Color.WHITE, new Font("Arial",Font.BOLD,25), true);
		game.draw.addUI(pointDisplay);
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
	
	public void clean() {
		game.draw.removeUI(pointDisplay);
		super.clean();
	}
	
	public void runUpdate() {
		if(!game.draw.getUIList().contains(pointDisplay) && level.mechanisms.contains(this)) {
			game.draw.addUI(pointDisplay);
		}
		pointDisplay.setText(thresh-level.score+""); //update display
		//check if players have collected enough cookies to remove door
		if(thresh>=0 && level.score>=thresh) {
			remove();
		}
	}
	
}