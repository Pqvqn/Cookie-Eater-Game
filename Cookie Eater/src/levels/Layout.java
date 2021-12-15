package levels;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import ce3.*;

public class Layout {

	public ArrayList<SaveData> roomGen;
	public int numRooms;
	public String store;
	public int rows, cols;
	public String nameSub;
	//public String[] themes;
	public ThemeSet presentThemes;
	
	public ArrayList<SaveData> enemyGen;
	
	public static void main(String[] args) {
		SaveData overall = new SaveData();
		Layout thisLayout = new Layout();
		File f = new File("Cookie Eater/src/resources/level/layouts.txt");
		try {
			f.getParentFile().mkdirs();
			f.createNewFile();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			overall.addData(thisLayout.nameSub,thisLayout.getSaveData());
			overall.saveToFile(f);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(f);
	}
	public Layout() {
		numRooms = 10;
		rows = 4;
		cols = 4;
		store = "Store2";
		roomGen = new ArrayList<SaveData>();
		SaveData room2 = new SaveData();
		
		room2.addData("room","Room2");
		room2.addData("weight",1);
		roomGen.add(room2);
		SaveData room3 = new SaveData();
		room3.addData("room","Room3");
		room3.addData("weight",2);
		roomGen.add(room3);
		
		nameSub = "Layout2";
		String[] th = {"dungeon"};
		themes = th;
	}
	
	public Layout(String layoutname, SaveData sd) {
		numRooms = sd.getInteger("length",0);
		rows = sd.getInteger("dimensions",0);
		cols = sd.getInteger("dimensions",1);
		store = sd.getString("store",0);
		roomGen = sd.getSaveDataList("rooms");
		themes = new String[sd.getData("themes").size()];
		for(int i=0; i<themes.length; i++) {
			themes[i] = sd.getString("themes",i);
		}
		nameSub = layoutname;
	}
	
	public SaveData getSaveData() {
		SaveData data = new SaveData();
		
		data.addData("length",numRooms);
		data.addData("dimensions",rows,0);
		data.addData("dimensions",cols,1);
		data.addData("store",store);
		for(int i=0; i<roomGen.size(); i++)
			data.addData("rooms",roomGen.get(i),i);
		for(int i=0; i<themes.length; i++)
			data.addData("themes",themes[i],i);
		return data;
	}

}
