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
		if(!rnd.getBoolean("topassage",0))return;
		int range = rnd.getInteger("range",0);
		
		ArrayList<Passage> options = lvl.getPassages();
		ArrayList<Double> weights = new ArrayList<Double>();
		double total = 0;
		for(Passage p : options) {
			double currW = preferredThemes.affinityWith(p.getOtherSide().getThemeWeights());
			total += currW;
			weights.add(currW);
		}
		
		double chosen = Math.random() * total;
		int i = 0;
		for(i=0; weights.get(i) < chosen; i++);
		Passage selected = options.get(i);
		
		double distance = range * Math.pow(Math.random(),2);
		double randangle = (Math.random()-1) * Math.PI;
		double passangle = Math.atan2(-selected.getY() + board.y_resol/2, -selected.getX() + board.x_resol/2);
		
		x = selected.getX() + distance * Math.cos(randangle + passangle);
		y = selected.getY() + distance * Math.sin(randangle + passangle);
		
	}
	
	public void paint(Graphics g) {
		if(sprite!=null) {
			sprite.paint(g);
		}
	}

}
