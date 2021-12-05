package mechanisms;

import java.awt.*;
import java.io.IOException;

import ce3.*;
import sprites.*;

public class Decoration extends Mechanism{

	private SpriteMechanism sprite;
	private String fileName;
	
	public Decoration(Game frame, Board gameboard, int xPos, int yPos, String name) {
		super(frame, gameboard, xPos, yPos);
		fileName = name;
		try {
			sprite = new SpriteMechanism(gameboard, this, fileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Decoration(Game frame, Board gameboard, SaveData sd) {
		super(frame,gameboard,sd);
		fileName = sd.getString("filename",0);
		try {
			sprite = new SpriteMechanism(gameboard, this, fileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public SaveData getSaveData() {
		SaveData data = new SaveData();
		data.addData("filename",fileName);
		return data;
	}
	
	public void paint(Graphics g) {
		if(sprite!=null) {
			sprite.paint(g);
		}
	}

}
