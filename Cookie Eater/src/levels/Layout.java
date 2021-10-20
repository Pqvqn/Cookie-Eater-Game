package levels;

/*import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;*/
import java.util.*;

import ce3.*;

public class Layout {

	public ArrayList<SaveData> roomGen;
	public int numRooms;
	public String store;
	public int rows, cols;
	public String nameSub;
	
	public ArrayList<SaveData> enemyGen;
	
	/*public static void main(String[] args) {
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
			thisLayout.getSaveData().saveToFile(f);
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
		store = "teststore";
		roomGen = new ArrayList<SaveData>();
		SaveData room2 = new SaveData();
		room2.addData("room","Room2");
		room2.addData("weight",1);
		roomGen.add(room2);
		nameSub = "Layout2";
	}*/
	
	public Layout(String layoutname, SaveData sd) {
		numRooms = sd.getInteger("length",0);
		rows = sd.getInteger("dimensions",0);
		cols = sd.getInteger("dimensions",1);
		store = sd.getString("store",0);
		roomGen = sd.getSaveDataList("rooms");
		nameSub = layoutname;
	}
	
	public SaveData getSaveData() {
		SaveData data = new SaveData();
		
		data.addData("length",numRooms);
		data.addData("dimensions",rows,0);
		data.addData("dimensions",cols,1);
		data.addData("store",store);
		data.addData("rooms",roomGen);
		
		return data;
	}

}