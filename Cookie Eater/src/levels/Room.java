package levels;


import java.awt.*;
import java.util.*;

import ce3.*;
/*import entities.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;*/

public class Room{
	
	public double scale; //zoom in/out of level
	public double[][] startposs; //start positions for players
	public int minDecay; //frames for cookie at edge corner to decay fully
	public int maxDecay; //frames for cookie at center to decay fully
	public Color bgColor;
	public Color wallColor;
	public String name; //name for display
	public String nameAbbrev; //name for files
	public String nameSub; //name for identification
	public double exitProportion; //proportion of cookies that must be collected to open doors
	
	public int[] pathGen;  //num nodes, min radius around nodes, max radius around nodes, radius around lines, nodes per line
	public int[] wallGen; //wall separation, wall min size, wall max size
	public int[] cookieGen; //clearance between cookies and walls, separation between cookies
	public int[][] regions; //board regions to fill when generating nodes
	public boolean angledWalls; //whether angled walls generate
	
	public boolean isStore; //if this room is a store
	public int[][][] vendorSpaces; //spaces for vendor and their items for sale (first coordinate pair for vendor)
	public int[][] passerbySpaces; //spaces for passerbys
	public int[][] mechanicSpaces; //spaces for mechanic and stat change cookies
	
	public boolean haltEnabled;
	public boolean specialsEnabled;
	public boolean installPickups;
	public boolean takeDamage;
	
	public ArrayList<SaveData> enemyGen;
	
	/*public static void main(String[] args) {
		Room thisRoom = new Room();
		File f = new File("Cookie Eater/src/resources/level/rooms.txt");
		try {
			f.getParentFile().mkdirs();
			f.createNewFile();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			thisRoom.getSaveData().saveToFile(f);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(f);
	}
	public Room() {
		scale = .95;
		minDecay = 90;
		maxDecay = 30000;
		name = "Dungeon Foyer";
		nameAbbrev = "dun";
		nameSub = name;
		exitProportion = .5;
		bgColor = Color.GRAY;
		wallColor = Color.red.darker();
		isStore = false;
		haltEnabled = false;
		specialsEnabled = true;
		installPickups = false;
		takeDamage = true;
		
		final int BORDER_THICKNESS = 40;
		double distToWall = BORDER_THICKNESS+40*scale*5;
		double[][] sp = {{1920-distToWall,1020-distToWall},{distToWall,distToWall},{distToWall,1020-distToWall},{1920-distToWall,distToWall}};
		startposs = sp;
		
		angledWalls = false;
		
		pathGen = new int[] {1, 100, 200, 100, 10};
		wallGen = new int[] {400, 300, 600};
		cookieGen = new int[] {50, 95};
		regions = null;
		ArrayList<SaveData> enemies = new ArrayList<SaveData>();
		SaveData blob = new SaveData();
		blob.addData("chance",-1);
		blob.addData("chance",3);
		blob.addData("type",EnemyBlob.class.getName());
		enemies.add(blob);
		SaveData bloc = new SaveData();
		bloc.addData("chance",-1);
		bloc.addData("chance",2);
		bloc.addData("type",EnemyBloc.class.getName());
		enemies.add(bloc);
		SaveData para = new SaveData();
		para.addData("chance",-1);
		para.addData("chance",3);
		para.addData("type",EnemyParasite.class.getName());
		enemies.add(para);
		
		enemyGen = enemies;
	}*/
	
	public Room(String roomname, SaveData sd) {
		scale = sd.getDouble("scale",0);
		minDecay = sd.getInteger("decay",0);
		maxDecay = sd.getInteger("decay",1);
		name = sd.getString("name",0);
		nameAbbrev = sd.getString("name",1);
		nameSub = roomname;
		exitProportion = sd.getDouble("requirement",0);
		bgColor = Color.GRAY;
		wallColor = Color.red.darker();
		isStore = sd.getBoolean("isstore",0);
		haltEnabled = sd.getBoolean("canhalt",0);
		specialsEnabled = sd.getBoolean("canspecial",0);
		installPickups = sd.getBoolean("caninstall",0);
		takeDamage = sd.getBoolean("candamage",0);
		
		startposs = new double[4][2];
		for(int i=0; i<startposs.length * startposs[0].length; i++) {
			startposs[i/startposs[0].length][i%startposs[0].length] = sd.getDouble("startpositions",i);
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
		for(int i=0; i<regions.length * regions[0].length; i++) {
			regions[i/regions[0].length][i%regions[0].length] = sd.getInteger("regions",i);
		}
		
		enemyGen = sd.getSaveDataList("enemies");
		
		if(isStore) {
			readStoreData(sd);
		}
	}
	
	public void readStoreData(SaveData sd) {
		ArrayList<Object> vendor_space_data = sd.getData("vendorspaces");
		if(vendor_space_data!=null) {
			int vn = sd.getInteger("vendorspacenum",0);
			int[][][] vs = new int[vn][vendor_space_data.size()/vn/2][2];
			int count = 0;
			for(int i=0; i<vs.length; i++) {
				for(int j=0; j<vs[i].length; j++) {
					for(int h=0; h<vs[i][j].length; h++) {
						vs[i][j][h] = Integer.parseInt(vendor_space_data.get(count).toString());
						count++;
					}
				}
			}
			vendorSpaces = vs;
		}
		ArrayList<Object> passerby_space_data = sd.getData("passerbyspaces");
		if(passerby_space_data!=null) {
			int[][] ps = new int[passerby_space_data.size()/2][2];
			int count = 0;
			for(int i=0; i<ps.length; i++) {
				for(int j=0; j<ps[i].length; j++) {
					ps[i][j] = Integer.parseInt(passerby_space_data.get(count).toString());
					count++;
				}
			}
			passerbySpaces = ps;
		}
		ArrayList<Object> mechanic_space_data = sd.getData("mechanicspaces");
		if(mechanic_space_data!=null) {
			int[][] ms = new int[mechanic_space_data.size()/2][2];
			int count = 0;
			for(int i=0; i<ms.length; i++) {
				for(int j=0; j<ms[i].length; j++) {
					ms[i][j] = Integer.parseInt(mechanic_space_data.get(count).toString());
					count++;
				}
			}
			mechanicSpaces = ms;
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
		data.addData("isstore",isStore);
		for(int i=0; i<startposs.length * startposs[0].length; i++) {
			data.addData("startpositions",startposs[i/startposs[0].length][i%startposs[0].length],i);
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
		if(regions!=null) {
			for(int i=0; i<regions.length * regions[0].length; i++) {
				data.addData("regions",regions[i/regions[0].length][i%regions[0].length],i);
			}
		}
		
		data.addData("canhalt",haltEnabled);
		data.addData("canspecial",specialsEnabled);
		data.addData("caninstall",installPickups);
		data.addData("candamage",takeDamage);
		
		data.addData("enemies",enemyGen);
		
		if(isStore) {
			writeStoreData(data);
		}
		
		return data;
	}
	
	public void writeStoreData(SaveData data) {
		int ci = 0;
		if(vendorSpaces!=null) {
			data.addData("vendorspacenum",vendorSpaces.length);
			for(int i=0; i<vendorSpaces.length; i++) {
				for(int j=0; j<vendorSpaces[i].length; j++) {
					for(int h=0; h<vendorSpaces[i][j].length; h++) {
						data.addData("vendorspaces",vendorSpaces[i][j][h],ci++);
					}
				}
			}
		}
		ci=0;
		if(passerbySpaces!=null) {
			for(int i=0; i<passerbySpaces.length; i++) {
				for(int j=0; j<passerbySpaces[i].length; j++) {
					data.addData("passerbyspaces",passerbySpaces[i][j],ci++);
				}
			}
		}
		ci=0;
		if(mechanicSpaces!=null) {
			for(int i=0; i<mechanicSpaces.length; i++) {
				for(int j=0; j<mechanicSpaces[i].length; j++) {
					data.addData("mechanicspaces",mechanicSpaces[i][j],ci++);
				}
			}
		}

	}
	
}
