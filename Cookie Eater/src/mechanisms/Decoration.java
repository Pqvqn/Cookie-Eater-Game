package mechanisms;

import java.awt.*;
import java.util.*;
import java.io.IOException;

import ce3.*;
import sprites.*;
import levels.*;

public class Decoration extends Mechanism{

	private SpriteMechanism sprite;
	private String fileName;
	private ThemeSet preferredThemes;
	
	public Decoration(Game frame, Board gameboard, int xPos, int yPos, String name, ThemeSet themes) {
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
		preferredThemes = new ThemeSet(sd.getSaveDataList("preferredthemes").get(0));
		try {
			sprite = new SpriteMechanism(gameboard, this, fileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public SaveData getSaveData() {
		SaveData data = super.getSaveData();
		data.addData("filename",fileName);
		data.addData("preferredthemes",preferredThemes.getSaveData());
		return data;
	}
	
	//set position nearer to passage with best theme affinity
	public void randomize(Level lvl, SaveData rnd) {
		super.randomize(lvl,rnd);
		ArrayList<Passage> options = lvl.getPassages();
		double bestW = 0;
		Passage best = null;
		for(Passage p : options) {
			double currW = preferredThemes.affinityWith(p.getOtherSide().getThemeWeights());
			if(currW > bestW) {
				bestW = currW;
				best = p;
			}
		}
		
		x = best.getX();
		y = best.getY();
		
	}
	
	public void paint(Graphics g) {
		if(sprite!=null) {
			sprite.paint(g);
		}
	}

}
