package mechanisms;

import ce3.*;

public class Decoration extends Mechanism{

	private SpriteMechanism sprite;
	private String fileName;
	
	public Decoration(Game frame, Board gameboard, int xPos, int yPos, String name) {
		super(frame, gameboard, xPos, yPos);
		//sprite;
		fileName = name;
	}
	
	public Decoration(Game frame, Board gameboard, SaveData sd) {
		super(frame,gameboard,sd);
		//sprite;
		fileName = sd.getString("filename",0);
	}
	
	public SaveData getSaveData() {
		SaveData data = new SaveData();
		data.addData("filename",fileName);
		return data;
	}
	
	public void clean() {
		super.clean();
		//sprite;
	}

}
