package levels;


import java.awt.*;
import java.util.*;

import ce3.*;
import entities.*;
import mechanisms.*;

public class Room{
	
	public double scale; //zoom in/out of level
	public double[][] startposs; //start positions for players
	public int minDecay; //frames for cookie at edge corner to decay fully
	public int maxDecay; //frames for cookie at center to decay fully
	public Color bgColor;
	public Color wallColor;
	public String name; //name for files
	public String nameAbbrev; //name for display
	public double exitProportion; //proportion of cookies that must be collected to open doors
	
	public int[] pathGen;  //num nodes, min radius around nodes, max radius around nodes, radius around lines, nodes per line
	public int[] wallGen; //wall separation, wall min size, wall max size
	public int[] cookieGen; //clearance between cookies and walls, separation between cookies
	public int[][] regions; //board regions to fill when generating nodes
	public boolean angledWalls; //whether angled walls generate
	
	public Room(SaveData sd) {
		scale = sd.getDouble("scale",0);
		minDecay = sd.getInteger("decay",0);
		maxDecay = sd.getInteger("decay",1);
		name = sd.getString("name",0);
		nameAbbrev = sd.getString("name",1);
		exitProportion = sd.getDouble("requirement",0);
		bgColor = Color.GRAY;
		wallColor = Color.red.darker();
		
		startposs = new double[4][2];
		for(int i=0; i<startposs.length * startposs[i].length; i++) {
			startposs[i/startposs[i].length][i%startposs[i].length] = sd.getDouble("startpositions",i);
		}
		
		angledWalls = sd.getBoolean("angled",0);
		
		pathGen = new int[5];
		for(int i=0; i<pathGen.length; i++) {
			pathGen[i] = sd.getInteger("pathgen",i);
		}
		wallGen = new int[3];
		for(int i=0; i<wallGen.length; i++) {
			wallGen[i] = sd.getInteger("wallgen",i);
		}
		cookieGen = new int[2];
		for(int i=0; i<cookieGen.length; i++) {
			cookieGen[i] = sd.getInteger("cookiegen",i);
		}
		regions = new int[sd.getData("regions").size()][4];
		for(int i=0; i<regions.length * regions[i].length; i++) {
			regions[i/regions[i].length][i%regions[i].length] = sd.getInteger("regions",i);
		}
	}
	
	public SaveData getSaveData() {
		SaveData data = new SaveData();
		data.addData("scale",scale);
		data.addData("decay",minDecay,0);
		data.addData("decay",maxDecay,1);
		data.addData("name",name,0);
		data.addData("name",nameAbbrev,1);
		data.addData("requirement",exitProportion);
		for(int i=0; i<startposs.length * startposs[i].length; i++) {
			data.addData("startpositions",startposs[i/startposs[i].length][i%startposs[i].length],i);
		}
		
		data.addData("angled",angledWalls);
		
		for(int i=0; i<pathGen.length; i++) {
			data.addData("pathgen",pathGen[i],i);
		}
		for(int i=0; i<wallGen.length; i++) {
			data.addData("wallgen",wallGen[i],i);
		}
		for(int i=0; i<cookieGen.length; i++) {
			data.addData("cookiegen",cookieGen[i],i);
		}
		for(int i=0; i<regions.length * regions[i].length; i++) {
			data.addData("regions",regions[i/regions[i].length][i%regions[i].length],i);
		}
		
		return data;
	}
	
}
