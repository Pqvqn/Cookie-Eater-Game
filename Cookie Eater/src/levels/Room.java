package levels;


import java.awt.*;
import java.util.*;

import ce3.*;
import entities.*;
import mechanisms.*;

public class Room{
	
	protected double scale; //zoom in/out of level
	protected double[][] startposs;
	protected int minDecay; //frames for cookie at edge corner to decay fully
	protected int maxDecay; //frames for cookie at center to decay fully
	protected Color bgColor;
	protected Color wallColor;
	protected String name; //name for files
	protected String nameAbbrev; //name for display
	protected String lvlid; //id code for this level's path
	protected double exitProportion; //proportion of cookies that must be collected to open doors
	
	public Room(SaveData sd) {
		scale = sd.getDouble("scale",0);
		minDecay = sd.getInteger("decay",0);
		maxDecay = sd.getInteger("decay",1);
		name = sd.getString("name",0);
		nameAbbrev = sd.getString("name",1);
		lvlid = sd.getString("name",2);
		exitProportion = sd.getDouble("requirement",0);
		bgColor = Color.GRAY;
		wallColor = Color.red.darker();
	}
	
	public SaveData getSaveData() {
		SaveData data = new SaveData();
		data.addData("scale",scale);
		data.addData("decay",minDecay,0);
		data.addData("decay",maxDecay,1);
		data.addData("name",name,0);
		data.addData("name",nameAbbrev,1);
		data.addData("name",lvlid,2);
		data.addData("requirement",exitProportion);
		return data;
	}
	
	public void build() {
		/*super.build();
		genPaths(1, 100, 200, 100, 10, null); //num nodes, min radius around nodes, max radius around nodes, radius around lines, nodes per line, board regions to fill
		genWalls(400, 300, 600, false); //wall separation, wall min size, wall max size, angled
		*/
	}
	public void placeCookies() {
		//super.placeCookies(50,(int)(100*scale));
	}
	public void spawnEnemies() { 
		/*int cycle = game.getCycle();
		//ArrayList<String> possible = new ArrayList<String>();
		//possible.add("Field");
		for(int i=0;i<Math.random()*3-1;i++) {
			//Enemy e;
			spawnAtRandom(new EnemyBlob(game,board,cycle,0,0));
			//e.giveCookie(new CookieItem(board,0,0,Level.generateItem(board,possible.get((int)(Math.random()*possible.size()))),0));
		}
		for(int i=0;i<Math.random()*2-1;i++) {
			//Enemy e;
			spawnAtRandom(new EnemyBloc(game,board,cycle,0,0));
			//e.giveCookie(new CookieItem(board,0,0,Level.generateItem(board,possible.get((int)(Math.random()*possible.size()))),0));
		}
		
		for(int i=0;i<Math.random()*3-1;i++) {
			//Enemy e;
			spawnAtRandom(new EnemyParasite(game,board,cycle,0,0));}
	}
	*/
}
