package mechanisms;

import java.awt.*;
import java.util.*;
import java.io.IOException;

import ce3.*;
import sprites.*;
import levels.*;

public class Decoration extends Mechanism{

	private SpriteMechanism sprite;
	private String fileNames[];
	private int variant;
	private ThemeSet preferredThemes;
	
	public Decoration(Game frame, Board gameboard, int xPos, int yPos, String[] names, ThemeSet themes) {
		super(frame, gameboard, xPos, yPos);
		fileNames = names;
		variant = (int)(Math.random() * fileNames.length);
		preferredThemes = themes;
		try {
			sprite = new SpriteMechanism(gameboard, this, fileNames[variant]);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Decoration(Game frame, Board gameboard, SaveData sd) {
		super(frame,gameboard,sd);
		fileNames = new String[sd.getData("filename").size()];
		for(int i=0; i<fileNames.length; i++) {
			fileNames[i] = sd.getString("filename",i);
		}
		variant = sd.getInteger("variant",0);
		preferredThemes = new ThemeSet(sd.getSaveDataList("preferredthemes").get(0));
		try {
			sprite = new SpriteMechanism(gameboard, this, fileNames);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public SaveData getSaveData() {
		SaveData data = super.getSaveData();
		for(int i=0; i<fileNames.length; i++) {
			data.addData("filename",fileNames[i],i);
		}
		data.addData("variant",variant);
		data.addData("preferredthemes",preferredThemes.getSaveData());
		return data;
	}
	
	//set position nearer to passage with best theme affinity
	public void randomize(Level lvl, SaveData rnd) {
		super.randomize(lvl,rnd);
		if(!rnd.getBoolean("topassage",0))return;
		int range = rnd.getInteger("range",1);
		int minr = rnd.getInteger("range",0);
		
		ArrayList<Passage> options = lvl.getPassages();
		ArrayList<Double> weights = new ArrayList<Double>();
		double total = 0;
		for(Passage p : options) {
			double currW = preferredThemes.affinityWith(p.getOtherSide().getThemeWeights());
			total += currW;
			weights.add(total);
		}
		
		double chosen = Math.random() * total;
		int i = 0;
		for(i=0; weights.get(i) < chosen; i++);
		Passage selected = options.get(i);
		
		double distance = range * Math.pow(Math.random(),2) + minr;
		//double randangle = (Math.random()-.5) * Math.PI;
		//double passangle = Math.atan2(-selected.getY() + board.y_resol/2, -selected.getX() + board.x_resol/2);
		double[] angleRange = selected.angleRange();
		double selectedAngle = Math.random() * (angleRange[1] - angleRange[0]) + angleRange[0];
		
		x = selected.getX() + distance * Math.cos(selectedAngle);
		y = selected.getY() - distance * Math.sin(selectedAngle);
		
		if(rnd.getBoolean("dovariant",0)) {
			variant = (int)(Math.random() * fileNames.length);
		}
	}
	
	public SpriteMechanism sprite() {return sprite;}
	
	public void paint(Graphics g) {
		//if(sprite!=null) {
		//	sprite.paint(g);
		//}
	}

}
