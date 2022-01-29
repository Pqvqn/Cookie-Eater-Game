package levels;

import ce3.*;

public class Training extends Level{
	
	public Training(Game frame, Board gameboard, Floor floorlevel, Room roomtemplate, ThemeSet themeWeights, String id) {
		super(frame,gameboard,floorlevel,roomtemplate,themeWeights,id);
	}
	public Training(Game frame, Board gameboard, Floor floor, SaveData sd) {
		super(frame, gameboard, floor, sd);
	}
	
	public void build() {
		loaded = true;
	}
	
}
