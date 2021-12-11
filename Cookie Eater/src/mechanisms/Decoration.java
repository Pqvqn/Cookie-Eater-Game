package mechanisms;

import java.awt.*;
import java.io.IOException;

import ce3.*;
import sprites.*;
import levels.*;

public class Decoration extends Mechanism{

	private SpriteMechanism sprite;
	private String fileName;
	private String[] preferredThemes; //affects placement to make closer to theme
	
	public Decoration(Game frame, Board gameboard, int xPos, int yPos, String name, String[] themes) {
		super(frame, gameboard, xPos, yPos);
		fileName = name;
		preferredThemes = themes;
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
		preferredThemes = new String[sd.getData("themes").size()];
		for(int i=0; i<preferredThemes.length; i++) {
			preferredThemes[i] = sd.getString("themes",i);
		}
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
		for(int i=0; i<preferredThemes.length; i++) {
			data.addData("themes",preferredThemes[i],i);
		}
		return data;
	}
	
	public void randomize(Level lvl, SaveData rnd) {
		super.randomize(lvl,rnd);
	}
	
	public void paint(Graphics g) {
		if(sprite!=null) {
			sprite.paint(g);
		}
	}

}
